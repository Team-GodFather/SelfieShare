package com.godfather.selfieshare.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.controllers.SettingsDataSource;
import com.godfather.selfieshare.models.Settings;

public class SettingsFragment extends Fragment implements OnClickListener {

	private SettingsDataSource datasource;
	private CheckBox male;
	private CheckBox female;
	private EditText minAge;
	private EditText maxAge;
	private Button settingSave;

	private ProgressDialog connectionProgressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);

		datasource = new SettingsDataSource(this.getActivity());
		datasource.open();

		this.connectionProgressDialog = new ProgressDialog(this.getActivity());
		this.connectionProgressDialog.setMessage("Saving...");

		Settings settings = datasource.getSettings();
		male = (CheckBox) rootView.findViewById(R.id.settingsMale);
		female = (CheckBox) rootView.findViewById(R.id.settingsFemale);
		minAge = (EditText) rootView.findViewById(R.id.settingsMinAge);
		maxAge = (EditText) rootView.findViewById(R.id.settingsMaxAge);
		settingSave = (Button) rootView.findViewById(R.id.settingSave);
		settingSave.setOnClickListener(this);

		boolean isMale = settings.getMale() == 1 ? true : false;
		boolean isFemale = settings.getFemale() == 1 ? true : false;

		male.setChecked(isMale);
		female.setChecked(isFemale);
		minAge.setText(String.valueOf(settings.getMinAge()));
		maxAge.setText(String.valueOf(settings.getMaxAge()));

		return rootView;
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
		int maleValue = male.isChecked() == true ? 1 : 0;
		int femaleValue = female.isChecked() == true ? 1 : 0;
		int minAgeValue = Integer.parseInt(minAge.getText().toString());
		int maxAgeValue = Integer.parseInt(maxAge.getText().toString());
		
		
		
		connectionProgressDialog.show();
		datasource.deleteSettings();
		datasource.createSettings(maleValue, femaleValue, minAgeValue,
				maxAgeValue);
		connectionProgressDialog.dismiss();
	}
}
