package com.example.kareem.theverge.DataSource.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class ArticlesContract {
    public static final String DatabaseName = "ArticleDatabase";
    public static final String authority = "com.example.kareem.theverge";
    final public static String MAIN_FEED_PATH = "main";
    final public static String Article_PATH = "article";
    final public static String FAVOURITES_PATH = "favourites";
    final public static String ArticlesSorting_PATH = "articlessorting";

    public static Uri BaseUri = Uri.parse("content://" + authority);

    public static class ArticleSortingEntity implements BaseColumns {
        public static final String TABLE_NAME = "articlessorting";
        public static final String COLUMN_ArticleID = "articleid";
        public static final String COLUMN_ArticleRanking = "ranking";
        public static final String COLUMN_Type = "myfeedtype";

        public static Uri ArticlesSortingUri() {
            return BaseUri.buildUpon().appendPath(ArticlesSorting_PATH).build();
        }
    }

    public static class ArticleEntity implements BaseColumns {
        public static final String TABLE_NAME = "articles";
        public static final String COLUMN_ArticleID = "articleid";
        public static final String COLUMN_Author = "author";
        public static final String COLUMN_Title = "title";
        public static final String COLUMN_Description = "description";
        public static final String COLUMN_Url = "url";
        public static final String COLUMN_UrlToImage = "urltoimage";
        public static final String COLUMN_PublishedAt = "publishedat";

        public static Uri ArticlesFeedUri() {
            return BaseUri.buildUpon().appendPath(MAIN_FEED_PATH).build();
        }

        public static Uri ArticleUri() {
            return BaseUri.buildUpon().appendPath(Article_PATH).build();
        }
    }

    public static class Favourites implements BaseColumns {
        public static final String TABLE_NAME = "Favourites";
        public static final String COLUMN_ArticleID = "articleid";

        public static Uri FavouritesUri() {
            return BaseUri.buildUpon().appendPath(FAVOURITES_PATH).build();
        }
    }
}
