package com.papon.noticeboard;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class NetConnection extends Activity {
	private SharedPreferences prefs;
	private String prefName="report";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(android.os.Build.VERSION.SDK_INT>9)
		{
			StrictMode.ThreadPolicy policy=
					new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		
		try {
			prefs=getSharedPreferences(prefName, MODE_PRIVATE);
			String net_ip=prefs.getString("ip", "http://www.paponsen.netii.net/");
			
			URL url=new URL(net_ip);
			executeUrl(url);
			SharedPreferences.Editor editor=prefs.edit();
			editor.putInt("connection", 1);
			editor.commit();
			
			finish();
			Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_LONG).show();
			startActivity(new Intent(NetConnection.this,Login.class));
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "check network connection"+"and IP address", Toast.LENGTH_LONG).show();
			
			SharedPreferences.Editor editor=prefs.edit();
			editor.putInt("connection", 0);
			editor.commit();
			
			finish();
			startActivity(new Intent(NetConnection.this,Login.class));
			
			
			
		}
	}

	private void executeUrl(URL desiredUrl) throws IOException {
		// TODO Auto-generated method stub
		
		HttpURLConnection connection=null;
		connection=(HttpURLConnection) desiredUrl.openConnection();
		connection.setReadTimeout(15*1000);
		connection.setConnectTimeout(3500);
		connection.setRequestMethod("GET");
		
		connection.connect();
		InputStream response=connection.getInputStream();
		Log.d("Response", response.toString());
		
		
		
	}

}
