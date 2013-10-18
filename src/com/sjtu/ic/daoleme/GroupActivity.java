package com.sjtu.ic.daoleme;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	private String groupname=null;
	private String gpsdata=null;
	private double longitude=0;
	private double latitude=0;
	private double locationLng=0;
	private double locationLat=0;
	private Symbol.Color textColor=null;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	public BMapManager mBMapMan = null;  
	public MapView mMapView = null; 
	
	public MyLocationOverlay myLocationOverlay=null;
	public TextOverlay txOverlay=null;
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
		myLocationOverlay = new MyLocationOverlay(mMapView); 
		
		Symbol textSymbol = new Symbol();    
		textColor = textSymbol.new Color();    
		textColor.alpha = 150;    
		textColor.red = 255;    
		textColor.blue = 0;    
		textColor.green = 0;   
		
		TextItem ti = new TextItem();
		ti.pt=point;
		ti.text=groupname;
		ti.fontColor=textColor;
		txOverlay.addText(ti);
		
		mMapView.getOverlays().add(myLocationOverlay);
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
					for(int i=1;i<userdatas.length;i++){
						String[] userdata=userdatas[i].split(":");
						GeoPoint p = new GeoPoint((int) (Double.valueOf(userdata[2]) * 1E6), (int) (Double.valueOf(userdata[1]) * 1E6));
						TextItem textItem = new TextItem();  
						textItem.text=userdata[0];
						textItem.pt=p;
						textItem.fontColor=textColor;
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
	
 
}
