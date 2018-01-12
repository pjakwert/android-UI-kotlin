package com.example.pjakwert.uidemo.model

import android.net.Uri
import com.google.gson.annotations.SerializedName


data class SearchResult(
        @SerializedName("Search") val Search : List<Item>,
        val totalResults : Int
)


data class Item(
        val Poster : String,
        val Title : String,
        val Released : String,
        val Director : String,
        val Plot : String,
        val imdbID : String,
        val Type : String,
        val Genre : String,
        val Ratings : List<Rating>
)

data class Rating(
        val Source : String,
        val Value : String
)