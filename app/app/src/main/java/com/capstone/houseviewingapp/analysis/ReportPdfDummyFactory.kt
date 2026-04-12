package com.capstone.houseviewingapp.analysis

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ReportPdfDummyFactory {
    fun createOrGet(context: Context, item: AnalysisRecordItem): Uri {
        val safeTitle = item.title.replace(Regex("[^0-9a-zA-Z가-힣_-]"), "_")
        val fileName = "report_${safeTitle.ifBlank { "analysis" }}.pdf"
        val dir = File(context.cacheDir, "analysis_report_dummy").apply { mkdirs() }
        val outFile = File(dir, fileName)
        if (!outFile.exists()) {
            createDummyPdf(outFile, item)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outFile
        )
    }

    private fun createDummyPdf(file: File, item: AnalysisRecordItem) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(1240, 1754, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 50f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val subtitlePaint = Paint().apply {
            color = Color.GRAY
            textSize = 24f
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 30f
            isAntiAlias = true
        }

        var y = 130f
        canvas.drawText("상세 대응 리포트 (DUMMY)", 80f, y, titlePaint)
        y += 70f
        canvas.drawText("실제 연동 전 시연용 더미 문서입니다.", 80f, y, subtitlePaint)
        y += 110f
        canvas.drawText("진단 대상: ${item.title}", 80f, y, bodyPaint)
        y += 55f
        canvas.drawText("주소: ${item.address}", 80f, y, bodyPaint)
        y += 70f
        canvas.drawText("위험 요약", 80f, y, titlePaint.apply { textSize = 40f })
        y += 60f
        canvas.drawText(item.riskSummary, 100f, y, bodyPaint)
        y += 80f
        canvas.drawText("권장 대응 항목", 80f, y, titlePaint)
        y += 70f
        canvas.drawText("- 권리관계 재확인", 100f, y, bodyPaint)
        y += 48f
        canvas.drawText("- 계약 특약 보완", 100f, y, bodyPaint)
        y += 48f
        canvas.drawText("- 등기부 최신본 재검증", 100f, y, bodyPaint)

        document.finishPage(page)
        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()
    }
}

