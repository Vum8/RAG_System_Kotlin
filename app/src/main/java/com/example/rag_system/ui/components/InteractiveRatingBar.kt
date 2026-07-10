package com.example.rag_system.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Thanh đánh giá sao tương tác (InteractiveRatingBar).
 * Hiển thị 5 ngôi sao, hỗ trợ click chọn số sao động từ 1 đến 5.
 */
@Composable
fun InteractiveRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isSelected = i <= rating
            Text(
                text = "★",
                fontSize = 32.sp,
                color = if (isSelected) Color(0xFFF5A623) else Color(0xFFCBD5E1), // Vàng cam nếu được chọn, xám nếu chưa chọn
                modifier = Modifier
                    .clickable { onRatingChanged(i) }
                    .padding(4.dp)
            )
        }
    }
}
