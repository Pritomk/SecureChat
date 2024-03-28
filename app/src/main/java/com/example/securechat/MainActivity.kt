package com.example.securechat

import android.R.attr
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.securechat.databinding.ActivityMainBinding
import com.example.securechat.utils.UserInfo
import com.example.securechat.utils.ViewAnimations
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isRotate: Boolean = false

    fun open(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveGeneratedQR()
        setUpFab()
    }


    private fun setUpFab() {
        ViewAnimations.init(binding.fabQrCode)
        ViewAnimations.init(binding.fabQrScan)
        binding.fabAdd.setOnClickListener {
            fabAddFunc(it)
        }

        binding.fabQrCode.setOnClickListener {
            fabQrCodeVisibilityFunc()
        }

        binding.fabQrScan.setOnClickListener {
            fabQrCodeScanFunc()
        }
    }

    private fun saveGeneratedQR() {
        val uid = UserInfo(this@MainActivity).userId
        val mWriter = MultiFormatWriter()
        try {
            //BitMatrix class to encode entered text and set Width & Height
            val jsonObject = JsonObject()
            jsonObject.addProperty("uid", uid)
            val mMatrix = mWriter.encode(Gson().toJson(jsonObject), BarcodeFormat.QR_CODE, 300, 300)
            val mEncoder = BarcodeEncoder()
            val mBitmap = mEncoder.createBitmap(mMatrix) //creating bitmap of code
            UserInfo(this@MainActivity).myQrCode = mBitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun fabAddFunc(view: View) {
        isRotate = ViewAnimations.rotateFab(view, rotate = !isRotate)
        if (isRotate) {
            ViewAnimations.showIn(binding.fabQrCode)
            ViewAnimations.showIn(binding.fabQrScan)
        } else {
            ViewAnimations.showOut(binding.fabQrScan)
            ViewAnimations.showOut(binding.fabQrCode)
        }
    }

    private fun fabQrCodeVisibilityFunc() {
        val dialog = Dialog(this, R.style.NoShadowDialogTheme)
        dialog.setContentView(R.layout.qr_layout)
        dialog.setCancelable(true)
        dialog.setOnCancelListener {
            if (isRotate) {
                isRotate = ViewAnimations.rotateFab(binding.fabAdd, rotate = !isRotate)
                ViewAnimations.showOut(binding.fabQrScan)
                ViewAnimations.showOut(binding.fabQrCode)
            }
        }

        val qrImg = dialog.findViewById<ImageView>(R.id.generated_qr_code_iv)
        val qrCode = UserInfo(this@MainActivity).myQrCode
        qrImg.setImageBitmap(qrCode)
        dialog.show()
    }

    private fun fabQrCodeScanFunc() {
        try {
            val intentIntegrator = IntentIntegrator(this@MainActivity)
            intentIntegrator.setPrompt("Scan QR to Add Contact")
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.initiateScan()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("pritom", "${intentResult.contents} ${intentResult.formatName}")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}