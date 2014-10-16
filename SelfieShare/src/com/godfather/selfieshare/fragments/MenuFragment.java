package com.godfather.selfieshare.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.godfather.selfieshare.AppMain;
import com.godfather.selfieshare.R;
import com.godfather.selfieshare.activities.HomeActivity;
import com.godfather.selfieshare.activities.NearByActivity;
import com.godfather.selfieshare.activities.ReceivedActivity;
import com.godfather.selfieshare.activities.RequestedActivity;
import com.godfather.selfieshare.activities.SettingsActivity;

public class MenuFragment extends Fragment implements OnClickListener {
	protected AppMain appMain;
	private Button menuHome;
	private Button menuNearBy;
	private Button menuRequested;
	private Button menuReceived;
	private Button menuSettings;
	private View view;

	private void initializeComponents(View view) {

		this.menuHome = (Button) view.findViewById(R.id.menuHome);
		this.menuNearBy = (Button) view.findViewById(R.id.menuNearBy);
		this.menuRequested = (Button) view.findViewById(R.id.menuRequested);
		this.menuReceived = (Button) view.findViewById(R.id.menuReceived);
		this.menuSettings = (Button) view.findViewById(R.id.menuSettings);

		this.menuHome.setOnClickListener(this);
		this.menuNearBy.setOnClickListener(this);
		this.menuRequested.setOnClickListener(this);
		this.menuReceived.setOnClickListener(this);
		this.menuSettings.setOnClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_menu, container, false);
		initializeComponents(view);
		return view;
	}

	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_home);
	}

	protected int getActivityLayout() {
		return R.layout.activity_home;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.menuHome:
			this.changeActivity(HomeActivity.class);
			break;
		case R.id.menuNearBy:
			this.changeActivity(NearByActivity.class);
			break;
		case R.id.menuRequested:
			this.changeActivity(RequestedActivity.class);
			break;

		case R.id.menuReceived:
			this.changeActivity(ReceivedActivity.class);
			break;
		case R.id.menuSettings:
			this.changeActivity(SettingsActivity.class);
			break;
		}
	}

	public void changeActivity(Class<?> toActivity) {
		Context context = getActivity().getApplicationContext();
		Intent intent = new Intent(context, toActivity);
		startActivity(intent);
	}
}
