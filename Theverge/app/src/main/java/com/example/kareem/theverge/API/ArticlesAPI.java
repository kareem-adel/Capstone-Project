package com.example.kareem.theverge.API;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.kareem.theverge.API.model.Article;
import com.example.kareem.theverge.API.model.ArticlesResponse;
import com.example.kareem.theverge.DataSource.Database.ArticlesContract;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticlesAPI {
    final public static String API_BASE_URL = "https://newsapi.org/v1/";
    final public static String API_SORT_BY_TOP = "top";
    final public static String API_SORT_BY_LATEST = "latest";

    public static ApiRoutes router;

    public static ApiRoutes getRouter() {
        if (router == null) {
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            router = restAdapter.create(ApiRoutes.class);
        }
        return router;
    }

    public static void requestFeed(final Context context, final String sortBy) {
        ArticlesAPI.getRouter().getFeed(sortBy).enqueue(new retrofit2.Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                if (response.body() == null) {
                    Log.d("requestFeed", "error in requesting feed");
                    return;
                }
                ArrayList<Article> articles = response.body().articles;
                ContentValues[] ArticlesValues = new ContentValues[articles.size()];
                ContentValues[] ArticlesRankingsValues = new ContentValues[articles.size()];
                for (int i = 0; i < articles.size(); i++) {

                    String uniqueString = articles.get(i).author + articles.get(i).title + articles.get(i).description + articles.get(i).url + articles.get(i).urlToImage + articles.get(i).publishedAt;
                    String generatedId = Hashing.sha256().hashString(uniqueString, Charset.forName("UTF-8")).toString();

                    ContentValues contentValue = new ContentValues();
                    ContentValues contentRankingValue = new ContentValues();
                    contentValue.put(ArticlesContract.ArticleEntity.COLUMN_ArticleID, generatedId);
                    contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Author, articles.get(i).author);
                    contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Title, articles.get(i).title);
                    contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Description, articles.get(i).description);
                    contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Url, articles.get(i).url);
                    contentValue.put(ArticlesContract.ArticleEntity.COLUMN_UrlToImage, articles.get(i).urlToImage);
                    contentValue.put(ArticlesContract.ArticleEntity.COLUMN_PublishedAt, articles.get(i).publishedAt);
                    ArticlesValues[i] = contentValue;


                    contentRankingValue.put(ArticlesContract.ArticleSortingEntity.COLUMN_ArticleID, generatedId);
                    contentRankingValue.put(ArticlesContract.ArticleSortingEntity.COLUMN_ArticleRanking, i + 1);
                    contentRankingValue.put(ArticlesContract.ArticleSortingEntity.COLUMN_Type, sortBy);
                    ArticlesRankingsValues[i] = contentRankingValue;
                }


                context.getContentResolver().bulkInsert(ArticlesContract.ArticleEntity.ArticlesFeedUri(), ArticlesValues);
                context.getContentResolver().delete(ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), ArticlesContract.ArticleSortingEntity.COLUMN_Type + " = ?", new String[]{sortBy});
                context.getContentResolver().bulkInsert(ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), ArticlesRankingsValues);
            }

            @Override
            public void onFailure(Call<ArticlesResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public static void requestFeedSync(final Context context, final String sortBy) throws IOException {
        Response<ArticlesResponse> response = ArticlesAPI.getRouter().getFeed(sortBy).execute();
        if (response.body() == null) {
            Log.d("requestFeed", "error in requesting feed");
            return;
        }
        ArrayList<Article> articles = response.body().articles;
        ContentValues[] ArticlesValues = new ContentValues[articles.size()];
        ContentValues[] ArticlesRankingsValues = new ContentValues[articles.size()];
        for (int i = 0; i < articles.size(); i++) {

            String uniqueString = articles.get(i).author + articles.get(i).title + articles.get(i).description + articles.get(i).url + articles.get(i).urlToImage + articles.get(i).publishedAt;
            String generatedId = Hashing.sha256().hashString(uniqueString, Charset.forName("UTF-8")).toString();

            ContentValues contentValue = new ContentValues();
            ContentValues contentRankingValue = new ContentValues();
            contentValue.put(ArticlesContract.ArticleEntity.COLUMN_ArticleID, generatedId);
            contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Author, articles.get(i).author);
            contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Title, articles.get(i).title);
            contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Description, articles.get(i).description);
            contentValue.put(ArticlesContract.ArticleEntity.COLUMN_Url, articles.get(i).url);
            contentValue.put(ArticlesContract.ArticleEntity.COLUMN_UrlToImage, articles.get(i).urlToImage);
            contentValue.put(ArticlesContract.ArticleEntity.COLUMN_PublishedAt, articles.get(i).publishedAt);
            ArticlesValues[i] = contentValue;


            contentRankingValue.put(ArticlesContract.ArticleSortingEntity.COLUMN_ArticleID, generatedId);
            contentRankingValue.put(ArticlesContract.ArticleSortingEntity.COLUMN_ArticleRanking, i + 1);
            contentRankingValue.put(ArticlesContract.ArticleSortingEntity.COLUMN_Type, sortBy);
            ArticlesRankingsValues[i] = contentRankingValue;
        }


        context.getContentResolver().bulkInsert(ArticlesContract.ArticleEntity.ArticlesFeedUri(), ArticlesValues);
        context.getContentResolver().delete(ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), ArticlesContract.ArticleSortingEntity.COLUMN_Type + " = ?", new String[]{sortBy});
        context.getContentResolver().bulkInsert(ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), ArticlesRankingsValues);
    }
}

