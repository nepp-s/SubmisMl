package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifier: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifier = ImageClassifierHelper(this)

        val imageUri = intent.getStringExtra(IMAGE_URI_KEY)?.let { Uri.parse(it) }
        imageUri?.let {
            binding.resultImage.setImageURI(it)
            classifyImage(it)
        } ?: showToast("Failed to load image.")
    }

    private fun classifyImage(imageUri: Uri) {
        imageClassifier.classifyStaticImage(imageUri) { label, confidence ->
            runOnUiThread {
                binding.resultText.text = "Prediction: $label\nConfidence: ${confidence * 100}%"
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val IMAGE_URI_KEY = "image_uri"
    }
}