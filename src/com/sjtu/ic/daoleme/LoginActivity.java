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

public class LoginActivity extends Activity{
	
	private Button btnSubmitLogin=null;
	private String username=null;
	private String password=null;
	private String groups="";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		btnSubmitLogin=(Button)findViewById(R.id.submit_login);
		btnSubmitLogin.setOnClickListener(new SubmitLoginListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class SubmitLoginListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			EditText etUsername=(EditText)findViewById(R.id.login_username);
			username=etUsername.getText().toString();
			
			EditText etPassword=(EditText)findViewById(R.id.login_password);
			password=etPassword.getText().toString();
			
			FutureTask<Integer> future = new FutureTask<Integer>(submitLoginHandler);
			new Thread(future).start();
			try {
				int respCode=future.get();
				if(respCode==1){
					Intent intent=new Intent();
					intent.putExtra("username", username);
					if(!groups.equals(""))
						intent.putExtra("groups", groups);
					intent.setClass(LoginActivity.this, UserActivity.class);
					LoginActivity.this.startActivity(intent);
				}else{
					Toast toast = Toast.makeText( getApplicationContext() ,"用户名不存在或者密码错误",Toast.LENGTH_LONG);
					toast.show();
				}
			    }catch (Exception e) {
				Toast toast = Toast.makeText( getApplicationContext() ,"网络连接存在异常",Toast.LENGTH_LONG);
				toast.show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	Callable<Integer> submitLoginHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/login.py");
			connection =(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
			connection.connect();
			
			DataOutputStream out=new DataOutputStream(connection.getOutputStream());
			out.writeBytes("username="+username+"&password="+password+"\n");
			out.flush();
			out.close();
			
			//getResponse();
			BufferedReader br=null;
			br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String loginResp=br.readLine();
			connection.disconnect();
			if("Login OK!".equals(loginResp)){
				groups=br.readLine();
				return 1;				
			}
			else
				return 0;
			
		}
	};

}
