package balraj.se.newsflash;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;

import balraj.se.newsflash.Retrofit.NewsApiInterface;
import balraj.se.newsflash.Retrofit.RetrofitBuilder;
import balraj.se.newsflash.Ui.HeadlinesFragment;
import balraj.se.newsflash.Ui.OfflineItemFragment;
import balraj.se.newsflash.Ui.SearchFragment;

public class NewsMainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private static final String HEAD_FRAG_TAG = "head_tag", SEARCH_TAG = "search_tag", OFF_TAG = "off_tag";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    actionBar.setTitle(getString(R.string.app_name));
                    HeadlinesFragment headlinesFragment = new HeadlinesFragment();
                    loadFragment(headlinesFragment, HEAD_FRAG_TAG);
                    return true;
                case R.id.navigation_dashboard:
                    actionBar.setTitle(getString(R.string.app_name));
                    SearchFragment searchFragment = new SearchFragment();
                    loadFragment(searchFragment, SEARCH_TAG);
                    return true;
                case R.id.navigation_notifications:
                    actionBar.setTitle("Offline Saved News");
                    OfflineItemFragment offlineItemFragment = new OfflineItemFragment();
                    loadFragment(offlineItemFragment, OFF_TAG);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment, String tag) {

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        actionBar = getSupportActionBar();

        Fragment fragment = getSavedFragment();
        if (fragment != null){
            //somework
        }
        else{
            HeadlinesFragment headlinesFragment = new HeadlinesFragment();
            loadFragment(headlinesFragment, HEAD_FRAG_TAG);
        }
    }

    private Fragment getSavedFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(HEAD_FRAG_TAG);
        if(null != fragment) return fragment;
        fragment = fragmentManager.findFragmentByTag(SEARCH_TAG);
        if(null  != fragment) return fragment;
        fragment = fragmentManager.findFragmentByTag(OFF_TAG);
        if(null != fragment) return fragment;
        return null;
    }

}
