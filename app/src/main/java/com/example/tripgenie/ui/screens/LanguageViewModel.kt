package com.example.tripgenie.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class LanguageViewModel : ViewModel() {

    // 🔐 Reusing the integrated Gemini key and config
    private val apiKey = "AIzaSyAdzKGYC1P4Ox6Qyi166HV3j9yoNcF7oHs"
    
    private val config = generationConfig {
        temperature = 0.3f // Lower temperature for more accurate translation
        topP = 0.95f
        topK = 40
        maxOutputTokens = 1024
    }

    private val model = GenerativeModel(
        modelName = "models/gemini-flash-latest",
        apiKey = apiKey,
        generationConfig = config
    )

    val translatedText = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun translate(text: String, targetLanguage: String = "French") {
        if (text.isBlank()) return

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
                val response = model.generateContent(prompt)
                translatedText.value = response.text?.trim() ?: "No translation returned."
            } catch (e: Exception) {
                error.value = "Translation failed: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
