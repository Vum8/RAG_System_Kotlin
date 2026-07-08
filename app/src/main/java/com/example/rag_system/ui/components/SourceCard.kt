package com.example.rag_system.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.models.SourceCitationUiModel
import com.example.rag_system.ui.theme.*

/**
 * Card hiển thị tài liệu nguồn trích dẫn (SourceCard) theo chuẩn Component Modularization.
 */
@Composable
fun EduSourceCitationCard(
    citation: SourceCitationUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = BrandSurfaceContainerLow,
        border = BorderStroke(1.dp, BrandOutlineVariant.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Icon tài liệu
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = BrandSurface,
                modifier = Modifier.size(36.dp),
                border = BorderStroke(1.dp, BrandBorderSubtle)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Tài liệu",
                        tint = BrandPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = citation.sourceDocumentName,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrandTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Trang ${citation.pageNumber ?: "?"} • ${citation.chapterSection ?: ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandTextSecondary
                )
                Text(
                    text = citation.rawExtractedText,
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
