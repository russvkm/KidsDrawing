package com.russvkm.kidsdrawing

import android.annotation.SuppressLint
import android.app.Activity
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
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.stroke_width.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
 private var mImageButtonCurrentPaint:ImageButton?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingStuff.setBrushSize(10.toFloat())
        mImageButtonCurrentPaint=colorPalette[4] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat
            .getDrawable(this,R.drawable.palette_selected))
        meshingWithPermission()
        brush()
        saveAction()
        setUndo()
    }

    private fun settingBrushLayout(){
        val brushDialog=Dialog(this)
        brushDialog.setContentView(R.layout.stroke_width)
        brushDialog.setTitle("Brush Size")
        configureBrushButton(brushDialog.smallButton,10,brushDialog)
        configureBrushButton(brushDialog.mediumButton,15,brushDialog)
        configureBrushButton(brushDialog.largeButton,20,brushDialog)
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun pickColor(view: View){
        if(view!= mImageButtonCurrentPaint){
            val imageButton=view as ImageButton
            val colorTag=imageButton.tag.toString()
            drawingStuff.setColor(colorTag)
            imageButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.palette_selected))
            mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(
                this,R.drawable.palette_normal))
            mImageButtonCurrentPaint=view
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
            createDialog()
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
            Toast.makeText(applicationContext,"$result saved successfully",Toast.LENGTH_LONG).show()
            MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result),null){
                path, uri ->  val shareIntent=Intent()
                shareIntent.action=Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
                shareIntent.type="image/png"
                startActivity(Intent.createChooser(shareIntent,"Share Your Drawing"))
            }

        }

        private fun createDialog(){
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
