package com.example.kareem.theverge.SyncJob;

import android.content.Context;

import com.example.kareem.theverge.API.ArticlesAPI;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;

public class NewsJob extends JobService {

    private static FirebaseJobDispatcher dispatcher;

    @Override
    public boolean onStartJob(final JobParameters job) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArticlesAPI.requestFeedSync(NewsJob.this, ArticlesAPI.API_SORT_BY_TOP);
                    ArticlesAPI.requestFeedSync(NewsJob.this, ArticlesAPI.API_SORT_BY_LATEST);
                    jobFinished(job, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    public static void setupNewsJob(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dispatcher == null)
                    dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
                Job myJob = dispatcher.newJobBuilder()
                        .setService(NewsJob.class)
                        .setTag("NewsJob")
                        .setReplaceCurrent(true)
                        .setRecurring(true)
                        .setLifetime(Lifetime.FOREVER)
                        .setTrigger(Trigger.executionWindow(0, 900))
                        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                        .build();
                dispatcher.mustSchedule(myJob);
            }
        }).start();
    }

    /*public static void removeNewsJob(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
                dispatcher.cancel("NewsJob");
            }
        }).start();
    }*/
}
