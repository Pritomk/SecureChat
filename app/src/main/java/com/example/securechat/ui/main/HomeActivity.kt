package com.example.securechat.ui.main

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
import androidx.lifecycle.ViewModelProvider
import com.example.securechat.R
import com.example.securechat.data.model.QrResponse
import com.example.securechat.databinding.ActivityMainBinding
import com.example.securechat.utils.ChatService
import com.example.securechat.utils.UserInfo
import com.example.securechat.utils.ViewAnimations
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.result.Result
import io.getstream.result.call.Call


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isRotate: Boolean = false
    private lateinit var viewModel: HomeViewModel

    fun open(context: Context) {
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModels()
        saveGeneratedQR()
        setUpFab()
        setUpObserver()
    }

    private fun setUpViewModels() {
        viewModel = ViewModelProvider(this@HomeActivity, HomeViewModel.provideViewModelFactory(::runOnUiThread))[HomeViewModel::class.java]
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
        val uid = UserInfo(this@HomeActivity).userId
        viewModel.qrCode.observe(this) {bitmap ->
            bitmap?.let {
                UserInfo(this@HomeActivity).myQrCode = it
            }
        }
        if (uid != null) {
            viewModel.generateQrCode(uid)
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
        val qrCode = UserInfo(this@HomeActivity).myQrCode
        qrImg.setImageBitmap(qrCode)
        dialog.show()
    }

    private fun fabQrCodeScanFunc() {
        try {
            val intentIntegrator = IntentIntegrator(this@HomeActivity)
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
                if (isRotate) {
                    isRotate = ViewAnimations.rotateFab(binding.fabAdd, rotate = !isRotate)
                    ViewAnimations.showOut(binding.fabQrScan)
                    ViewAnimations.showOut(binding.fabQrCode)
                }
                val qrResponse = Gson().fromJson(intentResult.contents, QrResponse::class.java)
                qrResponse.uid?.let { createNewChannel(it) }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun createNewChannel(newUserUid: String) {
        val myUid = UserInfo(this@HomeActivity).userId!!
        viewModel.createChannel(myUid, newUserUid)
    }


    private fun setUpObserver() {
        setUpAddedChannelObserver()
    }

    private fun setUpAddedChannelObserver() {
        viewModel.channels.observe(this@HomeActivity) {
            it?.let {

            }
        }
    }

}