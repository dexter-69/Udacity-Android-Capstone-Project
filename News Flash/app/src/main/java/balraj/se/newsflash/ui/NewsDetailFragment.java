package balraj.se.newsflash.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import balraj.se.newsflash.R;
import balraj.se.newsflash.model.NewsArticle;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by balra on 27-03-2018.
 */

public class NewsDetailFragment extends Fragment {
    @BindView(R.id.news_detail_image_Iv)
    ImageView newsImageView;
    @BindView(R.id.news_detail_title_tv)
    TextView newsTitleTextView;
    @BindView(R.id.news_detail_desc_tv)
    TextView newsDescriptionTv;
    @BindView(R.id.news_detail_source_tv)
    TextView newsSourceTv;
    @BindView(R.id.news_detail_published)
    TextView newsPublishedDateTv;
    @BindView(R.id.news_detail_url_tv)
    TextView newsUrlTv;
    private NewsArticle mNewsArticle;

    public NewsDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();


        if (args.containsKey(HeadlinesFragment.ARTICLE_KEY)) {
            mNewsArticle = args.getParcelable(HeadlinesFragment.ARTICLE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_detail_content, container, false);
        ButterKnife.bind(this, view);
        if (mNewsArticle != null) {
            setContentInViews();
        }
        if (getActivity() instanceof NewsDetailActivity) {
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            if (toolbar != null) {
                ((NewsDetailActivity) getActivity()).setSupportActionBar(toolbar);
                ActionBar actionBar = ((NewsDetailActivity) getActivity()).getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("");
            }
        }
        return view;
    }

    private void setContentInViews() {
        newsTitleTextView.setText(mNewsArticle.getTitle());
        setTextView(newsDescriptionTv, mNewsArticle.getDescription(), false, -1);
        setTextView(newsSourceTv, mNewsArticle.getSource().getName(), true,
                R.string.news_article_source);
        String publishedDate = mNewsArticle.getPublishedAt();
        if (!isEmpty(publishedDate) && publishedDate.length() >= 10) {
            setTextView(newsPublishedDateTv, publishedDate.substring(0, 10), true, R.string.published_date);
        } else newsPublishedDateTv.setVisibility(View.GONE);

        setUrl();
        setNewsThumbnail(mNewsArticle.getUrlToImage());
    }

    private void setUrl() {
        if (isEmpty(mNewsArticle.getUrl()))
            newsUrlTv.setVisibility(View.GONE);

        String source = "<a href=" + mNewsArticle.getUrl() + ">Here</a>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newsUrlTv.setText(Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT));
        } else {
            newsUrlTv.setText(Html.fromHtml(source));
        }

        newsUrlTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private boolean isEmpty(String description) {
        return TextUtils.isEmpty(description);
    }

    private void setTextView(TextView textView, String text, boolean format, int resId) {
        if (isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            if (format)
                format(textView, text, resId);
            else {
                textView.setText(text);
            }
        }
    }


    @SuppressLint("CheckResult")
    private void setNewsThumbnail(String imageUrl) {
        Context context = getContext();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(ContextCompat.getDrawable(context, R.drawable.news_fallback_drawable));
        requestOptions.fallback(ContextCompat.getDrawable(context, R.drawable.news_fallback_drawable));
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(newsImageView);
    }

    private void format(TextView textView, String text, int resId) {
        String formattedText = getString(resId, text);
        textView.setText(formattedText);
    }
}


