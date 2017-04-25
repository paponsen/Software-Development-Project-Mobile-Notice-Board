package com.papon.noticeboard;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class StartActivity extends Activity {
	
	protected boolean active=true;
	protected int splashTime=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        
        
        Thread splashThread=new Thread()
        {
        	public void run()
        	{
        		try {
        			int waited=0;
        			while(active && (waited<splashTime))
        			{
        				sleep(40);
        				if(active)
        				{
        					waited+=100;
        				}
        			}
					
				} catch (Exception e) {
					// TODO: handle exception
				}finally{
					finish();
					Intent intent=new Intent(StartActivity.this,NetConnection.class);
					startActivity(intent);
				}
        	}
        };
        
        splashThread.start();
        
        
        	
        
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
    
}
