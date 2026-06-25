package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChapterMetadata(
    val id: Int,
    val name: String,
    val startingPage: Int
)

data class QuranUiState(
    val isLoading: Boolean = true,
    val pages: List<MushafPage> = emptyList(),
    val chapters: List<ChapterMetadata> = emptyList(),
    val error: String? = null
)

class QuranViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = QuranRepository(application)

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        loadQuran()
    }

    private fun loadQuran() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getQuranBundle().fold(
                onSuccess = { bundle ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pages = bundle.pages,
                            chapters = bundle.chapters,
                            error = null
                        )
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, error = "فشل في تحميل القرآن الكريم") }
                }
            )
        }
    }

    suspend fun fetchVerseTranslationAndTafsir(verseId: Int): Result<Triple<String, String, String>> {
        return repository.getVerseTranslationAndTafsir(verseId)
    }
}
