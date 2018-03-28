package balraj.se.newsflash.data;

/**
 * Created by balra on 25-03-2018.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by balra on 12-10-2017.
 */

public class NewsProvider extends ContentProvider {

    private NewsOpenHelper newsOpenHelper;


    @Override
    public boolean onCreate() {
        newsOpenHelper = new NewsOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor retCursor = newsOpenHelper.getReadableDatabase().query(
                NewsContract.NewsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Unknown Uri: " + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = newsOpenHelper.getWritableDatabase();
        Uri returnUri;
        long id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, contentValues);
        if (id > 0) {
            returnUri = ContentUris.withAppendedId(NewsContract.NewsEntry.CONTENT_URI, id);
        } else {
            throw new android.database.SQLException("Failed to insert row into: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selections) {
        final SQLiteDatabase db = newsOpenHelper.getWritableDatabase();

        // Keep track of the number of deleted tasks
        int rowsDeleted = db.delete(NewsContract.NewsEntry.TABLE_NAME, selection, selections);
        if (rowsDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        return 0;
    }
}

