package com.manish.tripgenie.ui.screens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.tripgenie.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LanguageViewModel : ViewModel() {

    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    // Strict token control and low temperature for accuracy
    private val detectionConfig = generationConfig {
        temperature = 0.2f 
        maxOutputTokens = 100
    }

    private val translationConfig = generationConfig {
        temperature = 0.2f 
        maxOutputTokens = 150
    }

    // Fixed modelName and separated models to adhere to generateContent(String) rule
    private val detectionModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = detectionConfig,
        requestOptions = RequestOptions(apiVersion = "v1")
    )

    private val translationModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = translationConfig,
        requestOptions = RequestOptions(apiVersion = "v1")
    )

    val detectedLanguage = mutableStateOf("")
    val translatedText = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    private var processingJob: Job? = null

    /**
     * Optimized Single-Call Approach: Detects and translates in one go.
     * Includes input size control, debouncing logic, and strict token management.
     */
    fun processTranslation(inputText: String, targetLanguage: String) {
        if (inputText.isBlank()) return

        // Debounce / Multiple click protection: cancel previous job if still running
        processingJob?.cancel()

        processingJob = viewModelScope.launch {
            // Debounce delay
            delay(300)
            
            isLoading.value = true
            error.value = null
            
            try {
                // 3️⃣ INPUT SIZE CONTROL: Trim to 300 characters
                val safeText = withContext(Dispatchers.Default) {
                    inputText.take(300)
                }

                // 4️⃣ SINGLE CALL OPTIMIZATION: Combined Prompt
                val prompt = """
                    Detect language and translate.

                    Text: $safeText
                    Target: $targetLanguage

                    Return format:
                    Detected: <language>
                    Translation: <translated text>

                    Rules:
                    - No explanation
                    - Keep output short
                """.trimIndent()

                Log.d("LanguageViewModel", "Processing translation for: $safeText")
                
                // 2️⃣ FIX TYPE INFERENCE: Explicitly typing response
                val response: GenerateContentResponse = withContext(Dispatchers.IO) {
                    // 1️⃣ FIX generateContent CALL: Passing only prompt
                    translationModel.generateContent(prompt)
                }
                
                // 6️⃣ THREADING SAFETY & 3️⃣ FIX RESPONSE TEXT ACCESS
                withContext(Dispatchers.Default) {
                    val resultText = response.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.let { part ->
                            // Rule 3 fix: Accessing text from Part
                            (part as? TextPart)?.text ?: ""
                        } ?: ""
                    
                    parseResponse(resultText)
                }
                
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Step 1: Automatic Language Detection (Stand-alone with strict tokens)
     */
    fun detectLanguage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val safeText = text.take(300)
                val prompt = """
                    Detect language.

                    Text: $safeText

                    Rules:
                    - Output only language name
                    - One word only
                    - No explanation
                """.trimIndent()

                // Explicitly typing response to solve inference error
                val response: GenerateContentResponse = withContext(Dispatchers.IO) {
                    detectionModel.generateContent(prompt)
                }
                
                val result = response.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.let { (it as? TextPart)?.text ?: "" } ?: ""
                
                detectedLanguage.value = result.trim()
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Step 2: Translation Flow (Stand-alone with strict tokens)
     */
    fun translateText(text: String, targetLanguage: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val safeText = text.take(300)
                val prompt = """
                    Translate text.

                    Text: $safeText
                    To: $targetLanguage

                    Rules:
                    - Return ONLY translated text
                    - No explanation
                    - No formatting
                    - Keep output concise
                """.trimIndent()

                // Explicitly typing response to solve inference error
                val response: GenerateContentResponse = withContext(Dispatchers.IO) {
                    translationModel.generateContent(prompt)
                }
                
                val result = response.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.let { (it as? TextPart)?.text ?: "" } ?: ""
                
                translatedText.value = result.trim()
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun parseResponse(rawResult: String) {
        try {
            val lines = rawResult.lines()
            detectedLanguage.value = lines.find { it.startsWith("Detected:", true) }
                ?.substringAfter("Detected:")?.trim() ?: ""
            
            translatedText.value = lines.find { it.startsWith("Translation:", true) }
                ?.substringAfter("Translation:")?.trim() ?: ""
        } catch (e: Exception) {
            translatedText.value = rawResult
        }
    }

    private fun handleException(e: Exception) {
        Log.e("LanguageViewModel", "Gemini AI Error", e)
        error.value = when (e) {
            // 5️⃣ ERROR HANDLING: Catch MAX_TOKENS
            is ResponseStoppedException -> "Text too long. Showing partial translation."
            is ServerException -> "Translation failed. Try again."
            else -> "Translation failed. Try again."
        }
    }
}
