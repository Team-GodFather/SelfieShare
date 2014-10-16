package com.godfather.selfieshare.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.SelfieUser;
import com.godfather.selfieshare.utils.UserAdapter;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

import java.util.ArrayList;

public class NearByFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<SelfieUser> users;
    private UserAdapter usersAdapter;

    private Message message;
    private QueryExecutor queryExecutor;
    private ProgressDialog connectionProgressDialog;

    public ArrayList<SelfieUser> getUsers() {
        return users;
    }

    public ArrayAdapter<SelfieUser> getUsersAdapter() {
        return usersAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        Context context = getActivity();

//        this.message = new Message(context);
//        this.queryExecutor = QueryExecutor.getInstance();
//        this.connectionProgressDialog = new ProgressDialog(context);
//        this.connectionProgressDialog.setMessage("Logging out...");
//
//        ListView listView = (ListView) getView().findViewById(R.id.li_listView);
//        this.users = new ArrayList<SelfieUser>();
//        this.usersAdapter = new UserAdapter(context, R.layout.listview_item_row, users);
//
//        listView.setAdapter(usersAdapter);
//
//        this.loadUsers(listView, this);
//
//        listView.setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_near_by, container, false);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelfieUser selectedPost = (SelfieUser) parent.getAdapter().getItem(position);
        if (selectedPost != null) {
            //TODO: send selfie
//	                    BaseViewModel.getInstance().setSelectedPost(selectedPost);
//	                    Intent i = new Intent(getBaseContext(), DetailViewActivity.class);
//	                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//	                    startActivity(i);
        }
    }

    private void loadUsers(final ListView target, final NearByFragment listActivity) {
        this.queryExecutor.getAllUserNames(new RequestResultCallbackAction<ArrayList<SelfieUser>>() {
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

                }
            }
        });
    }
}
