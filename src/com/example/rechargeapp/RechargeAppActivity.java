package com.example.rechargeapp;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeAppActivity extends ActionBarActivity implements OnItemSelectedListener {
	Spinner spinner1;
	SQLiteDatabase db;
	boolean isfirst;
	AlertDialog crd;
	TelephonyManager t;
	String recsize,reccode,balcode,netbalcode,customer;
	boolean opfound;
	SmsManager sms;
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/RechargeApp/";
	public static final String lang = "eng";
	private static final String TAG = "RechargeApp.java";
	protected Button _button;
	protected EditText recharge;
	protected String _path;
	protected boolean _taken;
	protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
                for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}

		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_app);
		recharge = (EditText) findViewById(R.id.editText1);
		_button = (Button) findViewById(R.id.ocrbut);
		createsampledb();
		opfound=findoperator();
		final Button but1=(Button)findViewById(R.id.button1);
		final TextView oper=(TextView)findViewById(R.id.networkoperator);
		final TextView enter=(TextView)findViewById(R.id.enter);
		final TextView txt1=(TextView)findViewById(R.id.balance);
		final TextView txt2=(TextView)findViewById(R.id.netbalance);
		final TextView txt3=(TextView)findViewById(R.id.customercare);
		final SharedPreferences prrec=this.getPreferences(Context.MODE_PRIVATE);
		final ImageView balimage=(ImageView)findViewById(R.id.balanceimage);
		final ImageView netbalimage=(ImageView)findViewById(R.id.netbalanceimage);
		final ImageView customerimage=(ImageView)findViewById(R.id.customercareimage);
		final SharedPreferences.Editor preditor=prrec.edit();
		t=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		sms=SmsManager.getDefault();
		crd=new AlertDialog.Builder(RechargeAppActivity.this).create();
		spinner1 = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
		if(!opfound)
		{
			oper.setText("Network Operator : "+t.getNetworkOperatorName()+"(Not In Database)");
			but1.setVisibility(Button.GONE);
			recharge.setVisibility(EditText.GONE);
			enter.setVisibility(TextView.GONE);
			txt1.setVisibility(TextView.GONE);
			txt2.setVisibility(TextView.GONE);
			txt3.setVisibility(TextView.GONE);
			balimage.setVisibility(ImageView.GONE);
			netbalimage.setVisibility(ImageView.GONE);
			customerimage.setVisibility(ImageView.GONE);
			_button.setVisibility(Button.GONE);
			list.add("unknown");
			spinner1.setVisibility(Spinner.GONE);
	        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
	         (this, android.R.layout.simple_spinner_item,list);
	          
	dataAdapter.setDropDownViewResource
	         (android.R.layout.simple_spinner_dropdown_item);
	          
	spinner1.setAdapter(dataAdapter);
	isfirst=true;
	spinner1.setOnItemSelectedListener(this);
		}
		else
		{
		if(t.getNetworkOperatorName().toLowerCase().contains("vodafone"))
		{
			list.add("Select Other Services");
	        list.add("Voda Credit");
	        list.add("Voda Sms Balance");
	        list.add("Voda Number");
	        list.add("Voda ACT3G");
	        list.add("Voda Balance Transfer");
	        list.add("Voda DND");
		}
		else if(t.getNetworkOperatorName().toLowerCase().contains("idea"))
		{
			list.add("Select Other Services");
	        list.add("Idea Credit");
	        list.add("Idea Sms Balance");
	        list.add("Idea Number");
	        list.add("Idea ACT3G");
	        list.add("Idea Balance Transfer");
	        list.add("Idea DND");
		}
		else if(t.getNetworkOperatorName().toLowerCase().contains("docomo"))
		{
			list.add("Select Other Services");
	        list.add("Docomo Special Offers");
	        list.add("Docomo DND");
	        list.add("Docomo Services");
		}
		else if(t.getNetworkOperatorName().toLowerCase().contains("airtel"))
		{
			list.add("Select Other Services");
	        list.add("My Airtel");
	        list.add("Airtel Sms Balance");
	        list.add("Airtel Gift");
	        list.add("Airtel Number");
		}
		else if(t.getNetworkOperatorName().toLowerCase().contains("cellone")||t.getNetworkOperatorName().toLowerCase().contains("bsnl"))
		{
			list.add("Select Services");
	        list.add("Bsnl Sms Balance");
	        list.add("Bsnl Number");
	        list.add("Bsnl Last Call");
		}
		else if(t.getNetworkOperatorName().toLowerCase().contains("reliance"))
		{
			list.add("Select Services");
	        list.add("Reliance Special Packs");
	        list.add("Reliance Other Services");
	        list.add("Reliance Number");
		}
		else if(t.getNetworkOperatorName().toLowerCase().contains("aircel"))
		{
			list.add("Select Other Services");
	        list.add("Aircel DND");
	        list.add("Aircel VAS");
	        list.add("Aircel Rate Cutter");
	        list.add("Aircel Number");
		}
		else if(t.getNetworkOperatorName().toLowerCase().contains("uninor"))
		{
			list.add("Select Other Services");
			list.add("Uninor Number");
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
        (this, android.R.layout.simple_spinner_item,list);
         
dataAdapter.setDropDownViewResource
        (android.R.layout.simple_spinner_dropdown_item);
         
		spinner1.setAdapter(dataAdapter);
		isfirst=true;
		spinner1.setOnItemSelectedListener(this);
		oper.setText("Network Operator : "+t.getNetworkOperatorName());
		enter.setText("Enter "+recsize+" Digit Recharge Code :"); 
		recharge.setHint(prrec.getString("prevcode",""));
		but1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				int size=Integer.parseInt(recsize);
				if(recharge.getText().toString().length()!=size)
				{
					String s="NOT "+size+" DIGIT";
					Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
				}
				else
				{
					preditor.putString("prevcode",recharge.getText().toString());
					preditor.commit();
					recharge.setHint(prrec.getString("prevcode",""));
					Uri uri = Uri.parse("tel:"+reccode+recharge.getText().toString()+Uri.encode("#"));
					Intent in= new Intent(Intent.ACTION_CALL, uri);
					startActivity(in);
				}
			}
		});
		balimage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode(balcode)));
				startActivity(a);
			}
		});
		netbalimage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode(netbalcode)));
				startActivity(a);
			}
		});
		customerimage.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode(customer)));
		startActivity(a);
	}
		});
		_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG, "Starting Camera app");
				startCameraActivity();
			}
		});

		_path = DATA_PATH + "/ocr.jpg";
		}
	}
    public void createsampledb()
    {
    	db=openOrCreateDatabase("networkDB", Context.MODE_PRIVATE, null);
    	db.execSQL("CREATE TABLE IF NOT EXISTS mobnetwork(name VARCHAR,recsize VARCHAR,reccode VARCHAR,balcode VARCHAR,netbalcode VARCHAR,customer VARCHAR);");
    	insert("vodafone","16","*140*","*111*2#","*111*6*2#","111");
    	insert("idea","12","*457*","*121#","*125#","12345");
    	insert("bsnl","18","*123*","*123#","*124*4#","1503");
    	insert("cellone","18","*123*","*123#","*124*4#","1503");
    	insert("docomo","16","*135*2*","*111#","*111*1#","121");
    	insert("airtel","16","*101*","*123#","*123*10#","121");
    	insert("reliance","14","*368*","*333*1*1*1#","*333*1*3*1#","198");
    	insert("aircel","16","*124*","*125#","*126*4#","121");
    	insert("uninor","16","*222*3*","*222*2#","*222*2#","121");
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
			showMessage("Success", "Record Modified");
		}
		else
		{
			showMessage("Error", "Invalid Network Name");
			return false;
		}

    	return true;
    }
    public boolean findoperator()
    {
    	Cursor cur=db.rawQuery("SELECT * FROM mobnetwork", null);
    	t=(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
    	while(cur.moveToNext())
		 {
    		if(t.getNetworkOperatorName().toLowerCase().contains(cur.getString(0).toLowerCase()))
 			{
    			recsize=cur.getString(1);
    			reccode=cur.getString(2);
    			balcode=cur.getString(3);
    			netbalcode=cur.getString(4);
    			customer=cur.getString(5);
    			return true;
 			}
   		 }
    	return false;
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
    protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, 0);
	}
    @Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
            long id) {
    	if(isfirst==true)
       	 isfirst=false;
        else
        {
       	 if(parent.getItemAtPosition(pos).toString()=="Voda Sms Balance")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*142#")));
				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Voda Number")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*121*0#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Voda DND")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("1909")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Voda Balance Transfer")
       	 {
       		 final Dialog dialog = new Dialog(this);
       			dialog.setContentView(R.layout.activity_baltrans);
       			final TextView dtext=(TextView)dialog.findViewById(R.id.baltext);
       			final Button dsave=(Button)dialog.findViewById(R.id.balok);
       			final Button dexit=(Button)dialog.findViewById(R.id.balexit);
       			final EditText dedit=(EditText)dialog.findViewById(R.id.baledit);
       			dtext.setText("*131*<AMOUNT>*<VODA RECEIVER NUMBER>#");
       			dialog.setTitle("Balance Transfer");
       			dexit.setOnClickListener(new View.OnClickListener() {
       				
       				@Override
       				public void onClick(View arg0) {
       					// TODO Auto-generated method stub
       					dialog.dismiss();
       				}
       			});
       			dsave.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							sms.sendTextMessage("144",null,dedit.getText().toString(),null,null);
							dialog.dismiss();
						}
					});
