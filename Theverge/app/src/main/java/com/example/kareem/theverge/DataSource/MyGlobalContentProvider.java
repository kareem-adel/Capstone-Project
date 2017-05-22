package com.example.kareem.theverge.DataSource;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.kareem.theverge.DataSource.Database.ArticlesContract;
import com.example.kareem.theverge.DataSource.Database.VergeSQLiteDatabase;

public class MyGlobalContentProvider extends ContentProvider {
    UriMatcher uriMatcher;
    static final int MAIN_FEED_URI = 2;
    static final int FAVOURITES_URI = 3;
    static final int ARTICLES_SORTING_URI = 4;
    static final int ARTICLE_URI = 5;

    VergeSQLiteDatabase vergeSQLiteDatabase;

    public MyGlobalContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ArticlesContract.authority, ArticlesContract.MAIN_FEED_PATH, MAIN_FEED_URI);
        uriMatcher.addURI(ArticlesContract.authority, ArticlesContract.FAVOURITES_PATH, FAVOURITES_URI);
        uriMatcher.addURI(ArticlesContract.authority, ArticlesContract.ArticlesSorting_PATH, ARTICLES_SORTING_URI);
        uriMatcher.addURI(ArticlesContract.authority, ArticlesContract.Article_PATH, ARTICLE_URI);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = vergeSQLiteDatabase.getWritableDatabase();
        int type = uriMatcher.match(uri);
        int ret;
        switch (type) {
            case MAIN_FEED_URI: {
                ret = sqLiteDatabase.delete(ArticlesContract.ArticleEntity.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVOURITES_URI: {
                ret = sqLiteDatabase.delete(ArticlesContract.Favourites.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), null);
                break;
            }
            case ARTICLES_SORTING_URI: {
                ret = sqLiteDatabase.delete(ArticlesContract.ArticleSortingEntity.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ARTICLE_URI: {
                ret = sqLiteDatabase.delete(ArticlesContract.ArticleEntity.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public String getType(Uri uri) {
        int type = uriMatcher.match(uri);
        switch (type) {
            case MAIN_FEED_URI: {
                return "MAIN_FEED_URI" ;
            }
            case FAVOURITES_URI: {
                return "FAVOURITES_PATH" ;
            }
            case ARTICLES_SORTING_URI: {
                return "ARTICLES_SORTING_PATH" ;
            }
            case ARTICLE_URI: {
                return "Article_PATH" ;
            }
            default:
                return "Unknown" ;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = vergeSQLiteDatabase.getWritableDatabase();
        int type = uriMatcher.match(uri);
        switch (type) {
            case MAIN_FEED_URI: {
                sqLiteDatabase.insert(ArticlesContract.ArticleEntity.TABLE_NAME, null, values);//, "Could not insert into " + ArticlesContract.ArticleEntity.TABLE_NAME);
                break;
            }
            case FAVOURITES_URI: {
                sqLiteDatabase.insert(ArticlesContract.Favourites.TABLE_NAME, null, values);//, "Could not insert into " + ArticlesContract.Favourites.TABLE_NAME);
                getContext().getContentResolver().notifyChange(ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), null);
                break;
            }
            case ARTICLES_SORTING_URI: {
                sqLiteDatabase.insert(ArticlesContract.ArticleSortingEntity.TABLE_NAME, null, values);//, "Could not insert into " + ArticlesContract.Favourites.TABLE_NAME);
                break;
            }
            case ARTICLE_URI: {
                sqLiteDatabase.insert(ArticlesContract.ArticleEntity.TABLE_NAME, null, values);//, "Could not insert into " + ArticlesContract.Favourites.TABLE_NAME);
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    public void verifyCompletion(long operation, String message) {
        if (operation < 0) {
            try {
                throw new Exception(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreate() {
        vergeSQLiteDatabase = new VergeSQLiteDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = vergeSQLiteDatabase.getWritableDatabase();
        int type = uriMatcher.match(uri);
        Cursor cursor;
        switch (type) {
            case MAIN_FEED_URI: {
                cursor = sqLiteDatabase.query(ArticlesContract.ArticleEntity.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case FAVOURITES_URI: {
                String favouritesCase = "case when EXISTS(select 1 from " + ArticlesContract.Favourites.TABLE_NAME + " where " + ArticlesContract.Favourites.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " = " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " ) then 1 else 0 end as isFavourite" ;
                if (selectionArgs != null) {
                    String selectionCols = ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity._ID + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_UrlToImage + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_Title + " , " + favouritesCase;
                    String query = "select " + selectionCols + " from " + ArticlesContract.Favourites.TABLE_NAME + " inner join " + ArticlesContract.ArticleEntity.TABLE_NAME + " on " + ArticlesContract.Favourites.TABLE_NAME + "." + ArticlesContract.Favourites.COLUMN_ArticleID + " = " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " where " + ArticlesContract.Favourites.TABLE_NAME + "." + ArticlesContract.Favourites.COLUMN_ArticleID + " = ?" ;
                    cursor = sqLiteDatabase.rawQuery(query, selectionArgs);
                } else {
                    String selectionCols = ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity._ID + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_UrlToImage + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_Title + " , " + favouritesCase;
                    String query = "select " + selectionCols + " from " + ArticlesContract.Favourites.TABLE_NAME + " inner join " + ArticlesContract.ArticleEntity.TABLE_NAME + " on " + ArticlesContract.Favourites.TABLE_NAME + "." + ArticlesContract.Favourites.COLUMN_ArticleID + " = " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID;
                    cursor = sqLiteDatabase.rawQuery(query, null);
                }
                break;
            }
            case ARTICLES_SORTING_URI: {
                String favouritesCase = "case when EXISTS(select 1 from " + ArticlesContract.Favourites.TABLE_NAME + " where " + ArticlesContract.Favourites.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " = " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " ) then 1 else 0 end as isFavourite" ;
                String selectionCols = ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity._ID + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_UrlToImage + " , " + ArticlesContract.ArticleSortingEntity.TABLE_NAME + "." + ArticlesContract.ArticleSortingEntity.COLUMN_ArticleRanking + " , " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_Title + " , " + favouritesCase;
                String query = "select " + selectionCols + " from " + ArticlesContract.ArticleSortingEntity.TABLE_NAME + " inner join " + ArticlesContract.ArticleEntity.TABLE_NAME + " on " + ArticlesContract.ArticleSortingEntity.TABLE_NAME + "." + ArticlesContract.ArticleSortingEntity.COLUMN_ArticleID + " = " + ArticlesContract.ArticleEntity.TABLE_NAME + "." + ArticlesContract.ArticleEntity.COLUMN_ArticleID + " where " + ArticlesContract.ArticleSortingEntity.TABLE_NAME + "." + ArticlesContract.ArticleSortingEntity.COLUMN_Type + " = ?"
                        + " order by "
                        + ArticlesContract.ArticleSortingEntity.COLUMN_ArticleRanking + " asc" ;
                cursor = sqLiteDatabase.rawQuery(query, selectionArgs);
                break;
            }
            case ARTICLE_URI: {
                cursor = sqLiteDatabase.query(ArticlesContract.ArticleEntity.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = vergeSQLiteDatabase.getWritableDatabase();
        int type = uriMatcher.match(uri);
        int ret;
        switch (type) {
            case MAIN_FEED_URI: {
                ret = sqLiteDatabase.update(ArticlesContract.ArticleEntity.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case FAVOURITES_URI: {
                ret = sqLiteDatabase.update(ArticlesContract.Favourites.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ARTICLES_SORTING_URI: {
                ret = sqLiteDatabase.update(ArticlesContract.ArticleSortingEntity.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ARTICLE_URI: {
                ret = sqLiteDatabase.update(ArticlesContract.ArticleEntity.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = vergeSQLiteDatabase.getWritableDatabase();
        int type = uriMatcher.match(uri);
        sqLiteDatabase.beginTransaction();
        int count = 0;
        switch (type) {
            case MAIN_FEED_URI: {
                try {
                    for (ContentValues contentValue : values) {
                        String ArticleID = (String) contentValue.get(ArticlesContract.ArticleEntity.COLUMN_ArticleID);
                        sqLiteDatabase.delete(ArticlesContract.ArticleEntity.TABLE_NAME, ArticlesContract.ArticleEntity.COLUMN_ArticleID + " = ?", new String[]{ArticleID});
                        if (sqLiteDatabase.insert(ArticlesContract.ArticleEntity.TABLE_NAME, null, contentValue) != -1) {
                            count++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                break;
            }
            case FAVOURITES_URI: {
                try {
                    for (ContentValues contentValue : values) {
                        if (sqLiteDatabase.insert(ArticlesContract.Favourites.TABLE_NAME, null, contentValue) != -1) {
                            count++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                break;
            }
            case ARTICLES_SORTING_URI: {
                try {
                    for (ContentValues contentValue : values) {
                        if (sqLiteDatabase.insert(ArticlesContract.ArticleSortingEntity.TABLE_NAME, null, contentValue) != -1) {
                            count++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                break;
            }
            case ARTICLE_URI: {
                try {
                    for (ContentValues contentValue : values) {
                        if (sqLiteDatabase.insert(ArticlesContract.ArticleEntity.TABLE_NAME, null, contentValue) != -1) {
                            count++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
