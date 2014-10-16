package com.godfather.selfieshare.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.activities.UserDetailActivity;
import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.SelfieUser;
import com.godfather.selfieshare.utils.UserAdapter;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

import java.util.ArrayList;

import org.w3c.dom.UserDataHandler;

public class NearByFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<SelfieUser> users;
    private UserAdapter usersAdapter;

    private Message message;
    private QueryExecutor queryExecutor;
    private ProgressDialog connectionProgressDialog;
    private ListView listView;

    public ArrayList<SelfieUser> getUsers() {
        return users;
    }

    public ArrayAdapter<SelfieUser> getUsersAdapter() {
        return usersAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_near_by, container, false);

        Context context = getActivity();

        this.message = new Message(context);
        this.queryExecutor = QueryExecutor.getInstance();
        this.connectionProgressDialog = new ProgressDialog(context);
        this.connectionProgressDialog.setMessage("Logging out...");

        this.listView = (ListView) rootView.findViewById(R.id.li_listView);
        this.users = new ArrayList<SelfieUser>();
        this.usersAdapter = new UserAdapter(context, R.layout.listview_item_row, users);

        this.listView.setAdapter(usersAdapter);
        this.loadUsers(this.listView, this);
        this.listView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.loadUsers(this.listView, this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelfieUser selectedUser = (SelfieUser) parent.getAdapter().getItem(position);
        if (selectedUser != null) {
        	Intent intent = new Intent(this.getActivity(), UserDetailActivity.class);
        	intent.putExtra("userId", selectedUser.getId().toString());
        	startActivityForResult(intent, 1);
        }
    }

    private void loadUsers(final ListView target, final NearByFragment listActivity) {
        this.queryExecutor.getNearByUsers(new RequestResultCallbackAction<ArrayList<SelfieUser>>() {
            @Override
            public void invoke(RequestResult<ArrayList<SelfieUser>> requestResult) {
                if (requestResult.getSuccess()) {
                    listActivity.getUsers().clear();
                    for (SelfieUser user : requestResult.getValue()) {
                        listActivity.getUsers().add(user);
                    }

                    target.post(new Runnable() {
                        @Override
                        public void run() {
                            listActivity.getUsersAdapter().notifyDataSetChanged();
                        }
                    });
                } else {
                    // TODO: Handle error
                }
            }
        });
    }
}
