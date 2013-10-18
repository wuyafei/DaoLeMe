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

public class SignupActivity extends Activity{
	
	private Button btnSubmitSignup=null;
	private String username=null;
	private String password=null;
	private String password_repeated=null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		
		btnSubmitSignup=(Button)findViewById(R.id.submit_signup);
		btnSubmitSignup.setOnClickListener(new SubmitSignupListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class SubmitSignupListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			EditText etUsername=(EditText)findViewById(R.id.username);
			username=etUsername.getText().toString();
			
			EditText etPassword=(EditText)findViewById(R.id.password);
			password=etPassword.getText().toString();
			
			EditText etPasswordRepeat=(EditText)findViewById(R.id.password_repeat);
			password_repeated=etPasswordRepeat.getText().toString();
			
			if(!password.equals(password_repeated)){
				Toast toast = Toast.makeText( getApplicationContext() ,"两次输入的密码不同",Toast.LENGTH_LONG);
				toast.show();
			}else{
				FutureTask<Integer> future = new FutureTask<Integer>(submitSignupHandler);
				new Thread(future).start();  
				try {
					if(future.get()==1){
						Intent intent=new Intent();
						intent.putExtra("username", username);
						intent.setClass(SignupActivity.this, UserActivity.class);
						SignupActivity.this.startActivity(intent);
					}else{
						Toast toast = Toast.makeText( getApplicationContext() ,"用户名已经存在",Toast.LENGTH_LONG);
						toast.show();
					}
				} catch (Exception e) {
					Toast toast = Toast.makeText( getApplicationContext() ,"网络连接存在异常",Toast.LENGTH_LONG);
					toast.show();
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}

			}
		}
	}
	
	Callable<Integer> submitSignupHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/register.py");
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
			String signupResp=br.readLine();
			connection.disconnect();
			if("Signup OK!".equals(signupResp)){
				return 1;				
			}else{
				return 0;
			}
			
		}
	};


}
