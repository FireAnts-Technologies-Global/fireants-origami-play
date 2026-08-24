package com.fireants.template.ui.component.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import java.util.Stack
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot


class FoldPaperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class FoldGesture(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float
    )

    data class AutoFoldStep(
        val startXRatio: Float,
        val startYRatio: Float,
        val endXRatio: Float,
        val endYRatio: Float
    )

    // Callbacks
    var onLevelCompleted: ((Int) -> Unit)? = null
    var onReplay: (() -> Unit)? = null
    var onLevelWinAction: ((() -> Unit) -> Unit)? = null
    var onFoldHistoryChanged: ((Int) -> Unit)? = null

    var isManualFolded: Boolean = false
        private set

    private var currentLevelTarget: FloatArray? = null

    private var suggestAnimator: ValueAnimator? = null
    private var paperBitmap: Bitmap? = null

    // Paints
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paperOuterEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(85, 0, 0, 0)
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeJoin = Paint.Join.ROUND
    }
    private val paperInnerEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(190, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
        strokeJoin = Paint.Join.ROUND
    }
    private val paperBackShadePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(14, 0, 0, 0)
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }
    private val paperBackDesaturatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0.85f)
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#444444") // Dark gray
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
    }
    private val suggestPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFEB3B")
        style = Paint.Style.STROKE
        strokeWidth = 8f
        pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
        strokeCap = Paint.Cap.ROUND
    }
    private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val EDGE_TOUCH_THRESHOLD = 80f
    private val THREE_STAR_THRESHOLD = 0.05f
    private val TWO_STAR_THRESHOLD = 0.08f
    private val ONE_STAR_THRESHOLD = 0.12f
    private val SCORE_SAMPLE_STEP = 4

    private var currentPaperPath = Path()
    private val tempFrontPath = Path()
    private val targetPath = Path()

    private val historyBitmaps = Stack<Bitmap>()
    private val foldHistory = Stack<FoldGesture>()

    private var currentBackgroundBitmap: Bitmap? = null

    // Touch processing
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var touchX = 0f
    private var touchY = 0f
    private var maxDragDistance = 0f
    private var isDragging = false

    private var isAnimatingComplete = false
    private var foldAnimator: ValueAnimator? = null
    
    private var isShowSuggest = false
    private var suggestAlpha = 0f
    private val originalPaperRect = RectF()
    private val paperBounds = RectF()
    private val targetBounds = RectF()
    private val foldClipPath = Path()
    private val newPaperPath = Path()
    private var paperScoreBitmap: Bitmap? = null
    private var targetScoreSamples: BooleanArray? = null
    private var targetScorePixels = 0
    private var targetScoreWidth = 0
    private var targetScoreHeight = 0

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        // Set a default white bitmap so it doesn't crash if no paper texture is provided
        val defaultBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        defaultBitmap.eraseColor(Color.WHITE)
        paperBitmap = defaultBitmap
    }

    fun setPaperBitmap(bitmap: Bitmap) {
        this.paperBitmap = bitmap
        if (width <= 0 || height <= 0) {
            requestRender()
            return
        }
        if (foldHistory.isNotEmpty()) {
            reconstructFromHistory()
            requestRender()
            return
        }
        
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawPaperTexture(Canvas(newBitmap), currentPaperPath)
        
        if (historyBitmaps.isNotEmpty()) {
            val oldBitmap = historyBitmaps.pop()
            if (oldBitmap != newBitmap && !oldBitmap.isRecycled) {
                oldBitmap.recycle()
            }
            historyBitmaps.push(newBitmap)
            currentBackgroundBitmap = newBitmap
        }
        requestRender()
    }

    private fun setTargetPolygon(points: FloatArray) {
        if (points.size >= 6 && points.size % 2 == 0) {
            targetPath.reset()
            targetPath.moveTo(points[0], points[1])
            for (i in 2 until points.size step 2) {
                targetPath.lineTo(points[i], points[i + 1])
            }
            targetPath.close()
            clearTargetScoreCache()
        }
    }

    fun setLevelTarget(targetPoints: FloatArray) {
        currentLevelTarget = targetPoints
        post {
            val w = width.toFloat()
            val h = height.toFloat()
            if (w == 0f || h == 0f) return@post

            val minDimension = w.coerceAtMost(h) * 0.8f
            val offsetX = (w - minDimension) / 2f
            val offsetY = (h - minDimension) / 2f

            val scaledPoints = FloatArray(targetPoints.size)
            for (i in targetPoints.indices step 2) {
                scaledPoints[i] = (targetPoints[i] * minDimension) + offsetX
                scaledPoints[i + 1] = (targetPoints[i + 1] * minDimension) + offsetY
            }
            
            setTargetPolygon(scaledPoints)
            targetPaint.color = Color.parseColor("#444444")
            requestRender()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetPaperToFullSize()
    }

    fun resetPaperToFullSize() {
        foldAnimator?.cancel()
        foldAnimator = null
        isManualFolded = false

        val w = width.toFloat()
        val h = height.toFloat()
        if (w == 0f || h == 0f) return

        if (paperScoreBitmap?.width != width || paperScoreBitmap?.height != height) {
            paperScoreBitmap?.recycle()
            paperScoreBitmap = null
        }
        clearTargetScoreCache()
        recycleHistoryBitmaps()
        currentBackgroundBitmap = null
        currentPaperPath.reset()

        val minDimension = Math.min(w, h) * 0.8f
        val offsetX = (w - minDimension) / 2f
        val offsetY = (h - minDimension) / 2f
        val right = offsetX + minDimension
        val bottom = offsetY + minDimension

        currentPaperPath.addRect(offsetX, offsetY, right, bottom, Path.Direction.CW)
        originalPaperRect.set(offsetX, offsetY, right, bottom)

        currentLevelTarget?.let { setLevelTarget(it) }

        val initialBitmap = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)
        drawPaperTexture(Canvas(initialBitmap), currentPaperPath)
        
        foldHistory.clear()
        historyBitmaps.push(initialBitmap)
        currentBackgroundBitmap = initialBitmap
        onFoldHistoryChanged?.invoke(foldHistory.size)
        
        resetTouchData()
        requestRender()
    }

    private fun resetTouchData() {
        touchX = 0f
        touchY = 0f
        touchStartX = 0f
        touchStartY = 0f
    }

    private fun requestRender() {
        postInvalidateOnAnimation()
    }

    private fun recycleHistoryBitmaps() {
        for (bitmap in historyBitmaps) {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
        historyBitmaps.clear()
    }

    private fun clearTargetScoreCache() {
        targetScoreSamples = null
        targetScorePixels = 0
        targetScoreWidth = 0
        targetScoreHeight = 0
    }

    private fun getPaperScoreBitmap(w: Int, h: Int): Bitmap {
        val reusableBitmap = paperScoreBitmap
        if (reusableBitmap != null && reusableBitmap.width == w && reusableBitmap.height == h && !reusableBitmap.isRecycled) {
            reusableBitmap.eraseColor(Color.TRANSPARENT)
            return reusableBitmap
        }

        paperScoreBitmap?.recycle()
        return Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8).also {
            paperScoreBitmap = it
        }
    }

    private fun ensureTargetScoreSamples(w: Int, h: Int): BooleanArray {
        val cachedSamples = targetScoreSamples
        if (cachedSamples != null && targetScoreWidth == w && targetScoreHeight == h) {
            return cachedSamples
        }

        val targetBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        Canvas(targetBitmap).drawPath(targetPath, scorePaint)

        val sampleColumns = (w + SCORE_SAMPLE_STEP - 1) / SCORE_SAMPLE_STEP
        val sampleRows = (h + SCORE_SAMPLE_STEP - 1) / SCORE_SAMPLE_STEP
        val samples = BooleanArray(sampleColumns * sampleRows)
        var targetPixels = 0
        var index = 0

        for (x in 0 until w step SCORE_SAMPLE_STEP) {
            for (y in 0 until h step SCORE_SAMPLE_STEP) {
                val isTarget = Color.alpha(targetBitmap.getPixel(x, y)) > 0
                samples[index++] = isTarget
                if (isTarget) {
                    targetPixels++
                }
            }
        }

        targetBitmap.recycle()
        targetScoreSamples = samples
        targetScorePixels = targetPixels
        targetScoreWidth = w
        targetScoreHeight = h
        return samples
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFoldedPaper(canvas)
        
        if (!targetPath.isEmpty) {
            canvas.drawPath(targetPath, targetPaint)
        }
        
        if (isShowSuggest && currentLevelTarget != null) {
            drawSuggest(canvas)
        }
    }

    private fun drawSuggest(canvas: Canvas) {
        paperBounds.setEmpty()
        currentPaperPath.computeBounds(paperBounds, true)
        targetBounds.setEmpty()
        targetPath.computeBounds(targetBounds, true)
        
        suggestPaint.alpha = (suggestAlpha * 255).toInt()
        canvas.drawLine(paperBounds.left, paperBounds.top, targetBounds.centerX(), targetBounds.centerY(), suggestPaint)
        canvas.drawCircle(paperBounds.left, paperBounds.top, suggestAlpha * 30f, suggestPaint)
    }

    private fun drawPaperTexture(canvas: Canvas, path: Path) {
        paperBitmap?.let { bitmap ->
            canvas.save()
            canvas.clipPath(path)
            canvas.drawBitmap(bitmap, null, originalPaperRect, paint)
            canvas.restore()
            
            canvas.drawPath(path, paperOuterEdgePaint)
            canvas.drawPath(path, paperInnerEdgePaint)
        }
    }

    private fun drawFoldedPaper(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w == 0f || h == 0f) return

        if (!isDragging && !isAnimatingComplete && touchX == touchStartX && touchY == touchStartY) {
            val bg = currentBackgroundBitmap
            if (bg == null) {
                drawPaperTexture(canvas, currentPaperPath)
            } else {
                canvas.drawBitmap(bg, 0f, 0f, paint)
            }
            return
        }

        val dx = touchX - touchStartX
        val dy = touchY - touchStartY
        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()

        if (dist < 5f) {
            val bg = currentBackgroundBitmap
            if (bg == null) {
                drawPaperTexture(canvas, currentPaperPath)
            } else {
                canvas.drawBitmap(bg, 0f, 0f, paint)
            }
            return
        }

        val midX = (touchStartX + touchX) / 2f
        val midY = (touchStartY + touchY) / 2f
        val maxLen = hypot(w.toDouble(), h.toDouble()).toFloat() * 2f

        val nx = (-dy / dist) * maxLen
        val ny = (dx / dist) * maxLen

        val lineStartX = midX + nx
        val lineStartY = midY + ny
        val lineEndX = midX - nx
        val lineEndY = midY - ny

        tempFrontPath.reset()
        tempFrontPath.moveTo(lineStartX, lineStartY)
        tempFrontPath.lineTo(lineEndX, lineEndY)
        val extY = (dy / dist) * maxLen
        tempFrontPath.lineTo(lineEndX - ny, lineEndY - extY)
        tempFrontPath.lineTo(lineStartX - ny, lineStartY - extY)
        tempFrontPath.close()

        canvas.save()
        canvas.clipPath(tempFrontPath, Region.Op.DIFFERENCE)
        currentBackgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, paint)
        } ?: drawPaperTexture(canvas, currentPaperPath)
        canvas.restore()

        canvas.save()
        canvas.translate(midX, midY)
        val degrees = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        canvas.rotate(degrees)
        canvas.scale(-1f, 1f)
        canvas.rotate(-degrees)
        canvas.translate(-midX, -midY)
        
        canvas.clipPath(tempFrontPath)
        
        val saveLayer = canvas.saveLayer(-maxLen, -maxLen, maxLen * 2f, maxLen * 2f, null)
        currentBackgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, paperBackDesaturatePaint)
        } ?: drawPaperTexture(canvas, currentPaperPath)
        
        canvas.drawRect(-maxLen, -maxLen, maxLen * 2f, maxLen * 2f, paperBackShadePaint)
        canvas.restoreToCount(saveLayer)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            isShowSuggest = false
            maxDragDistance = 0f
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (foldAnimator?.isRunning == true) return false
                if (touchX != 0f && !isDragging && !isAnimatingComplete) return false

                val ex = event.x
                val ey = event.y

                val isNearLeft = abs(ex - originalPaperRect.left) < EDGE_TOUCH_THRESHOLD
                val isNearRight = abs(ex - originalPaperRect.right) < EDGE_TOUCH_THRESHOLD
                val isNearTop = abs(ey - originalPaperRect.top) < EDGE_TOUCH_THRESHOLD
                val isNearBottom = abs(ey - originalPaperRect.bottom) < EDGE_TOUCH_THRESHOLD

                if (isNearLeft || isNearRight || isNearTop || isNearBottom) {
                    val snapped = snapToEdgeIfClose(ex, ey)
                    touchStartX = snapped.first
                    touchStartY = snapped.second
                } else {
                    touchStartX = ex
                    touchStartY = ey
                }

                touchX = touchStartX
                touchY = touchStartY
                maxDragDistance = 0f
                isDragging = true
                requestRender()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging || touchStartX != 0f) {
                    isDragging = true
                    val coerced = coerceTouchToPaperBounds(event.x, event.y)
                    if (abs(coerced.first - touchX) < 0.5f && abs(coerced.second - touchY) < 0.5f) {
                        return true
                    }
                    touchX = coerced.first
                    touchY = coerced.second
                    val dist = hypot((touchX - touchStartX).toDouble(), (touchY - touchStartY).toDouble()).toFloat()
                    if (dist > maxDragDistance) {
                        maxDragDistance = dist
                    }
                    requestRender()
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                val dist = hypot((touchX - touchStartX).toDouble(), (touchY - touchStartY).toDouble()).toFloat()
                if (dist > 10f) {
                    isManualFolded = true
                    calculateStarsAndCheckWin()
                    completeFold()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun snapToEdgeIfClose(x: Float, y: Float): Pair<Float, Float> {
        val dLeft = abs(x - originalPaperRect.left)
        val dRight = abs(x - originalPaperRect.right)
        val dTop = abs(y - originalPaperRect.top)
        val dBottom = abs(y - originalPaperRect.bottom)
        
        val minD = minOf(dLeft, dRight, dTop, dBottom)
        
        return when (minD) {
            dLeft -> Pair(originalPaperRect.left, y)
            dRight -> Pair(originalPaperRect.right, y)
            dTop -> Pair(x, originalPaperRect.top)
            else -> Pair(x, originalPaperRect.bottom)
        }
    }

    private fun coerceTouchToPaperBounds(x: Float, y: Float): Pair<Float, Float> {
        val coercedX = x.coerceIn(originalPaperRect.left, originalPaperRect.right)
        val coercedY = y.coerceIn(originalPaperRect.top, originalPaperRect.bottom)
        return Pair(coercedX, coercedY)
    }

    private fun completeFold() {
        isAnimatingComplete = false
        isDragging = false
        foldAnimator = null

        val dx = touchX - touchStartX
        val dy = touchY - touchStartY
        if (hypot(dx.toDouble(), dy.toDouble()).toFloat() <= 10f) {
            resetTouchData()
            requestRender()
            return
        }

        foldHistory.push(FoldGesture(touchStartX, touchStartY, touchX, touchY))
        saveStepAndNext(false)
        applyFoldClip(dx, dy)
        onFoldHistoryChanged?.invoke(foldHistory.size)
        resetTouchData()
        requestRender()
    }

    private fun applyFoldClip(dx: Float, dy: Float) {
        val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        if (dist < 1f) return

        val midX = (touchStartX + touchX) / 2f
        val midY = (touchStartY + touchY) / 2f
        val nx = -dy / dist
        val ny = dx / dist

        val maxLen = hypot(width.toDouble(), height.toDouble()).toFloat() * 3f

        val extX = nx * maxLen
        val extY = ny * maxLen

        val lineStartX = midX + extX
        val lineStartY = midY + extY
        val lineEndX = midX - extX
        val lineEndY = midY - extY

        foldClipPath.reset()
        foldClipPath.moveTo(lineStartX, lineStartY)
        foldClipPath.lineTo(lineEndX, lineEndY)
        val py = (dy / dist) * maxLen
        foldClipPath.lineTo(lineEndX + extY, lineEndY + py) // Math needs to match original logic precisely
        foldClipPath.lineTo(lineStartX + extY, lineStartY + py)
        foldClipPath.close()

        newPaperPath.reset()
        newPaperPath.op(currentPaperPath, foldClipPath, Path.Op.INTERSECT)
        currentPaperPath.set(newPaperPath)
    }

    private fun saveStepAndNext(resetTouch: Boolean = true) {
        if (touchX == 0f && touchY == 0f) return
        val w = width
        val h = height
        if (w <= 0 || h <= 0) return

        val newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawFoldedPaper(Canvas(newBitmap))
        
        historyBitmaps.push(newBitmap)
        currentBackgroundBitmap = newBitmap
        
        if (resetTouch) {
            resetTouchData()
        }
        requestRender()
    }

    private fun calculateStarsAndCheckWin() {
        val w = width
        val h = height
        if (w <= 0 || h <= 0) return

        val targetSamples = ensureTargetScoreSamples(w, h)
        val paperBitmap = getPaperScoreBitmap(w, h)
        drawFoldedPaper(Canvas(paperBitmap))

        var differingPixels = 0
        var index = 0

        for (x in 0 until w step SCORE_SAMPLE_STEP) {
            for (y in 0 until h step SCORE_SAMPLE_STEP) {
                val tPix = if (targetSamples[index++]) 1 else 0
                val pPix = if (Color.alpha(paperBitmap.getPixel(x, y)) > 0) 1 else 0
                
                if ((tPix == 0 || pPix == 0) && tPix != pPix) {
                    differingPixels++
                }
            }
        }

        if (targetScorePixels == 0) return

        val differenceRatio = differingPixels.toFloat() / targetScorePixels.toFloat()
        val stars = when {
            differenceRatio <= THREE_STAR_THRESHOLD -> 3
            differenceRatio <= TWO_STAR_THRESHOLD -> 2
            differenceRatio <= ONE_STAR_THRESHOLD -> 1
            else -> 0
        }

        if (stars > 0) {
            targetPaint.color = Color.parseColor("#00FF00") // Green for success
            onLevelCompleted?.invoke(stars)
            // Original code showed Victory dialog here. 
            // We delegate this to the callback to make the view standalone.
        } else {
            targetPaint.color = Color.parseColor("#444444")
        }
        requestRender()
    }

    fun undoLastStep() {
        foldAnimator?.cancel()
        foldAnimator = null
        isAnimatingComplete = false
        isDragging = false

        if (foldHistory.isNotEmpty()) {
            foldHistory.pop()
            reconstructFromHistory()
        }
        onFoldHistoryChanged?.invoke(foldHistory.size)
        resetTouchData()
        requestRender()
    }

    private fun reconstructFromHistory() {
        if (width <= 0 || height <= 0 || originalPaperRect.isEmpty) return

        for (bitmap in historyBitmaps) {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
        historyBitmaps.clear()
        currentBackgroundBitmap = null
        currentPaperPath.reset()
        currentPaperPath.addRect(originalPaperRect, Path.Direction.CW)

        val initialBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawPaperTexture(Canvas(initialBitmap), currentPaperPath)
        historyBitmaps.push(initialBitmap)
        currentBackgroundBitmap = initialBitmap

        for (gesture in foldHistory) {
            touchStartX = gesture.startX
            touchStartY = gesture.startY
            touchX = gesture.endX
            touchY = gesture.endY

            val foldedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            drawFoldedPaper(Canvas(foldedBitmap))
            historyBitmaps.push(foldedBitmap)
            currentBackgroundBitmap = foldedBitmap

            applyFoldClip(gesture.endX - gesture.startX, gesture.endY - gesture.startY)
        }
        resetTouchData()
    }

    fun showSuggest() {
        suggestAnimator?.cancel()
        isShowSuggest = true
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000L
        animator.repeatCount = 2
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener {
            suggestAlpha = it.animatedValue as Float
            requestRender()
        }
        animator.start()
        suggestAnimator = animator
    }

    fun startAutoFold(step: AutoFoldStep) {
        foldAnimator?.cancel()
        if (foldAnimator?.isRunning == true) return

        val fw = originalPaperRect.width()
        val fh = originalPaperRect.height()
        val left = originalPaperRect.left
        val top = originalPaperRect.top

        val sX = left + (step.startXRatio * fw)
        val sY = top + (step.startYRatio * fh)
        val eX = left + (step.endXRatio * fw)
        val eY = top + (step.endYRatio * fh)

        touchStartX = sX
        touchStartY = sY

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1500L
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            val progress = it.animatedValue as Float
            val currentX = sX + (eX - sX) * progress
            val currentY = sY + (eY - sY) * progress
            
            val coerced = coerceTouchToPaperBounds(currentX, currentY)
            touchX = coerced.first
            touchY = coerced.second
            requestRender()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                calculateStarsAndCheckWin()
                completeFold()
            }
        })
        animator.start()
        foldAnimator = animator
    }
}
