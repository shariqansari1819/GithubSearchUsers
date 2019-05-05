package com.codebosses.githubsearchusers.retrofit;

import com.codebosses.githubsearchusers.pojo.user.UserData;
import com.codebosses.githubsearchusers.pojo.user.UserMainObject;
import com.codebosses.githubsearchusers.pojo.user_detail.UserDetailData;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("search/users")
    Call<UserMainObject> searchUser(@Query("q") String query);

    @GET("users/{name}")
    Call<UserDetailData> getUserDetail(@Path("name") String name);

    @GET("users/{name}/followers")
    Call<List<UserData>> getFollowers(@Path("name") String name);

}
