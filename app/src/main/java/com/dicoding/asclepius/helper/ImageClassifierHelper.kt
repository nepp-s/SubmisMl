package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.FileNotFoundException


class ImageClassifierHelper(private val context: Context) {
    private lateinit var imageClassifier: ImageClassifier

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
            val modelPath = "ml/cancer_classification.tflite"
            try {
                val modelExists = context.assets.list("ml")?.contains("cancer_classification.tflite") ?: false
                if (!modelExists) {
                    throw FileNotFoundException("Model file not found in assets: $modelPath")
                }

                val options = ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(1)
                .build()

            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelPath,
                options
            )
            } catch (e: FileNotFoundException) {
                throw RuntimeException("Model file not found in assets: $modelPath", e)
            } catch (e: Exception) {
                throw RuntimeException("Failed to initialize the Image Classifier: ${e.message}", e)
            }
    }

    fun classifyStaticImage(imageUri: Uri, callback: (label: String, confidence: Float) -> Unit) {
        val bitmap = getBitmapFromUri(imageUri)
        val tensorImage = TensorImage.fromBitmap(bitmap)

        try {
            val results = imageClassifier.classify(tensorImage)

            if (results.isNotEmpty()) {
                val topResult = results[0].categories[0]
                callback(topResult.label, topResult.score)
            } else {
                callback("Unknown", 0f)
            }
        } catch (e: Exception) {
            callback("Classification Failed: ${e.message}", 0f)
            Log.d("TAG", "classifyStaticImage: ${e.message}")
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: throw IllegalArgumentException("Unable to decode image from URI: $uri")
    }
}