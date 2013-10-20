package com.sjtu.ic.daoleme;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.BMapManager;  
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;  
import com.baidu.mapapi.map.MapView;  
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;  

public class GroupActivity extends Activity{
	private TextView tv=null;
	private String username=null;
	private String addingUser=null;
	private String removingUser=null;
	private String groupname=null;
	private String gpsdata=null;
	private String locationName=null;
	private double longitude=0;
	private double latitude=0;
	private double locationLng=0;
	private double locationLat=0;
	private Symbol.Color textColor=null;
	private Symbol.Color bgColor=null;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	public BMapManager mBMapMan = null;  
	public MapView mMapView = null; 
	
	public MyLocationOverlay myLocationOverlay=null;
	public TextOverlay txOverlay=null;
	public TextOverlay locTxOverlay=null;
	public LocationData myLocData = new LocationData();  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mBMapMan=new BMapManager(getApplication());  
		mBMapMan.init("3adbd1e5f3185969836637d68ad404d7", null);
		setContentView(R.layout.activity_group);
		Intent it=getIntent();
		username=it.getStringExtra("username");
		groupname=it.getStringExtra("groupname");
		locationName=it.getStringExtra("locationname");
		locationLng=it.getDoubleExtra("longitude",121.44581);
		locationLat=it.getDoubleExtra("latitude", 31.02243);
		tv=(TextView)findViewById(R.id.my_group);
		tv.setText(groupname);
		
		mMapView=(MapView)findViewById(R.id.bmapsView);  
		mMapView.setBuiltInZoomControls(true);  
		//�����������õ����ſؼ�  
		
		MapController mMapController=mMapView.getController();  
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����  
		GeoPoint point =new GeoPoint((int)(locationLat*1E6),(int)(locationLng*1E6));  
		//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)  
		mMapController.setCenter(point);//���õ�ͼ���ĵ�  
		mMapController.setZoom(12);//���õ�ͼzoom����  
		
		//Drawable mark= getResources().getDrawable(R.drawable.ic_launcher); 
		//itemOverlay=new ItemizedOverlay<OverlayItem>(mark, mMapView); 
		txOverlay=new TextOverlay(mMapView);
		locTxOverlay=new TextOverlay(mMapView);
		myLocationOverlay = new MyLocationOverlay(mMapView); 
		
		Symbol textSymbol = new Symbol();    
		textColor = textSymbol.new Color();    
		textColor.alpha = 255;    
		textColor.red = 255;    
		textColor.blue = 0;    
		textColor.green = 0;
		
		Symbol bgSymbol = new Symbol();    
		bgColor = bgSymbol.new Color();    
		bgColor.alpha = 150;    
		bgColor.red = 80;    
		bgColor.blue = 80;    
		bgColor.green = 80;
		
		TextItem ti = new TextItem();
		ti.pt=point;
		ti.text=locationName;
		ti.fontColor=textColor;
		ti.bgColor=bgColor;
		ti.fontSize=35;
		locTxOverlay.addText(ti);
		mMapView.refresh();
		
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.getOverlays().add(locTxOverlay);
		mMapView.getOverlays().add(txOverlay);
		
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
		mLocationClient.setAK("3adbd1e5f3185969836637d68ad404d7");
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(100000);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.disableCache(true);//��ֹ���û��涨λ
		//option.setPoiNumber(5);    //��෵��POI����   
		//option.setPoiDistance(1000); //poi��ѯ����        
		//option.setPoiExtraInfo(true); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ        
		mLocationClient.setLocOption(option);
	    mLocationClient.registerLocationListener( myListener );    //ע���������
	    
