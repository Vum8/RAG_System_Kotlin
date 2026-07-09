package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.*

/**
 * Thanh hàng ngang chọn bộ lọc tài liệu thư viện (LibraryFilterChips) dưới dạng Stateless UI.
 */
@Composable
fun LibraryFilterChips(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf("Tất cả", "Chương 1", "Chương 2", "Chương 3", "Slide bài giảng", "Tài liệu đọc")

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) BrandPrimaryContainer else BrandSurface)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) BrandPrimaryContainer else BrandBorderSubtle,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) BrandOnPrimary else BrandOnSurfaceVariant
                )
            }
        }
    }
}
