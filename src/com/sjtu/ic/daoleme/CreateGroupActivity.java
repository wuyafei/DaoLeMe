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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGroupActivity extends Activity{
	private String groupname=null;
	private String location=null;
	private String grouptime=null;
	private String username=null;
	
	private EditText etGroupName=null;
	private EditText etLocation=null;
	private EditText etTime=null;
	private Button btnSubmitCreateGroup=null;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		etGroupName=(EditText)findViewById(R.id.group_to_create);
		etLocation=(EditText)findViewById(R.id.group_location);
		etTime=(EditText)findViewById(R.id.group_time);
		Intent it=getIntent();
		username=it.getStringExtra("username");
		btnSubmitCreateGroup=(Button)findViewById(R.id.submit_create_group);
		btnSubmitCreateGroup.setOnClickListener(new submitCreateGroupListener());
		
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
    class submitCreateGroupListener implements android.view.View.OnClickListener{
    	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		groupname=etGroupName.getText().toString();
    		location=etLocation.getText().toString();
    		grouptime=etTime.getText().toString();
    		FutureTask<Integer> future = new FutureTask<Integer>(submitCrtGrpHandler);
			new Thread(future).start();
			try{
				if(future.get()==1){
					Intent intent=new Intent();
	                intent.putExtra("username", username);
	                intent.putExtra("groupname", groupname);
	        		intent.setClass(CreateGroupActivity.this, GroupActivity.class);
	        		CreateGroupActivity.this.startActivity(intent);
				}else{
					Toast toast = Toast.makeText( getApplicationContext() ,"组名已经存在",Toast.LENGTH_LONG);
					toast.show();
				}
			}catch(Exception e){
				Toast toast = Toast.makeText( getApplicationContext() ,"网络连接存在异常",Toast.LENGTH_LONG);
				toast.show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    
Callable<Integer> submitCrtGrpHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/creategroup.py");
			connection =(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
			connection.connect();
			
			DataOutputStream out=new DataOutputStream(connection.getOutputStream());
			out.writeBytes("username="+username+"&groupname="+groupname+"&location="+location+"&time="+grouptime+"\n");
			out.flush();
			out.close();
			
			//getResponse();
			BufferedReader br=null;
			br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String crtGrpResp=br.readLine();
			connection.disconnect();
			if("Create Group OK!".equals(crtGrpResp)){
				return 1;				
			}
			else
				return 0;
			
		}
	};
}
