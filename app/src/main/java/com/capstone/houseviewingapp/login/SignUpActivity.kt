package com.capstone.houseviewingapp.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottom = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }
        binding.backButton.setOnClickListener {
            finish()
        }

        // 약관 체크박스 동기화: 전체 동의 ↔ 개별 동의
        var isSyncingAgreementState = false
        fun syncAllCheckboxFromIndividuals() {
            if (isSyncingAgreementState) return
            isSyncingAgreementState = true
            binding.allCheckBox.isChecked =
                binding.termOfServieceCheckBox.isChecked && binding.infoCheckBox.isChecked
            isSyncingAgreementState = false
        }

        binding.allCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isSyncingAgreementState) return@setOnCheckedChangeListener
            isSyncingAgreementState = true
            binding.termOfServieceCheckBox.isChecked = isChecked
            binding.infoCheckBox.isChecked = isChecked
            isSyncingAgreementState = false
        }

        binding.termOfServieceCheckBox.setOnCheckedChangeListener { _, _ ->
            syncAllCheckboxFromIndividuals()
        }
        binding.infoCheckBox.setOnCheckedChangeListener { _, _ ->
            syncAllCheckboxFromIndividuals()
        }

        binding.termTextView.setOnClickListener {
            AgreementDialogFragment
                .newInstance(R.string.terms_title, R.string.terms_content)
                .show(supportFragmentManager, "TermsDialog")
        }
        binding.infoTextView.setOnClickListener {
            AgreementDialogFragment
                .newInstance(R.string.privacy_title, R.string.privacy_content)
                .show(supportFragmentManager, "PrivacyDialog")
        }

        binding.confirmButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}