package com.pquinonezv.ejemplosfirebase

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File

class FirebaseStorage : AppCompatActivity() {

    val CHOOSER_IMAGES : Int = 1
    lateinit var btnDownload :  Button
    lateinit var btnUpload : Button
    lateinit var ivImage : ImageView

    private lateinit var storageReference : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_storage)

        btnDownload = findViewById(R.id.btnDownload)
        btnUpload = findViewById(R.id.btnUpload)
        ivImage = findViewById(R.id.ivImagen)

        storageReference = FirebaseStorage.getInstance().getReference()

        ivImage.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            override fun onClick(v: View?) {
                var i: Intent = Intent()
                i.setType("image/*")
                i.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(
                    Intent.createChooser(i, "Selecciona una imagen"),
                    CHOOSER_IMAGES
                )
            }

        })

        btnUpload.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val time = System.currentTimeMillis()
                var ref: StorageReference = storageReference.child("foto"+ time +".png")
                ivImage.buildDrawingCache()

                var bitmap: Bitmap = ivImage.getDrawingCache()
                var baos: ByteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

                var imageBytes = baos.toByteArray()

                var uploadTask: UploadTask = ref.putBytes(imageBytes)
                uploadTask.addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(p0: Exception) {
                        TODO("Not yet implemented")
                    }

                })

                uploadTask.addOnSuccessListener(object :
                    OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(snapshot: UploadTask.TaskSnapshot?) {
                        Toast.makeText(applicationContext, "Subida con exito", Toast.LENGTH_SHORT)
                            .show()
                        var downloadUri: Uri? = snapshot?.uploadSessionUri
                        Log.w(TAG, "Image URL: " + downloadUri?.path)
                    }

                })
            }

        })

        btnDownload.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                var file : File
                try {

                    file = File.createTempFile("INE", "png")
                    storageReference.child("INE.png").getFile(file).addOnSuccessListener(object: OnSuccessListener<FileDownloadTask.TaskSnapshot> {
                        override fun onSuccess(p0: FileDownloadTask.TaskSnapshot?) {
                            var bitmap  = BitmapFactory.decodeFile(file.absolutePath)
                            ivImage.setImageBitmap(bitmap)
                        }

                    }).addOnFailureListener(object: OnFailureListener {
                        override fun onFailure(p0: java.lang.Exception) {
                            Log.e("PROYECTO", "Ocurrio un error al mostrar la imagen")
                            p0.printStackTrace()
                        }

                    })

                } catch (e : Exception) {
                    Log.e("PROYECTO", "Ocurrio un error al descargar la imagen")
                    e.printStackTrace()
                }
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( requestCode==CHOOSER_IMAGES) {
            var imageUri : Uri? = data?.getData()
            ivImage?.setImageURI(imageUri)
        }
    }
}