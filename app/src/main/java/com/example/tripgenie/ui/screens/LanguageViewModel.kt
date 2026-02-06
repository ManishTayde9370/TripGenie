package com.example.tripgenie.ui.screens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripgenie.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import kotlinx.coroutines.launch

class LanguageViewModel : ViewModel() {

    // 🔐 Secured: Loading the API key from BuildConfig
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    private val config = generationConfig {
        temperature = 0.3f // Lower temperature for more accurate translation
        topP = 0.95f
        topK = 40
        maxOutputTokens = 1024
    }

    /**
     * Using Gemini 1.5 Flash for translations.
     * Explicitly setting apiVersion to "v1" to avoid v1beta 404 errors.
     */
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = config,
        requestOptions = RequestOptions(apiVersion = "v1")
    )

    val translatedText = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun translate(text: String, targetLanguage: String = "French") {
        if (text.isBlank()) return

        if (apiKey.isBlank() || apiKey == "YOUR_NEW_API_KEY_HERE") {
            error.value = "Translation failed: API Key missing."
            return
        }

        val prompt = """
            You are a professional translator. 
            Translate the following text into $targetLanguage.
            Only provide the translated text, no explanation or context.
            
            Text: $text
        """.trimIndent()

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                Log.d("LanguageViewModel", "Translating to $targetLanguage using Gemini 1.5 Flash")
                val response = model.generateContent(prompt)
                translatedText.value = response.text?.trim() ?: "No translation returned."
            } catch (e: Exception) {
                Log.e("LanguageViewModel", "Translation Error", e)
                error.value = "Translation failed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
