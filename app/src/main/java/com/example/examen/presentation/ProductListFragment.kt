package com.example.examen.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.examen.MyApp
import com.example.examen.databinding.FragmentProductListBinding
import com.example.examen.presentation.CapturePhotoFragment.Companion.KEY_SCANNED_BARCODES
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        val view = binding.root
        val scannedBarcode = getScannedBarcode()
        if (scannedBarcode.isNotEmpty()) {
            viewModel.getProduct(scannedBarcode.toInt())
        } else {
            viewModel.getProduct(1)
        }

        viewModel.productLiveData.observe(viewLifecycleOwner) { product ->
            binding.textView1.text = product.title
            Glide.with(binding.root)
                .load(product.image)
                .into(binding.imageView)
            binding.textView3.text = product.description
        }

        return view
    }

    private fun getScannedBarcode(): String {
            val sharedPreferences = requireActivity().getSharedPreferences(MyApp.PREFS_NAME, Context.MODE_PRIVATE)
            val barcodeList = sharedPreferences.getString(KEY_SCANNED_BARCODES, "[]")
            val jsonArray = JSONArray(barcodeList)

            var mostRecentBarcodeItem = ""
            var mostRecentDate: Long = 0

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val dateString = jsonObject.getString("date")
                val barcode = jsonObject.getString("barcode")

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(dateString)?.time ?: 0

                if (date > mostRecentDate) {
                    mostRecentBarcodeItem = barcode
                    mostRecentDate = date
                }
            }

           return mostRecentBarcodeItem


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_SCANNED_BARCODE = "scanned_barcode"
    }
}
