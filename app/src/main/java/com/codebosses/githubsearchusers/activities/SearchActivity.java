package com.codebosses.githubsearchusers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.codebosses.githubsearchusers.R;
import com.codebosses.githubsearchusers.adapter.UserAdapter;
import com.codebosses.githubsearchusers.databinding.ActivitySearchBinding;
import com.codebosses.githubsearchusers.endpoints.EndpointKeys;
import com.codebosses.githubsearchusers.pojo.user.UserData;
import com.codebosses.githubsearchusers.pojo.user.UserMainObject;
import com.codebosses.githubsearchusers.retrofit.RetrofitClient;
import com.codebosses.githubsearchusers.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements UserAdapter.UserClickListener {

    //    Android fields....
    private SearchView searchView;
    private ActivitySearchBinding activitySearchBinding;

    //    Adapter fields....
    private UserAdapter userAdapter;
    private List<UserData> userDataList = new ArrayList<>();

    //    Retrofit fields....
    private Call<UserMainObject> userMainObjectCall;
    private RetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);

//        Setting custom action bar....
        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.toolbar_title);
        }

//        Setting adapter....
        userAdapter = new UserAdapter(this, userDataList);
        userAdapter.setUserClickListener(this);
        activitySearchBinding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        activitySearchBinding.recyclerViewSearch.setItemAnimator(new DefaultItemAnimator());
        activitySearchBinding.recyclerViewSearch.setAdapter(userAdapter);

//        Retrofit instance....
        retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchUser(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userMainObjectCall != null && userMainObjectCall.isExecuted()) {
            userMainObjectCall.cancel();
        }
    }

    private void searchUser(String user) {
        activitySearchBinding.progressBarSearch.setVisibility(View.VISIBLE);
        userMainObjectCall = retrofitInterface.searchUser(user);
        userMainObjectCall.enqueue(new Callback<UserMainObject>() {
            @Override
            public void onResponse(Call<UserMainObject> call, Response<UserMainObject> response) {
                activitySearchBinding.progressBarSearch.setVisibility(View.GONE);
                if (response != null && response.isSuccessful()) {
                    userAdapter.notifyItemRangeRemoved(0, userDataList.size());
                    userDataList.clear();
                    if (response.body() != null) {
                        if (response.body().getItems().size() > 0) {
                            activitySearchBinding.textViewNoUserFound.setVisibility(View.GONE);
                            for (int i = 0; i < response.body().getItems().size(); i++) {
                                userDataList.add(response.body().getItems().get(i));
                                userAdapter.notifyItemInserted(i);
                            }
                        } else {
                            activitySearchBinding.textViewNoUserFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserMainObject> call, Throwable t) {
                activitySearchBinding.progressBarSearch.setVisibility(View.GONE);
                activitySearchBinding.textViewNoUserFound.setVisibility(View.VISIBLE);
                activitySearchBinding.textViewNoUserFound.setText(t.getMessage());
            }
        });
    }

    @Override
    public void onUserClick(View view, int position) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(EndpointKeys.USER_ID, userDataList.get(position).getId());
        intent.putExtra(EndpointKeys.USER_NAME, userDataList.get(position).getLogin());
        intent.putExtra(EndpointKeys.USER_AVATAR, userDataList.get(position).getAvatarUrl());
        startActivity(intent);
    }
}
