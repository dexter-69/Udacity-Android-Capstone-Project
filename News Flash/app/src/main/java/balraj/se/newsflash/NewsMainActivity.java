package balraj.se.newsflash;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import balraj.se.newsflash.ui.HeadlinesFragment;
import balraj.se.newsflash.ui.OfflineItemFragment;
import balraj.se.newsflash.ui.SearchFragment;


public class NewsMainActivity extends AppCompatActivity {

    private static final String HEAD_FRAG_TAG = "head_tag";
    private static final String SEARCH_TAG = "search_tag";
    private static final String OFF_TAG = "off_tag";
    private static final String SAVED_TAG = "saved";
    private Fragment headlinesFragment = new HeadlinesFragment();
    private Fragment offlineItemFragment = new OfflineItemFragment();
    private Fragment searchFragment = new SearchFragment();
    private Fragment active = headlinesFragment;
    private FragmentManager fm;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_headlines_tab:
                    if (active == headlinesFragment) {
                        hideShowFrag(active, headlinesFragment);
                    } else {
                        hideShowFragWithAnimation(active, headlinesFragment);
                    }
                    active = headlinesFragment;
                    return true;
                case R.id.navigation_search_tab:
                    if (active == searchFragment) {
                        hideShowFrag(active, searchFragment);
                    } else {
                        hideShowFragWithAnimation(active, searchFragment);
                    }
                    active = searchFragment;
                    return true;
                case R.id.navigation_offline_tab:
                    if (active == offlineItemFragment) {
                        hideShowFrag(active, offlineItemFragment);
                    } else {
                        offlineItemFragment = new OfflineItemFragment();
                        loadFragment(offlineItemFragment, OFF_TAG);
                        hideShowFragWithAnimation(active, offlineItemFragment);
                    }
                    active = offlineItemFragment;
                    return true;
            }
            return false;
        }
    };

    public static float getScreenWidth() {
        float px = Resources.getSystem().getDisplayMetrics().widthPixels;
        float density = Resources.getSystem().getDisplayMetrics().density;
        return px / density;
    }

    private void hideShowFrag(Fragment toHide, Fragment toShow) {
        fm.beginTransaction().hide(toHide).show(toShow).commit();
    }

    private void hideShowFragWithAnimation(Fragment toHide, Fragment toShow) {
        fm.beginTransaction().hide(toHide)
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(toShow).commit();
    }

    private void loadFragment(Fragment fragment, String tag) {

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.fragment_container, fragment, tag)
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
        fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            loadFragment(offlineItemFragment, OFF_TAG);
            fm.beginTransaction().hide(offlineItemFragment).commit();
            loadFragment(searchFragment, SEARCH_TAG);
            fm.beginTransaction().hide(searchFragment).commit();
            loadFragment(headlinesFragment, HEAD_FRAG_TAG);
        } else {
            active = fm.getFragment(savedInstanceState, "saved");
            headlinesFragment = fm.getFragment(savedInstanceState, HEAD_FRAG_TAG);
            searchFragment = fm.getFragment(savedInstanceState, SEARCH_TAG);
            offlineItemFragment = fm.getFragment(savedInstanceState, OFF_TAG);
            if (active instanceof HeadlinesFragment) {
                fm.beginTransaction()
                        .hide(offlineItemFragment).hide(searchFragment).show(active).commit();
            } else if (active instanceof SearchFragment) {
                fm.beginTransaction()
                        .hide(offlineItemFragment).hide(headlinesFragment).show(active).commit();
            } else {
                fm.beginTransaction()
                        .hide(headlinesFragment).hide(searchFragment).show(active).commit();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fm.putFragment(outState, SAVED_TAG, active);
        fm.putFragment(outState, HEAD_FRAG_TAG, headlinesFragment);
        fm.putFragment(outState, SEARCH_TAG, searchFragment);
        fm.putFragment(outState, OFF_TAG, offlineItemFragment);
    }
}