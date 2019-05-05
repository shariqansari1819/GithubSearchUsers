package com.codebosses.githubsearchusers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codebosses.githubsearchusers.R;
import com.codebosses.githubsearchusers.databinding.ActivityUserDetailBinding;
import com.codebosses.githubsearchusers.endpoints.EndpointKeys;
import com.codebosses.githubsearchusers.pojo.user_detail.UserDetailData;
import com.codebosses.githubsearchusers.retrofit.RetrofitClient;
import com.codebosses.githubsearchusers.retrofit.RetrofitInterface;

public class UserDetailActivity extends AppCompatActivity {

    //    Android fields....
    ActivityUserDetailBinding userDetailBinding;
    private ClickHandler clickHandler;

    //    Retrofit fields....
    Call<UserDetailData> userDetailCall;
    RetrofitInterface retrofitInterface;

    //    Instance fields....
    private UserDetailData userDetailData;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail);

//        Setting custom action bar....
        Toolbar toolbar = findViewById(R.id.toolbarUserDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.user_detail);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        Getting data from previous intent....
        if (getIntent() != null) {
            Intent intent = getIntent();
            userName = intent.getStringExtra(EndpointKeys.USER_NAME);
            String profileImage = intent.getStringExtra(EndpointKeys.USER_AVATAR);

            Glide.with(this)
                    .load(profileImage)
                    .apply(new RequestOptions().circleCrop())
                    .apply(new RequestOptions().placeholder(R.drawable.boy_avatar))
                    .into(userDetailBinding.imageViewProfileUserDetail);

            //        Retrofit instance....
            retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            getUserDetail(userName);

        }

//        Setting click handler for data binding....
        clickHandler = new ClickHandler();
        userDetailBinding.setClickHandler(clickHandler);

    }

    private void getUserDetail(String name) {
        userDetailCall = retrofitInterface.getUserDetail(name);
        userDetailCall.enqueue(new Callback<UserDetailData>() {
            @Override
            public void onResponse(Call<UserDetailData> call, Response<UserDetailData> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body() != null) {
                        userDetailData = response.body();
                        if (userDetailData.getName() != null)
                            userDetailBinding.textViewNameUserDetail.setText(response.body().getName());
                        if (userDetailData.getEmail() != null)
                            userDetailBinding.textViewEmailUserDetail.setText(response.body().getEmail());
                        userDetailBinding.textViewFollowers.setText(String.valueOf(userDetailData.getFollowers()));
                        userDetailBinding.textViewFollowing.setText(String.valueOf(userDetailData.getFollowing()));
                        userDetailBinding.textViewRepositories.setText(String.valueOf(userDetailData.getPublicRepos()));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDetailData> call, Throwable t) {
                Toast.makeText(UserDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDetailCall != null && userDetailCall.isExecuted()) {
            userDetailCall.cancel();
        }
    }

    public class ClickHandler {

        public void onFollowerClick(View view) {
            if (userName != null && !userName.isEmpty()) {
                Intent intent = new Intent(UserDetailActivity.this, FollowersActivity.class);
                intent.putExtra(EndpointKeys.USER_NAME, userName);
                startActivity(intent);
            }
        }

    }
}
