package com.example.examen.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import android.content.Context
import android.content.Intent
import com.example.examen.MyApp
import com.example.examen.databinding.FragmentCapturePhotoBinding

class CapturePhotoFragment : Fragment() {

    private lateinit var binding: FragmentCapturePhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCapturePhotoBinding.inflate(inflater, container, false)
        val view = binding.root

        // Solicitar permiso de cámara si aún no está otorgado
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Iniciar el escaneo de código de barras
            startBarcodeScanner()
        }

        return view
    }

    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(false)
        integrator.setPrompt("Escanea un código de barras")
        integrator.initiateScan()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido, iniciar el escaneo de código de barras
                startBarcodeScanner()
            } else {
                // Permiso de cámara denegado, puedes mostrar un mensaje o realizar alguna acción
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null && result.contents != null) {
                val scannedBarcode = result.contents
                // Guardar el valor escaneado en las preferencias compartidas
                saveScannedBarcode(scannedBarcode)
            }
        }
    }

    private fun saveScannedBarcode(barcode: String) {
        val sharedPreferences =
            requireActivity().getSharedPreferences(MyApp.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_SCANNED_BARCODE, barcode)
        editor.apply()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val KEY_SCANNED_BARCODE = "scanned_barcode"
    }
}