dialog.show();
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Voda ACT3G")
       	 {
       		 crd.setTitle("confirm 3G");
 				crd.setMessage("Are you sure to activate 3G?");
 				crd.setButton2("yes",new DialogInterface.OnClickListener() {
 					
 					@Override
 					public void onClick(DialogInterface arg0, int arg1) {
 						// TODO Auto-generated method stub
 						sms.sendTextMessage("144",null,"ACT 3G",null,null);
 					}
 				});
 				crd.setButton("No",new DialogInterface.OnClickListener() {
 					
 					@Override
 					public void onClick(DialogInterface arg0, int arg1) {
 						// TODO Auto-generated method stub
 						
 					}
 				});
 				crd.show();
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Voda Credit")
       	 {
       		 crd.setTitle("confirm credit");
				crd.setMessage("Are you sure to accept credit from "+t.getNetworkOperatorName()+" ?");
				crd.setButton2("yes",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						sms.sendTextMessage("144",null,"credit",null,null);
					}
				});
				crd.setButton("No",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				crd.show();
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Idea Balance Transfer")
       	 {
       		 final Dialog dialog = new Dialog(this);
    			dialog.setContentView(R.layout.activity_baltrans);
    			final TextView dtext=(TextView)dialog.findViewById(R.id.baltext);
    			final Button dsave=(Button)dialog.findViewById(R.id.balok);
    			final Button dexit=(Button)dialog.findViewById(R.id.balexit);
    			final EditText dedit=(EditText)dialog.findViewById(R.id.baledit);
    			dtext.setText("*567*<Idea receiver no><space><Amount>#");
    			dialog.setTitle("Balance Transfer");
    			dexit.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View arg0) {
    					// TODO Auto-generated method stub
    					dialog.dismiss();
    				}
    			});
    			dsave.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							sms.sendTextMessage("144",null,dedit.getText().toString(),null,null);
							dialog.dismiss();
						}
					});
