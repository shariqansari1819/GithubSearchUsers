package com.codebosses.githubsearchusers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codebosses.githubsearchusers.R;
import com.codebosses.githubsearchusers.databinding.RowUsersBinding;
import com.codebosses.githubsearchusers.pojo.user.UserData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<UserData> userDataList = new ArrayList<>();
    private UserClickListener userClickListener;

    public UserAdapter(Context context, List<UserData> userDataList) {
        this.context = context;
        this.userDataList = userDataList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setUserClickListener(UserClickListener userClickListener) {
        this.userClickListener = userClickListener;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowUsersBinding rowUsersBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_users, parent, false);
        return new UserHolder(rowUsersBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.rowUsersBinding.setUser(userDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {

        RowUsersBinding rowUsersBinding;

        public UserHolder(@NonNull RowUsersBinding rowUsersBinding) {
            super(rowUsersBinding.getRoot());
            this.rowUsersBinding = rowUsersBinding;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userClickListener != null) {
                        userClickListener.onUserClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface UserClickListener {
        public void onUserClick(View view, int position);
    }

}
