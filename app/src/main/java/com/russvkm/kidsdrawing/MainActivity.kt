package com.russvkm.kidsdrawing

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.color_dialog.*
import kotlinx.android.synthetic.main.stroke_width.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private var alphaSeekBarValue="FF"
    private var redSeekBarValue="00"
    private var greenSeekBarValue="00"
    private var blueSeekBarValue="00"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingStuff.setBrushSize(10.toFloat())
        meshingWithPermission()
        brush()
        saveAction()
        setUndo()
        colorDialogBox()
    }

    private fun colorDialogBox(){
        colorPicker.setOnClickListener {
            val colorDialog=Dialog(this@MainActivity,android.R.style.Theme_Light)
            colorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            colorDialog.setContentView(R.layout.color_dialog)
            colorDialog.setTitle("Pick Your Color")
            colorDialog.alpha.max=255
            colorDialog.alpha.progress=Integer.parseInt(alphaSeekBarValue,16)
            colorDialog.red.max=255
            colorDialog.red.progress=Integer.parseInt(redSeekBarValue,16)
            colorDialog.green.max=255
            colorDialog.green.progress=Integer.parseInt(greenSeekBarValue,16)
            colorDialog.blue.max=255
            colorDialog.blue.progress=Integer.parseInt(blueSeekBarValue,16)
            val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
            colorDialog.colorIndicatorBox.setBackgroundColor(Color.parseColor(color))
            alphaSeekBar(colorDialog.alpha,colorDialog.colorIndicatorBox)
            redSeekBar(colorDialog.red,colorDialog.colorIndicatorBox)
            greenSeekBar(colorDialog.green,colorDialog.colorIndicatorBox)
            blueSeekBar(colorDialog.blue,colorDialog.colorIndicatorBox)
            colorDialog.ok.setOnClickListener {
                val colorBrush="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                drawingStuff.setColor(colorBrush)
                colorDialog.dismiss()
            }
            colorDialog.cancel.setOnClickListener {
                colorDialog.dismiss()
            }
            colorDialog.show()
        }
    }

    private fun alphaSeekBar(seekBar:SeekBar,colorBox:ImageView){
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    var progressReader=Integer.toHexString(progress)
                    if (progressReader.length==1){
                        progressReader="0${progressReader.toUpperCase(Locale.ROOT)}"
                        alphaSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }else{
                        progressReader=progressReader.toUpperCase(Locale.ROOT)
                        alphaSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun redSeekBar(seekBar:SeekBar,colorBox:ImageView){
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    var progressReader=Integer.toHexString(progress)
                    if (progressReader.length==1){
                        progressReader="0${progressReader.toUpperCase(Locale.ROOT)}"
                        redSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }else{
                        progressReader=progressReader.toUpperCase(Locale.ROOT)
                        redSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun greenSeekBar(seekBar:SeekBar,colorBox:ImageView){
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    var progressReader=Integer.toHexString(progress)
                    if (progressReader.length==1){
                        progressReader="0${progressReader.toUpperCase(Locale.ROOT)}"
                        greenSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }else{
                        progressReader=progressReader.toUpperCase(Locale.ROOT)
                        greenSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun blueSeekBar(seekBar:SeekBar,colorBox:ImageView){
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    var progressReader=Integer.toHexString(progress)
                    if (progressReader.length==1){
                        progressReader="0${progressReader.toUpperCase(Locale.ROOT)}"
                        blueSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }else{
                        progressReader=progressReader.toUpperCase(Locale.ROOT)
                        blueSeekBarValue=progressReader
                        val color="#$alphaSeekBarValue$redSeekBarValue$greenSeekBarValue$blueSeekBarValue"
                        colorBox.setBackgroundColor(Color.parseColor(color))
                    }

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun settingBrushLayout(){
        val brushDialog=Dialog(this)
        brushDialog.setContentView(R.layout.stroke_width)
        brushDialog.setTitle("Brush Size")
        configureBrushButton(brushDialog.oneSize,2,brushDialog)
        configureBrushButton(brushDialog.twoSize,5,brushDialog)
        configureBrushButton(brushDialog.threeSize,10,brushDialog)
        configureBrushButton(brushDialog.fourSize,15,brushDialog)
        configureBrushButton(brushDialog.fiveSize,20,brushDialog)
        brushDialog.show()
    }

    private fun brush(){
        brushManager.setOnClickListener{
            settingBrushLayout()
        }
    }

    private fun configureBrushButton(size:ImageButton,brushSize:Int,brushDialog:Dialog){
        size.setOnClickListener {
            drawingStuff.setBrushSize(brushSize.toFloat())
            brushDialog.dismiss()
        }
    }

    private fun meshingWithPermission(){
        galleryManager.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                           android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )||ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        showSnackBar()
                    }else{
                        askingPermission()
                    }
            }else{
                fetchingPhotosFromGallery()
            }
        }
    }

    private fun showSnackBar() {
        val snackBar=Snackbar.make(snackBarPurpose,R.string.snack_bar,Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction(R.string.enable,View.OnClickListener {
            askingPermission()
        })
        snackBar.show()
    }

    private fun askingPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CALL_PHONE), PERMISSION_CODE)
    }

    companion object{
        private const val PERMISSION_CODE=307
        private const val GALLERY_INTENT_CODE=92
    }

    private fun fetchingPhotosFromGallery(){
        val intentGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
       startActivityForResult(intentGallery,GALLERY_INTENT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_INTENT_CODE) {
                if (data!!.data != null) {
                    val uri: Uri? = data.data
                    backgroundImageView.setImageURI(uri)
                }
            }
        }
    }

    private fun setUndo(){
        undoAction.setOnClickListener {
            drawingStuff.undoDraw()
        }
    }

    private fun getBitmapFromView(view:View):Bitmap{
        val returnBitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(returnBitmap)
        val bgDrawable=view.background
        if(bgDrawable!=null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnBitmap
    }

    @SuppressLint("StaticFieldLeak")
    private inner class BitmapAsyncTask(val mBitmap:Bitmap): AsyncTask<Any, Void, String>() {
        private lateinit var progressBarDialog:Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            createProgressDialog()
        }
        override fun doInBackground(vararg params: Any?): String {
           var result=""
            try {
                val bytes=ByteArrayOutputStream()
                mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                val file= File(externalCacheDir!!.
                absoluteFile.toString()+File.separator+"KidsDrawing_"
                + System.currentTimeMillis()/1000+".png")
                val fos=FileOutputStream(file)
                fos.write(bytes.toByteArray())
                fos.close()
                result=file.absolutePath
            }catch (e:Exception){
                result=""
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            dismissProgressBar()
            super.onPostExecute(result)
            creatingExitDialog(result)
            Toast.makeText(applicationContext,"$result saved successfully",Toast.LENGTH_LONG).show()
        }

        private fun creatingExitDialog(result: String?){
            this@MainActivity.let {
                val builder=AlertDialog.Builder(it)
                builder.setTitle("Photo Saved Successfully")
                builder.setMessage("Do you want to continue with current drawing?")
                builder.apply {
                    setPositiveButton("Yes") { dialog, which ->
                    }
                    setNegativeButton("No") { dialog, which ->
                        val intent= intent
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        finish()
                        startActivity(intent)
                    }
                    setNeutralButton("Share") { dialog, which ->
                        sharePhoto(result)
                    }
                    builder.create()
                    builder.show()
                }
            }
        }

        private fun sharePhoto(result:String?){
            MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result),null){
                    path, uri ->  val shareIntent=Intent()
                shareIntent.action=Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
                shareIntent.type="image/png"
                startActivity(Intent.createChooser(shareIntent,"Share Your Drawing"))
            }
        }

        private fun createProgressDialog(){
            progressBarDialog= Dialog(this@MainActivity)
            progressBarDialog.setContentView(R.layout.custom_progress_bar)
            progressBarDialog.show()
        }
        private fun dismissProgressBar(){
            progressBarDialog.dismiss()
        }
    }

    private fun saveAction(){
        saveAction.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED){
            val pushBitmap=getBitmapFromView(canvasContainer)
            BitmapAsyncTask(pushBitmap).execute()}else{
                meshingWithPermission()
            }
        }
    }
}
