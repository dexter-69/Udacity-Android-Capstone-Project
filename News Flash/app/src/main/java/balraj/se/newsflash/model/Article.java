package balraj.se.newsflash.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by balra on 19-03-2018.
 */

public class Article implements Parcelable {
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    private List<NewsArticle> articles;

    protected Article(Parcel in) {
        in.readList(articles, NewsArticle.class.getClassLoader());
    }

    public List<balraj.se.newsflash.model.NewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articles = articles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(articles);
    }
}
