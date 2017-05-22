package com.example.kareem.theverge;

import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kareem.theverge.API.ArticlesAPI;
import com.example.kareem.theverge.DataSource.Database.ArticlesContract;
import com.example.kareem.theverge.Favourites.SetFavouritesTask;
import com.google.android.gms.ads.AdView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsFeedFragment extends Fragment {
    GridView NewsFeed;
    NewsFeedAdapter newsFeedAdapter;


    ArticlesLoader articlesLoader;
    public final static int TYPE_TOP = 0;
    public final static int TYPE_LATEST = 1;
    public final static int TYPE_FAVOURITES = 2;
    public final static int ArticlesLoaderCONST = 3;
    int CurrentSortingType = 0;
    int currentFeedClickPosition = 0;
    private Drawer mainMenu;
    private Toolbar activity_main_toolbar;
    @BindView(R.id.fragment_news_feed_GridView)
    GridView fragment_news_feed_GridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PrimaryDrawerItem Top = new PrimaryDrawerItem().withName(com.example.kareem.theverge.R.string.top).withIcon(CommunityMaterial.Icon.cmd_trending_up).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                CurrentSortingType = TYPE_TOP;
                requestFeed(ArticlesAPI.API_SORT_BY_TOP);
                getLoaderManager().restartLoader(ArticlesLoaderCONST, null, articlesLoader);
                mainMenu.closeDrawer();
                return true;
            }
        });

        PrimaryDrawerItem Latest = new PrimaryDrawerItem().withName(com.example.kareem.theverge.R.string.latest).withIcon(CommunityMaterial.Icon.cmd_sync).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                CurrentSortingType = TYPE_LATEST;
                requestFeed(ArticlesAPI.API_SORT_BY_LATEST);
                getLoaderManager().restartLoader(ArticlesLoaderCONST, null, articlesLoader);
                mainMenu.closeDrawer();
                return true;
            }
        });

        PrimaryDrawerItem Favourites = new PrimaryDrawerItem().withName(com.example.kareem.theverge.R.string.favourites).withIcon(CommunityMaterial.Icon.cmd_star).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                CurrentSortingType = TYPE_FAVOURITES;
                getLoaderManager().restartLoader(ArticlesLoaderCONST, null, articlesLoader);
                mainMenu.closeDrawer();
                return true;
            }
        });

        activity_main_toolbar = (Toolbar) getActivity().findViewById(R.id.activity_main_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(activity_main_toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        mainMenu = new DrawerBuilder()
                .withActivity(getActivity())
                .withToolbar(activity_main_toolbar)
                .withAccountHeader(new AccountHeaderBuilder()
                        .withActivity(getActivity())
                        .withHeaderBackground(R.drawable.background)
                        .withSelectionListEnabled(false)
                        .withProfileImagesVisible(false)
                        .build())
                .addDrawerItems(
                        Top,
                        Latest,
                        Favourites
                )
                .withHeaderPadding(true)
                .build();

        NewsFeed.setNumColumns(1);
        newsFeedAdapter = new NewsFeedAdapter(getContext(), null, 0);
        NewsFeed.setAdapter(newsFeedAdapter);


        CurrentSortingType = TYPE_TOP;
        requestFeed(ArticlesAPI.API_SORT_BY_TOP);
        requestFeed(ArticlesAPI.API_SORT_BY_LATEST);

        articlesLoader = new ArticlesLoader();
        getLoaderManager().initLoader(ArticlesLoaderCONST, null, articlesLoader);

        if (savedInstanceState != null)
            currentFeedClickPosition = savedInstanceState.getInt("FeedPos", 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("FeedPos", currentFeedClickPosition);
        super.onSaveInstanceState(outState);
    }

    public interface Callback {
        void launchDetailFragment(String ArticleID);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(ArticlesLoaderCONST, null, articlesLoader);
    }


    public class ArticlesLoader implements LoaderManager.LoaderCallbacks<Cursor> {


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (CurrentSortingType) {
                case TYPE_TOP: {
                    return new CursorLoader(getActivity(), ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), null, null, new String[]{ArticlesAPI.API_SORT_BY_TOP}, null);
                }
                case TYPE_LATEST: {
                    return new CursorLoader(getActivity(), ArticlesContract.ArticleSortingEntity.ArticlesSortingUri(), null, null, new String[]{ArticlesAPI.API_SORT_BY_LATEST}, null);
                }
                case TYPE_FAVOURITES: {
                    return new CursorLoader(getActivity(), ArticlesContract.Favourites.FavouritesUri(), null, null, null, null);
                }
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            newsFeedAdapter.swapCursor(data);
            NewsFeed.smoothScrollToPosition(currentFeedClickPosition);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            newsFeedAdapter.swapCursor(null);
        }
    }

    public class NewsFeedAdapter extends CursorAdapter {


        public NewsFeedAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.article_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();
            String ImageUrl = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntity.COLUMN_UrlToImage));
            Glide.with(getActivity()).load(ImageUrl).placeholder(R.drawable.placeholder).error(R.drawable.exclamation).into(viewHolder.article_item_image);
            viewHolder.article_item_title.setText(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntity.COLUMN_Title)));

            final String ArticleID = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntity.COLUMN_ArticleID));
            final int Position = cursor.getPosition();

            ((View) (viewHolder.article_item_image.getParent())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFeedClickPosition = Position;
                    ((Callback) getActivity()).launchDetailFragment(ArticleID);
                }
            });

            viewHolder.article_item_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFeedClickPosition = Position;
                    ((Callback) getActivity()).launchDetailFragment(ArticleID);
                }
            });

            viewHolder.article_item_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFeedClickPosition = Position;
                    ((Callback) getActivity()).launchDetailFragment(ArticleID);
                }
            });

            viewHolder.article_item_favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFeedClickPosition = Position;
                    switch ((String) v.getTag()) {
                        case "set": {
                            SetFavouritesTask setFavouritesTask = new SetFavouritesTask(getContext());
                            setFavouritesTask.execute("unset", ArticleID);
                            break;
                        }
                        case "unset": {
                            SetFavouritesTask setFavouritesTask = new SetFavouritesTask(getContext());
                            setFavouritesTask.execute("set", ArticleID);
                            break;
                        }
                    }
                }
            });

            if (cursor.getString(cursor.getColumnIndex("isFavourite")).equals("1")) {
                viewHolder.article_item_favourite.setImageDrawable(new IconicsDrawable(getActivity()).icon(GoogleMaterial.Icon.gmd_star).color(getResources().getColor(R.color.orange)));
                viewHolder.article_item_favourite.setTag("set");
            } else {
                viewHolder.article_item_favourite.setImageDrawable(new IconicsDrawable(getActivity()).icon(GoogleMaterial.Icon.gmd_star_border).color(getResources().getColor(R.color.orange)));
                viewHolder.article_item_favourite.setTag("unset");
            }

        }

        public class ViewHolder {
            ImageView article_item_image;
            TextView article_item_title;
            ImageView article_item_favourite;

            public ViewHolder(View itemView) {
                article_item_image = (ImageView) itemView.findViewById(R.id.article_item_image);
                article_item_title = (TextView) itemView.findViewById(R.id.article_item_title);
                article_item_favourite = (ImageView) itemView.findViewById(R.id.article_item_favourite);
            }
        }
    }

    public void requestFeed(final String sortBy) {
        ArticlesAPI.requestFeed(getActivity(), sortBy);
    }
}
