package balraj.se.newsflash.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import balraj.se.newsflash.Model.Article;
import balraj.se.newsflash.Model.NewsArticle;
import balraj.se.newsflash.Model.Source;
import balraj.se.newsflash.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by balra on 19-03-2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private OnNewsArticleClickListener onNewsArticleClickListener;
    private List<NewsArticle> articleList;

    public NewsAdapter(Context context, OnNewsArticleClickListener onNewsArticleClickListener,
                       List<NewsArticle> articleList) {
        this.context = context;
        this.onNewsArticleClickListener = onNewsArticleClickListener;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_list_content, parent,
                false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsArticle currentArticle = articleList.get(position);
        Source articleSource = currentArticle.getSource();
        holder.newsSourceTv.setText(articleSource.getName());
        holder.newsDatePubTv.setText(currentArticle.getPublishedAt());
        holder.newsTitleTv.setText(currentArticle.getTitle());
        setRecipeThumbnail(holder.newsThumbnaailIv, currentArticle.getUrlToImage());
    }

    @SuppressLint("CheckResult")
    private void setRecipeThumbnail(ImageView recipeThumnail, String imageUrl) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(ContextCompat.getDrawable(context, R.drawable.news_fallback_drawable));

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(recipeThumnail);
    }

    public List<NewsArticle> getArticleList() {return this.articleList;}
    @Override
    public int getItemCount() {
        return articleList == null ? 0 : articleList.size();
    }

    public void setArticleList(List<NewsArticle> articleList) {
        this.articleList = articleList;
        notifyDataSetChanged();
    }

    public interface OnNewsArticleClickListener {
        public void onNewsArticleClicked(NewsArticle clickedArticle);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.news_article_thumbnail_iv)
        ImageView newsThumbnaailIv;
        @BindView(R.id.news_title)
        TextView newsTitleTv;
        @BindView(R.id.news_source_tv)
        TextView newsSourceTv;
        @BindView(R.id.news_date_published_tv)
        TextView newsDatePubTv;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onNewsArticleClickListener.onNewsArticleClicked(articleList.get(position));
        }
    }
}
