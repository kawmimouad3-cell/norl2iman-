package com.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

@Composable
fun rememberQcfFontFamily(fontName: String): FontFamily {
    val context = LocalContext.current
    val resourceName = remember(fontName) {
        if (fontName.equals("QCF4_QBSML", ignoreCase = true)) {
            "qcf4_qbsml"
        } else {
            "${fontName.lowercase()}_w"
        }
    }
    val resourceId = remember(resourceName, context.packageName) {
        context.resources.getIdentifier(resourceName, "font", context.packageName)
    }

    return remember(resourceId) {
        if (resourceId != 0) FontFamily(Font(resourceId)) else FontFamily.Serif
    }
}
