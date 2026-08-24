package com.pegas.origami.paper.folding.art.ui.component.custom

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2
import kotlin.math.hypot

class InteractiveImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val drawMatrix = Matrix()
    private val savedMatrix = Matrix()
    private val startPoint = PointF()
    private val midPoint = PointF()
    private var mode = Mode.NONE
    private var startDistance = 0f
    private var startRotation = 0f
    private var isTransformResetPending = true

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                resetTransform()
                return true
            }
        }
    )

    init {
        scaleType = ScaleType.MATRIX
        isClickable = true
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isTransformResetPending) {
            post {
                fitImageToView()
                isTransformResetPending = false
            }
        }
    }

    fun resetTransformOnNextImage() {
        isTransformResetPending = true
    }

    fun resetTransform() {
        fitImageToView()
        isTransformResetPending = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(drawMatrix)
                startPoint.set(event.x, event.y)
                mode = Mode.DRAG
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                startDistance = spacing(event)
                if (startDistance > MIN_POINTER_DISTANCE) {
                    savedMatrix.set(drawMatrix)
                    midpoint(midPoint, event)
                    startRotation = rotation(event)
                    mode = Mode.ZOOM_ROTATE
                }
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                when (mode) {
                    Mode.DRAG -> {
                        drawMatrix.set(savedMatrix)
                        drawMatrix.postTranslate(event.x - startPoint.x, event.y - startPoint.y)
                    }

                    Mode.ZOOM_ROTATE -> {
                        if (event.pointerCount >= 2) {
                            val newDistance = spacing(event)
                            if (newDistance > MIN_POINTER_DISTANCE) {
                                drawMatrix.set(savedMatrix)
                                val scale = newDistance / startDistance
                                drawMatrix.postScale(scale, scale, midPoint.x, midPoint.y)
                                drawMatrix.postRotate(rotation(event) - startRotation, midPoint.x, midPoint.y)
                            }
                        }
                    }

                    Mode.NONE -> Unit
                }
                imageMatrix = drawMatrix
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                mode = Mode.NONE
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        return true
    }

    private fun fitImageToView() {
        val drawable = drawable ?: return
        val viewWidth = width - paddingLeft - paddingRight
        val viewHeight = height - paddingTop - paddingBottom
        if (viewWidth <= 0 || viewHeight <= 0 || drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            return
        }

        val scale = minOf(
            viewWidth.toFloat() / drawable.intrinsicWidth.toFloat(),
            viewHeight.toFloat() / drawable.intrinsicHeight.toFloat()
        )
        val dx = paddingLeft + (viewWidth - drawable.intrinsicWidth * scale) / 2f
        val dy = paddingTop + (viewHeight - drawable.intrinsicHeight * scale) / 2f

        drawMatrix.reset()
        drawMatrix.postScale(scale, scale)
        drawMatrix.postTranslate(dx, dy)
        imageMatrix = drawMatrix
    }

    private fun spacing(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return hypot(x, y)
    }

    private fun midpoint(point: PointF, event: MotionEvent) {
        if (event.pointerCount < 2) return
        point.set(
            (event.getX(0) + event.getX(1)) / 2f,
            (event.getY(0) + event.getY(1)) / 2f
        )
    }

    private fun rotation(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val deltaX = event.getX(0) - event.getX(1)
        val deltaY = event.getY(0) - event.getY(1)
        return Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
    }

    private enum class Mode {
        NONE,
        DRAG,
        ZOOM_ROTATE
    }

    companion object {
        private const val MIN_POINTER_DISTANCE = 10f
    }
}
