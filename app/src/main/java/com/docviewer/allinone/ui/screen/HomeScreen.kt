package com.docviewer.allinone.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.NoteAdd
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.docviewer.allinone.ui.components.FileCard
import com.docviewer.allinone.ui.theme.*
import com.docviewer.allinone.ui.viewmodel.HomeViewModel
import com.docviewer.allinone.viewer.DocumentViewerFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        onFileSelected: (Uri) -> Unit,
        onNavigateToCreatePdf: () -> Unit,
        viewModel: HomeViewModel = viewModel()
) {
        val context = LocalContext.current
        val searchQuery by viewModel.searchQuery.collectAsState()
        val files by viewModel.files.collectAsState()
        var isFabExpanded by remember { mutableStateOf(false) }

        val filePickerLauncher =
                rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                        if (result.resultCode == android.app.Activity.RESULT_OK) {
                                result.data?.data?.let { selectedUri ->
                                        val name =
                                                DocumentViewerFactory.getFileName(
                                                        context,
                                                        selectedUri
                                                )
                                        val type =
                                                DocumentViewerFactory.getDocumentType(
                                                        context,
                                                        selectedUri
                                                )
                                        val size =
                                                DocumentViewerFactory.getFileSize(
                                                        context,
                                                        selectedUri
                                                )

                                        viewModel.addRecentFile(
                                                context,
                                                selectedUri,
                                                name,
                                                type,
                                                size
                                        ) { localUri ->
                                                onFileSelected(localUri)
                                                viewModel.scanLocalDocuments(context)
                                        }
                                }
                        }
                }

        LaunchedEffect(Unit) { viewModel.scanLocalDocuments(context) }

        Scaffold(
                floatingActionButton = {
                        Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                AnimatedVisibility(
                                        visible = isFabExpanded,
                                        enter =
                                                fadeIn() +
                                                        slideInVertically(initialOffsetY = { 50 }),
                                        exit =
                                                fadeOut() +
                                                        slideOutVertically(targetOffsetY = { 50 })
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.End,
                                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                                modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                                // Create PDF Button
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        Surface(
                                                                shape = RoundedCornerShape(8.dp),
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant,
                                                                shadowElevation = 2.dp
                                                        ) {
                                                                Text(
                                                                        text =
                                                                                "Create PDF from Images",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        horizontal =
                                                                                                12.dp,
                                                                                        vertical =
                                                                                                6.dp
                                                                                )
                                                                )
                                                        }
                                                        FloatingActionButton(
                                                                onClick = {
                                                                        isFabExpanded = false
                                                                        onNavigateToCreatePdf()
                                                                },
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .secondaryContainer,
                                                                modifier = Modifier.size(48.dp)
                                                        ) {
                                                                Icon(
                                                                        Icons.Rounded.PictureAsPdf,
                                                                        contentDescription =
                                                                                "Create PDF"
                                                                )
                                                        }
                                                }

                                                // Open Document Button
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        Surface(
                                                                shape = RoundedCornerShape(8.dp),
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant,
                                                                shadowElevation = 2.dp
                                                        ) {
                                                                Text(
                                                                        text = "Open Document",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        horizontal =
                                                                                                12.dp,
                                                                                        vertical =
                                                                                                6.dp
                                                                                )
                                                                )
                                                        }
                                                        FloatingActionButton(
                                                                onClick = {
                                                                        isFabExpanded = false
                                                                        val intent =
                                                                                android.content
                                                                                        .Intent(
                                                                                                android.content
                                                                                                        .Intent
                                                                                                        .ACTION_OPEN_DOCUMENT
                                                                                        )
                                                                                        .apply {
                                                                                                addCategory(
                                                                                                        android.content
                                                                                                                .Intent
                                                                                                                .CATEGORY_OPENABLE
                                                                                                )
                                                                                                type =
                                                                                                        "*/*"
                                                                                                putExtra(
                                                                                                        android.content
                                                                                                                .Intent
                                                                                                                .EXTRA_MIME_TYPES,
                                                                                                        arrayOf(
                                                                                                                "application/pdf",
                                                                                                                "application/msword",
                                                                                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                                                                                                "application/vnd.ms-excel",
                                                                                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                                                                                                "application/vnd.ms-powerpoint",
                                                                                                                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                                                                                                        )
                                                                                                )
                                                                                                addFlags(
                                                                                                        android.content
                                                                                                                .Intent
                                                                                                                .FLAG_GRANT_READ_URI_PERMISSION or
                                                                                                                android.content
                                                                                                                        .Intent
                                                                                                                        .FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                                                                                )
                                                                                        }
                                                                        filePickerLauncher.launch(
                                                                                intent
                                                                        )
                                                                },
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .secondaryContainer,
                                                                modifier = Modifier.size(48.dp)
                                                        ) {
                                                                Icon(
                                                                        Icons.Rounded.NoteAdd,
                                                                        contentDescription =
                                                                                "Open Document"
                                                                )
                                                        }
                                                }
                                        }
                                }

                                // Main FAB
                                LargeFloatingActionButton(
                                        onClick = { isFabExpanded = !isFabExpanded },
                                        shape = CircleShape,
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                        Icon(
                                                if (isFabExpanded) Icons.Rounded.Clear
                                                else Icons.Rounded.Add,
                                                contentDescription =
                                                        if (isFabExpanded) "Close menu"
                                                        else "Expand menu",
                                                modifier = Modifier.size(32.dp)
                                        )
                                }
                        }
                }
        ) { innerPadding ->
                LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentPadding =
                                PaddingValues(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 12.dp,
                                        bottom = 100.dp
                                ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Header
                        item {
                                Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                ) {
                                        Text(
                                                text = "Document Viewer",
                                                style = MaterialTheme.typography.displayMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                                text = "Open & view your documents",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }

                        // Search bar
                        item {
                                OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { viewModel.updateSearchQuery(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                                Text(
                                                        "Search files…",
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant.copy(
                                                                        alpha = 0.6f
                                                                )
                                                )
                                        },
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Rounded.Search,
                                                        contentDescription = null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        },
                                        trailingIcon = {
                                                if (searchQuery.isNotEmpty()) {
                                                        IconButton(
                                                                onClick = {
                                                                        viewModel.updateSearchQuery(
                                                                                ""
                                                                        )
                                                                }
                                                        ) {
                                                                Icon(
                                                                        Icons.Rounded.Clear,
                                                                        contentDescription =
                                                                                "Clear",
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        unfocusedBorderColor =
                                                                MaterialTheme.colorScheme.outline
                                                )
                                )
                        }

                        // Section label
                        item {
                                Text(
                                        text =
                                                if (searchQuery.isBlank()) "All Documents"
                                                else "Search Results",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(top = 8.dp)
                                )
                        }

                        // File list or empty state
                        if (files.isEmpty()) {
                                item { EmptyState(searchQuery.isNotBlank()) }
                        } else {
                                items(files, key = { it.uri.toString() }) { file ->
                                        FileCard(
                                                file = file,
                                                onClick = { onFileSelected(file.uri) }
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun EmptyState(isSearchResult: Boolean) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Box(
                        modifier =
                                Modifier.size(80.dp)
                                        .clip(CircleShape)
                                        .background(
                                                Brush.linearGradient(
                                                        colors =
                                                                listOf(
                                                                        GradientStart.copy(
                                                                                alpha = 0.1f
                                                                        ),
                                                                        GradientEnd.copy(
                                                                                alpha = 0.1f
                                                                        )
                                                                )
                                                )
                                        ),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                Icons.Rounded.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                }

                Text(
                        text =
                                if (isSearchResult) "No files found"
                                else "No documents found on device",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                        text =
                                if (isSearchResult) "Try a different search term"
                                else "Tap the + button to open a document manually",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
        }
}
