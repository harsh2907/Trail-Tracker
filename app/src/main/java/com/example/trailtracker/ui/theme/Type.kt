package com.example.trailtracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.trailtracker.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val Roboto = FontFamily(
    listOf(
        Font(resId = R.font.roboto_light, weight = FontWeight.Light),
        Font(resId = R.font.roboto_thin, weight = FontWeight.Thin),
        Font(resId = R.font.roboto_regular, weight = FontWeight.Normal),
        Font(resId = R.font.roboto_medium, weight = FontWeight.Medium),
        Font(resId = R.font.roboto_bold, weight = FontWeight.Bold),
        Font(resId = R.font.roboto_black, weight = FontWeight.Black)
    )
)

val Poppins = FontFamily(
    listOf(
        Font(resId = R.font.poppins_light, weight = FontWeight.Light),
        Font(resId = R.font.poppins_thin, weight = FontWeight.Thin),
        Font(resId = R.font.poppins_regular, weight = FontWeight.Normal),
        Font(resId = R.font.poppins_medium, weight = FontWeight.Medium),
        Font(resId = R.font.poppins_black, weight = FontWeight.Black),
        Font(resId = R.font.poppins_extra_bold, weight = FontWeight.ExtraBold),
        Font(resId = R.font.poppins_extra_light, weight = FontWeight.ExtraLight),
        Font(resId = R.font.poppins_semi_bold, weight = FontWeight.SemiBold)
    )
)

// Utility function to copy a TextStyle with the Poppins font family
fun TextStyle.withPoppins() = this.copy(fontFamily = Poppins)

// Base typography from Material 3
val baseTypography = Typography()

// Apply Poppins font family to each text style
val PoppinsTypography = Typography(
    displayLarge = baseTypography.displayLarge.withPoppins(),
    displayMedium = baseTypography.displayMedium.withPoppins(),
    displaySmall = baseTypography.displaySmall.withPoppins(),
    headlineLarge = baseTypography.headlineLarge.withPoppins(),
    headlineMedium = baseTypography.headlineMedium.withPoppins(),
    headlineSmall = baseTypography.headlineSmall.withPoppins(),
    titleLarge = baseTypography.titleLarge.withPoppins(),
    titleMedium = baseTypography.titleMedium.withPoppins(),
    titleSmall = baseTypography.titleSmall.withPoppins(),
    bodyLarge = baseTypography.bodyLarge.withPoppins(),
    bodyMedium = baseTypography.bodyMedium.withPoppins(),
    bodySmall = baseTypography.bodySmall.withPoppins(),
    labelLarge = baseTypography.labelLarge.withPoppins(),
    labelMedium = baseTypography.labelMedium.withPoppins(),
    labelSmall = baseTypography.labelSmall.withPoppins(),
)
