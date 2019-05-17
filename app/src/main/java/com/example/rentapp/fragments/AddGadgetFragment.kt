package com.example.rentapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.rentapp.R
import com.example.rentapp.data.Gadget
import com.livinglifetechway.quickpermissions.annotations.WithPermissions
import kotlinx.android.synthetic.main.add_gadget.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddGadgetFragment : Fragment() {
    companion object{
        const val TAG = "ADD_GADGET_FRAGMENT_TAG"
    }

    interface AddGadgetListener {
        fun onGadgetCreated(gadget: Gadget)
    }

    private var addGadgetListener: AddGadgetListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_gadget, container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnCapturePhoto.setOnClickListener{
            dispatchTakePictureIntent()
        }

        etGadgetDescription.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                tilGadgetDescription.error = null
            }
        }

        etGadgetName.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                tilGadgetName.error = null
            }
        }

        btnAddGadget.setOnClickListener{
            //Eszköz létrehozása
            if(etGadgetName.text!!.isEmpty()) {
                tilGadgetName.error = "Név megadása kötelező"
            }
            if(etGadgetDescription.text!!.isEmpty()) {
                tilGadgetDescription.error = "Leírás megadása kötelező"
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is AddGadgetListener){
            addGadgetListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        addGadgetListener = null
    }

    lateinit var currentPhotoPath: String

    /**
     * Creates file with timestamp on internal storage of the app in a directory for photos.
     * If directory not exists then the function creates it.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        return context?.let{ context ->
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                    "JPEG_${timeStamp}_", /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            ).run {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
                this
            }
        }
    }

    /**
     * Sending image capture intent to default camera app with file uri for the image.
     */
    @WithPermissions(
            permissions = [Manifest.permission.CAMERA]
    )
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this.context!!,
                            "com.example.rentapp.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, GadgetRentFragment.TAKE_PHOTO)
                }
            }
        }
    }

    var photosAdded = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == GadgetRentFragment.TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            if(!photosAdded) {
                gadgetPhotosContainer.removeAllViews()
                photosAdded = true
            }

            val file = File(currentPhotoPath)
            val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, Uri.fromFile(file))

            bitmap?.let { bitmap ->
                val imageView = ImageView(context)
                imageView.setImageBitmap(bitmap)

                imageView.layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                    setMargins(4,0,4,0)
                    this
                }

                gadgetPhotosContainer.addView(imageView)
            }

            Toast.makeText(context, "Siker", Toast.LENGTH_LONG).show()
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}