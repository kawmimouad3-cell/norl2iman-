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
    val chapters: List<ChapterMetadata> = emptyList(),
    val error: String? = null,
    val searchResults: List<Verse> = emptyList()
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
                            chapters = bundle.chapters,
                            error = null
                        )
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, error = "فشل في تحميل فهرس القرآن الكريم") }
                }
            )
        }
    }

    suspend fun getPage(pageNumber: Int): Result<MushafPage> {
        return repository.getPage(pageNumber)
    }

    private val _allPages = MutableStateFlow<Map<Int, MushafPage>>(emptyMap())
    val allPages: StateFlow<Map<Int, MushafPage>> = _allPages.asStateFlow()

    fun loadPageIfNeeded(pageNumber: Int) {
        if (_allPages.value.containsKey(pageNumber)) return
        
        viewModelScope.launch {
            repository.getPage(pageNumber).onSuccess { page ->
                _allPages.update { it + (pageNumber to page) }
            }
        }
    }

    suspend fun fetchVerseTranslationAndTafsir(verseId: Int): Result<Triple<String, String, String>> {
        return repository.getVerseTranslationAndTafsir(verseId)
    }

    fun search(query: String) {
        if (query.trim().length < 2) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        viewModelScope.launch {
            val results = repository.searchQuran(query.trim())
            _uiState.update { it.copy(searchResults = results) }
        }
    }
}
