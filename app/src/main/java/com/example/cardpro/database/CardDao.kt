package com.example.ogabekroomretrofit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cardpro.model.Card

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCard(card: Card)

    @Query("SELECT * FROM cards")
    fun getCards(): List<Card>

//    @Query("SELECT * FROM cards WHERE isAvailable=:boolean")
//    fun getOfflineCards(boolean: Boolean = true): List<Card>

    @Query("DELETE FROM cards")
    fun deleteAllCards()

    @Query("DELETE FROM cards WHERE id =:id")
    fun deleteCard(id: Int)

}