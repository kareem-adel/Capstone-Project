package com.example.kareem.theverge.Favourites;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.example.kareem.theverge.DataSource.Database.ArticlesContract;

public class SetFavouritesTask extends AsyncTask<String, Void, Void> {

    Context mContext;

    public SetFavouritesTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String set_unset = params[0];
        String ArticleID = params[1];

        switch (set_unset) {
            case "set": {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ArticlesContract.Favourites.COLUMN_ArticleID, ArticleID);
                mContext.getContentResolver().insert(ArticlesContract.Favourites.FavouritesUri(), contentValues);
                break;
            }
            case "unset": {
                mContext.getContentResolver().delete(ArticlesContract.Favourites.FavouritesUri(), ArticlesContract.Favourites.COLUMN_ArticleID + " = ?", new String[]{ArticleID});
                break;
            }
        }
        return null;
    }
}
