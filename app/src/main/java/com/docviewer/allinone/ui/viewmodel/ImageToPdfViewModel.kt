package com.docviewer.allinone.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docviewer.allinone.viewer.ImageToPdfConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageToPdfViewModel : ViewModel() {

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    private val _pdfState = MutableStateFlow<PdfCreationState>(PdfCreationState.Idle)
    val pdfState: StateFlow<PdfCreationState> = _pdfState.asStateFlow()

    private val _pdfFileName = MutableStateFlow("document")
    val pdfFileName: StateFlow<String> = _pdfFileName.asStateFlow()

    fun addImages(uris: List<Uri>) {
        _selectedImages.value = _selectedImages.value + uris
    }

    fun removeImage(index: Int) {
        val current = _selectedImages.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _selectedImages.value = current
        }
    }

    fun moveImage(fromIndex: Int, toIndex: Int) {
        val current = _selectedImages.value.toMutableList()
        if (fromIndex in current.indices && toIndex in current.indices) {
            val item = current.removeAt(fromIndex)
            current.add(toIndex, item)
            _selectedImages.value = current
        }
    }

    fun clearImages() {
        _selectedImages.value = emptyList()
        _pdfState.value = PdfCreationState.Idle
    }

    fun updateFileName(name: String) {
        _pdfFileName.value = name
    }

    fun createPdf(context: Context) {
        val images = _selectedImages.value
        if (images.isEmpty()) return

        viewModelScope.launch {
            _pdfState.value = PdfCreationState.Creating

            try {
                val fileName =
                        _pdfFileName.value.ifBlank {
                            "IMG_PDF_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}"
                        }

                // Uses MediaStore API on Android 10+ → saves to Documents folder
                val contentUri = ImageToPdfConverter.convertAndSave(context, images, fileName)

                _pdfState.value =
                        PdfCreationState.Success(pdfUri = contentUri, fileName = "$fileName.pdf")
            } catch (e: Exception) {
                _pdfState.value = PdfCreationState.Error(e.message ?: "Failed to create PDF")
            }
        }
    }

    fun resetState() {
        _pdfState.value = PdfCreationState.Idle
    }
}

sealed class PdfCreationState {
    data object Idle : PdfCreationState()
    data object Creating : PdfCreationState()
    data class Success(val pdfUri: Uri, val fileName: String) : PdfCreationState()
    data class Error(val message: String) : PdfCreationState()
}
