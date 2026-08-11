
package com.example.rag_system.ui.screens.user

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.Toast
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.models.ReaderPageContentUiModel
import com.example.rag_system.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Màn hình Đọc Tài liệu chuyên dụng (DocumentReaderScreen).
 * Tự động chuyển đổi giữa PDF Reader thật (nếu tải được file) và Text Reader (Mock fallback).
 */
@Composable
fun DocumentReaderScreen(
    documentId: String,
    documentTitle: String = "Tài liệu EduRAG",
    pageContentProvider: (Int) -> ReaderPageContentUiModel = { page ->
        ReaderPageContentUiModel(
            chapterTitle = "Giáo trình trích xuất",
            sectionTitle = "Trang số $page",
            bodyTextBefore = "Nội dung trang $page của tài liệu EduRAG (#$documentId).",
            highlightedSnippet = "Đoạn kiến thức trọng tâm được hệ thống AI trích xuất và tối ưu hóa cho việc đọc hiểu nhanh.",
            bodyTextAfter = "Sử dụng tính năng hỏi đáp AI để tra cứu hoặc giải đáp thắc mắc liên quan."
        )
    },
    downloadFileProvider: suspend (android.content.Context, String, (Int) -> Unit) -> File? = { _, _, _ -> null },
    saveHistoryProvider: (android.content.Context, String) -> Unit = { _, _ -> },
    onExportClick: (File) -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    var currentPage by rememberSaveable { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(10) }
    val bookmarkedPages = remember { mutableStateListOf<Int>() }

    var pdfFile by remember { mutableStateOf<File?>(null) }
    var isLoadingFile by remember { mutableStateOf(true) }
    var downloadProgress by remember { mutableStateOf(0) }
    var isPdfRenderError by remember { mutableStateOf(false) }
    var extractedText by remember { mutableStateOf("") }

    // Quản lý trạng thái Responsive khi xoay ngang màn hình đọc tài liệu
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var isControlsVisible by rememberSaveable { mutableStateOf(true) }
    var showGuidance by rememberSaveable { mutableStateOf(true) }

    // Xử lý nút Back hệ thống/cử chỉ vuốt ngược: Khóa dọc lập tức trước khi pop backstack để tránh lag/vỡ giao diện màn hình trước
    BackHandler {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onBackClick()
    }

    // Cho phép tự động xoay ngang dọc dựa trên cảm biến khi ở màn hình này, và khóa lại chiều dọc khi thoát ra
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    // Tự động tắt hướng dẫn chạm 2 lần sau 5 giây khi người dùng quay ngang màn hình
    LaunchedEffect(isLandscape) {
        if (isLandscape && showGuidance) {
            kotlinx.coroutines.delay(5000)
            showGuidance = false
        }
    }
    val charsPerPage = 1500

    LaunchedEffect(documentId) {
        isLoadingFile = true
        downloadProgress = 0
        isPdfRenderError = false
        extractedText = ""
        val file = downloadFileProvider(context, documentId) { progress ->
            downloadProgress = progress
        }
        pdfFile = file
        isLoadingFile = false
        if (file != null && file.exists()) {
            saveHistoryProvider(context, documentId)
            // Kiểm tra định dạng file ngay từ đầu để tránh bắt PdfRenderer cố đọc file Word/Txt gây lỗi chậm trễ 2s
            if (isZipFile(file) || file.name.endsWith(".txt", true)) {
                isPdfRenderError = true
            }
        }
    }

    LaunchedEffect(pdfFile, isPdfRenderError) {
        if (isPdfRenderError && pdfFile != null && pdfFile!!.exists()) {
            withContext(Dispatchers.IO) {
                val text = when {
                    isZipFile(pdfFile!!) -> {
                        // File Word (.docx) thực chất là file nén zip chứa XML
                        extractTextFromDocx(pdfFile!!)
                    }
                    else -> {
                        // File Text (.txt) đọc trực tiếp
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

    val pageContent = remember(currentPage) { pageContentProvider(currentPage) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandSurface,
        topBar = {
            if (!isLandscape || isControlsVisible) {
                EduRAGTopAppBar(
                    navigationContent = {
                        Text(
                            text = "Quay lại",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandPrimary,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .clickable {
                                    val activity = context as? Activity
                                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                    onBackClick()
                                }
                        )
                    },
                    centerContent = {
                        Text(
                            text = documentTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandTextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp).widthIn(max = 200.dp),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    },
                    actionContent = {
                        if (pdfFile != null && pdfFile!!.exists()) {
                            Text(
                                text = "Tải xuống",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = BrandPrimary,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clickable {
                                        onExportClick(pdfFile!!)
                                    }
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!isLandscape || isControlsVisible) {
                DocumentReaderControls(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    isBookmarked = currentPage in bookmarkedPages,
                    onPageChanged = { currentPage = it },
                    onBookmarkToggled = {
                        if (currentPage in bookmarkedPages) {
                            bookmarkedPages.remove(currentPage)
                            toastManager.showToast("Đã bỏ lưu dấu trang $currentPage!", ToastType.INFO)
                        } else {
                            bookmarkedPages.add(currentPage)
                            toastManager.showToast("Đã lưu dấu trang $currentPage thành công!", ToastType.SUCCESS)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val padding = if (isLandscape && !isControlsVisible) PaddingValues(0.dp) else innerPadding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                    isLandscape = isLandscape,
                    onSingleTap = {
                        isControlsVisible = !isControlsVisible
                        if (showGuidance) showGuidance = false
                    },
                    onPageCountLoaded = { totalPages = it },
                    onRenderError = { isPdfRenderError = true },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback: Text Reader cho file Word/PPTX/Text hoặc khi lỗi render
                val pageText = if (extractedText.isNotEmpty()) {
                    val start = ((currentPage - 1) * charsPerPage).coerceIn(0, extractedText.length)
                    val end = (currentPage * charsPerPage).coerceIn(0, extractedText.length)
                    extractedText.substring(start, end)
                } else {
                    "Đang trích xuất nội dung văn bản..."
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(isLandscape) {
                            if (isLandscape) {
                                detectTapGestures(
                                    onTap = {
                                        isControlsVisible = !isControlsVisible
                                        if (showGuidance) showGuidance = false
                                    }
                                )
                            }
                        }
                ) {
                    DocumentContentArea(
                        chapterTitle = if (extractedText.isNotEmpty()) "Tài liệu trích xuất văn bản" else pageContent.chapterTitle,
                        sectionTitle = "Trang số $currentPage",
                        bodyTextBefore = pageText,
                        highlightedSnippet = if (extractedText.isNotEmpty()) "Nội dung được trích xuất trực tiếp từ file Word/Text gốc của giáo trình." else pageContent.highlightedSnippet,
                        bodyTextAfter = "",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Hiển thị hướng dẫn chạm khi quay ngang màn hình
            AnimatedVisibility(
                visible = isLandscape && showGuidance,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isControlsVisible) 80.dp else 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Chạm 1 lần để ẩn/hiện công cụ. Vuốt kéo để đọc tài liệu",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Đã hiểu",
                            color = Color(0xFF818CF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showGuidance = false }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Component hiển thị trang PDF bằng PdfRenderer mặc định của Android.
 * Hỗ trợ Fit Width, Pinch-to-zoom, Pan di chuyển và Nhấp đúp phóng to.
 */
@Composable
fun PdfPageViewer(
    file: File,
    pageNumber: Int,
    isLandscape: Boolean = false,
    onSingleTap: () -> Unit = {},
    onPageCountLoaded: (Int) -> Unit,
    onRenderError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(file, pageNumber) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(file, pageNumber) {
        withContext(Dispatchers.IO) {
            try {
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)
                onPageCountLoaded(renderer.pageCount)

                val pageIndex = (pageNumber - 1).coerceIn(0, renderer.pageCount - 1)
                val page = renderer.openPage(pageIndex)

                // Render trang PDF với độ phân giải tốt hơn (tỷ lệ 2.5x)
                val width = (page.width * 2.5).toInt()
                val height = (page.height * 2.5).toInt()
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                renderer.close()
                pfd.close()

                bitmap = bmp
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onRenderError()
                }
            }
        }
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Tự động khôi phục tỷ lệ 1x khi chuyển trang
    LaunchedEffect(pageNumber) {
        scale = 1f
        offset = Offset.Zero
    }

    // Khôi phục tỷ lệ và góc di chuyển khi xoay màn hình để tránh lệch tọa độ hiển thị
    LaunchedEffect(isLandscape) {
        scale = 1f
        offset = Offset.Zero
    }

    val density = androidx.compose.ui.platform.LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val bitmapAspectRatio = bitmap?.let { it.width.toFloat() / it.height.toFloat() } ?: 1f
        val imageWidthPx = screenWidthPx
        val imageHeightPx = imageWidthPx / bitmapAspectRatio

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isLandscape) {
                    detectTapGestures(
                        onTap = {
                            if (isLandscape) {
                                onSingleTap()
                            }
                        },
                        onDoubleTap = { tapOffset ->
                            if (!isLandscape) {
                                if (scale > 1f) {
                                    scale = 1f
                                    offset = Offset.Zero
                                } else {
                                    scale = 2.5f
                                }
                            }
                        }
                    )
                }
                .pointerInput(isLandscape) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (!isLandscape) {
                            scale = (scale * zoom).coerceIn(1f, 5f)
                        } else {
                            scale = 1f
                        }

                        // Tính toán giới hạn kéo động chính xác theo kích thước thực tế sau phóng to so với màn hình
                        val maxXOffset = if (imageWidthPx * scale > screenWidthPx) {
                            (imageWidthPx * scale - screenWidthPx) / 2f
                        } else {
                            0f
                        }
                        val maxYOffset = if (imageHeightPx * scale > screenHeightPx) {
                            (imageHeightPx * scale - screenHeightPx) / 2f
                        } else {
                            0f
                        }

                        if (maxXOffset > 0f || maxYOffset > 0f) {
                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxXOffset, maxXOffset),
                                y = (offset.y + pan.y).coerceIn(-maxYOffset, maxYOffset)
                            )
                        } else {
                            offset = Offset.Zero
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "PDF Page $pageNumber",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(bitmapAspectRatio)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = ContentScale.FillWidth
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
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
