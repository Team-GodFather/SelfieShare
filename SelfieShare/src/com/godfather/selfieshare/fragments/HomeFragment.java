package com.godfather.selfieshare.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.data.QueryExecutor;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class HomeFragment extends Fragment {
	private TextView requested;
	private TextView received;
	private QueryExecutor queryExecutor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
        
		this.queryExecutor = QueryExecutor.getInstance();
		this.received = (TextView) rootView.findViewById(R.id.receivedSelfies);
		this.requested = (TextView) rootView.findViewById(R.id.requestedSelfies);

		setRequestedCount();
		setReceivedCount();
		
		return rootView;
	}

	public void setRequestedCount() {
		final Activity current = this.getActivity();
		this.queryExecutor
				.countRequestedSelfies(new RequestResultCallbackAction() {
					@Override
					public void invoke(final RequestResult requestResult) {

						current.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (requestResult.getSuccess()) {
									int count = (Integer) requestResult
											.getValue();
									requested.setText(Integer.toString(count));
								} else {
									// TODO: Handle error
								}
							}
						});

					}
				});
	}

	public void setReceivedCount() {
		final Activity current = this.getActivity();
		this.queryExecutor
				.countReceivedSelfies(new RequestResultCallbackAction() {
					@Override
					public void invoke(final RequestResult requestResult) {
						current.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (requestResult.getSuccess()) {
									int count = (Integer) requestResult
											.getValue();
									received.setText(Integer.toString(count));
								} else {
									// TODO: Handle error
								}
							}
						});

					}
				});
	}
}
