package balraj.se.newsflash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

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

/**
 * Created by balra on 19-03-2018.
 */

public class HeadlinesFragment extends Fragment implements NewsAdapter.OnNewsArticleClickListener,
        NewsAdapter.SnackbarNotify {

    public static final String ARTICLE_KEY = "article_key";
    public static final String LIST_STATE = "list_state";
    public static final String NEWS_LIST = "news_list";
    private static final String name = "HeadlinesFragment";
    public static int listSize;
    @BindView(R.id.news_detail_rv)
    RecyclerView newsRecyclerView;
    @BindView(R.id.search_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.nsv_search)
    NestedScrollView nestedScrollView;
    @BindView(R.id.empty_image_view)
    ImageView emptyImageView;
    @BindView(R.id.empty_text_view)
    TextView emptyTextView;
    @BindView(R.id.go_offline_btn)
    Button offlineBtn;
    @BindView(R.id.empty_view_parent)
    LinearLayout emptyParent;
    @BindView(R.id.retry_btn)
    Button retryBtn;
    private NewsAdapter newsAdapter;
    private Parcelable parcelable;
    private Bundle saved = new Bundle();
    private InterstitialAd mInterstitialAd;
    private Tracker mTracker;

    public HeadlinesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.admob_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.headlines_fragment, container, false);
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
                new ArrayList<NewsArticle>(), this, false);
        newsRecyclerView.setAdapter(newsAdapter);
        newsRecyclerView.setHasFixedSize(true);
        nestedScrollView.setSmoothScrollingEnabled(true);
        newsRecyclerView.setNestedScrollingEnabled(false);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_fall_down);

        newsRecyclerView.setLayoutAnimation(controller);
        if (savedInstanceState != null) {
            List<NewsArticle> list = savedInstanceState.getParcelableArrayList(NEWS_LIST);
            newsAdapter.setArticleList(list);
            parcelable = savedInstanceState.getParcelable(LIST_STATE);
            newsRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        } else {
            loadHeadlines();
        }

        return view;
    }

    @OnClick(R.id.go_offline_btn)
    public void onOfflineBtnClick(View view) {
        BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.navigation_offline_tab);
    }

    @OnClick(R.id.retry_btn)
    public void onRetryBtnClick(View view) {
        if (NetworkConnectivityUtil.isNetworkAvailable(getContext())) {
            emptyParent.setVisibility(View.GONE);
            loadHeadlines();
        }
    }

    private void loadHeadlines() {
        progressBar.setVisibility(View.VISIBLE);
        if (!NetworkConnectivityUtil.isNetworkAvailable(getContext())) {
            emptyParent.setVisibility(View.VISIBLE);
            emptyImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.no_internet));
            emptyTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        NewsApiInterface newsApiInterface = RetrofitBuilder.getNewsClient().create(NewsApiInterface.class);
        final Call<Article> articleCall = newsApiInterface.getHeadlines(null, "en",
                20, null, BuildConfig.NewsSecAPIKEY);
        articleCall.enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article article = response.body();
                if (null == article) showServerError();
                List<NewsArticle> newsArticles = article.getArticles();
                newsAdapter.setArticleList(newsArticles);
                newsRecyclerView.setAdapter(newsAdapter);
                newsRecyclerView.scheduleLayoutAnimation();
                progressBar.setVisibility(View.INVISIBLE);
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                showServerError();
            }
        });
    }


    private void showServerError() {
        progressBar.setVisibility(View.INVISIBLE);
        emptyParent.setVisibility(View.VISIBLE);
        emptyImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.start_search));
        emptyTextView.setText(R.string.server_error_message);
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
        outState.putParcelable(LIST_STATE, parcelable);
        ArrayList<NewsArticle> articles = (ArrayList<NewsArticle>) newsAdapter.getArticleList();
        listSize = articles.size();
        outState.putParcelableArrayList(NEWS_LIST, (ArrayList<NewsArticle>) newsAdapter.getArticleList());
    }


    @Override
    public void notifyAndMakeSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.fragment_string) + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

