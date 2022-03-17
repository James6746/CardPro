package com.example.cardpro.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHttp {

    companion object {
        private val TAG: String = RetrofitHttp::class.java.simpleName

        const val IS_TESTER: Boolean = true

        const val SERVER_DEVELOPMENT = "https://62219d1fafd560ea69b4e18b.mockapi.io/api/"
        const val SERVER_PRODUCTION = "https://62219d1fafd560ea69b4e18b.mockapi.io/api/"

        private fun server(): String {
            return if (IS_TESTER) {
                SERVER_DEVELOPMENT
            } else {
                SERVER_PRODUCTION
            }
        }

        private fun getRetrofit(): Retrofit{
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(server())
                .build()
        }

        val cardService: CardService = getRetrofit().create(CardService::class.java)
    }
}