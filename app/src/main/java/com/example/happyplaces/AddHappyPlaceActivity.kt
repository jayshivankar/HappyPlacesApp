package com.example.happyplaces

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale.getDefault

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener
    private val tv_add_image = findViewById<TextView>(R.id.tv_add_image)
    private var et_date  = findViewById<EditText>(R.id.et_date)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)
         val toolbarAddPlace = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_place)

        setSupportActionBar(findViewById(R.id.toolbar_add_place))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = month
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth

            updateDateInView()
        }
        et_date.setOnClickListener(this)
        tv_add_image.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal[Calendar.YEAR],
                    cal[Calendar.MONTH],
                    cal[Calendar.DAY_OF_MONTH]
                ).show()
            }

            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }

                }
                pictureDialog.show()

            }
        }

    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(report :MultiplePermissionsReport?){
                    if(report!!.areAllPermissionsGranted()){
                        Toast.makeText(this@AddHappyPlaceActivity, "You have access to gallery", Toast.LENGTH_SHORT).show()

                }
                override fun onPermissionRationalShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken){
                    showRationalDialogForPermissions()

                }


    })onSameThread().check()
            }
                    private fun showRationalDialogForPermissions(){
                        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"){_,_ ->
                                try {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }catch (e: ActivityNotFoundException){
                                    e.printStackTrace()
                                }
                            }
                            .setNegativeButton("Cancel"){dialog, _ ->
                                dialog.dismiss()
                            }.show()

            }

            }



        private fun updateDateInView() {
            val myFormat = "MM/dd/yyyy"
            val sdf = SimpleDateFormat(myFormat, getDefault())
            et_date.setText(sdf.format(cal.time).toString())
        }




}