package com.godfather.selfieshare.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.models.SelfieUser;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;


public class UserAdapter extends ArrayAdapter<SelfieUser> {
    private Context context;
    private int layoutResourceId;
    private List<SelfieUser> users;

    public UserAdapter(Context context, int layoutResourceId, List<SelfieUser> users) {
        super(context, layoutResourceId, users);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;

//        Log.d("AndroidSandbox", "getView for position -> " + position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UserHolder();
            holder.userImage = (ImageView) row.findViewById(R.id.lvi_userImage);
            holder.postText = (TextView) row.findViewById(R.id.lvi_postText);
            holder.userName = (TextView) row.findViewById(R.id.lvi_userName);
            holder.postCreateDate = (TextView) row.findViewById(R.id.lvi_createDate);

            row.setTag(holder);
        } else {
            holder = (UserHolder) row.getTag();
        }

        SelfieUser user = this.users.get(position);
        if (user != null) {
            int imageId = user.getSex().trim().equals("Male") ? R.drawable.male : R.drawable.female; 
        	
        	holder.postText.setText(user.getUsername());
            holder.userImage.setBackgroundResource(imageId);
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            holder.postCreateDate.setText(dateFormat.format(user.getCreatedAt()).toUpperCase());
        }

        return row;
    }

    static class UserHolder {
        private ImageView userImage;
        private TextView postText;
        private TextView userName;
        private TextView postCreateDate;
    }
}