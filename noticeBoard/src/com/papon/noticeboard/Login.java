package com.papon.noticeboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
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

public class Login extends Activity {
	
	Spinner spinnerFrame;
	EditText edit_frame_pwd;
	Button  goButton;
	
	List<String> list_frame,list_frame_pwd,list_cnbid;
	String str_frame_pwd,str_alert_msg,str_alert_title;
	int int_frame_pos;
	
	InputStream inputStream=null;
	String result=null;
	String line=null;
	
	String IP_addres;
	
	private SharedPreferences prefs;
	private String prefName="report";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		int conn_check=prefs.getInt("connection", 100);
		//conn_check=1;
		
		if(conn_check==0)
		{
			AlertDialog.Builder alert_conn_error=new AlertDialog.Builder(Login.this);
			alert_conn_error.setMessage("check your internet connection...");
			alert_conn_error.setTitle("conncetin error");
			alert_conn_error.setPositiveButton("ok", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
					
				}
			});
			alert_conn_error.show();
			
		}
		else{
			
			initialise_variables();
			
			ArrayAdapter<String> adp=new ArrayAdapter<String>
			(this,android.R.layout.simple_expandable_list_item_1,list_frame);
			adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrame.setAdapter(adp);
			int_frame_pos=0;
			spinnerFrame.setSelection(int_frame_pos);
			str_frame_pwd=list_frame_pwd.get(int_frame_pos).toString();
			spinnerFrame.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							int_frame_pos=arg2;
							str_frame_pwd=list_frame_pwd.get(int_frame_pos).toString();
							
							
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}

					});
			
			goButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(edit_frame_pwd.getText().toString().equalsIgnoreCase(""))
					{
						str_alert_msg="enter password";
						str_alert_title="error";
						alert_method();
					}
					else {
						if(edit_frame_pwd.getText().toString().equals(str_frame_pwd))
						{
							if(int_frame_pos ==0)
							{
								save();
							}
							if(int_frame_pos ==1)
							{
								save();
							}
							if(int_frame_pos ==2)
							{
								save();
							}
							if(int_frame_pos ==3)
							{
								save();
							}
						}
						
						else {
							str_alert_msg="wrong password";
							str_alert_title="Error";
							alert_method();
						}
					}
					
				}
				private void save()
				{
					prefs=getSharedPreferences(prefName, MODE_PRIVATE);
					SharedPreferences.Editor editor=prefs.edit();
					editor.putInt("cnb_id",Integer.parseInt(list_cnbid.get(int_frame_pos).toString()));
					editor.commit();
					finish();
					
					Intent intent=new Intent(Login.this,User_login.class);
					startActivity(intent);
					edit_frame_pwd.setText(null);
				
				}
			});
			
		}
		
	}
	
	public void initialise_variables()
	{
		spinnerFrame=(Spinner) findViewById(R.id.spinner1);
		edit_frame_pwd=(EditText) findViewById(R.id.editText1);
		goButton=(Button) findViewById(R.id.button1);
		
		list_frame=new ArrayList<String>();
		list_frame_pwd=new ArrayList<String>();
		list_cnbid=new ArrayList<String>();
		
		prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		IP_addres=prefs.getString("ip", "http://www.paponsen.netii.net/");
		
		Database_NB_Frame();
	}
	
	public void Database_NB_Frame()
	{
		try {

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(IP_addres + "DB_CNB_Frame.php");
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_CNB_Frame",e.toString());
		}
		
		try {
			
			BufferedReader reader=new BufferedReader(new InputStreamReader(
					inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			while((line=reader.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			result=sb.toString();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_CNB_Frame",e.toString());
		}
		try {
			JSONArray jArray=new JSONArray(result);
			JSONObject jobj=null;
			
			for(int i=0;i<jArray.length();i++)
			{
				jobj=jArray.getJSONObject(i);
				
				list_frame.add(jobj.getString("cnb_name"));
				list_frame_pwd.add(jobj.getString("cnb_pwd"));
				list_cnbid.add(String.valueOf(jobj.getString("cnb_id")));
			}
			
			prefs=getSharedPreferences(prefName, MODE_PRIVATE);
			SharedPreferences.Editor editor=prefs.edit();
			
			editor.putString("ip", IP_addres);
			editor.commit();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_CNB_Frame",e.toString());
		}
		
	
		
	}
	
	public void alert_method()
	{
		AlertDialog.Builder alert=new AlertDialog.Builder(Login.this);
		alert.setMessage(str_alert_msg);
		alert.setTitle(str_alert_title);
		alert.setPositiveButton("ok", null);
		alert.show();
		edit_frame_pwd.setText(null);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
