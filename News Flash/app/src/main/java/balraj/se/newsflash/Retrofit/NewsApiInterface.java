package balraj.se.newsflash.Retrofit;

import java.util.List;

import balraj.se.newsflash.Model.Article;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by balra on 19-03-2018.
 */

public interface NewsApiInterface {

    @GET("top-headlines")
    Call<List<Article>> getHeadlines(@Query("q") String query, @Query("language") String language,
                                     @Query("category") String category, @Query("apiKey") String apiKey);

    @GET("everything")
    Call<List<Article>> searchArticle(@Query("q") String query, @Query("sortBy") String sortBy,
                                      @Query("apiKey") String apiKey);

}