dialog.show();
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Idea Sms Balance")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*161*1#")));
  				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Idea Number")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*100#")));
  				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Idea DND")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("1909")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Idea ACT3G")
       	 {
       		 crd.setTitle("confirm 3G");
 				crd.setMessage("Are you sure to activate 3G?");
 				crd.setButton2("yes",new DialogInterface.OnClickListener() {
 					
 					@Override
 					public void onClick(DialogInterface arg0, int arg1) {
 						// TODO Auto-generated method stub
 						sms.sendTextMessage("12345",null,"ACT 3G",null,null);
 					}
 				});
 				crd.setButton("No",new DialogInterface.OnClickListener() {
 					
 					@Override
 					public void onClick(DialogInterface arg0, int arg1) {
 						// TODO Auto-generated method stub
 						
 					}
 				});
 				crd.show();
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Idea Credit")
       	 {
       		 crd.setTitle("confirm credit");
				crd.setMessage("Are you sure to accept credit from "+t.getNetworkOperatorName()+" ?");
				crd.setButton2("yes",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*150*04#")));
		   				startActivity(a);
					}
				});
				crd.setButton("No",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				crd.show();
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Docomo DND")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("1909")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Docomo Special Offers")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*191*9*8#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Docomo Services")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*141#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="My Airtel")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*121#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Airtel Sms Balance")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*123*7#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Airtel Gift")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*141#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Airtel Number")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*282#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Bsnl Sms Balance")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*123*5#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Bsnl Number")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*8888#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Bsnl Last Call")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*102#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Reliance Number")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*1#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Reliance Special Packs")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*777#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Reliance Other Services")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*123#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Aircel DND")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("1909")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Aircel Rate Cutter")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("1215")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Aircel VAS")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("1215")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Aircel Number")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*1#")));
 				startActivity(a);
       	 }
       	 else if(parent.getItemAtPosition(pos).toString()=="Uninor Number")
       	 {
       		 Intent a= new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Uri.encode("*222*4#")));
 				startActivity(a);
       	 }

        }
    
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
 
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(RechargeAppActivity.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(RechargeAppActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	protected void onPhotoTaken() {
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );
		
		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		
		String recognizedText = baseApi.getUTF8Text();
		
		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with it.
		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.v(TAG, "OCRED TEXT: " + recognizedText);

		if ( lang.equalsIgnoreCase("eng") ) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}
		
		recognizedText = recognizedText.trim();
		StringBuffer bf=new StringBuffer();
		int len=recognizedText.length();
		char ch[]=recognizedText.toCharArray();
		int i;
		for(i=0;i<len;i++)
		{
			if(ch[i]>='0'&&ch[i]<='9')
				bf.append(ch[i]);
		}
		recognizedText=bf.toString();
		if ( recognizedText.length() != 0 ) {
			recharge.setText(recharge.getText().toString().length() == 0 ? recognizedText : recharge.getText() + " " + recognizedText);
			recharge.setSelection(recharge.getText().toString().length());
		}
		// Cycle done.
	}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recharge_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent i = new Intent(getApplicationContext(),DataMainActivity.class);
			startActivity(i);
			finish();
            return true;
        }
        if (id == R.id.action_offers) {
        	Intent i = new Intent(getApplicationContext(),OffersActivity.class);
			startActivity(i);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
