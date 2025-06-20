package com.example.buyva.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.buyva.BuildConfig

object StripeClient {
    private const val STRIPE_BASE_URL = "https://api.stripe.com/v1/"

    private const val STRIPE_SECRET_KEY = "sk_test_51Rbo4dG494n1ubkjYYNVgGQyzd0D7eNC9zLjgDWR1KzWeGbWfu51f6tmTTNUMlm9zf7oYuUk6d2kxS6cmu5GfsDj00nhu27HRm"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $STRIPE_SECRET_KEY")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
            chain.proceed(request)
        }

        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(STRIPE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: StripeAPI = retrofit.create(StripeAPI::class.java)
}