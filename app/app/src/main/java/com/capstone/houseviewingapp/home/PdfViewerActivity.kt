package com.capstone.houseviewingapp.home

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.Toast
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.houseviewingapp.BuildConfig
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.data.local.AuthTokenLocalStore
import com.capstone.houseviewingapp.databinding.ActivityPdfViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class PdfViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfViewerBinding

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var currentPageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.backButton.setOnClickListener { finish() }
        binding.prevButton.setOnClickListener { showPage(currentPageIndex - 1) }
        binding.nextButton.setOnClickListener { showPage(currentPageIndex + 1) }

        binding.titleTextView.text = intent.getStringExtra(EXTRA_TITLE).orEmpty().ifBlank { "PDF 보기" }
        val showReportButton = intent.getBooleanExtra(EXTRA_SHOW_REPORT_BUTTON, false)
        if (showReportButton) {
            binding.reportIssueTopButton.visibility = android.view.View.VISIBLE
            binding.reportIssueTopButton.setOnClickListener {
                ReportReceivedDialogFragment().show(supportFragmentManager, "ReportReceivedDialog")
            }
        } else {
            binding.reportIssueTopButton.visibility = android.view.View.GONE
        }

        val uriRaw = intent.getStringExtra(EXTRA_URI).orEmpty()
        if (uriRaw.isBlank()) {
            Toast.makeText(this, "PDF 경로를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val uri = Uri.parse(uriRaw)
        val scheme = uri.scheme?.lowercase().orEmpty()
        when (scheme) {
            "http", "https" -> openRemotePdf(uriRaw)
            "content", "file" -> {
                val opened = openPdf(uri)
                if (!opened) {
                    Toast.makeText(this, "PDF를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }
                showPage(0)
            }
            else -> {
                // 스킴 없는 상대경로/절대경로는 서버 PDF 경로로 간주
                openRemotePdf(uriRaw)
            }
        }
    }

    private fun openPdf(uri: Uri): Boolean {
        return runCatching {
            fileDescriptor = when (uri.scheme?.lowercase()) {
                "file" -> ParcelFileDescriptor.open(uri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
                else -> contentResolver.openFileDescriptor(uri, "r")
            }
            val fd = fileDescriptor ?: return false
            pdfRenderer = PdfRenderer(fd)
            true
        }.getOrDefault(false)
    }

    private fun showPage(index: Int) {
        val renderer = pdfRenderer ?: return
        if (index < 0 || index >= renderer.pageCount) return

        currentPage?.close()
        currentPage = renderer.openPage(index)
        currentPageIndex = index

        val page = currentPage ?: return
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        binding.pdfImageView.setImageBitmap(bitmap)

        binding.pageTextView.text = "${index + 1} / ${renderer.pageCount}"
        binding.prevButton.isEnabled = index > 0
        binding.nextButton.isEnabled = index < renderer.pageCount - 1
    }

    private fun openRemotePdf(url: String) {
        binding.pdfImageView.visibility = View.INVISIBLE
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val resolvedUrl = resolvePdfUrl(url)
                    val token = AuthTokenLocalStore.getAccessToken(this@PdfViewerActivity).orEmpty()
                    downloadPdfToCache(resolvedUrl, token)
                }.getOrElse { PdfLoadResult.Failure("네트워크 요청 예외") }
            }
            val localUri = (result as? PdfLoadResult.Success)?.uri
            if (localUri == null) {
                val reason = (result as? PdfLoadResult.Failure)?.reason ?: "알 수 없는 오류"
                Toast.makeText(this@PdfViewerActivity, "대응 PDF를 불러오지 못했습니다. ($reason)", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            val opened = openPdf(localUri)
            if (!opened) {
                Toast.makeText(this@PdfViewerActivity, "PDF를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            binding.pdfImageView.visibility = View.VISIBLE
            showPage(0)
        }
    }

    private fun downloadPdfToCache(url: String, accessToken: String): PdfLoadResult {
        val httpUrl = url.toHttpUrlOrNull() ?: return PdfLoadResult.Failure("잘못된 URL")
        val attachAuthFirst = shouldAttachAuthHeader(httpUrl.toString(), accessToken)
        val firstToken = if (attachAuthFirst) accessToken else ""
        val secondToken = if (attachAuthFirst) "" else accessToken

        val firstResult = executePdfRequest(httpUrl.toString(), firstToken)
        if (firstResult is PdfLoadResult.Success) return firstResult

        val firstFailure = (firstResult as? PdfLoadResult.Failure)?.reason.orEmpty()
        val shouldRetryWithoutAuth =
            accessToken.isNotBlank() && (firstFailure.contains("HTTP 400") || firstFailure.contains("HTTP 401") || firstFailure.contains("HTTP 403"))
        if (!shouldRetryWithoutAuth) return firstResult

        val secondResult = executePdfRequest(httpUrl.toString(), secondToken)
        if (secondResult is PdfLoadResult.Success) return secondResult
        val secondFailure = (secondResult as? PdfLoadResult.Failure)?.reason.orEmpty()
        return PdfLoadResult.Failure("$firstFailure / retry: $secondFailure")
    }

    private fun executePdfRequest(url: String, accessToken: String): PdfLoadResult {
        val requestBuilder = Request.Builder().url(url).get()
        if (accessToken.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }
        val request = requestBuilder.build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("PdfViewerActivity", "pdf fetch failed url=$url http=${response.code}")
                return PdfLoadResult.Failure("HTTP ${response.code}")
            }
            val body = response.body ?: return PdfLoadResult.Failure("응답 본문 없음")
            val file = File(cacheDir, "analysis_remote_${url.hashCode()}.pdf")
            body.byteStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return PdfLoadResult.Success(Uri.fromFile(file))
        }
    }

    private fun resolvePdfUrl(raw: String): String {
        val trimmed = raw.trim()
        val resolved = if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            trimmed
        } else {
            val base = BuildConfig.API_BASE_URL.trimEnd('/')
            val path = trimmed.trimStart('/')
            "$base/$path"
        }
        return alignPdfUrlHostWithApiBase(resolved)
    }

    /**
     * 백엔드가 PDF 주소에 127.0.0.1 / localhost 를 주는데, 앱은 API_BASE_URL 을 10.0.2.2 로 둔 경우(에뮬레이터) 등
     * 호스트만 달라서 PDF GET 이 실패하는 것을 맞춤. S3 등 외부 호스트 URL 은 그대로 둠.
     */
    private fun alignPdfUrlHostWithApiBase(url: String): String {
        val target = url.toHttpUrlOrNull() ?: return url
        val base = BuildConfig.API_BASE_URL.toHttpUrlOrNull() ?: return url
        val loopbackHosts = setOf("127.0.0.1", "localhost")
        val targetHost = target.host.lowercase()
        if (targetHost !in loopbackHosts) return url
        val baseHost = base.host.lowercase()
        // API 도 루프백이면 그대로 (adb reverse + 127.0.0.1 유지 시)
        if (baseHost in loopbackHosts) return url
        return target.newBuilder()
            .scheme(base.scheme)
            .host(base.host)
            .port(base.port)
            .build()
            .toString()
    }

    override fun onDestroy() {
        currentPage?.close()
        pdfRenderer?.close()
        fileDescriptor?.close()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_URI = "extra_uri"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_SHOW_REPORT_BUTTON = "extra_show_report_button"
    }

    private fun shouldAttachAuthHeader(url: String, accessToken: String): Boolean {
        if (accessToken.isBlank()) return false
        val target = url.toHttpUrlOrNull() ?: return false
        // presigned URL은 Authorization 헤더를 같이 보내면 400/403이 날 수 있음
        if (target.queryParameterNames.any { it.startsWith("X-Amz-", ignoreCase = true) }) return false

        val base = BuildConfig.API_BASE_URL.toHttpUrlOrNull() ?: return false
        return target.host.equals(base.host, ignoreCase = true) && target.port == base.port
    }

    private sealed class PdfLoadResult {
        data class Success(val uri: Uri) : PdfLoadResult()
        data class Failure(val reason: String) : PdfLoadResult()
    }
}

