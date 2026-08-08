package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FileFormatIcon(
    format: String, // "PDF", "WORD", "SLIDE", "TXT"
    modifier: Modifier = Modifier
) {
    when (format) {
        "PDF" -> PdfAppIcon(modifier)
        "WORD" -> WordAppIcon(modifier)
        "SLIDE" -> SlideAppIcon(modifier)
        else -> TxtAppIcon(modifier)
    }
}

@Composable
fun PdfAppIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD32F2F)), // Đỏ đậm của Adobe PDF
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Biểu tượng giả lập giấy
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 4.dp)
                        .background(Color(0xFFD32F2F).copy(alpha = 0.5f))
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "PDF",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun WordAppIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2B579A)), // Xanh dương đậm của Microsoft Word
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Phần chữ W
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth(0.55f)
                    .padding(start = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E88E5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "W",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
            
            // Phần dòng kẻ mô phỏng văn bản
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 6.dp, start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.height(3.dp).fillMaxWidth().background(Color.White.copy(alpha = 0.8f)))
                Box(modifier = Modifier.height(3.dp).fillMaxWidth(0.8f).background(Color.White.copy(alpha = 0.8f)))
                Box(modifier = Modifier.height(3.dp).fillMaxWidth().background(Color.White.copy(alpha = 0.8f)))
                Box(modifier = Modifier.height(3.dp).fillMaxWidth(0.6f).background(Color.White.copy(alpha = 0.8f)))
            }
        }
    }
}

@Composable
fun SlideAppIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD24726)), // Đỏ cam của PowerPoint
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Phần chữ P
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth(0.55f)
                    .padding(start = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF44336)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
            
            // Phần chart mô phỏng slide
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 6.dp, start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.height(8.dp).fillMaxWidth().background(Color.White.copy(alpha = 0.8f)))
                Box(modifier = Modifier.height(4.dp).fillMaxWidth(0.8f).background(Color.White.copy(alpha = 0.8f)))
            }
        }
    }
}

@Composable
fun TxtAppIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF757575)), // Xám cho Text
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TXT",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.height(2.dp).width(20.dp).background(Color.White.copy(alpha = 0.6f)))
            Spacer(modifier = Modifier.height(2.dp))
            Box(modifier = Modifier.height(2.dp).width(12.dp).background(Color.White.copy(alpha = 0.6f)))
        }
    }
}
