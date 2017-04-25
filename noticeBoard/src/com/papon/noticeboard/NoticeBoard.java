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
import android.R.color;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class NoticeBoard extends Activity {
	
	private SharedPreferences prefs;
	private  String prefNameString="report";
	
	InputStream inputStream=null;
	String result=null;
	String line=null;
	
	RelativeLayout r1,relative,rl1[];
	LinearLayout linear1;
	ScrollView scroll;
	
	TextView text_owner;
	TextView text_user[],text_date[],text_msg[];
	View view_line[];
	CheckBox cb_admin,cb_hod,cb_staff,cb_student;
	EditText edit_msg;
	
	List<String> list_receiver_id;
	List<String> list_out_se_cnbid,list_out_se_uid,list_out_date,list_out_se_name,list_out_msg,list_out_msgid,list_out_tag;
	String se_name,se_cnb,str_tag,str_db_delete;
	
	String str_user_name,str_user_id,str_msg;
	String str_listname,str_listtag,str_listdt,str_listid;
	int int_cnbid,int_msgcount;
	
	
	String ip_address;
	
	AlertDialog.Builder alert,alert_update;
	String str_pre_msg;
	int int_pre_msgid;
	List<Integer> list_pre_tagList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_board);
		
		intialise_variables();
		db_get_out_count();
		db_userdata();
		
		rl1=new RelativeLayout[int_msgcount];
		text_user=new TextView[int_msgcount];
		text_date=new TextView[int_msgcount];
		text_msg=new TextView[int_msgcount];
		view_line=new View[int_msgcount];
		
		for(int i=0;i<int_msgcount;i++)
		{
			rl1[i]=new RelativeLayout(NoticeBoard.this);
			
			db_get_taglist(list_out_msgid.get(i).toString());
			
			db_get_senderdata(list_out_se_cnbid.get(i).toString(),list_out_se_uid.get(i).toString());
			
			str_listid="("+se_cnb+" : "+list_out_se_uid.get(i).toString()+")";
			
			str_listdt=list_out_date.get(i).toString();
			for(int x=0;x<list_out_tag.size();x++)
			{
				String dummy=list_out_tag.get(x);
				str_tag=str_tag.concat(dummy).concat(" ");
			}
			str_listname="<font color = 'red'>" +se_name+ "</font>" + " : " +list_out_msg.get(i).toString()+ "<br/><br/>" + "<font color = 'blue'>" +str_tag+ "</font>" + "<br/><br/>";
			
			str_tag="";
			list_out_tag.clear();
			
			rl1[i].setPadding(10, 10, 10, 0);
			
			text_user[i]=new TextView(NoticeBoard.this);
			text_user[i].setText(str_listid);
			text_user[i].setTextSize(15);
			RelativeLayout.LayoutParams param_textuser=new RelativeLayout.LayoutParams((int)LayoutParams.WRAP_CONTENT,(int)LayoutParams.WRAP_CONTENT);
			param_textuser.topMargin=20;
			text_user[i].setLayoutParams(param_textuser);
			rl1[i].addView(text_user[i]);
			
			
			text_date[i]=new TextView(NoticeBoard.this);
			text_date[i].setText(str_listdt);
			text_date[i].setTextSize(15);
			RelativeLayout.LayoutParams param_textdate=new RelativeLayout.LayoutParams((int)LayoutParams.WRAP_CONTENT,(int)LayoutParams.WRAP_CONTENT);
			param_textdate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			param_textdate.topMargin=20;
			text_date[i].setLayoutParams(param_textdate);
			rl1[i].addView(text_date[i]);
			
			
			text_msg[i]=new TextView(NoticeBoard.this);
			text_msg[i].setText(Html.fromHtml(str_listname));
			text_msg[i].setTextSize(14);
			RelativeLayout.LayoutParams param_text_userid=new RelativeLayout.LayoutParams((int)LayoutParams.MATCH_PARENT,(int)LayoutParams.WRAP_CONTENT);
			param_text_userid.topMargin=60;
			text_msg[i].setLayoutParams(param_text_userid);
			rl1[i].addView(text_msg[i]);
			
			view_line[i]=new View(NoticeBoard.this);
			RelativeLayout.LayoutParams params_line=new RelativeLayout.LayoutParams((int)LayoutParams.MATCH_PARENT,8);
			params_line.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			view_line[i].setLayoutParams(params_line);
			view_line[i].setBackgroundColor(Color.MAGENTA);
			rl1[i].addView(view_line[i]);
			
			linear1.addView(rl1[i]);
			
			
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		case R.id.home:
			finish();
			Intent intent1 = new Intent(NoticeBoard.this, Login.class);
			startActivity(intent1);
			return true;
		case R.id.logout:
			finish();
			Intent intent2 = new Intent(NoticeBoard.this, User_login.class);
			startActivity(intent2);
			return true;
		case R.id.update:
			update_msg();
			return true;
			
		case R.id.newmsg:
			alertbox_newmsg();
			return true;
			
		case R.id.refresh:
			finish();
			Intent intent3=new Intent(NoticeBoard.this,NoticeBoard.class);
			startActivity(intent3);
			return true;
			
		}
		return false;
	}
	
	public void update_msg()
	{
		
		alert=new AlertDialog.Builder(NoticeBoard.this);
		alert.setTitle("Update post");
		relative=new RelativeLayout(NoticeBoard.this);
		relative.setPadding(0, 0, 0, 45);
		
		edit_msg=new EditText(NoticeBoard.this);
		edit_msg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		RelativeLayout.LayoutParams param2=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		param2.topMargin=40;
		edit_msg.setLayoutParams(param2);
		
		
		cb_admin=new CheckBox(NoticeBoard.this);
		cb_admin.setText("Admin");
		cb_admin.setTextColor(Color.RED);
		RelativeLayout.LayoutParams param4=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param4.leftMargin=20;
		param4.topMargin=140;
		cb_admin.setLayoutParams(param4);
		
		cb_hod=new CheckBox(NoticeBoard.this);
		cb_hod.setText("HOD");
		cb_hod.setTextColor(Color.RED);
		RelativeLayout.LayoutParams param5=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param5.leftMargin=200;
		param5.topMargin=140;
		cb_hod.setLayoutParams(param5);
		
		cb_staff=new CheckBox(NoticeBoard.this);
		cb_staff.setText("Teacher");
		cb_staff.setTextColor(Color.GREEN);
		RelativeLayout.LayoutParams param6=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param6.leftMargin=20;
		param6.topMargin=200;
		cb_staff.setLayoutParams(param6);
		
		cb_student=new CheckBox(NoticeBoard.this);
		cb_student.setText("Student");
		cb_student.setTextColor(Color.GREEN);
		RelativeLayout.LayoutParams param7=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param7.leftMargin=180;
		param7.topMargin=200;
		cb_student.setLayoutParams(param7);
		
		
//		Button but_newmsg=new Button(NoticeBoard.this);
//		but_newmsg.setText("Update");
//		RelativeLayout.LayoutParams param3=new RelativeLayout.LayoutParams(150,LayoutParams.WRAP_CONTENT);
//		param3.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		param3.topMargin=330;
//		but_newmsg.setLayoutParams(param3);
		
		Button but_newmsg=new Button(NoticeBoard.this);
		but_newmsg.setWidth(300);
		but_newmsg.setHeight(40);
		
		but_newmsg.setText("Update");
		//but_newmsg.setBackgroundColor(Color.BLUE);
		but_newmsg.setWidth(100);
		LayoutParams param3=new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//param3.topMargin=260;
		//param3.leftMargin=100;
		param3.addRule(RelativeLayout.CENTER_HORIZONTAL);
		param3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		but_newmsg.setLayoutParams(param3);
		
		
		
		
		//
		
		db_show_previous_msg();
		
		edit_msg.setText(str_pre_msg);
		
		for (int i = 0; i < list_pre_tagList.size(); i++) {
			switch (list_pre_tagList.get(i)) {
			case 1:
				cb_admin.setChecked(true);
				break;
			case 2:
				cb_hod.setChecked(true);
				break;
			case 3:
				cb_staff.setChecked(true);
				break;
			case 4:
				cb_student.setChecked(true);
				break;
			}
			
		}
		
		but_newmsg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				str_msg=edit_msg.getText().toString();
				
				if(str_msg.equalsIgnoreCase(""))
				{
					AlertDialog.Builder alert_error=new AlertDialog.Builder(NoticeBoard.this);
					alert_error.setMessage("Enter Message");
					alert_error.setTitle("Error");
					alert_error.setPositiveButton("OK", null);
					alert_error.show();
				}
				
				else {
					list_receiver_id.clear();
					
					if(cb_admin.isChecked()){
						list_receiver_id.add("1");
					}
					if(cb_hod.isChecked())
					{
						list_receiver_id.add("2");
					}
					if(cb_staff.isChecked())
					{
						list_receiver_id.add("3");
					}
					if(cb_student.isChecked())
					{
						list_receiver_id.add("4");
					}
					if(list_receiver_id.size()==0)
					{
						AlertDialog.Builder alert_error=new AlertDialog.Builder(NoticeBoard.this);
						alert_error.setMessage("Tag Person");
						alert_error.setTitle("Error");
						alert_error.setPositiveButton("OK", null);
						alert_error.show();
					}
					
					else {
						db_update_msg();
					}
				}
				
			}
		});
		
		relative.addView(edit_msg);
		relative.addView(cb_admin);
		relative.addView(cb_hod);
		relative.addView(cb_staff);
		relative.addView(cb_student);
		relative.addView(but_newmsg);
		alert.setView(relative);
		alert.setPositiveButton("Cancel", null);
		alert.show();
		
	}
	
	public void db_show_previous_msg()
	{
		ArrayList<NameValuePair> name=new ArrayList<NameValuePair>();
		
		name.add(new BasicNameValuePair("se_cnbid", String.valueOf(int_cnbid)));
		name.add(new BasicNameValuePair("se_uid", str_user_id));
		
		try {
			HttpClient http=new DefaultHttpClient();
			HttpPost post=new HttpPost(ip_address+"DB_Show_Previous_msg.php");
			post.setEntity(new UrlEncodedFormEntity(name));
			HttpResponse response=http.execute(post);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
		}
		
		try {
			BufferedReader buffer=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			while((line=buffer.readLine())!=null)
			{
				sb.append(line+"\n");
				
			}
			
			inputStream.close();
			result=sb.toString();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
		}
		
		try {
			
			JSONArray jArray=new JSONArray(result);
			JSONObject jObject=null;
			str_pre_msg=null;
			int_pre_msgid=0;
			list_pre_tagList=new ArrayList<Integer>();
			
			for(int i=0;i<jArray.length();i++)
			{
				jObject=jArray.getJSONObject(i);
				
				int_pre_msgid=jObject.getInt("msg_id");
				str_pre_msg=jObject.getString("msg");
				list_pre_tagList.add(jObject.getInt("re_cnbid"));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
		}
	}
	
	public void db_update_msg()
	{
		
		ArrayList<NameValuePair> name=new ArrayList<NameValuePair>();
		
		name.add(new BasicNameValuePair("msg_id", String.valueOf(int_pre_msgid)));
		
		name.add(new BasicNameValuePair("se_cnbid", String.valueOf(int_cnbid)));
		
		name.add(new BasicNameValuePair("se_uid",str_user_id));
		
		name.add(new BasicNameValuePair("msg", str_msg));
		
		name.add(new BasicNameValuePair("recount", String.valueOf(list_receiver_id.size())));
		
		
		for(int i=0;i<list_receiver_id.size();i++)
		{
			String receiverid="recnbid" + i;
			
			name.add(new BasicNameValuePair(receiverid, list_receiver_id.get(i).toString()));
			
		}
		
		try {
			
			HttpClient http=new DefaultHttpClient();
			HttpPost post=new HttpPost(ip_address+"DB_Update_Msg.php");
			post.setEntity(new UrlEncodedFormEntity(name));
			HttpResponse response=http.execute(post);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
			
		}
		
		try {
			BufferedReader buffer=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			while((line=buffer.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			result=sb.toString();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
		}
		
		try {
			
			JSONObject jObject=new JSONObject(result);
			
			int code=jObject.getInt("code");
			AlertDialog.Builder alert_update_report=new AlertDialog.Builder(NoticeBoard.this);
			alert_update_report.setTitle("Report");
			alert_update_report.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
					startActivity(new Intent(NoticeBoard.this,NoticeBoard.class));
					
				}
			});
			
			if(code==1)
			{
				alert_update_report.setMessage("Successfully Updated");
				alert_update_report.show();
				clear();
			}
			
			else {
				alert_update_report.setMessage("Try Again");
				alert_update_report.show();
				clear();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			
		
			Log.e("DB_Get_Out_Count", e.toString());
		}
		
	}
	
	public void alertbox_newmsg()
	{
		
		alert=new AlertDialog.Builder(NoticeBoard.this);
		alert.setTitle("New Post");
		relative=new RelativeLayout(NoticeBoard.this);
		relative.setPadding(0, 0, 0, 45);
		
		
		edit_msg=new EditText(NoticeBoard.this);
		edit_msg.setHint("Enter your message");
		edit_msg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		RelativeLayout.LayoutParams param2=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		param2.topMargin=40;
		edit_msg.setLayoutParams(param2);
		
		cb_admin=new CheckBox(NoticeBoard.this);
		cb_admin.setText("Admin");
		cb_admin.setTextColor(Color.RED);
		RelativeLayout.LayoutParams param4=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param4.leftMargin=20;
		param4.topMargin=140;
		cb_admin.setLayoutParams(param4);
		
		cb_hod=new CheckBox(NoticeBoard.this);
		cb_hod.setText("HOD");
		cb_hod.setTextColor(Color.RED);
		RelativeLayout.LayoutParams param5=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param5.leftMargin=180;
		param5.topMargin=140;
		cb_hod.setLayoutParams(param5);
		
		
		cb_staff=new CheckBox(NoticeBoard.this);
		cb_staff.setText("Teacher");
		cb_staff.setTextColor(Color.GREEN);
		RelativeLayout.LayoutParams param6=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param6.leftMargin=20;
		param6.topMargin=200;
		cb_staff.setLayoutParams(param6);
		
		cb_student=new CheckBox(NoticeBoard.this);
		cb_student.setText("Student");
		cb_student.setTextColor(Color.GREEN);
		RelativeLayout.LayoutParams param7=new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param7.leftMargin=180;
		param7.topMargin=200;
		cb_student.setLayoutParams(param7);
		
//		Button newCheckBox=new Button(NoticeBoard.this);
//		newCheckBox.setText("OK");
//		newCheckBox.setTextColor(Color.RED);
//		RelativeLayout.LayoutParams param10=new RelativeLayout.LayoutParams
//				((int)LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//		param10.leftMargin=100;
//		param10.topMargin=260;
//		newCheckBox.setLayoutParams(param10);
		
		
//		Button button=new Button(NoticeBoard.this);
//		button.setText("previous");
//		LayoutParams param10=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//		param10.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		button.setLayoutParams(param10);
		
		Button but_newmsg=new Button(NoticeBoard.this);
		but_newmsg.setWidth(300);
		but_newmsg.setHeight(40);
		
		but_newmsg.setText("Post");
		//but_newmsg.setBackgroundColor(Color.BLUE);
		but_newmsg.setWidth(100);
		LayoutParams param3=new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//param3.topMargin=260;
		//param3.leftMargin=100;
		param3.addRule(RelativeLayout.CENTER_HORIZONTAL);
		param3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		but_newmsg.setLayoutParams(param3);
		
		but_newmsg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				str_msg=edit_msg.getText().toString();
				if (str_msg.equalsIgnoreCase("")) {
					AlertDialog.Builder alert_error=new AlertDialog.Builder(NoticeBoard.this);
					alert_error.setMessage("Enter message");
					alert_error.setTitle("Error");
					alert_error.setPositiveButton("OK", null);
					alert_error.show();
					
				}
				else {
					list_receiver_id.clear();
					if(cb_admin.isChecked()){
						list_receiver_id.add("1");
						
					}
					if(cb_hod.isChecked())
					{
						list_receiver_id.add("2");
						
					}
					if(cb_staff.isChecked())
					{
						list_receiver_id.add("3");
					}
					if(cb_student.isChecked())
					{
						list_receiver_id.add("4");
					}
					createNewMessage();
				}
				
			}
		});
		
		alert.setNegativeButton("Cancel", null);
		
		relative.addView(edit_msg);
		relative.addView(cb_admin);
		relative.addView(cb_hod);
		relative.addView(cb_staff);
		relative.addView(cb_student);
		//relative.addView(button);
		relative.addView(but_newmsg);
		alert.setView(relative);
		alert.show();
		

	}
	
	

	private void db_userdata() {
		// TODO Auto-generated method stub
		ArrayList<NameValuePair> name=new ArrayList<NameValuePair>();
		name.add(new BasicNameValuePair("re_cnbid", String.valueOf(int_cnbid)));
		
		try {
			HttpClient http=new DefaultHttpClient();
			HttpPost post=new HttpPost(ip_address+"DB_UserData.php");
			post.setEntity(new UrlEncodedFormEntity(name));
			HttpResponse response=http.execute(post);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_UserData", e.toString());
		}
		
		try {
			BufferedReader buffer=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			
			while((line=buffer.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			result=sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_UserData", e.toString());
			
		}
		
		try {
			JSONArray jArray=new JSONArray(result);
			JSONObject jObject=null;
			for(int i=0;i<jArray.length();i++)
			{
				jObject=jArray.getJSONObject(i);
				
				list_out_se_cnbid.add(String.valueOf(jObject.getInt("se_cnbid")));
				list_out_se_uid.add(jObject.getString("se_uid"));
				list_out_msgid.add(jObject.getString("msg_id"));
				list_out_msg.add(jObject.getString("msg"));
				list_out_date.add(jObject.getString("date"));
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_UserData", e.toString());
		}
		
	}



	private void db_get_out_count() {
		// TODO Auto-generated method stub
		
		ArrayList<NameValuePair> name=new ArrayList<NameValuePair>();
		name.add(new BasicNameValuePair("re_cnbid", String.valueOf(int_cnbid)));
		
		try {
			
			HttpClient http=new DefaultHttpClient();
			HttpPost post=new HttpPost(ip_address+"DB_Get_Out_Count.php");
			post.setEntity(new UrlEncodedFormEntity(name));
			HttpResponse response=http.execute(post);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
		}
		
		try {
			
			BufferedReader buffer=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			while((line=buffer.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			result=sb.toString();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
		}
		try {
			JSONObject jObject=new JSONObject(result);
			int_msgcount=jObject.getInt("count");
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_Out_Count", e.toString());
		}
		
	}
	
	public void db_get_senderdata(String string1,String string2)
	{
		ArrayList<NameValuePair> name=new ArrayList<NameValuePair>();
		
		name.add(new BasicNameValuePair("se_cnbid", string1));
		name.add(new BasicNameValuePair("se_uid", string2));
		
		try {
			HttpClient http=new DefaultHttpClient();
			HttpPost post=new HttpPost(ip_address+"DB_Get_SenderData.php");
			post.setEntity(new UrlEncodedFormEntity(name));
			HttpResponse response=http.execute(post);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_SenderData", e.toString());
			
		}
		
		try {
			BufferedReader buffer=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			while((line=buffer.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			result=sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_SenderData", e.toString());
		}
		
		try {
			JSONObject jObject=new JSONObject(result);
			se_cnb=jObject.getString("se_cnb");
			se_name=jObject.getString("se_name");
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_SenderData", e.toString());
		}
		
		
	}
	
	public void createNewMessage()
	{
		if(list_receiver_id.size()==0)
		{
			AlertDialog.Builder alert_error=new AlertDialog.Builder(NoticeBoard.this);
			alert_error.setMessage("Tag Anyone");
			alert_error.setTitle("Error");
			alert_error.setPositiveButton("OK", null);
			alert_error.show();
		}
		
		else {
			InsertNewEntry();
		}
	}
	
	public void InsertNewEntry()
	{
		
		ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("se_cnbid", String.valueOf(int_cnbid)));
		nameValuePairs.add(new BasicNameValuePair("se_uid", str_user_id));
		nameValuePairs.add(new BasicNameValuePair("msg", str_msg));
		nameValuePairs.add(new BasicNameValuePair("recount", String.valueOf(list_receiver_id.size())));
		
		for(int i=0;i<list_receiver_id.size();i++)
		{
			String receiverid="recnbid"+i;
			nameValuePairs.add(new BasicNameValuePair(receiverid, list_receiver_id.get(i).toString()));
			
			
		}
		
		try {
			HttpClient httpClient=new DefaultHttpClient();
			HttpPost httpPost=new HttpPost(ip_address+"InsertNewEntry.php");
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response=httpClient.execute(httpPost);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("InsertNewEntry", e.toString());
		}
		
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			
			while((line=reader.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			result=sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("InsertNewEntry", e.toString());
		}
		try {
			JSONObject json_data=new JSONObject(result);
			int code=json_data.getInt("code");
			
			AlertDialog.Builder alert_report=new AlertDialog.Builder(NoticeBoard.this);
			alert_report.setTitle("Report");
			alert_report.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
					startActivity(new Intent(NoticeBoard.this,NoticeBoard.class));
					
				}
			});
			
			if(code==1)
			{
				alert_report.setMessage("successfully inserted");
				alert_report.show();
				clear();
			}
			else {
				alert_report.setMessage("Try Again");
				alert_report.show();
				clear();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("InsertNewEntry", e.toString());;
		}
		
	}
	
	
	public void clear()
	{
		edit_msg.setText(null);
		cb_admin.setChecked(false);
		cb_hod.setChecked(false);
		cb_staff.setChecked(false);
		cb_student.setChecked(false);
	}
	
	
	public void db_get_taglist(String str)
	{
		ArrayList<NameValuePair> name=new ArrayList<NameValuePair>();
		name.add(new BasicNameValuePair("msg_id", str));
		try {
			HttpClient http=new DefaultHttpClient();
			HttpPost post=new HttpPost(ip_address+"DB_Get_TagList.php");
			post.setEntity(new UrlEncodedFormEntity(name));
			HttpResponse response=http.execute(post);
			HttpEntity entity=response.getEntity();
			inputStream=entity.getContent();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_TagList", e.toString());
		}
		
		try {
			BufferedReader buffer=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
			StringBuilder sb=new StringBuilder();
			while((line=buffer.readLine())!=null)
			{
				sb.append(line+"\n");
			}
			inputStream.close();
			result=sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_TagList", e.toString());
			
		}
		
		try {
			JSONArray jArray=new JSONArray(result);
			JSONObject jObject=null;
			List<String> dummy=new ArrayList<String>();
			
			for(int i=0;i<jArray.length();i++)
			{
				jObject=jArray.getJSONObject(i);
				dummy.add(jObject.getString("re_cnbid"));
			}
			
			for (int i = 0; i < dummy.size(); i++) {
				int x = Integer.parseInt(dummy.get(i).toString());
				switch (x) {
				case 1:
					list_out_tag.add("Admin");

					break;
				case 2:
					list_out_tag.add("HOD");

					break;
				case 3:
					list_out_tag.add("Staff");

					break;
				case 4:
					list_out_tag.add("Student");

					break;
				}
			}
			
			dummy.clear();
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("DB_Get_TagList", e.toString());
			
		}
	}



	private void intialise_variables() {
		// TODO Auto-generated method stub
		
		r1=(RelativeLayout) findViewById(R.id.r1);
		text_owner=(TextView) findViewById(R.id.textView1);
		
		prefs=getSharedPreferences(prefNameString, MODE_PRIVATE);
		str_user_name=prefs.getString("uname", "");
		
		ip_address=prefs.getString("ip", "http://www.paponsen.netii.net/");
		
		int_cnbid=prefs.getInt("cnb_id", 0);
		str_user_id=prefs.getString("uid", null);
		
		list_receiver_id=new ArrayList<String>();
		
		text_owner.setText(str_user_name);
		
		scroll=(ScrollView) findViewById(R.id.scrollView1);
		linear1=(LinearLayout) findViewById(R.id.linear1);
		
		list_out_se_cnbid=new ArrayList<String>();
		list_out_se_uid=new ArrayList<String>();
		list_out_date=new ArrayList<String>();
		list_out_se_name=new ArrayList<String>();
		list_out_msg=new ArrayList<String>();
		list_out_tag=new ArrayList<String>();
		list_out_msgid=new ArrayList<String>();
		
		str_tag="";
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		Intent intent=new Intent(NoticeBoard.this,User_login.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notice_board, menu);
		return true;
	}

}
