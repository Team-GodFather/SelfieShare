package com.godfather.selfieshare.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.models.Selfie;
import com.telerik.everlive.sdk.core.model.system.File;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;


public class SelfieAdapter extends ArrayAdapter<Selfie> {
    private Context context;
    private int layoutResourceId;
    private List<Selfie> selfies;
    private Boolean isRequested;

    public SelfieAdapter(Context context, int layoutResourceId, List<Selfie> selfies, Boolean isRequested) {
        super(context, layoutResourceId, selfies);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.selfies = selfies;
        this.isRequested = isRequested;
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
            String username = isRequested ? selfie.get_To().getUsername() : selfie.get_Owner().getUsername();

            holder.postText.setText(username);

            DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
            holder.postCreateDate.setText(dateFormat.format(selfie.getCreatedAt()).toUpperCase());


            String url = selfie.get_Picture().getUri();
            if (_imagesCache.containsKey(url)) {
                holder.userImage.setImageBitmap(_imagesCache.get(url));
            } else {
                new DownloadFileFromURL(holder).execute(url);
            }
        }

        return row;
    }

    static class SelfieHolder {
        private ImageView userImage;
        private TextView postText;
        private TextView userName;
        private TextView postCreateDate;
    }


    public static final ArrayMap<String, Bitmap> _imagesCache = new ArrayMap<String, Bitmap>();

    class DownloadFileFromURL extends AsyncTask<String, String, Bitmap> {
        SelfieHolder holder;

        public DownloadFileFromURL(SelfieHolder holder) {
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... f_url) {
            return downloadImage(f_url[0]);
        }

        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.holder.userImage.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;

            try {
                URL imageUrl = null;

                imageUrl = new URL(url);

                InputStream inputStream = null;

                inputStream = imageUrl.openStream();

                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            _imagesCache.put(url, bitmap);
            return bitmap;
        }
    }
}