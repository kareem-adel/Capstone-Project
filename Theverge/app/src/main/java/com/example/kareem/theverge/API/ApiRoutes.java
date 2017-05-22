package com.example.kareem.theverge.API;

import com.example.kareem.theverge.API.model.ArticlesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRoutes {

    @GET("articles?source=the-verge&apiKey=2a5b5e14facb4461a2003dcf866dab56")
    Call<ArticlesResponse> getFeed(@Query("sortBy") String sortBy);

}
