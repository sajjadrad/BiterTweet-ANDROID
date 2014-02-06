package com.biter.bitertweet;

import java.io.IOException;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends Activity {
	
	private EditText tweet;
	private Button sendTweet;
	private TextView message;
	ProgressDialog progressDialog;
	private JSONObject jObject;
	public boolean successFlag=false;
	
	String sendTweetApiUrl = "YOUR_DOMAIN_WHERE_BITERTWEET_PHP_INSTALLED/api/1/tweet.php";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.home);
	    tweet=(EditText)findViewById(R.id.tweetContent);
	    sendTweet=(Button)findViewById(R.id.sendTweet);
	    sendTweet.setOnClickListener(send);
	    message=(TextView)findViewById(R.id.welcomeMessage);
	    SharedPreferences loginShared = getSharedPreferences("loginInfo",0);
        String fname=loginShared.getString("firstname", "");
        if(!fname.toString().trim().equals(""))
		{
        	message.setText("Hello There,"+fname);
		}
	    // TODO Auto-generated method stub
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)  
    {
         if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
         {
        	
            this.moveTaskToBack(true);
            return true;
         }
        return super.onKeyDown(keyCode, event);
    }
	private OnClickListener send = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!tweet.getText().toString().trim().equals(""))
			{
				progressDialog = ProgressDialog.show(Home.this, "Sending tweet", "Please Wait...");
				new Thread()
				{
					public void run()
					{
						try
						{
							HttpClient httpc=new DefaultHttpClient();
							HttpPost httpp=new HttpPost(sendTweetApiUrl);
							try
							{
								List<NameValuePair> namevalue=new ArrayList<NameValuePair>(2);
								SharedPreferences loginShared = getSharedPreferences("loginInfo",0);
							    String email=loginShared.getString("email", "");
							    String password=loginShared.getString("password", "");
								String tweetText=tweet.getText().toString();
								namevalue.add(new BasicNameValuePair("email",email));
								namevalue.add(new BasicNameValuePair("password",password));
								namevalue.add(new BasicNameValuePair("text",tweetText));
								namevalue.add(new BasicNameValuePair("apptoken",""));
								namevalue.add(new BasicNameValuePair("expirehash",""));
								httpp.setEntity(new UrlEncodedFormEntity(namevalue,"UTF-8"));
								HttpResponse res=httpc.execute(httpp);
								HttpEntity entity = res.getEntity();
								String temp = EntityUtils.toString(entity,HTTP.UTF_8);
								jObject = new JSONObject(temp);
								JSONObject postInfo = jObject.getJSONObject("post");
								String postStatus=postInfo.getString("status");
								if(postStatus.toString().trim().equals("success"))
								{
									runOnUiThread(new Runnable() 
							        {                
							            @Override
							            public void run() 
							            {
							            	tweet.setText("");
							            	Toast.makeText(getApplicationContext(), "Tweet Sent Successfully",Toast.LENGTH_LONG).show();
							            }
							        });
								}
								else if(postStatus.toString().trim().equals("tokenfailed"))
								{
									runOnUiThread(new Runnable() 
							        {                
							            @Override
							            public void run() 
							            {
							            	Toast.makeText(getApplicationContext(), "Bitertweet isn't linked to your Twitter account.Please login via web and link your twitter account.",Toast.LENGTH_LONG).show();
							            }
							        });
								}
								else if(postStatus.toString().trim().equals("loginfailed"))
								{
									
									runOnUiThread(new Runnable() 
							        {                
							            @Override
							            public void run() 
							            {
							            	tweet.setText("");
							            	Toast.makeText(getApplicationContext(), "Your password changed.",Toast.LENGTH_LONG).show();
							            }
							        });
									loginShared=getSharedPreferences("loginInfo", 0);
									SharedPreferences.Editor editor = loginShared.edit();
									editor.putString("status", "failed");
									editor.putString("email", "");
									editor.putString("firstname", "");
									editor.putString("password", "");
									editor.commit();
									finish();
								}
								else
								{
									runOnUiThread(new Runnable() 
							        {                
							            @Override
							            public void run() 
							            {
							            	Toast.makeText(getApplicationContext(), "There is some errors,try again",Toast.LENGTH_LONG).show();
							            }
							        });
								}
									
							}
							catch (ClientProtocolException e) {
						        // TODO Auto-generated catch block
						    } catch (IOException ex) {
						        // TODO Auto-generated catch block
						    	ex.printStackTrace();
						    	Log.e("result",ex.getMessage());
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
			
		}
	};
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.logout)
		{
			SharedPreferences loginShared=getSharedPreferences("loginInfo", 0);
			SharedPreferences.Editor editor = loginShared.edit();
			editor.putString("status", "failed");
			editor.putString("email", "");
			editor.putString("firstname", "");
			editor.putString("password", "");
			editor.commit();
			finish();
		}
		if(item.getItemId()==R.id.exit)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
