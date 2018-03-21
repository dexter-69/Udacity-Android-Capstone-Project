package balraj.se.newsflash.Ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import balraj.se.newsflash.Adapter.NewsAdapter;
import balraj.se.newsflash.Model.Article;
import balraj.se.newsflash.Model.NewsArticle;
import balraj.se.newsflash.R;
import balraj.se.newsflash.Retrofit.NewsApiInterface;
import balraj.se.newsflash.Retrofit.RetrofitBuilder;
import balraj.se.newsflash.Util.DividerItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by balra on 19-03-2018.
 */

public class HeadlinesFragment extends Fragment implements NewsAdapter.OnNewsArticleClickListener {

    @BindView(R.id.news_detail_rv)
    RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    @BindView(R.id.search_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.nsv_search)
    NestedScrollView nestedScrollView;
    private Parcelable parcelable;
    public HeadlinesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.headlines_fragment, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), R.drawable.divider,
                        false, false));
        newsAdapter = new NewsAdapter(getContext(), this, new ArrayList<NewsArticle>());
        newsRecyclerView.setAdapter(newsAdapter);
        newsRecyclerView.setHasFixedSize(true);
        nestedScrollView.setSmoothScrollingEnabled(true);
        newsRecyclerView.setNestedScrollingEnabled(false);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_fall_down);

        newsRecyclerView.setLayoutAnimation(controller);
        if(savedInstanceState != null) {
            List<NewsArticle> list = savedInstanceState.getParcelableArrayList("list");
            newsAdapter.setArticleList(list);
            parcelable = savedInstanceState.getParcelable("state");
            newsRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loadHeadlines();
        }
        return view;
    }

    private void loadHeadlines() {
        NewsApiInterface newsApiInterface = RetrofitBuilder.getNewsClient().create(NewsApiInterface.class);
        final Call<Article> articleCall = newsApiInterface.getHeadlines(null, "en", null, SearchFragment.API_KEY);
        articleCall.enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article article = response.body();
                List<NewsArticle> newsArticles = article.getArticles();
                newsAdapter.setArticleList(newsArticles);
                newsRecyclerView.setAdapter(newsAdapter);
                newsRecyclerView.scheduleLayoutAnimation();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                Log.e("Err", "Error");
                Log.e("Err", t.getMessage());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        parcelable = newsRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onNewsArticleClicked(NewsArticle clickedArticle) {
        Toast.makeText(getContext(), "Wow Hold Your Horses", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("state", parcelable);
        outState.putParcelableArrayList("list", (ArrayList<NewsArticle>)newsAdapter.getArticleList());
    }

}
