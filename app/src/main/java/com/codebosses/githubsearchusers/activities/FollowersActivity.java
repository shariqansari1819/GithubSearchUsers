package com.codebosses.githubsearchusers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.codebosses.githubsearchusers.R;
import com.codebosses.githubsearchusers.adapter.UserAdapter;
import com.codebosses.githubsearchusers.databinding.ActivityFollowersBinding;
import com.codebosses.githubsearchusers.endpoints.EndpointKeys;
import com.codebosses.githubsearchusers.pojo.user.UserData;
import com.codebosses.githubsearchusers.pojo.user.UserMainObject;
import com.codebosses.githubsearchusers.retrofit.RetrofitClient;
import com.codebosses.githubsearchusers.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity implements UserAdapter.UserClickListener {

    //    Android fields....
    private ActivityFollowersBinding followersBinding;

    //    Retrofit fields....
    private RetrofitInterface retrofitInterface;
    private Call<List<UserData>> userMainObjectCall;

    //    Instance fields....
    private List<UserData> userDataList = new ArrayList<>();

    //    Adapter fields....
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        followersBinding = DataBindingUtil.setContentView(this, R.layout.activity_followers);

//        Setting custom action bar....
        Toolbar toolbar = findViewById(R.id.toolbarUserFollowers);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.followers);
        }

        userAdapter = new UserAdapter(this, userDataList);
        userAdapter.setUserClickListener(this);
        followersBinding.recyclerViewUserFollowers.setLayoutManager(new LinearLayoutManager(this));
        followersBinding.recyclerViewUserFollowers.setItemAnimator(new DefaultItemAnimator());
        followersBinding.recyclerViewUserFollowers.setAdapter(userAdapter);

//        Getting followers....
        if (getIntent() != null) {
            Intent intent = getIntent();
            String userName = intent.getStringExtra(EndpointKeys.USER_NAME);

//        Retrofit instance....
            retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            getFollowers(userName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userMainObjectCall != null && userMainObjectCall.isExecuted()) {
            userMainObjectCall.cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFollowers(String user) {
        followersBinding.progressBarUserFollowers.setVisibility(View.VISIBLE);
        userMainObjectCall = retrofitInterface.getFollowers(user);
        userMainObjectCall.enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                followersBinding.progressBarUserFollowers.setVisibility(View.GONE);
                if (response != null && response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().size() > 0) {
                            followersBinding.textViewNoFollowerFound.setVisibility(View.GONE);
                            userDataList.addAll(response.body());
                            userAdapter.notifyItemRangeChanged(0, userDataList.size());
                        } else {
                            followersBinding.textViewNoFollowerFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {
                followersBinding.progressBarUserFollowers.setVisibility(View.GONE);
                followersBinding.textViewNoFollowerFound.setVisibility(View.VISIBLE);
                followersBinding.textViewNoFollowerFound.setText(t.getMessage());
            }
        });
    }

    @Override
    public void onUserClick(View view, int position) {

    }

}
