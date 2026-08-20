package com.fireants.template.ui.component.custom // Thay bằng package của bạn

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt

class GlowBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val baseBgColor = "#F6F5FA".toColorInt()

    var glowColor: Int = "#FFC2A2".toColorInt()
        set(value) {
            field = value
            updateShader()
            invalidate()
        }

    init {
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateShader()
    }

    private fun updateShader() {
        if (width <= 0 || height <= 0) return

        val radius = width * 0.65f
        val softGlowColor = Color.argb(
            100,
            Color.red(glowColor),
            Color.green(glowColor),
            Color.blue(glowColor)
        )

        paint.shader = RadialGradient(
            width / 2f,
            height * 0.08f,
            radius,
            intArrayOf(
                softGlowColor,
                Color.argb(
                    25,
                    Color.red(glowColor),
                    Color.green(glowColor),
                    Color.blue(glowColor)
                ),
                Color.TRANSPARENT
            ),
            floatArrayOf(
                0f,
                0.55f,
                1f
            ),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(baseBgColor)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        super.onDraw(canvas)
    }
}