	    if (!mLocationClient.isStarted())
	    	mLocationClient.start();
	    mLocationClient.requestLocation();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE, Menu.NONE, 1, "�޸ġ�");
		menu.add(Menu.NONE, Menu.NONE, 2, "��ӳ�Ա");
		menu.add(Menu.NONE, Menu.NONE, 3, "�Ƴ���Ա");
		MenuItem addUserItem=menu.getItem(1);
		MenuItem rmUserItem=menu.getItem(2);
		addUserItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				final EditText et0=new EditText(GroupActivity.this);
				AlertDialog dlg0=new AlertDialog.Builder(GroupActivity.this).setTitle("�������û���")
						                                     .setView(et0)
						                                     .setPositiveButton("���", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	// TODO Auto-generated method stub
																	addingUser=et0.getText().toString();
																	FutureTask<Integer> future = new FutureTask<Integer>(addUserHandler);
													    			new Thread(future).start();
													    			try {
													    				int response=future.get();
																		if(response==2){
																			Toast toast = Toast.makeText( getApplicationContext() ,"��ӳɹ�",Toast.LENGTH_LONG);
																			toast.show();
																		}else if(response==1){
																			Toast toast = Toast.makeText( getApplicationContext() ,"��Ա�Ѿ���������",Toast.LENGTH_LONG);
																			toast.show();
																		}else{
																			Toast toast = Toast.makeText( getApplicationContext() ,"�û���������",Toast.LENGTH_LONG);
																			toast.show();
																		}
																	} catch (Exception e) {
																		// TODO Auto-generated catch block
																		Toast toast = Toast.makeText( getApplicationContext() ,"�������Ӵ����쳣",Toast.LENGTH_LONG);
																		toast.show();
																		e.printStackTrace();
																	}
																}
															  })													
						                                     .create();
				dlg0.show();
				return false;
			}
		});
		
		rmUserItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				final EditText et1=new EditText(GroupActivity.this);
				AlertDialog dlg1=new AlertDialog.Builder(GroupActivity.this).setTitle("�������û���")
						                                     .setView(et1)
						                                     .setPositiveButton("ɾ��", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	// TODO Auto-generated method stub
																	removingUser=et1.getText().toString();
																	FutureTask<Integer> future = new FutureTask<Integer>(rmUserHandler);
													    			new Thread(future).start();
													    			try {
													    				int response1=future.get();
																		if(response1==2){
																			Toast toast = Toast.makeText( getApplicationContext() ,"�Ƴ��ɹ�",Toast.LENGTH_LONG);
																			toast.show();
																		}else if(response1==1){
																			Toast toast = Toast.makeText( getApplicationContext() ,"�û���������",Toast.LENGTH_LONG);
																			toast.show();
																		}else{
																			Toast toast = Toast.makeText( getApplicationContext() ,"�û���������",Toast.LENGTH_LONG);
																			toast.show();
																		}
																	} catch (Exception e) {
																		// TODO Auto-generated catch block
																		Toast toast = Toast.makeText( getApplicationContext() ,"�������Ӵ����쳣",Toast.LENGTH_LONG);
																		toast.show();
																		e.printStackTrace();
																	}
																}
															  })													
						                                     .create();
				dlg1.show();
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.destroy();  
        if(mBMapMan!=null){  
                mBMapMan.destroy();  
                mBMapMan=null;  
        }  
		super.onDestroy();
		if(mLocationClient.isStarted())
			mLocationClient.stop();
		
	}
	@Override  
	protected void onPause(){  
	        mMapView.onPause();  
	        if(mBMapMan!=null){  
	               mBMapMan.stop();  
	        }  
	        super.onPause();  
	}
	
	@Override  
	protected void onResume(){  
	        mMapView.onResume();  
	        if(mBMapMan!=null){  
	                mBMapMan.start();  
	        }  
	       super.onResume();  
	}
	
	public class MyLocationListener implements BDLocationListener {
	    @Override
	   public void onReceiveLocation(BDLocation location) {
	      if (location == null)
	          return ;
	      
	      latitude=location.getLatitude();
	      longitude=location.getLongitude();
	      
	      myLocData.latitude = latitude;  
	      myLocData.longitude = longitude;  
	      myLocData.direction = 2.0f;  
	      myLocationOverlay.setData(myLocData);    
	      mMapView.refresh();  
	      
	      FutureTask<Integer> future = new FutureTask<Integer>(updateGPSHandler);
	      new Thread(future).start();  
	      try {
				if(future.get()==0){					
					Toast toast = Toast.makeText( getApplicationContext() ,"�ϴ�GPS����ʧ��",Toast.LENGTH_LONG);
					toast.show();
				}else{
					Toast toast = Toast.makeText( getApplicationContext() ,gpsdata,Toast.LENGTH_LONG);
					toast.show();
					String[] userdatas=gpsdata.split(";");
					txOverlay.removeAll();
					for(int i=0;i<userdatas.length;i++){
						String[] userdata=userdatas[i].split(":");
						if(userdata[0].equals(username))
							continue;
						GeoPoint p = new GeoPoint((int) (Double.valueOf(userdata[2]) * 1E6), (int) (Double.valueOf(userdata[1]) * 1E6));
						TextItem textItem = new TextItem();  
						textItem.text=userdata[0];
						textItem.pt=p;
						textItem.fontSize=25;
						textItem.fontColor=textColor;
						textItem.bgColor=bgColor;
						txOverlay.addText(textItem);
					}
					mMapView.refresh();  
				}
				
	      } catch (Exception e) {
				Toast toast = Toast.makeText( getApplicationContext() ,"�������Ӵ����쳣",Toast.LENGTH_LONG);
				toast.show();
				// TODO Auto-generated catch block
				//e.printStackTrace();
	      }
	      
	      //logMsg(sb.toString());
	    }
	    public void onReceivePoi(BDLocation poiLocation) {
	         
		}
	}
	
	Callable<Integer> updateGPSHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/updategps.py");
			connection =(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
			connection.connect();
			
			DataOutputStream out=new DataOutputStream(connection.getOutputStream());
			out.writeBytes("username="+username+"&groupname="+groupname+"&longitude="+longitude+"&latitude="+latitude+"\n");
			
			out.flush();
			out.close();
			
			//getResponse();
			BufferedReader br=null;
			br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String updateGpsResp=br.readLine();
			connection.disconnect();
			if("UpdateGPS OK!".equals(updateGpsResp)){
				gpsdata=br.readLine();
				return 1;				
			}else{
				return 0;
			}
			
		}
	};
	
	Callable<Integer> addUserHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/adduser.py");
			connection =(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
			connection.connect();
			
			DataOutputStream out=new DataOutputStream(connection.getOutputStream());
			out.writeBytes("username="+addingUser+"&groupname="+groupname+"\n");
			out.flush();
			out.close();
			
			//getResponse();
			BufferedReader br=null;
			br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String addUserResp=br.readLine();
			connection.disconnect();
			if("Add User OK!".equals(addUserResp)){
				return 2;
			}else if("Username already in group!".equals(addUserResp)){
				return 1;
			}else{
				return 0;
			}
			
		}
	};
	
Callable<Integer> rmUserHandler=new Callable<Integer>() {
		
		@Override
		public Integer call() throws Exception {
			// TODO Auto-generated method stub
			HttpURLConnection connection=null;
			URL  url=new URL("http://0.daoleme.duapp.com/rmuser.py");
			connection =(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");
			connection.connect();
			
			DataOutputStream out=new DataOutputStream(connection.getOutputStream());
			out.writeBytes("username="+removingUser+"&groupname="+groupname+"\n");
			out.flush();
			out.close();
			
			//getResponse();
			BufferedReader br=null;
			br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String rmUserResp=br.readLine();
			connection.disconnect();
			if("Remove User OK!".equals(rmUserResp)){
				return 2;
			}else if("Username not in group!".equals(rmUserResp)){
				return 1;
			}else
				return 0;
			
		}
	};
 
}
