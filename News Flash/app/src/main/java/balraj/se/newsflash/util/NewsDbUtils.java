package balraj.se.newsflash.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import balraj.se.newsflash.data.NewsContract.NewsEntry;
import balraj.se.newsflash.model.NewsArticle;

/**
 * Created by balra on 25-03-2018.
 */

public final class NewsDbUtils {

    public static void deleteNewsArticleFromDatabase(Context context, NewsArticle article) {
        String selection = NewsEntry.NEWS_ARTICLE_TITLE + "=? AND "
                + NewsEntry.NEWS_ARTICLE_SOURCE + "=? AND "
                + NewsEntry.NEWS_ARTICLE_DATE_PUB + "=?";

        String[] selections = {
                article.getTitle(), article.getSource().getName(), article.getPublishedAt()
        };
        int rowsDeleted = context.getContentResolver().delete(
                NewsEntry.CONTENT_URI,
                selection,
                selections
        );

    }

    public static boolean isAlreadyPresent(Context context, NewsArticle article) {
        String selection = NewsEntry.NEWS_ARTICLE_TITLE + "=? AND "
                + NewsEntry.NEWS_ARTICLE_SOURCE + "=? AND "
                + NewsEntry.NEWS_ARTICLE_DATE_PUB + "=?";

        String[] selections = {
                article.getTitle(), article.getSource().getName(), article.getPublishedAt()
        };

        Cursor data = context.getContentResolver().query(
                NewsEntry.CONTENT_URI,
                null,
                selection,
                selections,
                null
        );

        if(data != null && data.moveToFirst()) {
            data.close();
            return true;
        }
        if(data != null)
            data.close();
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    public static void saveArticleToDb(final Context context, final NewsArticle article) {
        Gson gson = new Gson();
        final String newsJson = gson.toJson(article);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values = new ContentValues();
                values.put(NewsEntry.NEWS_ARTICLE_BLOB, newsJson);
                values.put(NewsEntry.NEWS_ARTICLE_SOURCE, article.getSource().getName());
                values.put(NewsEntry.NEWS_ARTICLE_DATE_PUB, article.getPublishedAt());
                values.put(NewsEntry.NEWS_ARTICLE_TITLE, article.getTitle());
                context.getContentResolver().insert(NewsEntry.CONTENT_URI, values);
                return null;
            }
        }.execute();

    }

    public static List<NewsArticle> getNewsArticleFromDatabase(Context context) {
        String orderBy = NewsEntry.ID + " DESC";

        List<NewsArticle> articles = new ArrayList<>();
        Cursor data = context.getContentResolver().query(
                NewsEntry.CONTENT_URI,
                null,
                null,
                null,
                orderBy
        );

        if (null != data && data.moveToFirst()) {
            do {
                int movieIndex = data.getColumnIndex(NewsEntry.NEWS_ARTICLE_BLOB);
                String movieJson = data.getString(movieIndex);
                Gson gson = new Gson();
                NewsArticle article = gson.fromJson(movieJson, NewsArticle.class);
                articles.add(article);
            } while (data.moveToNext());
            data.close();
        }
        return articles;
    }
}
