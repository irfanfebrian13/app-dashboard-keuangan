package com.example.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object CatIcon {
    private var _filled: ImageVector? = null
    private var _outlined: ImageVector? = null

    val Filled: ImageVector
        get() {
            if (_filled != null) return _filled!!
            _filled = ImageVector.Builder(
                name = "CatFilled",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                // Background face shape (filled)
                path(
                    fill = SolidColor(Color.Black), // Icon tint will override this
                    stroke = null
                ) {
                    moveTo(12f, 5f)
                    curveTo(12.67f, 5f, 13.35f, 5.09f, 14f, 5.26f)
                    lineTo(18.5f, 2f)
                    lineTo(18f, 7.5f)
                    curveTo(19.23f, 8.84f, 20f, 10.62f, 20f, 12.58f)
                    curveTo(20f, 16.79f, 16.42f, 20.2f, 12f, 20.2f)
                    curveTo(7.58f, 20.2f, 4f, 16.79f, 4f, 12.58f)
                    curveTo(4f, 10.62f, 4.77f, 8.84f, 6f, 7.5f)
                    lineTo(5.5f, 2f)
                    lineTo(10f, 5.26f)
                    curveTo(10.65f, 5.09f, 11.33f, 5f, 12f, 5f)
                    close()
                }

                // Happy Eyes (drawn in white to contrast with the filled face)
                // Left Eye (Squinting Happy ^)
                path(
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(7.5f, 11.5f)
                    lineTo(9f, 10f)
                    lineTo(10.5f, 11.5f)
                }

                // Right Eye (Squinting Happy ^)
                path(
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(13.5f, 11.5f)
                    lineTo(15f, 10f)
                    lineTo(16.5f, 11.5f)
                }

                // Smiling Cute Kitty Mouth "w" shape in white
                path(
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(12f, 13f)
                    curveTo(11.6f, 13f, 11.2f, 13.3f, 11.2f, 13.8f)
                    curveTo(11.2f, 14.3f, 11.6f, 14.5f, 12f, 14f)
                    curveTo(12.4f, 14.5f, 12.8f, 14.3f, 12.8f, 13.8f)
                    curveTo(12.8f, 13.3f, 12.4f, 13f, 12f, 13f)
                }

                // Whiskers Left in white
                path(
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.2f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveTo(6f, 13.5f)
                    lineTo(3.5f, 13f)
                    moveTo(5.8f, 15f)
                    lineTo(3.8f, 15.5f)
                }

                // Whiskers Right in white
                path(
                    fill = null,
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 1.2f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveTo(18f, 13.5f)
                    lineTo(20.5f, 13f)
                    moveTo(18.2f, 15f)
                    lineTo(20.2f, 15.5f)
                }
            }.build()
            return _filled!!
        }

    val Outlined: ImageVector
        get() {
            if (_outlined != null) return _outlined!!
            _outlined = ImageVector.Builder(
                name = "CatOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                // Main head outline plus ears
                path(
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(12f, 5f)
                    curveTo(12.67f, 5f, 13.35f, 5.09f, 14f, 5.26f)
                    lineTo(18.5f, 2f)
                    lineTo(18f, 7.5f)
                    curveTo(19.23f, 8.84f, 20f, 10.62f, 20f, 12.58f)
                    curveTo(20f, 16.79f, 16.42f, 20.2f, 12f, 20.2f)
                    curveTo(7.58f, 20.2f, 4f, 16.79f, 4f, 12.58f)
                    curveTo(4f, 10.62f, 4.77f, 8.84f, 6f, 7.5f)
                    lineTo(5.5f, 2f)
                    lineTo(10f, 5.26f)
                    curveTo(10.65f, 5.09f, 11.33f, 5f, 12f, 5f)
                    close()
                }

                // Happy Eyes (drawn in outline stroke color)
                // Left Eye (Squinting Happy ^)
                path(
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(7.5f, 11.5f)
                    lineTo(9f, 10f)
                    lineTo(10.5f, 11.5f)
                }

                // Right Eye (Squinting Happy ^)
                path(
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(13.5f, 11.5f)
                    lineTo(15f, 10f)
                    lineTo(16.5f, 11.5f)
                }

                // Smiling Cute Kitty Mouth "w" shape
                path(
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(12f, 13f)
                    curveTo(11.6f, 13f, 11.2f, 13.3f, 11.2f, 13.8f)
                    curveTo(11.2f, 14.3f, 11.6f, 14.5f, 12f, 14f)
                    curveTo(12.4f, 14.5f, 12.8f, 14.3f, 12.8f, 13.8f)
                    curveTo(12.8f, 13.3f, 12.4f, 13f, 12f, 13f)
                }

                // Whiskers Left
                path(
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.2f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveTo(6f, 13.5f)
                    lineTo(3.5f, 13f)
                    moveTo(5.8f, 15f)
                    lineTo(3.8f, 15.5f)
                }

                // Whiskers Right
                path(
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.2f,
                    strokeLineCap = StrokeCap.Round
                ) {
                    moveTo(18f, 13.5f)
                    lineTo(20.5f, 13f)
                    moveTo(18.2f, 15f)
                    lineTo(20.2f, 15.5f)
                }
            }.build()
            return _outlined!!
        }
}
