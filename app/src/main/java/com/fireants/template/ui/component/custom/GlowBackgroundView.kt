package com.fireants.template.ui.component.custom

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

    var baseBgColor: Int = "#F6F5FA".toColorInt()
        set(value) {
            field = value
            invalidate()
        }

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
        val red = Color.red(glowColor)
        val green = Color.green(glowColor)
        val blue = Color.blue(glowColor)

        val softGlowColor = Color.argb(100, red, green, blue)
        val faintGlowColor = Color.argb(25, red, green, blue)

        paint.shader = RadialGradient(
            width / 2f,
            height * 0.08f,
            radius,
            intArrayOf(softGlowColor, faintGlowColor, Color.TRANSPARENT),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(baseBgColor)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        super.onDraw(canvas)
    }
}