package com.russvkm.kidsdrawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingStuff(context: Context, attrs: AttributeSet): View(context,attrs) {
    private var mDrawPath:CustomPath?=null
    private var mCanvasBitmap:Bitmap?=null
    private var mDrawPaint: Paint?=null
    private var mCanvasPaint:Paint?=null
    private var brushSize:Float=0.toFloat()
    private var color= Color.BLACK
    private var canvas: Canvas?=null
    private val path=ArrayList<CustomPath>()
    private val mUndoPath=ArrayList<CustomPath>()
    init {
        setUpDrawing()
    }

    private fun setUpDrawing(){
        mDrawPaint= Paint()
        mDrawPath= CustomPath(color,brushSize)
        mDrawPaint!!.color=color
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint= Paint(Paint.DITHER_FLAG)
        brushSize=20.toFloat()
    }

    fun undoDraw(){
        if(path.size>0){
            mUndoPath.add(path.removeAt(path.size-1))
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas= Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mCanvasBitmap!!,0f,0f,mDrawPaint)
        for (mPath in path){
            mDrawPaint!!.strokeWidth=mPath.brushThickness
            mDrawPaint!!.color=mPath.color
            canvas?.drawPath(mPath,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
            mDrawPaint!!.color=mDrawPath!!.color
            canvas?.drawPath(mDrawPath!!,mDrawPaint!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
       val touchX = event?.x
       val touchY = event?.y

       when (event?.action) {
           MotionEvent.ACTION_DOWN -> {
               mDrawPath!!.color = color
               mDrawPath!!.brushThickness = brushSize
               mDrawPath!!.reset()
               mDrawPath!!.moveTo(touchX!!, touchY!!)
           }
           MotionEvent.ACTION_MOVE -> {
               mDrawPath!!.lineTo(touchX!!, touchY!!)
           }
           MotionEvent.ACTION_UP -> {
               path.add(mDrawPath!!)
               mDrawPath = CustomPath(color, brushSize)
               mDrawPaint!!.strokeWidth=brushSize
           }
           else ->
               return false
       }
       invalidate()
       return true
    }

    fun setBrushSize(newSize:Float){
        brushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize,resources.displayMetrics)
    }

    fun setColor(newColor:String){
        color=Color.parseColor(newColor)
        mDrawPaint!!.color=color
    }

    private class CustomPath(var color:Int,var brushThickness:Float): Path() {

    }

}