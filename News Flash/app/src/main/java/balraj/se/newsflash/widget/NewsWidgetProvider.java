package balraj.se.newsflash.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import balraj.se.newsflash.BuildConfig;
import balraj.se.newsflash.NewsMainActivity;
import balraj.se.newsflash.R;
import balraj.se.newsflash.model.Article;
import balraj.se.newsflash.model.NewsArticle;
import balraj.se.newsflash.retrofit.NewsApiInterface;
import balraj.se.newsflash.retrofit.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by balra on 27-03-2018.
 */

public class NewsWidgetProvider extends AppWidgetProvider {

    private AppWidgetTarget appWidgetTarget;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        NewsApiInterface newsApiInterface = RetrofitBuilder.getNewsClient().create(NewsApiInterface.class);
        final Call<Article> articleCall = newsApiInterface.getHeadlines(null, "en", 1,
                null, BuildConfig.NewsSecAPIKEY);
        articleCall.enqueue(new Callback<Article>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article article = response.body();
                if (article != null) {
                    List<NewsArticle> articleList = article.getArticles();
                    NewsArticle newsArticle = articleList.get(0);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.news_widget);
                    remoteViews.setTextViewText(R.id.widget_news_title, newsArticle.getTitle());
                    appWidgetTarget = new AppWidgetTarget(context, R.id.widget_news_image_view, remoteViews, appWidgetIds) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                        }
                    };
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.error(ContextCompat.getDrawable(context.getApplicationContext(),
                            R.drawable.news_fallback_drawable));
                    Glide
                            .with(context.getApplicationContext())
                            .setDefaultRequestOptions(requestOptions)
                            .asBitmap()
                            .load(newsArticle.getUrlToImage())
                            .into(appWidgetTarget);
                    Intent intent = new Intent(context, NewsMainActivity.class);
                    intent.addCategory(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_CANCEL_CURRENT);
                    remoteViews.setOnClickPendingIntent(R.id.widget_parent_layout, pendingIntent);
                    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    final ComponentName cn = new ComponentName(context, NewsWidgetProvider.class);
                    mgr.updateAppWidget(cn, remoteViews);
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {

            }
        });
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
