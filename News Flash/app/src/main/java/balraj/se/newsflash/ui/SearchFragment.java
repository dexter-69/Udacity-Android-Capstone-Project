package balraj.se.newsflash.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import balraj.se.newsflash.BuildConfig;
import balraj.se.newsflash.NewsMainActivity;
import balraj.se.newsflash.R;
import balraj.se.newsflash.adapter.NewsAdapter;
import balraj.se.newsflash.model.Article;
import balraj.se.newsflash.model.NewsArticle;
import balraj.se.newsflash.retrofit.NewsApiInterface;
import balraj.se.newsflash.retrofit.RetrofitBuilder;
import balraj.se.newsflash.util.AnalyticsApplication;
import balraj.se.newsflash.util.DividerItemDecoration;
import balraj.se.newsflash.util.NetworkConnectivityUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static balraj.se.newsflash.ui.HeadlinesFragment.ARTICLE_KEY;

/**
 * Created by balra on 19-03-2018.
 */

public class SearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener,
        NewsAdapter.OnNewsArticleClickListener, NewsAdapter.SnackbarNotify {
    private final static String SUGGESTION_KEY = "suggestions";
    private static final String name = "SearchFragment";
    private static final String NO_RESULTS = "No Results Found";
    @BindView(R.id.news_detail_rv)
    RecyclerView searchNewsRv;
    @BindView(R.id.search_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.nsv_search)
    NestedScrollView nestedScrollView;
    @BindView(R.id.searchBar)
    MaterialSearchBar materialSearchBar;
    @BindView(R.id.empty_image_view)
    ImageView emptyImageView;
    @BindView(R.id.empty_text_view)
    TextView emptyTextView;
    @BindView(R.id.empty_view_parent)
    LinearLayout emptyParent;
    @BindView(R.id.go_offline_btn)
    Button offlineBtn;
    @BindView(R.id.retry_btn)
    Button retryBtn;
    private NewsAdapter newsAdapter;
    private Parcelable parcelable;
    private Tracker mTracker;
    private int listSize;
    private String query;

    public SearchFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();
        parcelable = searchNewsRv.getLayoutManager().onSaveInstanceState();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.bind(this, view);
        materialSearchBar.setOnSearchActionListener(this);
        materialSearchBar.setLastSuggestions(loadSuggestionsFromDisk());
        RecyclerView.LayoutManager layoutManager;
        if (NewsMainActivity.getScreenWidth() >= 900) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }
        searchNewsRv.setLayoutManager(layoutManager);
        searchNewsRv.addItemDecoration(
                new DividerItemDecoration(getActivity(), R.drawable.divider,
                        false, false));
        newsAdapter = new NewsAdapter(getContext(), this,
                new ArrayList<NewsArticle>(), this, false);
        searchNewsRv.setAdapter(newsAdapter);
        searchNewsRv.setHasFixedSize(true);
        nestedScrollView.setSmoothScrollingEnabled(true);
        searchNewsRv.setNestedScrollingEnabled(false);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_fall_down);

        searchNewsRv.setLayoutAnimation(controller);
        if (savedInstanceState != null) {
            List<NewsArticle> list = savedInstanceState.getParcelableArrayList(HeadlinesFragment.NEWS_LIST);
            newsAdapter.setArticleList(list);
            parcelable = savedInstanceState.getParcelable(HeadlinesFragment.LIST_STATE);
            searchNewsRv.getLayoutManager().onRestoreInstanceState(parcelable);
        }

        if (!NetworkConnectivityUtil.isNetworkAvailable(getContext())) {
            progressBar.setVisibility(View.INVISIBLE);
            emptyParent.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            offlineBtn.setVisibility(View.VISIBLE);
            emptyImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.no_internet));
            emptyTextView.setVisibility(View.GONE);
        } else if (savedInstanceState == null || newsAdapter.getItemCount() == 0) {
            emptyParent.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.INVISIBLE);
            offlineBtn.setVisibility(View.INVISIBLE);
            emptyImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.start_search));
            emptyImageView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.start_search_message);
            emptyTextView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private List<String> loadSuggestionsFromDisk() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> set = sharedPreferences.getStringSet(SUGGESTION_KEY, new HashSet<String>());
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    private void saveSuggestionsToDisk(List<String> searchSuggestions) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = sharedPreferences.getStringSet(SUGGESTION_KEY, new HashSet<String>());
        set.clear();
        set.addAll(searchSuggestions);
        editor.putStringSet(SUGGESTION_KEY, set);
        editor.apply();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        hideKeyboard();
        if (query != null && query.equals(text.toString()) || TextUtils.isEmpty(text)) {
            return;
        }
        query = text.toString();
        startSearch(query);
    }

    @OnClick(R.id.go_offline_btn)
    public void onOfflineBtnClick(View view) {
        BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.navigation_offline_tab);
    }

    private void hideKeyboard() {
        materialSearchBar.clearFocus();
        getActivity().findViewById(R.id.search_app_bar).clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), 0);
    }

    @OnClick(R.id.retry_btn)
    public void onRetryBtnClick(View view) {
        if (!NetworkConnectivityUtil.isNetworkAvailable(getContext())) {
            searchNewsRv.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            emptyParent.setVisibility(View.GONE);
            searchNewsRv.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.GONE);
            offlineBtn.setVisibility(View.GONE);

            if (query != null && query.length() > 0)
                startSearch(query);
            emptyImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.start_search));
        }
    }

    private void startSearch(final String query) {
        emptyParent.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if (!NetworkConnectivityUtil.isNetworkAvailable(getContext())) {
            progressBar.setVisibility(View.INVISIBLE);
            emptyParent.setVisibility(View.VISIBLE);
            emptyImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.no_internet));
            emptyTextView.setVisibility(View.GONE);
            searchNewsRv.setVisibility(View.INVISIBLE);
            offlineBtn.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            return;
        }
        NewsApiInterface newsApiInterface = RetrofitBuilder.getNewsClient().create(NewsApiInterface.class);
        final Call<Article> articleCall = newsApiInterface.searchArticle(query, BuildConfig.NewsSecAPIKEY);
        articleCall.enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article article = response.body();
                if (null == query || TextUtils.isEmpty(query)) {
                    showSearchEntrance();
                    return;
                } else if (article.getArticles().size() == 0) {
                    showSearchError(getString(R.string.no_results_found));
                    return;
                }
                List<NewsArticle> newsArticles = article.getArticles();
                newsAdapter.setArticleList(newsArticles);
                searchNewsRv.setAdapter(newsAdapter);
                searchNewsRv.scheduleLayoutAnimation();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                showSearchError(getString(R.string.some_error_occured));
            }
        });
    }

    private void showSearchError(String message) {
        searchNewsRv.setVisibility(View.INVISIBLE);
        if (message.equals(NO_RESULTS))
            emptyImageView.setVisibility(View.VISIBLE);
        else
            emptyTextView.setText(message);
        emptyTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showSearchEntrance() {
        searchNewsRv.setVisibility(View.INVISIBLE);
        emptyParent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
    }


    @Override
    public void onNewsArticleClicked(NewsArticle clickedArticle) {

        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra(ARTICLE_KEY, clickedArticle);
        View rootView = searchNewsRv.getRootView();
        Pair<View, String> p1 = Pair.create(rootView.findViewById(R.id.news_article_thumbnail_iv),
                getString(R.string.news_image_trans));
        Pair<View, String> p2 = Pair.create(rootView.findViewById(R.id.news_title),
                getString(R.string.news_title_trans));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), p1, p2);

        startActivity(intent, options.toBundle());

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(HeadlinesFragment.LIST_STATE, parcelable);
        ArrayList<NewsArticle> articles = (ArrayList<NewsArticle>) newsAdapter.getArticleList();
        if (articles != null)
            listSize = articles.size();
        outState.putParcelableArrayList(HeadlinesFragment.NEWS_LIST, (ArrayList<NewsArticle>) newsAdapter.getArticleList());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listSize == 0 && query != null && query.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            startSearch(query);
        }
        mTracker.setScreenName(getString(R.string.fragment_string) + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //save last queries to disk
        saveSuggestionsToDisk(materialSearchBar.getLastSuggestions());
    }


    @Override
    public void notifyAndMakeSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (NetworkConnectivityUtil.isNetworkAvailable(getContext()) &&
                    searchNewsRv.getAdapter().getItemCount() == 0) {
                //emptyParent.setVisibility(View.GONE);
                retryBtn.setVisibility(View.GONE);
                offlineBtn.setVisibility(View.GONE);
                emptyTextView.setText(R.string.start_search_message);
                emptyImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.start_search));

                emptyTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}

