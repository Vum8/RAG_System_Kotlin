package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Thanh điều khiển chân trang lật trang đọc tài liệu (DocumentReaderControls).
 * Hỗ trợ chuyển chế độ gõ số trang khi ấn vào chỉ số trang hoặc icon bút viết.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentReaderControls(
    currentPage: Int,
    totalPages: Int,
    isBookmarked: Boolean,
    onPageChanged: (Int) -> Unit,
    onBookmarkToggled: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Lưu tạm vị trí slider
    var sliderValue by rememberSaveable(currentPage) { mutableStateOf(currentPage.toFloat()) }
    var isEditingPage by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrandSurface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Thanh Slider kéo chọn trang nhanh
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
            },
            onValueChangeFinished = {
                onPageChanged(sliderValue.toInt())
            },
            valueRange = 1f..totalPages.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = BrandPrimary,
                activeTrackColor = BrandPrimary,
                inactiveTrackColor = BrandBorderSubtle
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Hàng nút lật trang và lưu dấu trang
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút TRƯỚC
            TextButton(
                onClick = { if (currentPage > 1) onPageChanged(currentPage - 1) },
                enabled = currentPage > 1
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Trước",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Chế độ gõ nhập số trang hoặc xem thông thường
            if (isEditingPage) {
                var inputPageText by remember { mutableStateOf(currentPage.toString()) }
                OutlinedTextField(
                    value = inputPageText,
                    onValueChange = { inputPageText = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        val pageVal = inputPageText.toIntOrNull()
                        if (pageVal != null && pageVal in 1..totalPages) {
                            onPageChanged(pageVal)
                        }
                        isEditingPage = false
                    }),
                    modifier = Modifier.width(70.dp).height(48.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BrandBorderSubtle,
                        focusedContainerColor = BrandSurface,
                        unfocusedContainerColor = BrandSurface
                    )
                )
            } else {
                Row(
                    modifier = Modifier
                        .clickable { isEditingPage = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$currentPage / $totalPages",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrandTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📝", fontSize = 14.sp)
                }
            }

            // Nút SAU và BOOKMARK
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = { if (currentPage < totalPages) onPageChanged(currentPage + 1) },
                    enabled = currentPage < totalPages
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sau",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Nút lưu dấu trang
                IconButton(
                    onClick = onBookmarkToggled,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text(
                        text = if (isBookmarked) "🔖" else "🏳️",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
