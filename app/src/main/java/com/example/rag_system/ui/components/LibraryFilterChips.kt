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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** Mapping hiển thị (label) → giá trị API (fileType param). */
data class LibraryFilter(val label: String, val apiValue: String?)

private val FILE_TYPE_FILTERS = listOf(
    LibraryFilter("Tất cả",  null),
    LibraryFilter("📄 PDF",  "PDF"),
    LibraryFilter("📃 TXT",  "TXT")
)

private val SORT_OPTIONS = listOf(
    LibraryFilter("Mới nhất",    "newest"),
    LibraryFilter("Cũ nhất",     "oldest"),
    LibraryFilter("Tên A→Z",     "title_asc"),
    LibraryFilter("Tên Z→A",     "title_desc")
)

/**
 * Thanh hàng ngang gộp chung Bộ lọc và Sắp xếp dùng DropdownMenu để gọn gàng.
 */
@Composable
fun LibraryFilterAndSortRow(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    selectedSort: String,
    onSortSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bộ lọc Loại file
        val currentFilterLabel = FILE_TYPE_FILTERS.find { it.apiValue == selectedFilter }?.label ?: "Tất cả"
        DropdownSelector(
            label = "Loại: $currentFilterLabel",
            options = FILE_TYPE_FILTERS,
            onOptionSelected = { onFilterSelected(it.apiValue) },
            modifier = Modifier.weight(1f)
        )

        // Bộ lọc Sắp xếp
        val currentSortLabel = SORT_OPTIONS.find { it.apiValue == selectedSort }?.label ?: "Mới nhất"
        DropdownSelector(
            label = "Sắp xếp: $currentSortLabel",
            options = SORT_OPTIONS,
            onOptionSelected = { it.apiValue?.let { v -> onSortSelected(v) } },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DropdownSelector(
    label: String,
    options: List<LibraryFilter>,
    onOptionSelected: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BrandSurface)
                .border(1.dp, BrandBorderSubtle, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandOnSurfaceVariant,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = BrandOnSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

