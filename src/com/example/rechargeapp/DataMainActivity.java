package com.example.rechargeapp;



import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DataMainActivity extends ActionBarActivity {
	SQLiteDatabase db;
	Button viewall;
	Button add,modify,view;
	EditText at,bt,ct,dt,et,ft;
	String a,b,c,d,e,f;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_main);
		db=openOrCreateDatabase("networkDB", Context.MODE_PRIVATE, null);
        viewall=(Button)findViewById(R.id.viewall);
        add=(Button)findViewById(R.id.add);
        modify=(Button)findViewById(R.id.modify);
        view=(Button)findViewById(R.id.view);
        at=(EditText)findViewById(R.id.name);
        bt=(EditText)findViewById(R.id.recsize);
        ct=(EditText)findViewById(R.id.reccode);
        dt=(EditText)findViewById(R.id.balcode);
        et=(EditText)findViewById(R.id.netbalcode);
        ft=(EditText)findViewById(R.id.customer);
        viewall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showdb();
			}
		});
        add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(at.getText().toString().length()==0||bt.getText().toString().length()==0||ct.getText().toString().length()==0||dt.getText().toString().length()==0||et.getText().toString().length()==0||ft.getText().toString().length()==0)
				{
					showMessage("ERROR","INCOMPLETE");
				}
				else
				{
					if(!insert(at.getText().toString().toLowerCase(),bt.getText().toString(),ct.getText().toString(),dt.getText().toString(),et.getText().toString(),ft.getText().toString()))
						showMessage("ERROR","ALREADY EXISTS");
					else
						showMessage("SUCCESS","RECORD ADDED");
				}
			}
		});
        modify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(at.getText().toString().length()==0||bt.getText().toString().length()==0||ct.getText().toString().length()==0||dt.getText().toString().length()==0||et.getText().toString().length()==0||ft.getText().toString().length()==0)
				{
					showMessage("ERROR","INCOMPLETE");
				}
				else
				{
					if(modify(at.getText().toString().toLowerCase(),bt.getText().toString(),ct.getText().toString(),dt.getText().toString(),et.getText().toString(),ft.getText().toString()))
						showMessage("Success", "RECORD MODIFIED");
				}
			}
		});
        view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(at.getText().toString().length()==0)
					showMessage("ERROR","ENTER NETWORK NAME");
				else
				{
					if(view(at.getText().toString().toLowerCase()))
					{
						bt.setText(b);
						ct.setText(c);
						dt.setText(d);
						et.setText(e);
						ft.setText(f);
					}
				}
			}
		});
	}
	public boolean insert(String a,String b,String c,String d,String e,String f)
    {
    	Cursor cur=db.rawQuery("SELECT * FROM mobnetwork", null);
    	while(cur.moveToNext())
		 {
    		if(a.toLowerCase().contains(cur.getString(0).toLowerCase()))
    			return false;
		 }
    	db.execSQL("INSERT INTO mobnetwork VALUES('"+a+"','"+b+"','"+c+"','"+d+"','"+e+"','"+f+"');");
    	return true;
    }
    public boolean modify(String a,String b,String c,String d,String e,String f)
    {
    	if(a.length()==0)
    	{
    		showMessage("EMPTY","ENTER NETWORK NAME");
    		return false;
    	}
    	Cursor cur=db.rawQuery("SELECT * FROM mobnetwork where name='"+a+"'",null);
    	if(cur.moveToFirst())
		{
			db.execSQL("UPDATE mobnetwork SET recsize='"+b+"',reccode='"+c+
					"',balcode='"+d+"',netbalcode='"+e+"',customer='"+f+"' WHERE name='"+a+"'");
			//showMessage("Success", "RECORD MODIFIED");
		}
		else
		{
			showMessage("ERROR", "INVALID NETWORK NAME");
			return false;
		}

    	return true;
    }
    public boolean view(String a)
    {
    	Cursor cur=db.rawQuery("SELECT * FROM mobnetwork where name='"+a+"'",null);
    	if(cur.moveToFirst())
		{
    		b=cur.getString(1);
    		c=cur.getString(2);
    		d=cur.getString(3);
    		e=cur.getString(4);
    		f=cur.getString(5);
		}
    	else
    	{
    		showMessage("ERROR", "INVALID NETWORK NAME");
			return false;
    	}
    	return true;
    }

	public void showdb()
    {
    	Cursor cur=db.rawQuery("SELECT * FROM mobnetwork", null);
    	StringBuffer buffer=new StringBuffer();
    	while(cur.moveToNext())
    	{
    			buffer.append("Name: "+cur.getString(0)+"\n");
				buffer.append("Recsize: "+cur.getString(1)+"\n");
				buffer.append("Reccode: "+cur.getString(2)+"\n");
				buffer.append("Balcode: "+cur.getString(3)+"\n");
				buffer.append("Netbalcode: "+cur.getString(4)+"\n");
				buffer.append("Customer: "+cur.getString(5)+"\n\n");
    	}
    	showMessage("MobNetwork",buffer.toString());
    }
    public void showMessage(String title,String message)
    {
    	Builder builder=new Builder(this);
    	builder.setCancelable(true);
    	builder.setTitle(title);
    	builder.setMessage(message);
    	builder.show();
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_main, menu);
		return true;
	}

	@Override
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
	@Override
	public void onBackPressed() {
		Intent i = new Intent(getApplicationContext(),RechargeAppActivity.class);
		startActivity(i);
		finish();
	}
}
