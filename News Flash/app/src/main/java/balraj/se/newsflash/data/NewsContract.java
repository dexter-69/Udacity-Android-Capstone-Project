package balraj.se.newsflash.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by balra on 25-03-2018.
 */

public class NewsContract {

    public static final String AUTHORITY = "balraj.se.newsflash";
    public static final String PATH_NEWS = "news";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String TABLE_NAME = "news_article";
        public static final String ID = BaseColumns._ID;
        public static final String NEWS_ARTICLE_BLOB = "news_blob";
        public static final String NEWS_ARTICLE_TITLE = "news_article_title";
        public static final String NEWS_ARTICLE_DATE_PUB = "news_article_date_published";
        public static final String NEWS_ARTICLE_SOURCE = "news_article_source";
    }
}
