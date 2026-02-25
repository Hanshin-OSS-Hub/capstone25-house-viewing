package com.capstone.houseviewingapp.registration

import android.net.Uri
import java.net.URI

data class AttachedFileUiModel (
    val id: String, // 고유 식별자
    val uri: Uri,  // 실제 파일 uri
    val fileName: String, // 파일 이름
    val fileMeta: String // 파일 메타 정보 (예: 크기, 유형 등)
)