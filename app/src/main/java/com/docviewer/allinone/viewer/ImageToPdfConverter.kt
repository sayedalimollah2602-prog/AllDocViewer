package com.docviewer.allinone.viewer

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageToPdfConverter {

    // A4 dimensions at 72 DPI
    private const val A4_WIDTH = 595
    private const val A4_HEIGHT = 842

    /**
     * Creates a PDF from images and saves it using MediaStore API (Android 10+) or direct file IO
     * (Android 9 and below).
     *
     * On Android 10+, the PDF is saved to Internal Storage > Documents using ContentResolver +
     * MediaStore — no WRITE_EXTERNAL_STORAGE needed.
     *
     * @return A content:// URI pointing to the saved PDF
     */
    suspend fun convertAndSave(context: Context, imageUris: List<Uri>, fileName: String): Uri =
            withContext(Dispatchers.IO) {
                // Build the PDF in memory first
                val pdfDocument = PdfDocument()

                try {
                    imageUris.forEachIndexed { index, uri ->
                        val bitmap =
                                decodeBitmap(context, uri)
                                        ?: throw IllegalStateException(
                                                "Failed to decode image: $uri"
                                        )

                        try {
                            val (scaledWidth, scaledHeight) =
                                    calculateFitDimensions(
                                            bitmap.width,
                                            bitmap.height,
                                            A4_WIDTH,
                                            A4_HEIGHT
                                    )

                            val pageInfo =
                                    PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, index + 1)
                                            .create()
                            val page = pdfDocument.startPage(pageInfo)

                            val canvas: Canvas = page.canvas
                            canvas.drawColor(Color.WHITE)

                            // Center the image on the page
                            val left = (A4_WIDTH - scaledWidth) / 2f
                            val top = (A4_HEIGHT - scaledHeight) / 2f

                            val scaledBitmap =
                                    Bitmap.createScaledBitmap(
                                            bitmap,
                                            scaledWidth,
                                            scaledHeight,
                                            true
                                    )
                            canvas.drawBitmap(scaledBitmap, left, top, null)

                            if (scaledBitmap != bitmap) {
                                scaledBitmap.recycle()
                            }

                            pdfDocument.finishPage(page)
                        } finally {
                            bitmap.recycle()
                        }
                    }

                    // Save using the appropriate method
                    val safeName = fileName.replace(Regex("[^a-zA-Z0-9._\\- ]"), "_")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10+ → use MediaStore to save into Documents folder
                        saveWithMediaStore(context, pdfDocument, safeName)
                    } else {
                        // Android 9 and below → direct file write to Documents
                        saveDirectly(context, pdfDocument, safeName)
                    }
                } finally {
                    pdfDocument.close()
                }
            }

    /**
     * Save PDF via MediaStore API into shared Documents folder. This is the modern approach that
     * works on Android 10+ without needing WRITE_EXTERNAL_STORAGE permission.
     */
    private fun saveWithMediaStore(
            context: Context,
            pdfDocument: PdfDocument,
            fileName: String
    ): Uri {
        val contentValues =
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

        val resolver = context.contentResolver
        val uri =
                resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                        ?: throw IllegalStateException("Failed to create MediaStore entry")

        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
                    ?: throw IllegalStateException("Failed to open output stream")

            // Mark as no longer pending so it becomes visible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val updateValues =
                        ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }
                resolver.update(uri, updateValues, null, null)
            }
        } catch (e: Exception) {
            // Clean up the MediaStore entry on failure
            resolver.delete(uri, null, null)
            throw e
        }

        return uri
    }

    /** Fallback for Android 9 and below — save directly to Documents directory. */
    private fun saveDirectly(context: Context, pdfDocument: PdfDocument, fileName: String): Uri {
        val documentsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        documentsDir.mkdirs()
        val outputFile = File(documentsDir, "$fileName.pdf")

        FileOutputStream(outputFile).use { outputStream -> pdfDocument.writeTo(outputStream) }

        return Uri.fromFile(outputFile)
    }

    private fun decodeBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)

                val sampleSize =
                        calculateSampleSize(
                                options.outWidth,
                                options.outHeight,
                                A4_WIDTH * 3,
                                A4_HEIGHT * 3
                        )

                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                    BitmapFactory.decodeStream(stream, null, decodeOptions)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateSampleSize(
            rawWidth: Int,
            rawHeight: Int,
            maxWidth: Int,
            maxHeight: Int
    ): Int {
        var sampleSize = 1
        if (rawWidth > maxWidth || rawHeight > maxHeight) {
            val halfWidth = rawWidth / 2
            val halfHeight = rawHeight / 2
            while (halfWidth / sampleSize >= maxWidth && halfHeight / sampleSize >= maxHeight) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }

    private fun calculateFitDimensions(
            srcWidth: Int,
            srcHeight: Int,
            maxWidth: Int,
            maxHeight: Int
    ): Pair<Int, Int> {
        val widthRatio = maxWidth.toFloat() / srcWidth
        val heightRatio = maxHeight.toFloat() / srcHeight
        val ratio = minOf(widthRatio, heightRatio)
        return Pair((srcWidth * ratio).toInt(), (srcHeight * ratio).toInt())
    }
}
