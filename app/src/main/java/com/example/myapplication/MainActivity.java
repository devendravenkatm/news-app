package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Api Key
    private static String API_KEY = "43dc933f401c405c957c92c1f25cbf6b";

    // Setting the TAG for debugging purposes
    private static String TAG = "MainActivity";

    // Declaring the views
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    // Declaring an ArrayList of articles
    private ArrayList<NewsArticle> mArticleList;

    private ArticleAdapter mArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the Fast Android Networking Library
        AndroidNetworking.initialize(getApplicationContext());

        // Setting the JacksonParserFactory
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        // Assigning views to their IDs
        mProgressBar = findViewById(R.id.progressbar_id);
        mRecyclerView = findViewById(R.id.recyclerview_id);

        // Setting the RecyclerView layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initializing the ArrayList of articles
        mArticleList = new ArrayList<>();

        // Calling get_news_from_api()
        get_news_from_api();
    }

    public void get_news_from_api() {
        // Clearing the articles list before adding new ones
        mArticleList.clear();

        // Making a GET Request using Fast Android Networking Library
        AndroidNetworking.get("https://newsapi.org/v2/top-headlines")
                .addQueryParameter("country", "in")
                .addQueryParameter("apiKey", API_KEY)
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Disabling the progress bar
                        mProgressBar.setVisibility(View.GONE);

                        // Handling the response
                        try {
                            // Storing the response in a JSONArray
                            JSONArray articles = response.getJSONArray("articles");

                            // Looping through all the articles to access them individually
                            for (int j = 0; j < articles.length(); j++) {
                                // Accessing each article object in the JSONArray
                                JSONObject article = articles.getJSONObject(j);

                                // Initializing an empty NewsArticle object
                                NewsArticle currentArticle = new NewsArticle();

                                // Storing values of the article object properties
                                String author = article.getString("author");
                                String title = article.getString("title");
                                String description = article.getString("description");
                                String url = article.getString("url");
                                String urlToImage = article.getString("urlToImage");
                                String publishedAt = article.getString("publishedAt");
                                String content = article.getString("content");

                                // Setting the values of the NewsArticle object using the set methods
                                currentArticle.setAuthor(author);
                                currentArticle.setTitle(title);
                                currentArticle.setDescription(description);
                                currentArticle.setUrl(url);
                                currentArticle.setUrlToImage(urlToImage);
                                currentArticle.setPublishedAt(publishedAt);
                                currentArticle.setContent(content);

                                // Adding an article to the articles list
                                mArticleList.add(currentArticle);
                            }

                            // Setting the adapter
                            mArticleAdapter = new ArticleAdapter(getApplicationContext(), mArticleList);
                            mRecyclerView.setAdapter(mArticleAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Logging the JSONException to LogCat
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // Logging the error detail and response to LogCat
                        Log.d(TAG, "Error detail: " + error.getErrorDetail());
                        Log.d(TAG, "Error response: " + error.getResponse());
                    }
                });
    }
}
