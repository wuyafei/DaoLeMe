package com.sjtu.ic.daoleme;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button btnSignup=null;
	private Button btnLogin=null;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnSignup=(Button)findViewById(R.id.btn_signup);
		btnLogin=(Button)findViewById(R.id.btn_login);
		
		btnSignup.setOnClickListener(new SignupListener());
		btnLogin.setOnClickListener(new LoginListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class SignupListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
    		intent.setClass(MainActivity.this, SignupActivity.class);
    		MainActivity.this.startActivity(intent);
		}
	}
	
	class LoginListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
    		intent.setClass(MainActivity.this, LoginActivity.class);
    		MainActivity.this.startActivity(intent);
		}
	}

}
