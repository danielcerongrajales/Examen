package com.example.examen.domain

import com.example.examen.data.model.Product
import com.example.examen.data.repository.ProductsAPi
import javax.inject.Inject

interface ProductRepository {
    suspend fun getProduct(id: Int):Product
}



class ProductRepositoryImpl @Inject constructor(private val productService: ProductsAPi):ProductRepository{
    override suspend fun getProduct(id: Int): Product {
        return productService.getProduct(id)
    }
}
