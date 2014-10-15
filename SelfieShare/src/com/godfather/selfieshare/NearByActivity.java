package com.godfather.selfieshare;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.SelfieUser;
import com.godfather.selfieshare.utils.UserAdapter;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;


public class NearByActivity extends BaseActivity {
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
		protected void onCreate() {
			this.message = new Message(this);
			this.queryExecutor = QueryExecutor.getInstance();
			this.connectionProgressDialog = new ProgressDialog(this);
			this.connectionProgressDialog.setMessage("Logging out...");
			

	        ListView listView = (ListView) findViewById(R.id.li_listView);
	        this.users = new ArrayList<SelfieUser>();
	        this.usersAdapter = new UserAdapter(this, R.layout.listview_item_row, users);

	        listView.setAdapter(usersAdapter);

	        ActionBar actionBar = getActionBar();
	        actionBar.setDisplayShowHomeEnabled(false);
	        actionBar.setDisplayShowTitleEnabled(false);
	        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 52, 73, 94)));

	        this.loadUsers(listView, this);

	        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
	        });
			
		}
		
		private void loadUsers(final ListView target, final NearByActivity listActivity) {
	        this.queryExecutor.getAllUsernames(new RequestResultCallbackAction<ArrayList<SelfieUser>>() {
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
		

	
	
	@Override
	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_near_by);
	}

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_near_by;
	}
}
