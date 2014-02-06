package com.biter.bitertweet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Login extends Activity {
	ImageButton buttonTest;
	ImageView img;
	EditText email;
	EditText pass;
	Button btn;
	ProgressDialog progressDialog;
	ProgressDialog checkShared;
	String loginApiUrl = "YOUR_DOMAIN_WHERE_BITERTWEET_PHP_INSTALLED/api/1/checklogin.php";
	private JSONObject jObject;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	checkShared = ProgressDialog.show(Login.this, "Starting", "Please Wait...");
    	new Thread()
		{
			public void run()
			{
				try
				{
			    	SharedPreferences loginShared = getSharedPreferences("loginInfo",0);
			        String loginValue=loginShared.getString("status", "failed");
			        if(loginValue.toString().trim().equals("successLogin"))
			        {
			        	Intent intentMain = new Intent(Login.this,Home.class);
						Login.this.startActivity(intentMain);
			        }
				}
				catch(Exception ex)
				{
					Log.e("checking",ex.getMessage());
				}
				checkShared.dismiss();
			}
		}.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonTest=(ImageButton)findViewById(R.id.imageButton1);
        buttonTest.setOnClickListener(strLogoAnim);
        btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(login);
        email=(EditText)findViewById(R.id.editText1);
        pass=(EditText)findViewById(R.id.editText2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        
        return true;
    }
    
    private OnClickListener login = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			progressDialog = ProgressDialog.show(Login.this, "Logging in", "Please Wait...");
			new Thread()
			{
				public void run()
				{
					try
					{
						HttpClient httpc=new DefaultHttpClient();
						HttpPost httpp=new HttpPost(loginApiUrl);
						try
						{
							
							List<NameValuePair> namevalue=new ArrayList<NameValuePair>(2);
							String emailTxt = email.getText().toString();
							String passTxt = pass.getText().toString();
							namevalue.add(new BasicNameValuePair("email",emailTxt));
							namevalue.add(new BasicNameValuePair("password",passTxt));
							httpp.setEntity(new UrlEncodedFormEntity(namevalue));
							HttpResponse res=httpc.execute(httpp);
							HttpEntity entity = res.getEntity();
							String temp = EntityUtils.toString(entity,HTTP.UTF_8);
							jObject = new JSONObject(temp);
							JSONObject loginInfo = jObject.getJSONObject("loginInfo");
							String loginStatus=loginInfo.getString("status");
							if(loginStatus.toString().trim().equals("successLogin"))
							{
								String fname = loginInfo.getString("firstname");
								SharedPreferences loginShared = getSharedPreferences("loginInfo", 0);
								SharedPreferences.Editor editor = loginShared.edit();
								editor.putString("status", "successLogin");
								editor.putString("firstname", fname);
								editor.putString("email", emailTxt);
								editor.putString("password", passTxt);
								editor.commit();
								Log.d("login","success");
								Intent intentMain = new Intent(Login.this,Home.class);
								Login.this.startActivity(intentMain);
							}	
							else
							{
								Log.d("login","failed");
								runOnUiThread(new Runnable() 
						        {                
						            @Override
						            public void run() 
						            {
						            	Toast.makeText(getApplicationContext(), "Email or password is invalid",Toast.LENGTH_LONG).show();
						            }
						        });
							}
								
						}
						catch (ClientProtocolException e) {
					        // TODO Auto-generated catch block
							runOnUiThread(new Runnable() 
					        {                
					            @Override
					            public void run() 
					            {
					            	Toast.makeText(getApplicationContext(), "Can not connect to host",Toast.LENGTH_LONG).show();
					            }
					        });
					    } catch (IOException ex) {
					        // TODO Auto-generated catch block
					    	ex.printStackTrace();
					    	Log.e("result2",ex.getMessage());
					    	runOnUiThread(new Runnable() 
					        {                
					            @Override
					            public void run() 
					            {
					            	Toast.makeText(getApplicationContext(), "Can not connect to host",Toast.LENGTH_LONG).show();
					            }
					        });
					    }
					}
					catch (Exception ex)
					{
						Log.e("tag",ex.getMessage());
					}
					progressDialog.dismiss();
				}
			}.start();
		}
	};
    private OnClickListener strLogoAnim = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(0, 0,0,-120);
			animation.setDuration(1000);
			animation.setFillAfter(true);
			buttonTest.startAnimation(animation);
			Animation imgview=new AlphaAnimation(0, 1);
			imgview.setDuration(2000);
			email.setVisibility(View.VISIBLE);
			email.startAnimation(imgview);
			pass.setVisibility(View.VISIBLE);
			pass.startAnimation(imgview);
			btn.setVisibility(View.VISIBLE);
			btn.startAnimation(imgview);
			buttonTest.setEnabled(false);
		}
	};
    
    @Override
    public void onAttachedToWindow() {
    	super.onAttachedToWindow();
    	Window window = getWindow();
    	window.setFormat(PixelFormat.RGBA_8888);
    }

}
