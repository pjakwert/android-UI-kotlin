package com.example.pjakwert.uidemo.network

import com.example.pjakwert.uidemo.model.Item
import com.example.pjakwert.uidemo.model.SearchResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface SearchRequestInterface {

    // http://www.omdbapi.com/
    // ?apikey=3110888f
    // ?s=blade
    @GET("/")
    fun getData(
            @Query("apikey")    apikey : String,
            @Query("s")         search : String,
            @Query("page")      page :   Int
    ) : Observable<SearchResult>

}


interface DetailsRequestInterface {
    // http://www.omdbapi.com/
    // ?apikey=3110888f
    // ?i=imdbId (i.e. tt1856101)
    @GET("/")
    fun getData(
            @Query("apikey")    apikey : String,
            @Query("i")         imdbId : String
    ) : Observable<Item>
}