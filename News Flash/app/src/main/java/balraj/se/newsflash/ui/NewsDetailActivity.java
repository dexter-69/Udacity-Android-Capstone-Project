package balraj.se.newsflash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import balraj.se.newsflash.R;

/**
 * Created by balra on 27-03-2018.
 */

public class NewsDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            Intent intent = getIntent();
            if (intent.hasExtra(HeadlinesFragment.ARTICLE_KEY)) {
                args.putParcelable(HeadlinesFragment.ARTICLE_KEY,
                        intent.getParcelableExtra(HeadlinesFragment.ARTICLE_KEY));

                NewsDetailFragment fragment = new NewsDetailFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.news_detail_container, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
