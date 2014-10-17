package com.godfather.selfieshare.fragments;

import java.util.ArrayList;

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
import com.godfather.selfieshare.activities.SelfieDetailActivity;
import com.godfather.selfieshare.activities.UserDetailActivity;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.Selfie;
import com.godfather.selfieshare.models.SelfieUser;
import com.godfather.selfieshare.utils.SelfieAdapter;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class ReceivedFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<Selfie> selfies;
    private SelfieAdapter selfieAdapter;

    private QueryExecutor queryExecutor;
    private ListView listView;

    public ArrayList<Selfie> getUsers() {
        return selfies;
    }

    public ArrayAdapter<Selfie> getSelfieAdapter() {
        return selfieAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_received,container, false);

        Context context = getActivity();

        this.queryExecutor = QueryExecutor.getInstance();

        this.listView = (ListView) rootView.findViewById(R.id.received_list_view);
        this.selfies = new ArrayList<Selfie>();
        this.selfieAdapter = new SelfieAdapter(context, R.layout.listview_item_row, selfies, false);

        this.listView.setAdapter(selfieAdapter);
        this.listView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.loadReceived(this.listView, this);
        }
    }

    private void loadReceived(final ListView target, final ReceivedFragment listActivity) {
        this.queryExecutor
                .getReceivedSelfies(new RequestResultCallbackAction<ArrayList<Selfie>>() {
                    @Override
                    public void invoke(RequestResult<ArrayList<Selfie>> requestResult) {
                        if (requestResult.getSuccess()) {
                            listActivity.getUsers().clear();
                            for (Selfie selfie : requestResult.getValue()) {
                                listActivity.getUsers().add(selfie);
                            }

                            target.post(new Runnable() {
                                @Override
                                public void run() {
                                    listActivity.getSelfieAdapter()
                                            .notifyDataSetChanged();
                                }
                            });
                        } else {
                            // TODO: Handle error
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Selfie selectedSelfie = (Selfie) parent.getAdapter().getItem(position);
        if (selectedSelfie != null) {
            Intent intent = new Intent(this.getActivity(), SelfieDetailActivity.class);
            intent.putExtra("selfie", selectedSelfie);
            startActivity(intent);
        }
    }
}
