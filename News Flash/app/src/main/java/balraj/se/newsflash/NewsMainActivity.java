package balraj.se.newsflash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import balraj.se.newsflash.Adapter.NewsAdapter;
import balraj.se.newsflash.Model.NewsArticle;
import balraj.se.newsflash.Ui.HeadlinesFragment;
import balraj.se.newsflash.Ui.OfflineItemFragment;
import balraj.se.newsflash.Ui.SearchFragment;
import retrofit2.http.HEAD;

public class NewsMainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private static final String HEAD_FRAG_TAG = "head_tag", SEARCH_TAG = "search_tag", OFF_TAG = "off_tag";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(getSavedFragment(HEAD_FRAG_TAG) != null) return true;
                    HeadlinesFragment headlinesFragment = new HeadlinesFragment();
                    loadFragment(headlinesFragment, HEAD_FRAG_TAG);
                    return true;
                case R.id.navigation_dashboard:
                    if(getSavedFragment(SEARCH_TAG) != null) return true;
                    SearchFragment searchFragment = new SearchFragment();
                    loadFragment(searchFragment, SEARCH_TAG);
                    return true;
                case R.id.navigation_notifications:
                    if(getSavedFragment(OFF_TAG) != null) return true;
                    OfflineItemFragment offlineItemFragment = new OfflineItemFragment();
                    loadFragment(offlineItemFragment, OFF_TAG);
                    return true;
            }
            return false;
        }
    };



    private void loadFragment(Fragment fragment, String tag) {

        this.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);

        if(savedInstanceState == null) {
            HeadlinesFragment headlinesFragment = new HeadlinesFragment();
            loadFragment(headlinesFragment, HEAD_FRAG_TAG);
            actionBar.setTitle(getString(R.string.app_name));
        }

    }

    private Fragment getSavedFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (null != fragment) {
            fragmentTransaction.show(fragment);
            return fragment;
        }
        /*fragment = fragmentManager.findFragmentByTag(SEARCH_TAG);
        if (null != fragment) {
            fragmentTransaction.show(fragment);
            return fragment;
        }
        fragment = fragmentManager.findFragmentByTag(OFF_TAG);
        if (null != fragment) {
            fragmentTransaction.show(fragment);
            return fragment;
        }*/
        return null;
    }


}
