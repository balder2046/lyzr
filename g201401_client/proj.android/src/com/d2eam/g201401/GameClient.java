/****************************************************************************
Copyright (c) 2010-2011 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package com.d2eam.g201401;

//import cn.SdkWrapper;

import org.cocos2dx.lib.Cocos2dxFakeActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHelper;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.app.ActivityManager;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.app.ActivityManager.MemoryInfo;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGBasicPushNotificationBuilder;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.horse.Tools;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Camera.PreviewCallback;

import java.security.MessageDigest;
import android.os.Handler;
import android.os.Message;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.Vector;
import java.util.Iterator;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.content.pm.PackageInfo;
import android.net.Uri;

//import com.dataeye.DCAgent;
import com.tendcloud.tenddata.TalkingDataGA;

public class GameClient extends Cocos2dxFakeActivity implements PreviewCallback  {

	private static final String TAG = "cocos2dx";
	private static GameClient sSelf = null;
	private static CCAudioPlayer audioPlayer = null;
	private static CCAudioRecoder audioRecorder = null;
	private static Camera camera = null;
	private static String platformStr = null;
	public static int platformId = 0;

	private static native void nativeAudioRecodeEnded(final byte [] recData, final int recSize, 
		final int sampleRate, final int channelConfig, final int audioFormat);
	public static native void nativeSetPCMStreamInThread(byte [] pcmStream, final int writeLength, 
		final int sampleRate, final int channelConfig, final int audioFormat);
	public static native void nativeOnPreviewFrame(final byte[] data, final int width, final int height);
	public static native void nativeOnLoginResult(final int retCode, final String userId, final String signStr);
	public static native void nativeTerminateProcess();
	public static native void nativeOnStop();
	public static native void nativeReturnToLogin();
	public static native void verifyChargeReceipt(final String transId, final String receipt);

	private String getGameLibname( Activity context ) {
		String path = context.getFilesDir().getParent();
		String sysname = path + "/lib/libcocos2dcpp.so";
		String myname = path + "/files/assets/g201401.so";
		File dst = new File(myname);  
		if ( !dst.exists() )
			return sysname;
		return myname;
	}

	private final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'}; 
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }	

	@Override
	protected void onCreate(final Activity context, final Bundle savedInstanceState) {
		Log.v("test2d", "onCreate activity = " +  context );

		sSelf = this;

		final int dir = context.getWindowManager().getDefaultDisplay().getRotation();
		if ( dir == 1 )
		 	context.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
		else if ( dir == 3 )
		 	context.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE );
		else
			context.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );		

		String fullpath = getGameLibname( context );
		Log.i( TAG, "load lib "+fullpath );
		System.load(fullpath);

		super.onCreate(context, savedInstanceState);

		context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
			WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		context.getWindow().setFlags(0x08000000, 0x08000000);	

		audioPlayer = new CCAudioPlayer(); 
		audioPlayer.begin();

		

		try {
			ApplicationInfo appInfo = context.getPackageManager()
	            .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
	    	platformId = appInfo.metaData.getInt("platformId");
	    	String appId = appInfo.metaData.get("appId").toString();
	    	Log.v(TAG, "platformId="+platformId+" appId="+appId);
	    	platformStr = "xx_"+platformId;
	       	//SdkWrapper.initSdk(Integer.parseInt(platformId), appId);
	       	SdkWrapper.initSdk(platformId, appId);
		} catch( Exception e ) {
			e.printStackTrace();
		}

		// TCAgent.init( context );
		TalkingDataGA.init( context, "9AAEE3DB61137433E86CD4BC91E16240", "xx_"+platformId );

		// 开启logcat输出，方便debug，发布时请关闭
		XGPushConfig.enableDebug(getContext(), true);
		// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
		// 如果需要绑定账号，请使用registerPush(getApplicationContext(),"account")版本
		// 具体可参考详细的开发指南
		// 传递的参数为ApplicationContext
		XGPushManager.registerPush(getContext());	
		Log.v("cocos2dx", "XGPushManager.registerPush and getToken="+XGPushConfig.getToken(getContext()));
	}

	@Override
	protected void onResume() {
		audioPlayer.onResume();
		super.onResume();
		//DCAgent.onResume( getContext() );
		TalkingDataGA.onResume( getContext() );
		SdkWrapper.onResume();
	}

	@Override
	protected void onPause() {
//		sSelf.queueEventGLView(new Runnable() {
//			@Override
//			public void run() {
//				GameClient.nativeOnStop();
//			}
//		});		
		if ( camera != null ) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		audioPlayer.onPause();
		super.onPause();
		//DCAgent.onPause( getContext() );
		TalkingDataGA.onPause( getContext() );
		SdkWrapper.onPause();
	}
	protected void onStart(){
		try{
			Method method = SdkWrapper.class.getDeclaredMethod("onStart");
			method.setAccessible(true);
			method.invoke(null);
		}
		catch(Exception e){
			
		}
	}
	//@Override
	protected void onRestart() {
		SdkWrapper.onRestart();
	}

	//@Override
	protected void onSaveInstanceState(Bundle outState) {
		//super.onSaveInstanceState(outState);
	}

	//@Override
	protected void onStop() {
		try{
			Method method = SdkWrapper.class.getDeclaredMethod("onStop");
			method.setAccessible(true);
			method.invoke(null);
		}
		catch(Exception e){
			
		}
	}

	//@Override
	protected void onDestroy() {
		audioPlayer.onDestroy();
		audioPlayer = null;
		SdkWrapper.onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}

	@Override
	protected Cocos2dxGLSurfaceView onCreateView() {
		Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(getContext());
		// GameClient should create stencil buffer
		glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);

		return glSurfaceView;
	}

	public static boolean isWifiConnected()	{
		WifiManager wifiMgr = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
		if ( wifiMgr == null )
			return false;
		WifiInfo connInfo = wifiMgr.getConnectionInfo();
		if ( connInfo == null )
			return false;
		return ( wifiMgr.isWifiEnabled() && connInfo.getIpAddress() != 0 );
	}

	public static void screenRotationEnabled( boolean enable ) {
		Activity activity = ( Activity )getContext();
		if ( enable ) {
			activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
		} else {
			final int dir = getContext().getWindowManager().getDefaultDisplay().getRotation();
			if ( dir == 1 )
			 	activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
			else if ( dir == 3 )
			 	activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE );
			else
				activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

			// activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LOCKED );
			// Configuration cfg = activity.getResources().getConfiguration();
			// if ( cfg.orientation == cfg.ORIENTATION_LANDSCAPE ) {
			// 	activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
			// } else {
			// 	activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
			// }
		}			
	}

	public static void lockScreenOrientation( int dir ) {
		Activity activity = ( Activity )getContext();
		if ( dir == 1 )
		 	activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
		else if ( dir == 3 )
		 	activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE );
		else
			activity.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
	}

	public static boolean beginAudioRecode( int recMaxSeconds ) {
		if ( audioRecorder != null )
			return false;

		audioRecorder = new CCAudioRecoder();
		if ( audioRecorder == null )
			return false;

		if ( !audioRecorder.beginRecode( recMaxSeconds ) ) {
			audioRecorder = null;
			return false;
		}

		return true;
	}

	public static boolean endAudioRecode() {
		if ( audioRecorder == null )
			return false;

		audioRecorder.endRecode();
		return true;
	}

	public static void onAudioRecodeEnded( final byte [] recBuffer, final int recSize, 
		final int sampleRate, final int channelConfig, final int audioFormat ) {
		sSelf.queueEventGLView(new Runnable() {
			@Override
			public void run() {
				// Log.v("cocos2dx", "rec ended with buffer size " + recSize + " buf=" + recBuffer.length);
				GameClient.audioRecorder = null;
				GameClient.nativeAudioRecodeEnded( recBuffer, recSize, sampleRate, channelConfig, audioFormat );
			}
		});
	}

	public static void playPCM( final byte [] pcmBuffer ) {
		CCAudioPlayer audioPlayer = new CCAudioPlayer(); 
		audioPlayer.begin( pcmBuffer );
	}

	public static String getToken() {
		Log.v("cocos2dx", "getToken="+XGPushConfig.getToken(getContext()));
		return XGPushConfig.getToken(getContext());
		//return "0123456789abcdef0123456789abcdef";
	}

	public static String getPlatformStr() {
		return platformStr;
	}

	public static int getVersionCode() {
		try {  
		    PackageInfo info = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);  
		    return info.versionCode;  
		} catch (Exception e) { 
		    e.printStackTrace();  
		}
		return -1;
	}		

	public static void logToTalkingData( String EVENT_ID, String EVENT_LABEL ) {
		// Log.v("cocos2dx", "logToTalkingData="+EVENT_ID+":"+EVENT_LABEL);
		// TCAgent.onEvent( getContext(), EVENT_ID, EVENT_LABEL );
	}

	public static Camera getCameraInstance(){ 
	    Camera c = null; 
	    try { 
	        c = Camera.open(); // try get
	    } 
	    catch (Exception e){ 
	        // Camera in use or not exists
	    } 
	    return c;
	}

	public static void showUrl( String url ) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW"); 
		Uri content_url = Uri.parse(url);
		intent.setData(content_url); 
		getContext().startActivity(intent);
	}

	@Override
	public void onPreviewFrame(final byte[] data, Camera camera) {
		final Size size = camera.getParameters().getPreviewSize();
		Log.v( "cocos2dx", "onPreviewFrame data.length="+data.length+" size="+size.width+":"+size.height );

		sSelf.queueEventGLView(new Runnable() {
			@Override
			public void run() {
				GameClient.nativeOnPreviewFrame( data, size.width, size.height );
			}
		});
	}

	public static boolean beginCamera() {
		if ( camera != null )
			return false;
		camera = getCameraInstance();
		if ( camera == null )
			return false;
		camera.setPreviewCallback(sSelf);
		camera.startPreview();
		return true;
	}

	public static void endCamera() {
		if ( camera != null ) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	public static void loginUseSdk() {
		getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SdkWrapper.loginUseSdk();
			}
		});
	}	

	public static void showPayView(final int amount, final long roleId, final String roleName, final String callbackInfo, final String chargeUrl) {
		getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SdkWrapper.showPayView(amount, roleId, roleName, callbackInfo, chargeUrl);
			}
		});
	}

	public static void submitGameInfo(final int infoType, final int serverId, final String serverName, final long roleId, final String roleName, final int roleLevel) {
		getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SdkWrapper.submitGameInfo(infoType, serverId, serverName, roleId, roleName, roleLevel);
			}
		});
	}

	public static void showUserCenter() {
		getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SdkWrapper.showUserCenter();
			}
		});
	}

	public static void showExitView() {
		getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SdkWrapper.showExitView();
			}
		});
	}

	public static void returnToLogin() {
		sSelf.queueEventGLView(new Runnable() {
			@Override
			public void run() {
				GameClient.nativeReturnToLogin();
			}
		});
	}

	public static void replaceXGPushTag(final String prevTag, final String newTag) {
		if (prevTag.equals(newTag))
			return;
		Log.v(TAG, "XG-PUSH remove "+prevTag+" add "+newTag);
		if (prevTag.length() != 0)
			XGPushManager.deleteTag(getContext(), prevTag);
		if (newTag.length() != 0)
			XGPushManager.setTag(getContext(), newTag);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try{
			Log.v("cocos2dx", "MainActivity, onActivityResult,requestCode="+requestCode+", resultCode="+resultCode);
			Method method = SdkWrapper.class.getDeclaredMethod("onActivityResult", new Class[] { int.class, int.class,Intent.class});
			method.setAccessible(true);
			method.invoke(null, new Object[] {requestCode, resultCode,data });
		}
		catch(Exception e){
			
		}
	}  	

	protected void onNewIntent(Intent intent) {
		try{
			Log.v("cocos2dx", "MainActivity, onNewIntent");
			Method method = SdkWrapper.class.getDeclaredMethod("onNewIntent", new Class[] {Intent.class});
			method.setAccessible(true);
			method.invoke(null, new Object[] {intent});
		}
		catch(Exception e){
			
		}
	}
}

