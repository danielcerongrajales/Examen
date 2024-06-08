package com.example.examen.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.example.examen.MyApp
import com.example.examen.databinding.FragmentGalleryBinding
import org.json.JSONArray
import org.json.JSONObject

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val view = binding.root

        // Configurar RecyclerView con un GridLayoutManager
        val recyclerView: RecyclerView = binding.recyclerViewGallery
        recyclerView.layoutManager = GridLayoutManager(context, 3) // 3 columnas en la cuadrícula

        val barcodeList = getScannedBarcodes()
        recyclerView.adapter = GalleryAdapter(barcodeList)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getScannedBarcodes(): List<BarcodeItem> {
        val sharedPreferences = requireActivity().getSharedPreferences(MyApp.PREFS_NAME, Context.MODE_PRIVATE)
        val barcodeList = sharedPreferences.getString(KEY_SCANNED_BARCODES, "[]")
        val jsonArray = JSONArray(barcodeList)

        val items = mutableListOf<BarcodeItem>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val barcode = jsonObject.getString("barcode")
            val date = jsonObject.getString("date")
            val image = jsonObject.getString("image")
            items.add(BarcodeItem(barcode, date,image))
        }

        return items
    }

    companion object {
        private const val KEY_SCANNED_BARCODES = "scanned_barcodes"
    }
}

data class BarcodeItem(val barcode: String, val date: String,val image: String)
