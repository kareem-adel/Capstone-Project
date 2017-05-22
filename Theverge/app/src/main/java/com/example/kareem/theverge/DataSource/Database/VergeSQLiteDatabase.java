package com.example.kareem.theverge.DataSource.Database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class VergeSQLiteDatabase extends SQLiteOpenHelper {
    public static final int mVersion = 6;

    public VergeSQLiteDatabase(Context context) {
        super(context, ArticlesContract.DatabaseName, null, mVersion);
    }


    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        String CREATE_ARTICLES_SORTING_TABLE = "CREATE TABLE " + ArticlesContract.ArticleSortingEntity.TABLE_NAME + " ( " +
                ArticlesContract.ArticleSortingEntity._ID + " INTEGER AUTO INCREMENT, " +
                ArticlesContract.ArticleSortingEntity.COLUMN_ArticleID + " VARCHAR(64), " +
                ArticlesContract.ArticleSortingEntity.COLUMN_ArticleRanking + " INTEGER, " +
                ArticlesContract.ArticleSortingEntity.COLUMN_Type + " TEXT" +
                ");";
        db.execSQL(CREATE_ARTICLES_SORTING_TABLE);

        String CREATE_ARTICLES_FEED_TABLE = "CREATE TABLE " + ArticlesContract.ArticleEntity.TABLE_NAME + " ( " +
                ArticlesContract.ArticleEntity._ID + " INTEGER AUTO INCREMENT," +
                ArticlesContract.ArticleEntity.COLUMN_ArticleID + " VARCHAR(64) UNIQUE, " +
                ArticlesContract.ArticleEntity.COLUMN_Author + " TEXT, " +
                ArticlesContract.ArticleEntity.COLUMN_Title + " TEXT, " +
                ArticlesContract.ArticleEntity.COLUMN_Description + " TEXT, " +
                ArticlesContract.ArticleEntity.COLUMN_Url + " TEXT, " +
                ArticlesContract.ArticleEntity.COLUMN_UrlToImage + " TEXT, " +
                ArticlesContract.ArticleEntity.COLUMN_PublishedAt + " TEXT" +
                ");";
        db.execSQL(CREATE_ARTICLES_FEED_TABLE);

        String CREATE_ARTICLES_FAVOURITES_TABLE = "CREATE TABLE " + ArticlesContract.Favourites.TABLE_NAME + " ( " +
                ArticlesContract.Favourites._ID + " INTEGER AUTO INCREMENT, " +
                ArticlesContract.Favourites.COLUMN_ArticleID + " VARCHAR(64) " +
                ");";
        db.execSQL(CREATE_ARTICLES_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArticlesContract.ArticleSortingEntity.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ArticlesContract.Favourites.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ArticlesContract.ArticleEntity.TABLE_NAME);
        onCreate(db);
    }
}
