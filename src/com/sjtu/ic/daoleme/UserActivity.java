package com.sjtu.ic.daoleme;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity{
	private TextView tv=null;
	private ListView lv=null;
	private String username=null;
	private String groups=null;
	private String clickedGroup=null;
	private String groupGPS=null;
	private String locName=null;
	private String refreshedInfo=null;
	private Button btnCreateGroup=null;
	private Button btnRefreshUser=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		tv=(TextView)findViewById(R.id.my_username);
		lv=(ListView)findViewById(R.id.groups_list);
		Intent it=getIntent();
		username=it.getStringExtra("username");
		tv.setText(username);
		if(it.hasExtra("groups")){
			groups=it.getStringExtra("groups");
			String[] groupnames=groups.split(";");
			lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, groupnames));
			
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int arg2,long arg3){
	                clickedGroup=((TextView)arg1).getText().toString();
	                FutureTask<Integer> future = new FutureTask<Integer>(getGroupGPSHandler);
	    			new Thread(future).start();
	    			try{
		    			if(future.get()==1){
		    				String[] gpsdata=groupGPS.split(":");
		    				Double lon=Double.valueOf(gpsdata[0]);
		    				Double lat=Double.valueOf(gpsdata[1]);
		    				Intent intent=new Intent();
			                intent.putExtra("username", username);
			                intent.putExtra("groupname", clickedGroup);
			                intent.putExtra("locationname", locName);
			                intent.putExtra("longitude", lon);
			                intent.putExtra("latitude", lat);
			        		intent.setClass(UserActivity.this, GroupActivity.class);
			        		UserActivity.this.startActivity(intent);
						}else{
							Toast toast = Toast.makeText( getApplicationContext() ,"载入位置错误",Toast.LENGTH_LONG);
							toast.show();
						} 
	    			}catch (Exception e) {
						Toast toast = Toast.makeText( getApplicationContext() ,"网络连接存在异常",Toast.LENGTH_LONG);
						toast.show();
						// TODO Auto-generated catch block
						e.printStackTrace();
	    			}

	            }
			});
		}
		btnRefreshUser=(Button)findViewById(R.id.refresh_user);
		btnRefreshUser.setOnClickListener(new refreshUserListener());
		btnCreateGroup=(Button)findViewById(R.id.create_group);
		btnCreateGroup.setOnClickListener(new createGroupListener());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class createGroupListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.putExtra("username", username);
    		intent.setClass(UserActivity.this, CreateGroupActivity.class);
    		UserActivity.this.startActivity(intent);
		}
	}
	
	class refreshUserListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			FutureTask<Integer> future = new FutureTask<Integer>(refreshUserHandler);
			new Thread(future).start();
			try {
				if(future.get()==1){
					String[] groups=refreshedInfo.split(";");
					lv.setAdapter(new ArrayAdapter<String>(UserActivity.this,android.R.layout.simple_expandable_list_item_1, groups));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast toast = Toast.makeText( getApplicationContext() ,"网络连接存在异常",Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
		}
	}
	
Callable<Integer> getGroupGPSHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/getgroupgps.py");
			connection =(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
			connection.connect();
			
			DataOutputStream out=new DataOutputStream(connection.getOutputStream());
			out.writeBytes("groupname="+clickedGroup+"\n");
			out.flush();
			out.close();
			
			//getResponse();
			BufferedReader br=null;
			br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String getGPSResp=br.readLine();
			connection.disconnect();
			if("Get Group GPS OK!".equals(getGPSResp)){
				groupGPS=br.readLine();
				locName=br.readLine();
				return 1;
			}else{
				return 0;
			}
			
		}
	};
	
Callable<Integer> refreshUserHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/refreshuser.py");
			connection =(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
			connection.connect();
			
			DataOutputStream out=new DataOutputStream(connection.getOutputStream());
			out.writeBytes("username="+username+"\n");
			out.flush();
			out.close();
			
			//getResponse();
			BufferedReader br=null;
			br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String refreshResp=br.readLine();
			connection.disconnect();
			if("Refresh User OK!".equals(refreshResp)){
				refreshedInfo=br.readLine();
				return 1;
			}else{
				return 0;
			}
			
		}
	};
	
	
}
