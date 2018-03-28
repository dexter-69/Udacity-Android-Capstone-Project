package balraj.se.newsflash.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import balraj.se.newsflash.data.NewsContract.NewsEntry;

/**
 * Created by balra on 25-03-2018.
 */

public class NewsOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "news_article_db";
    private static final int DATABASE_VERSION = 2;

    public NewsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_NEWS_ARTICLE_TABLE =
                "CREATE TABLE " + NewsEntry.TABLE_NAME + " ("
                        + NewsEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + NewsEntry.NEWS_ARTICLE_TITLE + " TEXT NOT NULL, "
                        + NewsEntry.NEWS_ARTICLE_SOURCE + " TEXT, "
                        + NewsEntry.NEWS_ARTICLE_BLOB + " TEXT NOT NULL, "
                        + NewsEntry.NEWS_ARTICLE_DATE_PUB + " TEXT ); ";

        sqLiteDatabase.execSQL(CREATE_NEWS_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
