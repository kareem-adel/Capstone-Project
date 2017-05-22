package com.example.kareem.theverge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ArticleDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);
        Bundle bundle = new Bundle();
        bundle.putString("ArticleID", getIntent().getExtras().getString("ArticleID"));
        bundle.putBoolean("OnePane", true);
        ArticleDetailFragment articleDetailFragment = new ArticleDetailFragment();
        articleDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.article_detail_activity_container, articleDetailFragment).commit();
    }
}
