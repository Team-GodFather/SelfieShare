package com.godfather.selfieshare.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.models.Selfie;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;


public class SelfieAdapter extends ArrayAdapter<Selfie> {
    private Context context;
    private int layoutResourceId;
    private List<Selfie> selfies;

    public SelfieAdapter(Context context, int layoutResourceId, List<Selfie> selfies) {
        super(context, layoutResourceId, selfies);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.selfies = selfies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SelfieHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SelfieHolder();
            holder.userImage = (ImageView) row.findViewById(R.id.lvi_userImage);
            holder.postText = (TextView) row.findViewById(R.id.lvi_postText);
            holder.userName = (TextView) row.findViewById(R.id.lvi_userName);
            holder.postCreateDate = (TextView) row.findViewById(R.id.lvi_createDate);

            row.setTag(holder);
        } else {
            holder = (SelfieHolder) row.getTag();
        }

        Selfie selfie = this.selfies.get(position);
        if (selfie != null) {
        	// TODO: change list item
        	holder.postText.setText(selfie.getTo().toString());
            
        //    holder.userImage.setBackgroundResource(imageId);
            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
            holder.postCreateDate.setText(dateFormat.format(selfie.getCreatedAt()).toUpperCase());
        }

        return row;
    }

    static class SelfieHolder {
        private ImageView userImage;
        private TextView postText;
        private TextView userName;
        private TextView postCreateDate;
    }

    class CurrentRequestResultCallbackAction extends RequestResultCallbackAction {
        private SelfieHolder selfieHolder;
        private View parentView;

        CurrentRequestResultCallbackAction(SelfieHolder selfieHolder, View parentView) {
            this.selfieHolder = selfieHolder;
            this.parentView = parentView;
        }

        @Override
        public void invoke(RequestResult requestResult) {
            if (requestResult.getSuccess()) {
                final Selfie selfie = (Selfie) requestResult.getValue();

                final String userName = selfie.getTo().toString();
//                Log.d("AndroidSandbox", "get display nane for user -> " + user.getDisplayName());
                this.parentView.post(new Runnable() {
                    @Override
                    public void run() {
                    	selfieHolder.userName.setText(userName);
                    }
                });
              
            }
        }
    }
}