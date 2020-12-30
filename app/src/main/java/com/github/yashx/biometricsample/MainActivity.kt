package com.github.yashx.biometricsample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.github.yashx.biometricsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var canUseBiometricAuth = false
    private lateinit var biometricManager: BiometricManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        biometricManager = BiometricManager.from(this)

        val promptInfo = BiometricPrompt.PromptInfo.Builder().run {
            setTitle(getString(R.string.authenticate))
            setNegativeButtonText(getString(R.string.cancel))
            setDeviceCredentialAllowed(false)
            build()
        }

        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    setTextViewFailed()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    setTextViewAuthenticated()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    setTextViewFailed()
                }
            })

        binding.authButton.setOnClickListener {
            if (canUseBiometricAuth) {
                resetTextView()
                prompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, R.string.cannotDoBioAuth, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        canUseBiometricAuth = when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    private fun setTextViewAuthenticated() {
        binding.authTextView.apply {
            text = getString(R.string.authenticated)
            setTextColor(ContextCompat.getColor(context, R.color.green))
        }
    }

    private fun setTextViewFailed() {
        binding.authTextView.apply {
            text = getString(R.string.failed)
            setTextColor(ContextCompat.getColor(context, R.color.red))
        }
    }

    private fun resetTextView() {
        binding.authTextView.apply {
            text = getString(R.string.unauthenticated)
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
}