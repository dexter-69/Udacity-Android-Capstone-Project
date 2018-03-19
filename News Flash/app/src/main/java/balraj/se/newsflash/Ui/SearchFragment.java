package balraj.se.newsflash.Ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;


import com.mancj.materialsearchbar.MaterialSearchBar;

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

public class SearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener, NewsAdapter.OnNewsArticleClickListener {
    public SearchFragment() {}
    @BindView(R.id.news_detail_rv)
    RecyclerView searchNewsRv;
    private NewsAdapter newsAdapter;
    private LinearLayoutManager layoutManager;
    private static final String API_KEY = "bb77c20d92ea45c78344ae9abd560d16";
    @BindView(R.id.search_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.nsv_search)
    NestedScrollView nestedScrollView;
    private Parcelable parcelable;

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
        MaterialSearchBar materialSearchBar = view.findViewById(R.id.searchBar);
        materialSearchBar.setOnSearchActionListener(this);
        layoutManager = new LinearLayoutManager(getContext());
        searchNewsRv.setLayoutManager(layoutManager);
        searchNewsRv.addItemDecoration(
                new DividerItemDecoration(getActivity(), R.drawable.divider,
                        false, false));
        newsAdapter = new NewsAdapter(getContext(), this, new ArrayList<NewsArticle>());
        searchNewsRv.setAdapter(newsAdapter);
        searchNewsRv.setHasFixedSize(true);
        nestedScrollView.setSmoothScrollingEnabled(true);
        searchNewsRv.setNestedScrollingEnabled(false);
        if(savedInstanceState != null) {
            List<NewsArticle> list = savedInstanceState.getParcelableArrayList("list");
            newsAdapter.setArticleList(list);
            parcelable = savedInstanceState.getParcelable("state");
            searchNewsRv.getLayoutManager().onRestoreInstanceState(parcelable);
        }
        return view;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        startSearch(text.toString());
    }

    private void startSearch(String query) {
        Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        NewsApiInterface newsApiInterface = RetrofitBuilder.getNewsClient().create(NewsApiInterface.class);
        final Call<Article> articleCall = newsApiInterface.searchArticle(query, API_KEY);
        articleCall.enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article article = response.body();
                List<NewsArticle> newsArticles = article.getArticles();
                newsAdapter.setArticleList(newsArticles);
                searchNewsRv.setAdapter(newsAdapter);
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
    public void onButtonClicked(int buttonCode) {
    }


    @Override
    public void onNewsArticleClicked(NewsArticle clickedArticle) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("state", parcelable);
        outState.putParcelableArrayList("list", (ArrayList<NewsArticle>)newsAdapter.getArticleList());
    }
}
