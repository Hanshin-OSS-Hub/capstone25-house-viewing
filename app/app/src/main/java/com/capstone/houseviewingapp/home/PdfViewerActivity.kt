package com.capstone.houseviewingapp.home

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * Android [PdfRenderer]로 페이지 비트맵 생성 + [SubsamplingScaleImageView](핀치/더블탭 줌, 팬).
 * PhotoView(io.github.chrisbanes)는 일부 환경에서 Gradle classpath에 안 붙는 경우가 있어 대체.
 */
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, bars.top, 0, bars.bottom)
            insets
        }

        binding.backButton.setOnClickListener { finish() }
        binding.prevButton.setOnClickListener { showPage(currentPageIndex - 1) }
        binding.nextButton.setOnClickListener { showPage(currentPageIndex + 1) }

        setupPdfScaleView()

        binding.titleTextView.text = intent.getStringExtra(EXTRA_TITLE).orEmpty().ifBlank { "PDF 보기" }
        val showReportButton = intent.getBooleanExtra(EXTRA_SHOW_REPORT_BUTTON, false)
        if (showReportButton) {
            binding.reportIssueTopButton.visibility = View.VISIBLE
            binding.reportIssueTopButton.setOnClickListener {
                ReportReceivedDialogFragment().show(supportFragmentManager, "ReportReceivedDialog")
            }
        } else {
            binding.reportIssueTopButton.visibility = View.GONE
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
            "content", "file" -> loadPdfFromUri(uri)
            else -> openRemotePdf(uriRaw)
        }
    }

    private fun setupPdfScaleView() {
        binding.pdfImageView.apply {
            maxScale = 4f
            setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
        }
    }

    private fun loadPdfFromUri(uri: Uri) {
        binding.pdfImageView.visibility = View.INVISIBLE
        lifecycleScope.launch {
            val file = withContext(Dispatchers.IO) { uriToTempFile(uri) }
            if (file == null || !file.exists() || file.length() == 0L) {
                Toast.makeText(this@PdfViewerActivity, "PDF를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            val opened = openPdf(Uri.fromFile(file))
            if (!opened) {
                Toast.makeText(this@PdfViewerActivity, "PDF를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            binding.pdfImageView.visibility = View.VISIBLE
            binding.pdfImageView.post { showPage(0) }
        }
    }

    private fun uriToTempFile(uri: Uri): File? {
        return runCatching {
            when (uri.scheme?.lowercase()) {
                "file" -> uri.toFile().takeIf { it.exists() && it.length() > 0L }
                else -> {
                    val out = File(cacheDir, "pdf_viewer_${uri.hashCode()}.pdf")
                    contentResolver.openInputStream(uri)?.use { input ->
                        out.outputStream().use { output -> input.copyTo(output) }
                    }
                    out.takeIf { it.exists() && it.length() > 0L }
                }
            }
        }.getOrNull()
    }

    private fun openPdf(uri: Uri): Boolean {
        return runCatching {
            closePdf()
            fileDescriptor = when (uri.scheme?.lowercase()) {
                "file" -> ParcelFileDescriptor.open(uri.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
                else -> contentResolver.openFileDescriptor(uri, "r")
            }
            val fd = fileDescriptor ?: return false
            pdfRenderer = PdfRenderer(fd)
            true
        }.getOrDefault(false)
    }

    private fun closePdf() {
        currentPage?.close()
        currentPage = null
        pdfRenderer?.close()
        pdfRenderer = null
        fileDescriptor?.close()
        fileDescriptor = null
    }

    private fun showPage(index: Int) {
        val renderer = pdfRenderer ?: return
        if (index < 0 || index >= renderer.pageCount) return

        currentPage?.close()
        currentPage = renderer.openPage(index)
        currentPageIndex = index

        val page = currentPage ?: return
        // 화면 너비에 맞춰 스케일 업 (한 페이지가 가로로 꽉 차게)
        val targetW = binding.pdfImageView.width.takeIf { it > 0 }
            ?: resources.displayMetrics.widthPixels
        val scale = (targetW.toFloat() / page.width).coerceIn(1f, 4f)
        var bw = (page.width * scale).toInt().coerceAtLeast(page.width)
        var bh = (page.height * scale).toInt().coerceAtLeast(page.height)
        val maxSide = 4096
        val longSide = maxOf(bw, bh)
        if (longSide > maxSide) {
            val r = maxSide.toFloat() / longSide
            bw = (bw * r).toInt().coerceAtLeast(1)
            bh = (bh * r).toInt().coerceAtLeast(1)
        }

        val bitmap = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        binding.pdfImageView.setImage(ImageSource.bitmap(bitmap))
        binding.pdfImageView.post { binding.pdfImageView.resetScaleAndCenter() }

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
            binding.pdfImageView.post { showPage(0) }
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

    private fun alignPdfUrlHostWithApiBase(url: String): String {
        val target = url.toHttpUrlOrNull() ?: return url
        val base = BuildConfig.API_BASE_URL.toHttpUrlOrNull() ?: return url
        val loopbackHosts = setOf("127.0.0.1", "localhost")
        val targetHost = target.host.lowercase()
        if (targetHost !in loopbackHosts) return url
        val baseHost = base.host.lowercase()
        if (baseHost in loopbackHosts) return url
        return target.newBuilder()
            .scheme(base.scheme)
            .host(base.host)
            .port(base.port)
            .build()
            .toString()
    }

    override fun onDestroy() {
        binding.pdfImageView.recycle()
        closePdf()
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
        if (target.queryParameterNames.any { it.startsWith("X-Amz-", ignoreCase = true) }) return false

        val base = BuildConfig.API_BASE_URL.toHttpUrlOrNull() ?: return false
        return target.host.equals(base.host, ignoreCase = true) && target.port == base.port
    }

    private sealed class PdfLoadResult {
        data class Success(val uri: Uri) : PdfLoadResult()
        data class Failure(val reason: String) : PdfLoadResult()
    }
}
