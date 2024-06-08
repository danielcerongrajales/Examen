package com.example.examen.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.examen.R
import com.example.examen.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        // Configurar click listeners para los botones
        binding.buttonCapturePhoto.setOnClickListener {
            navigateToCapturePhotoFragment()
        }
        binding.buttonGallery.setOnClickListener {
            navigateToGalleryFragment()
        }
        binding.buttonProductList.setOnClickListener {
            navigateToProductListFragment()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToCapturePhotoFragment() {
        val action = R.id.action_mainFragment_to_capturePhotoFragment
        findNavController().navigate(action)
    }

    private fun navigateToGalleryFragment() {
        val action = R.id.action_mainFragment_to_galleryFragment
        findNavController().navigate(action)
    }

    private fun navigateToProductListFragment() {
        val action = R.id.action_mainFragment_to_productListFragment
        findNavController().navigate(action)
    }
}
