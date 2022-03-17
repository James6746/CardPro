package com.example.cardpro.activities

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cardpro.R
import com.example.cardpro.adapter.CardAdapter
import com.example.cardpro.database.CardRepository
import com.example.cardpro.model.Card
import com.example.cardpro.networking.RetrofitHttp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var cards: ArrayList<Card>
    lateinit var text: TextView
    val TAG: String = MainActivity::class.java.simpleName
    lateinit var rvCards: RecyclerView
    lateinit var adapter: CardAdapter
    lateinit var addbtn: ImageView

    companion object {
        val offlineAddedCards: ArrayList<Card> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        rvCards = findViewById(R.id.rv_cards)
        cards = ArrayList()
        addbtn = findViewById(R.id.add_btn)

        addbtn.setOnClickListener {
            val intent = Intent(applicationContext, ActivityAddNewCard::class.java)
            startActivity(intent)
        }

        if (isInternetAvailable()) {
//            addCardToApi(getOfflineCardsFromDatabase())
            getCardsFromApi()
        } else {
            cards.addAll(getCardsFromDatabase())
            refreshAdapter(cards)
        }
    }

    private fun refreshAdapter(cards: ArrayList<Card>) {
        adapter = CardAdapter(cards)
        rvCards.adapter = adapter
    }

    fun getCardsFromApi() {
        RetrofitHttp.cardService.getAllCards().enqueue(object : Callback<ArrayList<Card>> {
            override fun onResponse(
                call: Call<ArrayList<Card>>,
                response: Response<ArrayList<Card>>
            ) {
                if (response.body() != null) {
                    Log.d(TAG, "onREsponse: ${response.body()}")
                    saveToDatabase(response.body()!!)
                    refreshAdapter(getCardsFromDatabase())

                } else {
                    Log.d(TAG, "onResponse: null")
                }
            }

            override fun onFailure(call: Call<ArrayList<Card>>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.localizedMessage}")
            }

        })

    }

    private fun saveToDatabase(cards: ArrayList<Card>) {
        val repository = CardRepository(application)
        repository.deleteUsers()
        for (card in cards)
            repository.saveCard(card)
    }

    private fun getCardsFromDatabase(): ArrayList<Card> {
        val repository = CardRepository(application)
        return repository.getCards() as ArrayList<Card>
    }

//    private fun getOfflineCardsFromDatabase(): ArrayList<Card> {
//        val repository = CardRepository(application)
//        return repository.getOfflineCards() as ArrayList<Card>
//    }

    private fun isInternetAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return infoMobile!!.isConnected || infoWifi!!.isConnected
    }

    override fun onResume() {
        super.onResume()
        refreshAdapter(getCardsFromDatabase())
        if (offlineAddedCards.size != 0) {
            addCardToApi(offlineAddedCards)
        }
    }

    fun addCardToApi(cards: ArrayList<Card>) {
        for (card in cards) {
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
}