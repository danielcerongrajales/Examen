package com.example.examen.data.repository

import com.example.examen.data.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductsAPi {
    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product
}