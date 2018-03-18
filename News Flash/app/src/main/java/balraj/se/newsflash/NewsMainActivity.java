package balraj.se.newsflash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import balraj.se.newsflash.Retrofit.NewsApiInterface;
import balraj.se.newsflash.Retrofit.RetrofitBuilder;
import balraj.se.newsflash.Ui.HeadlinesFragment;
import balraj.se.newsflash.Ui.OfflineItemFragment;
import balraj.se.newsflash.Ui.SearchFragment;

public class NewsMainActivity extends AppCompatActivity {

    private ActionBar actionBar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.setTitle(getString(R.string.title_nav_headlines));
                    HeadlinesFragment headlinesFragment = new HeadlinesFragment();
                    loadFragment(headlinesFragment);
                    return true;
                case R.id.navigation_dashboard:
                    actionBar.setTitle(getString(R.string.title_nav_search) + " News");
                    SearchFragment searchFragment = new SearchFragment();
                    loadFragment(searchFragment);
                    return true;
                case R.id.navigation_notifications:
                    actionBar.setTitle("Offline Saved News");
                    OfflineItemFragment offlineItemFragment = new OfflineItemFragment();
                    loadFragment(offlineItemFragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(R.string.title_nav_headlines));
        }

        //Load Initial Fragment
        HeadlinesFragment headlinesFragment = new HeadlinesFragment();
        loadFragment(headlinesFragment);
    }

}
