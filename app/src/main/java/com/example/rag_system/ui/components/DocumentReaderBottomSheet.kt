package com.example.rag_system.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.data.repository.DocumentRepository
import com.example.rag_system.ui.models.SourceCitationUiModel
import com.example.rag_system.ui.screens.user.PdfPageViewer
import com.example.rag_system.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Component hiển thị tài liệu học tập thực tế dạng Bottom Sheet đè lên màn hình Chat.
 * Tải file thật từ Server, mở đến đúng trang được trích dẫn và cho phép duyệt toàn bộ tài liệu.
 */
@Composable
fun DocumentReaderBottomSheet(
    citation: SourceCitationUiModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val repository = remember { DocumentRepository() }

    // Bắt đầu đọc trực tiếp tại trang được trích dẫn
    var currentPage by rememberSaveable { mutableStateOf(citation.pageNumber ?: 1) }
    var totalPages by remember { mutableStateOf(10) }
    val bookmarkedPages = remember { mutableStateListOf<Int>() }

    var pdfFile by remember { mutableStateOf<File?>(null) }
    var isLoadingFile by remember { mutableStateOf(true) }
    var downloadProgress by remember { mutableStateOf(0) }
    var isPdfRenderError by remember { mutableStateOf(false) }
    var extractedText by remember { mutableStateOf("") }
    val charsPerPage = 1500

    // Tự động tải file thật về Cache cục bộ
    LaunchedEffect(citation.documentId) {
        isLoadingFile = true
        downloadProgress = 0
        isPdfRenderError = false
        extractedText = ""
        val file = repository.downloadDocumentFile(context, citation.documentId) { progress ->
            downloadProgress = progress
        }
        pdfFile = file
        isLoadingFile = false
        if (file != null && file.exists()) {
            if (isZipFile(file) || file.name.endsWith(".txt", true)) {
                isPdfRenderError = true
            }
        }
    }

    // Tự động bóc tách text nếu gặp file Word hoặc lỗi render PDF
    LaunchedEffect(pdfFile, isPdfRenderError) {
        if (isPdfRenderError && pdfFile != null && pdfFile!!.exists()) {
            withContext(Dispatchers.IO) {
                val text = when {
                    isZipFile(pdfFile!!) -> {
                        extractTextFromDocx(pdfFile!!)
                    }
                    else -> {
                        try {
                            pdfFile!!.readText(Charsets.UTF_8)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ""
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    extractedText = text
                    if (text.isNotEmpty()) {
                        totalPages = java.lang.Math.ceil(text.length.toDouble() / charsPerPage).toInt().coerceIn(1, 1000)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Lớp phủ đen trong suốt (Backdrop)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )

        // Bottom Sheet tài liệu trượt lên (Cao 85% màn hình)
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = BrandSurface,
            shadowElevation = 16.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Thanh kéo Drag Handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp)
                        .width(48.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BrandOutlineVariant)
                )

                // Top App Bar
                EduRAGTopAppBar(
                    applyStatusBarPadding = false,
                    navigationContent = {
                        Column {
                            Text(
                                text = citation.sourceDocumentName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = BrandTextPrimary,
                                maxLines = 1
                             )
                            Text(
                                text = "Trang $currentPage • Tài liệu học tập",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandTextSecondary
                            )
                        }
                    },
                    actionContent = {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandSurfaceContainerLow)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = BrandOnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                // Vùng hiển thị nội dung tài liệu
                Box(modifier = Modifier.weight(1f)) {
                    if (isLoadingFile) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                progress = { downloadProgress / 100f },
                                color = BrandPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Đang tải tài liệu... $downloadProgress%",
                                color = BrandTextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else if (pdfFile != null && pdfFile!!.exists() && !isPdfRenderError) {
                        // PDF Reader thật
                        PdfPageViewer(
                            file = pdfFile!!,
                            pageNumber = currentPage,
                            onPageCountLoaded = { totalPages = it },
                            onRenderError = { isPdfRenderError = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Fallback: Text Reader cho file Word hoặc khi lỗi render PDF
                        val pageText = if (extractedText.isNotEmpty()) {
                            val start = ((currentPage - 1) * charsPerPage).coerceIn(0, extractedText.length)
                            val end = (currentPage * charsPerPage).coerceIn(0, extractedText.length)
                            extractedText.substring(start, end)
                        } else {
                            "Đang trích xuất nội dung văn bản..."
                        }

                        // Nếu ở đúng trang trích dẫn, làm nổi bật (highlight) đoạn văn bản AI tham chiếu
                        val isCitedPage = currentPage == citation.pageNumber
                        DocumentContentArea(
                            chapterTitle = if (extractedText.isNotEmpty()) "Tài liệu trích xuất văn bản" else "Đang xử lý tài liệu",
                            sectionTitle = "Trang số $currentPage",
                            bodyTextBefore = if (isCitedPage) "" else pageText,
                            highlightedSnippet = if (isCitedPage) citation.rawExtractedText else "",
                            bodyTextAfter = if (isCitedPage) pageText.replace(citation.rawExtractedText, "") else "",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Thanh điều khiển trang và bookmark
                DocumentReaderControls(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    isBookmarked = currentPage in bookmarkedPages,
                    onPageChanged = { page -> currentPage = page },
                    onBookmarkToggled = {
                        val isNowBookmarked = currentPage !in bookmarkedPages
                        if (isNowBookmarked) {
                            bookmarkedPages.add(currentPage)
                            toastManager.showToast("Đã lưu dấu trang $currentPage thành công!", ToastType.SUCCESS)
                        } else {
                            bookmarkedPages.remove(currentPage)
                            toastManager.showToast("Đã bỏ lưu dấu trang $currentPage!", ToastType.INFO)
                        }
                    }
                )
            }
        }
    }
}

private fun isZipFile(file: File): Boolean {
    return try {
        file.inputStream().use { input ->
            val header = ByteArray(2)
            val read = input.read(header)
            read == 2 && header[0] == 0x50.toByte() && header[1] == 0x4B.toByte()
        }
    } catch (e: Exception) {
        false
    }
}

private fun extractTextFromDocx(file: File): String {
    return try {
        java.util.zip.ZipFile(file).use { zip ->
            val entry = zip.getEntry("word/document.xml") ?: return ""
            zip.getInputStream(entry).use { inputStream ->
                val content = inputStream.bufferedReader().use { it.readText() }
                
                val pRegex = "<w:p(?: [^>]*)?>(.*?)</w:p>".toRegex(RegexOption.DOT_MATCHES_ALL)
                val elementRegex = "<w:t(?: [^>]*)?>(.*?)</w:t>|<w:br(?: [^>]*)?/?>".toRegex(RegexOption.DOT_MATCHES_ALL)
                
                val pMatches = pRegex.findAll(content)
                val paragraphs = pMatches.map { pMatch ->
                    val pContent = pMatch.groupValues[1]
                    val elMatches = elementRegex.findAll(pContent)
                    val pText = StringBuilder()
                    for (elMatch in elMatches) {
                        val value = elMatch.value
                        if (value.startsWith("<w:br")) {
                            pText.append("\n")
                        } else {
                            val textVal = elMatch.groupValues[1]
                            pText.append(
                                textVal.replace("&amp;", "&")
                                       .replace("&lt;", "<")
                                       .replace("&gt;", ">")
                                       .replace("&quot;", "\"")
                                       .replace("&apos;", "'")
                            )
                        }
                    }
                    pText.toString()
                }
                paragraphs.filter { it.isNotBlank() }.joinToString("\n\n")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
