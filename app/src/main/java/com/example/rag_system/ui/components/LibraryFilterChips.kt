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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.*

/** Mapping hiển thị (label) → giá trị API (fileType param). */
data class LibraryFilter(val label: String, val apiValue: String?)

private val FILE_TYPE_FILTERS = listOf(
    LibraryFilter("Tất cả",  null),
    LibraryFilter("📄 PDF",  "PDF"),
    LibraryFilter("📝 DOCX", "DOCX"),
    LibraryFilter("📃 TXT",  "TXT")
)

private val SORT_OPTIONS = listOf(
    LibraryFilter("Mới nhất",    "newest"),
    LibraryFilter("Cũ nhất",     "oldest"),
    LibraryFilter("Tên A→Z",     "title_asc"),
    LibraryFilter("Tên Z→A",     "title_desc")
)

/**
 * Thanh hàng ngang chọn bộ lọc theo loại file tài liệu thư viện (Stateless UI).
 * [selectedFilter]: giá trị API hiện tại (null = Tất cả, "PDF", "DOCX", "TXT").
 * [onFilterSelected]: callback trả về giá trị API (null hoặc string).
 */
@Composable
fun LibraryFilterChips(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(FILE_TYPE_FILTERS) { filter ->
            val isSelected = filter.apiValue == selectedFilter
            FilterChipItem(
                label = filter.label,
                isSelected = isSelected,
                onClick = { onFilterSelected(filter.apiValue) }
            )
        }
    }
}

/**
 * Thanh hàng ngang chọn sắp xếp tài liệu thư viện (Stateless UI).
 * [selectedSort]: giá trị sort API hiện tại (mặc định "newest").
 * [onSortSelected]: callback trả về giá trị sort API.
 */
@Composable
fun LibrarySortChips(
    selectedSort: String,
    onSortSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(SORT_OPTIONS) { option ->
            val isSelected = option.apiValue == selectedSort
            FilterChipItem(
                label = option.label,
                isSelected = isSelected,
                onClick = { option.apiValue?.let { onSortSelected(it) } }
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) BrandPrimaryContainer else BrandSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) BrandPrimary.copy(alpha = 0.6f) else BrandBorderSubtle,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) BrandOnPrimary else BrandOnSurfaceVariant
        )
    }
}
