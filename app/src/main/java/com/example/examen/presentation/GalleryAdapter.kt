package com.example.examen.presentation


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.examen.databinding.ItemGalleryBinding
import kotlinx.coroutines.CoroutineStart
import android.graphics.BitmapFactory
import android.util.Base64

class GalleryAdapter(private val items: List<BarcodeItem>) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class GalleryViewHolder(private val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BarcodeItem) {
            binding.textViewBarcode.text = item.barcode
            binding.textViewDate.text = item.date
            val decodedImageBytes = Base64.decode(item.image, Base64.DEFAULT)

            val decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)
            binding.imageView2.setImageBitmap(decodedBitmap)
        }
    }
}
