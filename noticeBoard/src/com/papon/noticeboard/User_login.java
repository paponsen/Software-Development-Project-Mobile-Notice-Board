package com.papon.noticeboard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.DialogInterface.OnClickListener;

public class User_login extends Activity {
	
	private SharedPreferences prefs;
	private String prefName="report";
	
	Spinner spinnerFrame;
	EditText editTextPassword;
	Button loginButton;
	
	List<String> listFrame,listFramePassword,listUid;
	
	int intFramePos,intCnbId;
	String strFramePwd,strAlertMsg,strAlertTitle;
	
	InputStream inputStream=null;
	String resutl=null;
	String line=null;
	String ip_address;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		
		initialise_variables();
		
		spinnerFrame.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				intFramePos=arg2;
				strFramePwd=listFrame.get(intFramePos).toString();
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(editTextPassword.getText().toString().equalsIgnoreCase(""))
				{
					strAlertMsg="enter password!!!";
					strAlertTitle="Error";
					alert_method();
				}
				else {
					if(editTextPassword.getText().toString().equals(strFramePwd))
 {
						if (intFramePos == 0) {
							save();
						}
						if (intFramePos == 1) {
							save();
						}
						if (intFramePos == 2) {
							save();
						}
						if (intFramePos == 3) {
							save();
						}

					}
					else {
						strAlertMsg=strFramePwd+"\nEnter correct password";
						strAlertTitle="Error";
						alert_method();
					}
				}
				
			}
		});
		
		
	}
	
	private void save() {
		// TODO Auto-generated method stub
		
		prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		SharedPreferences.Editor editor=prefs.edit();
		
		editor.putString("uid", listUid.get(intFramePos).toString());
		editor.putString("uname", listFrame.get(intFramePos).toString());
		
		
		editor.commit();
		finish();
		
		Intent intent=new Intent(User_login.this,NoticeBoard.class);
		startActivity(intent);
		editTextPassword.setText(null);
	}
	


	private void initialise_variables() {
		// TODO Auto-generated method stub
		spinnerFrame=(Spinner) findViewById(R.id.spinner1);
		editTextPassword=(EditText) findViewById(R.id.editText1);
		loginButton=(Button) findViewById(R.id.button1);
		
		listFrame=new ArrayList<String>();
		listFramePassword=new ArrayList<String>();
		listUid=new ArrayList<String>();
		
		prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		intCnbId=prefs.getInt("cnb_id", 1);
		
		prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		ip_address=prefs.getString("ip", "http://www.paponsen.netii.net/");
		
		DB_ListFrame();
		
		intFramePos=0;
		ArrayAdapter<String> adp=new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,listFrame);
		spinnerFrame.setAdapter(adp);
		intFramePos=0;
		spinnerFrame.setSelection(intFramePos);
		strFramePwd=listFramePassword.get(intFramePos).toString();
		
		
	}
	
	public void DB_ListFrame()
	{
		ArrayList<NameValuePair> name=new ArrayList<NameValuePair>();
		
		name.add(new BasicNameValuePair("cnbid", String.valueOf(intCnbId)));
		
		try {
			
			HttpClient httpClient=new DefaultHttpClient();
			HttpPost httpPost=new HttpPost(ip_address + "DB_ListFrame.php");
			httpPost.setEntity(new UrlEncodedFormEntity(name));
			HttpResponse response=httpClient.execute(httpPost);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
		} catch (Exception e) {
			// TODO: handle exception
			
			Log.e("DB_ListFrame", e.toString());
		}
		
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			while((line=reader.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			resutl=sb.toString();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_ListFrame", e.toString());
		}
		try {
			
			JSONArray jArray=new JSONArray(resutl);
			JSONObject jObj=null;
			
			for(int i=0;i<jArray.length();i++)
			{
				jObj=jArray.getJSONObject(i);
				
				listFrame.add(jObj.getString("1"));
				listFramePassword.add(jObj.getString("2"));
				listUid.add(jObj.getString("0"));
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("", e.toString());
		}
		
	}
	
	public void alert_method()
	{
		AlertDialog.Builder alert=new AlertDialog.Builder(User_login.this);
		alert.setMessage(strAlertMsg);
		alert.setTitle(strAlertTitle);
		alert.setPositiveButton("OK", null);
		alert.show();
		editTextPassword.setText(null);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		
		Intent intent=new Intent(User_login.this,Login.class);
		startActivity(intent);

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.user_login, menu);
//		return true;
//	}

}
