package com.example.examen.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examen.data.model.Product
import com.example.examen.domain.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {

    val productLiveData = MutableLiveData<Product>()
    val errorLiveData = MutableLiveData<String>()

    fun getProduct(id: Int) {
        viewModelScope.launch {
            try {
                val product = withContext(Dispatchers.IO) {
                    repository.getProduct(id)
                }
                productLiveData.postValue(product)
            } catch (e: Exception) {
                errorLiveData.postValue(e.message)
            }
        }
    }
}
