package com.docviewer.allinone.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.docviewer.allinone.ui.theme.GradientEnd
import com.docviewer.allinone.ui.theme.GradientStart
import com.docviewer.allinone.ui.viewmodel.ImageToPdfViewModel
import com.docviewer.allinone.ui.viewmodel.PdfCreationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageToPdfScreen(onPdfCreated: (Uri) -> Unit, viewModel: ImageToPdfViewModel = viewModel()) {
        val context = LocalContext.current
        val selectedImages by viewModel.selectedImages.collectAsState()
        val pdfState by viewModel.pdfState.collectAsState()
        val pdfFileName by viewModel.pdfFileName.collectAsState()

        val imagePickerLauncher =
                rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetMultipleContents()
                ) { uris: List<Uri> ->
                        if (uris.isNotEmpty()) {
                                viewModel.addImages(uris)
                        }
                }

        // Success dialog with Open, Share, and View in App buttons
        if (pdfState is PdfCreationState.Success) {
                val successState = pdfState as PdfCreationState.Success

                AlertDialog(
                        onDismissRequest = {
                                viewModel.resetState()
                                viewModel.clearImages()
                        },
                        title = {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(40.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .primary.copy(
                                                                                alpha = 0.12f
                                                                        )
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        Icons.Rounded.CheckCircle,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(24.dp)
                                                )
                                        }
                                        Text("PDF Created!", fontWeight = FontWeight.Bold)
                                }
                        },
                        text = {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(
                                                text =
                                                        "\"${successState.fileName}\" has been saved to your Documents folder.",
                                                style = MaterialTheme.typography.bodyMedium
                                        )

                                        Card(
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant
                                                                                .copy(alpha = 0.5f)
                                                        )
                                        ) {
                                                Row(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(12.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(10.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.PictureAsPdf,
                                                                contentDescription = null,
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                modifier = Modifier.size(28.dp)
                                                        )
                                                        Column {
                                                                Text(
                                                                        text =
                                                                                successState
                                                                                        .fileName,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleSmall,
                                                                        fontWeight =
                                                                                FontWeight.SemiBold
                                                                )
                                                                Text(
                                                                        text =
                                                                                "Internal Storage › Documents",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                }
                                        }
                                }
                        },
                        confirmButton = {
                                Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        // View in App button
                                        Button(
                                                onClick = {
                                                        val uri = successState.pdfUri
                                                        viewModel.resetState()
                                                        viewModel.clearImages()
                                                        onPdfCreated(uri)
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(vertical = 12.dp)
                                        ) {
                                                Icon(
                                                        Icons.Rounded.Visibility,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        "View in App",
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                        }

                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                // Open with external app
                                                OutlinedButton(
                                                        onClick = {
                                                                try {
                                                                        val openIntent =
                                                                                Intent(
                                                                                                Intent.ACTION_VIEW
                                                                                        )
                                                                                        .apply {
                                                                                                setDataAndType(
                                                                                                        successState
                                                                                                                .pdfUri,
                                                                                                        "application/pdf"
                                                                                                )
                                                                                                addFlags(
                                                                                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                                                                )
                                                                                        }
                                                                        context.startActivity(
                                                                                Intent.createChooser(
                                                                                        openIntent,
                                                                                        "Open with"
                                                                                )
                                                                        )
                                                                } catch (_: Exception) {}
                                                        },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        contentPadding =
                                                                PaddingValues(vertical = 12.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.OpenInNew,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Open")
                                                }

                                                // Share button
                                                OutlinedButton(
                                                        onClick = {
                                                                try {
                                                                        val shareIntent =
                                                                                Intent(
                                                                                                Intent.ACTION_SEND
                                                                                        )
                                                                                        .apply {
                                                                                                type =
                                                                                                        "application/pdf"
                                                                                                putExtra(
                                                                                                        Intent.EXTRA_STREAM,
                                                                                                        successState
                                                                                                                .pdfUri
                                                                                                )
                                                                                                addFlags(
                                                                                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                                                                )
                                                                                        }
                                                                        context.startActivity(
                                                                                Intent.createChooser(
                                                                                        shareIntent,
                                                                                        "Share PDF"
                                                                                )
                                                                        )
                                                                } catch (_: Exception) {}
                                                        },
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        contentPadding =
                                                                PaddingValues(vertical = 12.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.Share,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Share")
                                                }
                                        }

                                        // Done button
                                        TextButton(
                                                onClick = {
                                                        viewModel.resetState()
                                                        viewModel.clearImages()
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                        ) { Text("Done") }
                                }
                        },
                        shape = RoundedCornerShape(24.dp)
                )
        }

        // Error dialog
        if (pdfState is PdfCreationState.Error) {
                val errorState = pdfState as PdfCreationState.Error
                AlertDialog(
                        onDismissRequest = { viewModel.resetState() },
                        title = { Text("Error") },
                        text = { Text(errorState.message) },
                        confirmButton = {
                                TextButton(onClick = { viewModel.resetState() }) { Text("OK") }
                        },
                        shape = RoundedCornerShape(20.dp)
                )
        }

        Scaffold(
                floatingActionButton = {
                        if (selectedImages.isNotEmpty() && pdfState !is PdfCreationState.Creating) {
                                ExtendedFloatingActionButton(
                                        onClick = { viewModel.createPdf(context) },
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Icon(
                                                Icons.Rounded.PictureAsPdf,
                                                contentDescription = null,
                                                modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Create PDF", fontWeight = FontWeight.SemiBold)
                                }
                        }
                }
        ) { innerPadding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(innerPadding)
                                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                        // Header
                        Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        ) {
                                Text(
                                        text = "Create PDF",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                        text = "Select images to combine into a PDF",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // File name input + action bar
                        if (selectedImages.isNotEmpty()) {
                                OutlinedTextField(
                                        value = pdfFileName,
                                        onValueChange = { viewModel.updateFileName(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("PDF File Name") },
                                        placeholder = { Text("Enter file name") },
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Rounded.Description,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                )
                                        },
                                        trailingIcon = {
                                                Text(
                                                        ".pdf",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant,
                                                        modifier = Modifier.padding(end = 12.dp)
                                                )
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(14.dp),
                                        colors =
                                                OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        unfocusedBorderColor =
                                                                MaterialTheme.colorScheme.outline
                                                )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Action bar
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text =
                                                        "${selectedImages.size} image${if (selectedImages.size > 1) "s" else ""} selected",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onBackground
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                FilledTonalIconButton(
                                                        onClick = {
                                                                imagePickerLauncher.launch(
                                                                        "image/*"
                                                                )
                                                        }
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.AddPhotoAlternate,
                                                                contentDescription =
                                                                        "Add more images"
                                                        )
                                                }

                                                FilledTonalIconButton(
                                                        onClick = { viewModel.clearImages() },
                                                        colors =
                                                                IconButtonDefaults
                                                                        .filledTonalIconButtonColors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .errorContainer
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.6f
                                                                                                )
                                                                        )
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.DeleteOutline,
                                                                contentDescription = "Clear all",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .error
                                                        )
                                                }
                                        }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Info card about save location
                                Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme
                                                                        .primaryContainer.copy(
                                                                        alpha = 0.3f
                                                                )
                                                )
                                ) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                Icon(
                                                        Icons.Rounded.Info,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                        text =
                                                                "PDF will be saved to Internal Storage › Documents",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Content
                        if (pdfState is PdfCreationState.Creating) {
                                // Loading state
                                Box(
                                        modifier = Modifier.fillMaxSize().weight(1f),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(20.dp)
                                        ) {
                                                CircularProgressIndicator(
                                                        modifier = Modifier.size(56.dp),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        strokeWidth = 4.dp
                                                )
                                                Text(
                                                        text = "Creating PDF…",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                                Text(
                                                        text =
                                                                "Processing ${selectedImages.size} images",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant.copy(
                                                                        alpha = 0.7f
                                                                )
                                                )
                                        }
                                }
                        } else if (selectedImages.isEmpty()) {
                                // Empty state
                                Box(
                                        modifier = Modifier.fillMaxSize().weight(1f),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(100.dp)
                                                                        .clip(CircleShape)
                                                                        .background(
                                                                                Brush.linearGradient(
                                                                                        colors =
                                                                                                listOf(
                                                                                                        GradientStart
                                                                                                                .copy(
                                                                                                                        alpha =
                                                                                                                                0.1f
                                                                                                                ),
                                                                                                        GradientEnd
                                                                                                                .copy(
                                                                                                                        alpha =
                                                                                                                                0.1f
                                                                                                                )
                                                                                                )
                                                                                )
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.AddPhotoAlternate,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(48.dp),
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .primary.copy(
                                                                                alpha = 0.6f
                                                                        )
                                                        )
                                                }

                                                Text(
                                                        text = "No images selected",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )

                                                Text(
                                                        text =
                                                                "Select images from your gallery\nto combine them into a PDF",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant.copy(
                                                                        alpha = 0.7f
                                                                ),
                                                        textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Button(
                                                        onClick = {
                                                                imagePickerLauncher.launch(
                                                                        "image/*"
                                                                )
                                                        },
                                                        shape = RoundedCornerShape(14.dp),
                                                        contentPadding =
                                                                PaddingValues(
                                                                        horizontal = 24.dp,
                                                                        vertical = 14.dp
                                                                )
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.PhotoLibrary,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                                "Select Images",
                                                                fontWeight = FontWeight.SemiBold
                                                        )
                                                }
                                        }
                                }
                        } else {
                                // Image grid
                                LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        modifier = Modifier.weight(1f).fillMaxWidth(),
                                        contentPadding = PaddingValues(bottom = 100.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                        itemsIndexed(selectedImages) { index, uri ->
                                                ImagePreviewCard(
                                                        uri = uri,
                                                        pageNumber = index + 1,
                                                        onRemove = { viewModel.removeImage(index) },
                                                        onMoveUp =
                                                                if (index > 0) {
                                                                        {
                                                                                viewModel.moveImage(
                                                                                        index,
                                                                                        index - 1
                                                                                )
                                                                        }
                                                                } else null,
                                                        onMoveDown =
                                                                if (index < selectedImages.size - 1
                                                                ) {
                                                                        {
                                                                                viewModel.moveImage(
                                                                                        index,
                                                                                        index + 1
                                                                                )
                                                                        }
                                                                } else null
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun ImagePreviewCard(
        uri: Uri,
        pageNumber: Int,
        onRemove: () -> Unit,
        onMoveUp: (() -> Unit)?,
        onMoveDown: (() -> Unit)?
) {
        Card(
                shape = RoundedCornerShape(14.dp),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Box {
                        Column {
                                // Image preview
                                AsyncImage(
                                        model = uri,
                                        contentDescription = "Page $pageNumber",
                                        contentScale = ContentScale.Crop,
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(180.dp)
                                                        .clip(
                                                                RoundedCornerShape(
                                                                        topStart = 14.dp,
                                                                        topEnd = 14.dp
                                                                )
                                                        )
                                )

                                // Bottom controls
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 8.dp,
                                                                vertical = 6.dp
                                                        ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        // Page number badge
                                        Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color =
                                                        MaterialTheme.colorScheme.primaryContainer
                                                                .copy(alpha = 0.6f)
                                        ) {
                                                Text(
                                                        text = "Page $pageNumber",
                                                        modifier =
                                                                Modifier.padding(
                                                                        horizontal = 8.dp,
                                                                        vertical = 2.dp
                                                                ),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onPrimaryContainer
                                                )
                                        }

                                        // Move & delete buttons
                                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                                                if (onMoveUp != null) {
                                                        IconButton(
                                                                onClick = onMoveUp,
                                                                modifier = Modifier.size(28.dp)
                                                        ) {
                                                                Icon(
                                                                        Icons.Rounded
                                                                                .KeyboardArrowUp,
                                                                        contentDescription =
                                                                                "Move up",
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant,
                                                                        modifier =
                                                                                Modifier.size(18.dp)
                                                                )
                                                        }
                                                }
                                                if (onMoveDown != null) {
                                                        IconButton(
                                                                onClick = onMoveDown,
                                                                modifier = Modifier.size(28.dp)
                                                        ) {
                                                                Icon(
                                                                        Icons.Rounded
                                                                                .KeyboardArrowDown,
                                                                        contentDescription =
                                                                                "Move down",
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant,
                                                                        modifier =
                                                                                Modifier.size(18.dp)
                                                                )
                                                        }
                                                }
                                                IconButton(
                                                        onClick = onRemove,
                                                        modifier = Modifier.size(28.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Rounded.Close,
                                                                contentDescription = "Remove",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .error,
                                                                modifier = Modifier.size(18.dp)
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
