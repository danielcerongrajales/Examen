package com.example.examen.di

import com.example.examen.data.repository.ProductsAPi
import com.example.examen.domain.ProductRepository
import com.example.examen.domain.ProductRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideProductService(retrofit: Retrofit): ProductsAPi {
        return retrofit.create(ProductsAPi::class.java)
    }

    @Provides
    fun provideProductRepository(productService: ProductsAPi): ProductRepository {
        return ProductRepositoryImpl(productService)
    }
}
