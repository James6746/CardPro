package com.example.cardpro.activities

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.cardpro.R
import com.example.cardpro.database.CardRepository
import com.example.cardpro.model.Card
import com.example.cardpro.networking.RetrofitHttp
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityAddNewCard : AppCompatActivity() {
    lateinit var btnCancel: ImageView
    lateinit var btnSave: MaterialButton
    lateinit var etCardNumber: EditText
    lateinit var etExpireYear: EditText
    lateinit var etExpireMonth: EditText
    lateinit var etCvv: EditText
    val TAG: String = ActivityAddNewCard::class.java.simpleName

    lateinit var etCardHolder: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_card)

        initViews()
    }

    private fun initViews() {
        btnCancel = findViewById(R.id.btn_cancel)
        btnSave = findViewById(R.id.btn_save)
        etCardNumber = findViewById(R.id.et_card_number)
        etCardHolder = findViewById(R.id.et_card_holder)
        etCvv = findViewById(R.id.et_card_cvv)
        etExpireMonth = findViewById(R.id.et_expire_month)
        etExpireYear = findViewById(R.id.et_expire_year)

        btnSave.setOnClickListener {

            if (isInternetAvailable()) {
                val card = Card(
                    0,
                    etCardHolder.text.toString(),
                    etCardNumber.text.toString(),
                    etExpireMonth.text.toString() + "/" + etExpireYear.text.toString(),
                    etCvv.text.toString().toInt()
                )
                addCardToApi(card)
                MainActivity.offlineAddedCards.clear()
                saveToDatabase(card)
            } else {
                val card = Card(
                    0,
                    etCardHolder.text.toString(),
                    etCardNumber.text.toString(),
                    etExpireMonth.text.toString() + "/" + etExpireYear.text.toString(),
                    etCvv.text.toString().toInt(),
                    true
                )
                MainActivity.offlineAddedCards.add(card)
                saveToDatabase(card)
            }
            Log.d(TAG, "adding to database!")

            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveToDatabase(card: Card) {
        val repository = CardRepository(application)
        repository.saveCard(card)
    }

    private fun isInternetAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return infoMobile!!.isConnected || infoWifi!!.isConnected
    }

    private fun addCardToApi(card: Card) {
        RetrofitHttp.cardService.createCard(card).enqueue(object : Callback<Card> {
            override fun onResponse(
                call: Call<Card>,
                response: Response<Card>
            ) {
                if (response.body() != null) {
                    Log.d(TAG, "@@@ADDED! onResponse: ${response.body().toString()}")
                } else {
                    Log.d(TAG, "onResponse: null")
                }
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.localizedMessage}")
            }

        })

    }
}