package com.esfandune.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import notisync.composeapp.generated.resources.Res
import notisync.composeapp.generated.resources.vazir_regular
import org.jetbrains.compose.resources.Font

@Composable
fun getTypography(): Typography {
val Vazirmatn = FontFamily(
    Font(Res.font.vazir_regular, FontWeight.Normal, FontStyle.Normal),
//    Font(Res.font.shabnammedium, FontWeight.W500, FontStyle.Normal),
//        Font(Res.font.shabnambold, FontWeight.W700, FontStyle.Normal),
)
val baseline = Typography()
return Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = Vazirmatn),
    displayMedium = baseline.displayMedium.copy(fontFamily = Vazirmatn),
    displaySmall = baseline.displaySmall.copy(fontFamily = Vazirmatn),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = Vazirmatn),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = Vazirmatn),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = Vazirmatn),
    titleLarge = baseline.titleLarge.copy(fontFamily = Vazirmatn),
    titleMedium = baseline.titleMedium.copy(fontFamily = Vazirmatn),
    titleSmall = baseline.titleSmall.copy(fontFamily = Vazirmatn),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = Vazirmatn),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = Vazirmatn),
    bodySmall = baseline.bodySmall.copy(fontFamily = Vazirmatn),
    labelLarge = baseline.labelLarge.copy(fontFamily = Vazirmatn),
    labelMedium = baseline.labelMedium.copy(fontFamily = Vazirmatn),
    labelSmall = baseline.labelSmall.copy(fontFamily = Vazirmatn),
)

}