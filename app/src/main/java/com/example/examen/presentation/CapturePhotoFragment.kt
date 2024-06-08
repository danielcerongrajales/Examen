package com.example.examen.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.examen.MyApp
import com.example.examen.R
import com.example.examen.databinding.FragmentCapturePhotoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CapturePhotoFragment : Fragment() {
    private val scanLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        run {
            if (result.contents == null) {

            } else {
                saveScannedBarcode(result.contents,  BitmapFactory.decodeFile(result.barcodeImagePath))
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scanQRCodeFromGallery(uri)
            }
        } else {
            Snackbar.make(requireView(), "No se seleccionó ninguna imagen", Snackbar.LENGTH_SHORT).show()
        }
    }
    private lateinit var binding: FragmentCapturePhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCapturePhotoBinding.inflate(inflater, container, false)
        val view = binding.root

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scanButton.setOnClickListener {
            startBarcodeScanner()
        }
        binding.galleryButton.setOnClickListener {
            openGallery()
        }
        getMostRecentBarcode()
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
    private fun scanQRCodeFromGallery(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

        try {

            val intArray = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            val result = MultiFormatReader().decode(binaryBitmap)

            val qrContent = result.text

            saveScannedBarcode(qrContent, bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NotFoundException) {
            e.printStackTrace()
            // Manejar el caso en el que no se encuentra ningún código QR en la imagen
            // Por ejemplo, mostrar un mensaje de error o realizar alguna otra acción
        }
    }


    private fun getMostRecentBarcode() {
        val sharedPreferences = requireActivity().getSharedPreferences(MyApp.PREFS_NAME, Context.MODE_PRIVATE)
        val barcodeList = sharedPreferences.getString(KEY_SCANNED_BARCODES, "[]")
        val jsonArray = JSONArray(barcodeList)

        var mostRecentBarcodeItem: String = ""
        var mostRecentDate: Long = 0

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val dateString = jsonObject.getString("date")
            val image = jsonObject.getString("image")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateString)?.time ?: 0

            if (date > mostRecentDate) {
                mostRecentBarcodeItem = image
                mostRecentDate = date
            }
        }

        val decodedImageBytes = Base64.decode(mostRecentBarcodeItem, Base64.DEFAULT)

        val decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)
        binding.imageView3.setImageBitmap(decodedBitmap)
    }


    private fun startBarcodeScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Escanear qr")
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(true)
        scanLauncher.launch(options)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner()
            } else {
                // Permiso de cámara denegado, puedes mostrar un mensaje o realizar alguna acción
            }
        }
    }

    private fun saveScannedBarcode(barcode: String, imagePath: Bitmap?) {
        if (imagePath != null) {
            val imageBase64 = bitmapToBase64(imagePath)

            val sharedPreferences = requireActivity().getSharedPreferences(MyApp.PREFS_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val barcodeList = sharedPreferences.getString(KEY_SCANNED_BARCODES, "[]")
            val jsonArray = JSONArray(barcodeList)

            val jsonObject = JSONObject().apply {
                put("barcode", barcode)
                put("date", getCurrentDate())
                put("image", imageBase64)
            }

            jsonArray.put(jsonObject)

            editor.putString(KEY_SCANNED_BARCODES, jsonArray.toString())
            editor.apply()
            val action = R.id.action_capturePhotoFragment_to_galleryFragment
            findNavController().navigate(action)

        } else {

        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        const val KEY_SCANNED_BARCODES = "scanned_barcodes"
    }
}
