package balraj.se.newsflash.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import balraj.se.newsflash.R;
import balraj.se.newsflash.model.NewsArticle;
import balraj.se.newsflash.model.Source;
import balraj.se.newsflash.util.NewsDbUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by balra on 19-03-2018.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final OnNewsArticleClickListener onNewsArticleClickListener;
    private Context context;
    private List<NewsArticle> articleList;
    private boolean isOfflineFlag;
    private SnackbarNotify snackbarNotify;

    public NewsAdapter(Context context, OnNewsArticleClickListener onNewsArticleClickListener,
                       List<NewsArticle> articleList, SnackbarNotify snackbarNotify, boolean isOfflineFrag) {
        this.context = context;
        this.onNewsArticleClickListener = onNewsArticleClickListener;
        this.articleList = articleList;
        this.isOfflineFlag = isOfflineFrag;
        this.snackbarNotify = snackbarNotify;
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

        String publishedDate = currentArticle.getPublishedAt();
        if (!TextUtils.isEmpty(publishedDate) && publishedDate.length() >= 10) {
            holder.newsDatePubTv.setText(publishedDate.substring(0, 10));
        } else {
            holder.newsDatePubTv.setText(currentArticle.getPublishedAt());
        }
        holder.newsTitleTv.setText(currentArticle.getTitle());
        setNewsThumbnail(holder.newsThumbnaailIv, currentArticle.getUrlToImage());

        setUpPopupMenu(holder.cardOptionsTv, currentArticle);
    }

    private void setUpPopupMenu(final TextView cardOptionsTv, final NewsArticle article) {
        cardOptionsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menuId = isOfflineFlag ? R.menu.ofline_news_card_options : R.menu.news_card_options;
                PopupMenu popupMenu = new PopupMenu(context, cardOptionsTv);
                popupMenu.inflate(menuId);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.save_offline:
                                if(NewsDbUtils.isAlreadyPresent(context, article)) {
                                    snackbarNotify.notifyAndMakeSnackbar(context.getString(R.string.already_present));
                                } else {
                                    NewsDbUtils.saveArticleToDb(context, article);
                                    snackbarNotify.notifyAndMakeSnackbar(context.getString(R.string.saved_message));
                                }
                                return true;
                            case R.id.share_news:
                                generateSharedIntent(article);
                                return true;
                            case R.id.remove_news:
                                NewsDbUtils.deleteNewsArticleFromDatabase(context, article);
                                deleteFromList(article);
                                snackbarNotify.notifyAndMakeSnackbar(context.getString(R.string.removed_message));
                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }


    private void deleteFromList(NewsArticle article) {
        articleList.remove(article);
        notifyDataSetChanged();
    }

    private void generateSharedIntent(NewsArticle article) {
        Intent sharedIntent = new Intent(Intent.ACTION_SEND);
        sharedIntent.setType("text/plain");
        sharedIntent.putExtra(Intent.EXTRA_SUBJECT,
                article.getTitle());
        sharedIntent.putExtra(Intent.EXTRA_TEXT, article.getUrl());
        context.startActivity(Intent.createChooser(sharedIntent, context.getString(R.string.share_news)));
    }

    @SuppressLint("CheckResult")
    private void setNewsThumbnail(ImageView recipeThumnail, String imageUrl) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(ContextCompat.getDrawable(context, R.drawable.news_fallback_drawable));
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(recipeThumnail);
    }

    public List<NewsArticle> getArticleList() {
        return this.articleList;
    }

    public void setArticleList(List<NewsArticle> articleList) {
        this.articleList = articleList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return articleList == null ? 0 : articleList.size();
    }

    private void presentSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public interface SnackbarNotify {
        void notifyAndMakeSnackbar(String message);
    }

    public interface OnNewsArticleClickListener {
        void onNewsArticleClicked(NewsArticle clickedArticle);
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
        @BindView(R.id.news_card_option_tv)
        TextView cardOptionsTv;

        NewsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onNewsArticleClickListener.onNewsArticleClicked(articleList.get(position));
        }
    }
}
