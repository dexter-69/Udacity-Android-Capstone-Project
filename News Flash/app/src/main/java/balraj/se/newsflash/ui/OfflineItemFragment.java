package balraj.se.newsflash.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import balraj.se.newsflash.NewsMainActivity;
import balraj.se.newsflash.R;
import balraj.se.newsflash.adapter.NewsAdapter;
import balraj.se.newsflash.model.NewsArticle;
import balraj.se.newsflash.util.AnalyticsApplication;
import balraj.se.newsflash.util.DividerItemDecoration;
import balraj.se.newsflash.util.NewsDbUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static balraj.se.newsflash.ui.HeadlinesFragment.ARTICLE_KEY;

/**
 * Created by balra on 19-03-2018.
 */

public class OfflineItemFragment extends Fragment implements NewsAdapter.OnNewsArticleClickListener,
        android.support.v4.app.LoaderManager.LoaderCallbacks<List<NewsArticle>>, NewsAdapter.SnackbarNotify {

    private static final String name = "OfflineFragment";
    @BindView(R.id.news_detail_rv)
    RecyclerView newsRecyclerView;
    @BindView(R.id.search_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.nsv_search)
    NestedScrollView nestedScrollView;
    private NewsAdapter newsAdapter;
    private Parcelable parcelable;
    private int LOADER_ID = 0;
    private Tracker mTracker;

    public OfflineItemFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.offline_news_fragment, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager layoutManager;
        if (NewsMainActivity.getScreenWidth() >= 900) {
            layoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), R.drawable.divider,
                        false, false));
        newsAdapter = new NewsAdapter(getContext(), this,
                new ArrayList<NewsArticle>(), this, true);
        newsRecyclerView.setAdapter(newsAdapter);
        newsRecyclerView.setHasFixedSize(true);
        nestedScrollView.setSmoothScrollingEnabled(true);
        newsRecyclerView.setNestedScrollingEnabled(false);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_fall_down);

        newsRecyclerView.setLayoutAnimation(controller);
        if (savedInstanceState != null) {
            List<NewsArticle> list = savedInstanceState.getParcelableArrayList(HeadlinesFragment.NEWS_LIST);
            newsAdapter.setArticleList(list);
            parcelable = savedInstanceState.getParcelable(HeadlinesFragment.LIST_STATE);
            newsRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        return view;
    }

    private void loadSavedNews(List<NewsArticle> newsArticles) {
        newsAdapter.setArticleList(newsArticles);
        newsRecyclerView.setAdapter(newsAdapter);
        newsRecyclerView.scheduleLayoutAnimation();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        parcelable = newsRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onNewsArticleClicked(NewsArticle clickedArticle) {

        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra(ARTICLE_KEY, clickedArticle);
        View rootView = newsRecyclerView.getRootView();
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
        outState.putParcelableArrayList(HeadlinesFragment.NEWS_LIST, (ArrayList<NewsArticle>) newsAdapter.getArticleList());
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<List<NewsArticle>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(getContext());
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<List<NewsArticle>> loader,
                               List<NewsArticle> articles) {
        progressBar.setVisibility(View.GONE);
        loadSavedNews(articles);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<List<NewsArticle>> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.fragment_string) + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void notifyAndMakeSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @SuppressLint("StaticFieldLeak")
    static class NewsLoader extends AsyncTaskLoader<List<NewsArticle>> {
        private Context context;

        NewsLoader(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<NewsArticle> loadInBackground() {
            return NewsDbUtils.getNewsArticleFromDatabase(context);
        }
    }
}
