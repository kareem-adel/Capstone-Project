package com.example.kareem.theverge;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kareem.theverge.DataSource.Database.ArticlesContract;
import com.example.kareem.theverge.Favourites.SetFavouritesTask;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailFragment extends Fragment {


    private Cursor mShareCursor;

    String ArticleID;

    public final static int GeneralDetailFragmentLoaderCONST = 0;
    public final static int DetailFragmentLoaderFavouriteCONST = 1;

    GeneralDetailFragmentLoader generalDetailFragmentLoader;
    DetailFragmentLoaderFavourite detailFragmentLoaderFavourite;
    private boolean OnePane;

    @BindView(R.id.fragment_article_detail_favourite)
    FloatingActionButton fragment_article_detail_favourite;
    @BindView(R.id.fragment_article_detail_title)
    TextView fragment_article_detail_title;
    @BindView(R.id.fragment_article_detail_image)
    ImageView fragment_article_detail_image;
    @BindView(R.id.fragment_article_detail_description)
    TextView fragment_article_detail_description;
    @BindView(R.id.fragment_article_detail_toolbar)
    Toolbar fragment_article_detail_toolbar;
    @BindView(R.id.fragment_article_detail_share)
    FloatingActionButton fragment_article_detail_share;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArticleID = getArguments().getString("ArticleID");
        OnePane = getArguments().getBoolean("OnePane", false);

        if (OnePane) {
            TypedArray typedArray = getActivity().getTheme().obtainStyledAttributes(R.style.AppTheme, new int[]{android.R.attr.homeAsUpIndicator});
            Drawable drawable = getResources().getDrawable(typedArray.getResourceId(0, 0));
            fragment_article_detail_toolbar.setNavigationIcon(drawable);
            fragment_article_detail_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
            });
        }

        fragment_article_detail_share.setImageDrawable(new IconicsDrawable(getActivity()).icon(GoogleMaterial.Icon.gmd_share).sizeDp(24));
        fragment_article_detail_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareCursor != null && !mShareCursor.isClosed()) {
                    String content = mShareCursor.getString(COLUMN_Url);
                    startActivityForResult(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                            .setType("text/plain")
                            .setText(content)
                            .getIntent(), getString(R.string.share)), 0);
                }
            }
        });

        fragment_article_detail_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        generalDetailFragmentLoader = new GeneralDetailFragmentLoader();
        getLoaderManager().initLoader(GeneralDetailFragmentLoaderCONST, null, generalDetailFragmentLoader);
        detailFragmentLoaderFavourite = new DetailFragmentLoaderFavourite();
        getLoaderManager().initLoader(DetailFragmentLoaderFavouriteCONST, null, detailFragmentLoaderFavourite);
    }


    public static final int COLUMN_Author = 0;
    public static final int COLUMN_Title = 1;
    public static final int COLUMN_Description = 2;
    public static final int COLUMN_Url = 3;
    public static final int COLUMN_UrlToImage = 4;
    public static final int COLUMN_PublishedAt = 5;

    public class GeneralDetailFragmentLoader implements LoaderManager.LoaderCallbacks<Cursor> {


        public final String[] DetailProjection = new String[]{
                ArticlesContract.ArticleEntity.COLUMN_Author,
                ArticlesContract.ArticleEntity.COLUMN_Title,
                ArticlesContract.ArticleEntity.COLUMN_Description,
                ArticlesContract.ArticleEntity.COLUMN_Url,
                ArticlesContract.ArticleEntity.COLUMN_UrlToImage,
                ArticlesContract.ArticleEntity.COLUMN_PublishedAt};


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), ArticlesContract.ArticleEntity.ArticleUri(), DetailProjection, ArticlesContract.ArticleEntity.COLUMN_ArticleID + " = ?", new String[]{ArticleID}, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mShareCursor = data;
            if (data.moveToFirst()) {
                fragment_article_detail_title.setText(data.getString(COLUMN_Title));
                fragment_article_detail_description.setText(data.getString(COLUMN_Description));

                String ImageUrl = data.getString(COLUMN_UrlToImage);
                Glide.with(getActivity()).load(ImageUrl).placeholder(R.drawable.placeholder).error(R.drawable.exclamation).into(fragment_article_detail_image);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }


    public class DetailFragmentLoaderFavourite implements LoaderManager.LoaderCallbacks<Cursor> {

        public final String[] FavouriteProjection = new String[]{
                ArticlesContract.Favourites._ID,
                ArticlesContract.Favourites.COLUMN_ArticleID};


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), ArticlesContract.Favourites.FavouritesUri(), FavouriteProjection, ArticlesContract.Favourites.COLUMN_ArticleID + " = ?", new String[]{ArticleID}, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToFirst()) {
                fragment_article_detail_favourite.setImageDrawable(new IconicsDrawable(getActivity()).icon(GoogleMaterial.Icon.gmd_star).color(getResources().getColor(R.color.orange)));
                fragment_article_detail_favourite.setTag("set");
            } else {
                fragment_article_detail_favourite.setImageDrawable(new IconicsDrawable(getActivity()).icon(GoogleMaterial.Icon.gmd_star_border).color(getResources().getColor(R.color.orange)));
                fragment_article_detail_favourite.setTag("unset");
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
