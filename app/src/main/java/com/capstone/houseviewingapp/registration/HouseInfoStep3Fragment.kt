package com.capstone.houseviewingapp.registration


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.FragmentHouseInfoStep3Binding

class HouseInfoStep3Fragment : Fragment(R.layout.fragment_house_info_step3) {
    private var _binding: FragmentHouseInfoStep3Binding? = null
    private val binding get() = _binding!!

    private var selectedFileUri: Uri? = null // 선택된 파일의 URI를 저장하는 변수

    // NOTE : PDF 파일 선택을 위한 ActivityResultLauncher
    private val pickPDfLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri == null) {
                return@registerForActivityResult
            }

            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedFileUri = uri
            renderSelectedFileState()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHouseInfoStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pdfCardView.setOnClickListener {
            pickPDfLauncher.launch(arrayOf("application/pdf")) // NOTE : PDF 파일만 선택하도록 MIME 타입 지정
        }
        // NOTE : 삭제 버튼 클릭시
        binding.attachedFileItem.deleteIcon.setOnClickListener {
            selectedFileUri = null
            renderSelectedFileState()
        }
        renderSelectedFileState()
    }

    // NOTE : PDF 파일이 선택되었는지 여부를 반환하는 함수
    fun hasSelectedFile(): Boolean {
        return selectedFileUri != null
    }

    // NOTE : 선택된 PDF 파일의 URI 문자열을 반환하는 함수 (없으면 null)
    fun getSelectedFileUriString() : String? = selectedFileUri?.toString()

    //TODO : 나중에 백엔드와 연동할 때, getSelectedFileUriString() 대신, selectedFileUri를 서버에 업로드하고, 서버에서 반환된 URL을 저장하도록 수정 필요

    // NOTE : 파일 상태에 따라 UI 업데이트
    private fun renderSelectedFileState() {
        val hasFile = selectedFileUri != null

        binding.emptyFileTextView.visibility = if (hasFile) View.GONE else View.VISIBLE
        binding.attachedFileItem.root.visibility = if (hasFile) View.VISIBLE else View.GONE

        if(hasFile) {
            val uri = selectedFileUri!!
            val fileName = getDisplayName(uri) ?: "문서.pdf"
            val fileSize = getFileSizeText(uri)

            binding.attachedFileItem.fileNameTextView.text = fileName
            binding.attachedFileItem.fileDescTextView.text = "$fileSize  ·  분석 준비 완료"

        }
        (activity as? HouseRegistrationActivity)?.setNextButtonEnabled(hasFile) // 다음 버튼 활성화

    }

    // NOTE : URI에서 파일 이름을 읽어오는 함수
    private fun getDisplayName(uri: Uri): String? {
        requireContext().contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return cursor.getString(nameIndex)
                }
            }
        }
        return null
    }

    // NOTE : 파일 크기를 읽어서 사람이 읽기 쉬운 형식으로 변환하는 함수
    private fun getFileSizeText(uri: Uri): String {
        var bytes = 0L
        requireContext().contentResolver.query(
            uri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    bytes = cursor.getLong(sizeIndex)
                }
            }
        }
        if (bytes < 1024) return "크기 미확인"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1) String.format("%.1f MB", mb) else String.format("%.0f KB", kb)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}