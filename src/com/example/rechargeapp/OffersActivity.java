package com.example.rechargeapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class OffersActivity extends ActionBarActivity {
	TextView t;
	SQLiteDatabase db;
	TelephonyManager tel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offers);
		t=(TextView)findViewById(R.id.txt);
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.scr));
		scrollview.post(new Runnable() {
		    @Override
		    public void run() {
		        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
		    }
		});
        tel=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        try {
			postData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        show();

	}
	public void postData() throws JSONException {
	    
	   	// Create a new HttpClient and Post Header
	    	db=openOrCreateDatabase("offersDB", Context.MODE_PRIVATE, null);
	    	db.execSQL("CREATE TABLE IF NOT EXISTS mobof(id VARCHAR,provider VARCHAR,description VARCHAR,rdate VARCHAR );");
	    	HttpClient httpclient = new DefaultHttpClient();
	    	HttpPost httppost = new HttpPost("http://192.168.44.1/sel.php");

	    	//This is the data to send
	    	String MyName = "akshayc"; //any data to send

	    	try {
	    	// Add your data
	    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	    	nameValuePairs.add(new BasicNameValuePair("action", MyName));

	    	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    	// Execute HTTP Post Request

	    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    	String response = httpclient.execute(httppost, responseHandler);

	    	
	
	    	JSONObject json = new JSONObject(response);
	        JSONArray jArray = json.getJSONArray("posts");
	        ArrayList<HashMap<String, String>> mylist = 
	               new ArrayList<HashMap<String, String>>();
	        StringBuffer buffer=new StringBuffer();
	        for (int i = 1; i < jArray.length(); i++) {
	            JSONObject e = jArray.getJSONObject(i);
	            String s = e.getString("post");
	            JSONObject jObject = new JSONObject(s);
	            buffer.append("provider   :"+jObject.getString("provider")+"\n");
	            buffer.append("desciption :"+jObject.getString("description")+"\n");
	            buffer.append("date :"+jObject.getString("rdate")+"\n");
	            if(tel.getNetworkOperatorName().toLowerCase().contains(jObject.getString("provider").toLowerCase())||jObject.getString("provider").toLowerCase().contains("freecharge")||jObject.getString("provider").toLowerCase().contains("paytm")||jObject.getString("provider").toLowerCase().contains("mobikwik"))
	            	if(insert(jObject.getString("id"),jObject.getString("provider"),jObject.getString("description"),jObject.getString("rdate")))
	            		Toast.makeText(this,"NEW OFFERS ADDED", Toast.LENGTH_LONG).show();
	        }
	//t.setText(buffer.toString());
	//Toast.makeText(this,buffer.toString(), Toast.LENGTH_LONG).show();
	    	} catch (ClientProtocolException e) {
	    	Toast.makeText(this, "CPE response " + e.toString(), Toast.LENGTH_LONG).show();
	    	// TODO Auto-generated catch block
	    	} catch (IOException e) {
	    	Toast.makeText(this, "IOE response " + e.toString(), Toast.LENGTH_LONG).show();
	    	// TODO Auto-generated catch block
	    	}

	    	}//end postData()
	    public boolean insert(String a,String b,String c,String d)
	    {
	    	Cursor cur=db.rawQuery("SELECT * FROM mobof", null);
	    	while(cur.moveToNext())
			 {
	    		if(b.equalsIgnoreCase(cur.getString(1))&&c.equalsIgnoreCase(cur.getString(2)))
	    			return false;
			 }
	    	db.execSQL("INSERT INTO mobof VALUES('"+a+"','"+b+"','"+c+"','"+d+"');");
	    	return true;
	    }
	    public void show()
	    {
	    	Cursor cur=db.rawQuery("SELECT * FROM mobof", null);
	    	StringBuffer buffer=new StringBuffer();
	    	SpannableStringBuilder ssb = new SpannableStringBuilder( "" );
	      //  ssb.setSpan(new RelativeSizeSpan(2f), 0, 5, 0);
	       // ssb.append("kkk");
	      //  ssb.setSpan(new RelativeSizeSpan(2f), 6, 9, 0);
	       // t.setText(ssb,BufferType.SPANNABLE);
	    	int prev=0;
	    	while(cur.moveToNext())
			 {
	    		// buffer.append(cur.getString(0)+"\n");
	    		 ssb.append(cur.getString(1)+"\n");
	    		 ssb.setSpan(new RelativeSizeSpan(2f),prev,prev+cur.getString(1).length(), 0);
	    		 ssb.setSpan(new ForegroundColorSpan(Color.RED),prev,prev+cur.getString(1).length(), 0);
	    		 ssb.setSpan(new UnderlineSpan(),prev,prev+cur.getString(1).length(), 0);
	    		 ssb.setSpan(new StyleSpan(Typeface.ITALIC),prev,prev+cur.getString(1).length(), 0);
	    		 prev=prev+cur.getString(1).length()+1;
	    		 ssb.append(cur.getString(2)+"\n");
	    		 ssb.setSpan(new RelativeSizeSpan(1.5f),prev,prev+cur.getString(2).length(), 0);
	    		 prev=prev+cur.getString(2).length()+1;
	    		 ssb.append(cur.getString(3)+"\n");
	    		 ssb.setSpan(new ForegroundColorSpan(Color.GRAY),prev,prev+cur.getString(3).length(), 0);
	    		 prev=prev+cur.getString(3).length()+1;
			 }
	    	//Toast.makeText(this,buffer.toString(), Toast.LENGTH_LONG).show();
	    	t.setText(ssb,BufferType.SPANNABLE);
	    }
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.offers, menu);
		return true;
	}*/

/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}*/
}
