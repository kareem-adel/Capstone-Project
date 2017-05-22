package com.example.kareem.theverge;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.example.kareem.theverge.SyncJob.NewsJob;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NewsFeedFragment.Callback {

    boolean twoPane;
    @BindView(R.id.main_feed_detail_fragment_container)
    FrameLayout main_feed_detail_fragment_container;
    @BindView(R.id.activity_main_banner)
    AdView activity_main_banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        twoPane = main_feed_detail_fragment_container != null;
        NewsJob.setupNewsJob(this);
        activity_main_banner.loadAd(new AdRequest.Builder()
                //.addTestDevice("6D991FFAB8AF4D6B773C527206554A61")
                .build());
    }


    @Override
    public void launchDetailFragment(String ArticleID) {
        if (twoPane) {
            Bundle bundle = new Bundle();
            bundle.putString("ArticleID", ArticleID);
            ArticleDetailFragment ArticleDetail = new ArticleDetailFragment();
            ArticleDetail.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_feed_detail_fragment_container, ArticleDetail).commit();
        } else {
            Intent intent = new Intent(this, ArticleDetail.class);
            intent.putExtra("ArticleID", ArticleID);
            startActivity(intent);
        }
    }
}
