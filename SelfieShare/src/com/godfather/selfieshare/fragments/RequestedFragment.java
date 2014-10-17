package com.godfather.selfieshare.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.Selfie;
import com.godfather.selfieshare.utils.SelfieAdapter;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class RequestedFragment extends Fragment {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_requested, container, false);

		Context context = getActivity();

		this.queryExecutor = QueryExecutor.getInstance();

		this.listView = (ListView) rootView.findViewById(R.id.requested_list_view);
		this.selfies = new ArrayList<Selfie>();
		this.selfieAdapter = new SelfieAdapter(context, R.layout.listview_item_row, selfies, true);

		this.listView.setAdapter(selfieAdapter);

		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			this.loadRequests(this.listView, this);
		}
	}

	private void loadRequests(final ListView target, final RequestedFragment listActivity) {
		this.queryExecutor
				.getRequestedSelfies(new RequestResultCallbackAction<ArrayList<Selfie>>() {
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
}
