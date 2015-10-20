package com.ashish.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends AppCompatActivity {

    private RecyclerView mListView;
    private TextView mEmptyView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<NewsItem> mDataset;
    private NewsListAdapter mAdapter;
    private NewsApiTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        mListView = (RecyclerView) findViewById(R.id.list);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int visibleItemCount, totalItemCount, firstVisibleItem;
            private int previousTotal = 0;
            private final int visibleBuffer = 5;
            private boolean loading = true;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                } else if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleBuffer)) {
                    loading = true;

                    mTask = new NewsApiTask() {
                        @Override
                        protected void onPostExecute(String response) {
                            if (response == null) {
                                Toast.makeText(NewsListActivity.this, "Error loading news, check your connection.", Toast.LENGTH_SHORT).show();
                            }

                            Log.i("INFO", response);

                            try {
                                mDataset.addAll(parseJSON(response));
                            } catch (JSONException ex) {
                                //do something
                            }

                            mAdapter.notifyDataSetChanged();
                        }

                    };

                    mTask.execute(totalItemCount);
                }
            }
        });

        if (savedInstanceState == null) {
            mTask = new NewsApiTask() {
                @Override
                protected void onPostExecute(String response) {
                    if (response == null) {
                        Toast.makeText(NewsListActivity.this, "Error loading news, check your connection.", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("INFO", response);

                    try {
                        mDataset = parseJSON(response);
                    } catch (JSONException ex) {
                        //do something
                    }

                    mAdapter = new NewsListAdapter(NewsListActivity.this, mDataset);

                    if (mDataset.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        mListView.setAdapter(mAdapter);
                    }
                }
            };

            mTask.execute(0);
        } else {
            mDataset = savedInstanceState.getParcelableArrayList("dataset");
            mListView.setAdapter(new NewsListAdapter(this, mDataset));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("dataset", mDataset);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTask.cancel(true);
    }

    private ArrayList<NewsItem> parseJSON(String json) throws JSONException {
        ArrayList<NewsItem> list = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.getInt("num_results") > 0) {
            JSONArray resultArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject articleInfo = resultArray.getJSONObject(i);
                NewsItem item = new NewsItem();
                item.mUrl = articleInfo.getString("url");
                item.mTitle = Html.fromHtml(articleInfo.getString("title")).toString();
                item.mAuthor = Html.fromHtml(articleInfo.getString("byline")).toString();
                item.mAbstract = Html.fromHtml(articleInfo.getString("abstract")).toString();

                //media token might not be array if there are no images
                JSONArray mediaArray = articleInfo.optJSONArray("media");
                if (mediaArray != null && mediaArray.length() > 0) {
                    JSONArray mediaSubArray = mediaArray.getJSONObject(0).getJSONArray("media-metadata");

                    for (int j = 0; j < mediaSubArray.length(); j++) {
                        JSONObject mediaMetadata = mediaSubArray.getJSONObject(j);
                        if (mediaMetadata.getString("format").equals("Standard Thumbnail")) {
                            item.mImageUrl = mediaMetadata.getString("url");
                            break;
                        }
                    }
                }

                list.add(item);
            }
        }

        return list;
    }
}
