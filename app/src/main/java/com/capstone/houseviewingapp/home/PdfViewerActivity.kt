package com.capstone.houseviewingapp.home

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ActivityPdfViewerBinding

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

        val opened = openPdf(Uri.parse(uriRaw))
        if (!opened) {
            Toast.makeText(this, "PDF를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        showPage(0)
    }

    private fun openPdf(uri: Uri): Boolean {
        return runCatching {
            fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
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
}

