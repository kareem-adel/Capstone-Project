package com.example.kareem.theverge.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kareem.theverge.API.ArticlesAPI;
import com.example.kareem.theverge.DataSource.Database.ArticlesContract;
import com.example.kareem.theverge.R;

import java.util.concurrent.ExecutionException;

public class VergeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class StackRemoteViewsFactory implements RemoteViewsFactory {

        private final Context mContext;
        private Cursor cursor;

        public StackRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {

            final AppWidgetManager mgr = AppWidgetManager.getInstance(mContext);
            final ComponentName cn = new ComponentName(mContext, VergeWidgetProvider.class);
            ContentObserver contentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    cursor = new android.content.CursorLoader(VergeWidgetService.this, ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), null, null, new String[]{ArticlesAPI.API_SORT_BY_TOP}, null).loadInBackground();
                    mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.stack_view);
                }
            };
            mContext.getContentResolver().registerContentObserver(ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), true, contentObserver);

            cursor = new android.content.CursorLoader(VergeWidgetService.this, ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), null, null, new String[]{ArticlesAPI.API_SORT_BY_TOP}, null).loadInBackground();
        }

        @Override
        public void onDataSetChanged() {

        }


        @Override
        public void onDestroy() {
            if (cursor != null) cursor.close();
        }

        @Override
        public int getCount() {
            int count = 0;
            if (cursor != null) {
                count = cursor.getCount();
            }
            return count;
        }


        @Override
        public synchronized RemoteViews getViewAt(int position) {
            if (position == -1)
                return null;

            cursor.moveToPosition(position);

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.article_item_widget);
            if (cursor.getPosition() != -1) {
                final String ImageUrl = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntity.COLUMN_UrlToImage));
                rv.setTextViewText(R.id.article_item_title, cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntity.COLUMN_Title)));

                try {
                    Bitmap bitmap = Glide.with(VergeWidgetService.this).load(ImageUrl).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(200, 200).get();
                    rv.setImageViewBitmap(R.id.article_item_image, bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


    }

}
