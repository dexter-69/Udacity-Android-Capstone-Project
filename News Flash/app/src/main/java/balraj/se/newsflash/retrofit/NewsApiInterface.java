package balraj.se.newsflash.retrofit;

import balraj.se.newsflash.model.Article;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by balra on 19-03-2018.
 */

public interface NewsApiInterface {

    @GET("top-headlines")
    Call<Article> getHeadlines(@Query("q") String query, @Query("language") String language,
                               @Query("pageSize") int pageSize, @Query("category") String category, @Query("apiKey") String apiKey);

    @GET("everything")
    Call<Article> searchArticle(@Query("q") String query,
                                @Query("apiKey") String apiKey);

}
