// root activity for dynamic loader

package com.d2eam.g201401;

import java.io.File;
import java.io.InputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.lang.reflect.Method;
import dalvik.system.DexClassLoader;
import android.app.Activity;
import android.os.Bundle;
import android.content.res.Configuration;
import android.util.Log;
import android.content.res.AssetManager;
import java.io.InputStreamReader;
import android.content.Intent;

public class g201401_loader extends Activity {

	private Object instance = null;
	private static Class localClass = null;
	private static Activity activity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("cocos2dx", "onCreate");
		super.onCreate(savedInstanceState);

		// Log.i("cocos2dx", "localClass = " + localClass + "instance = " + instance);
		// if ( localClass != null && instance != null ) {
		if ( activity != null && activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			// Intent intent = new Intent(activity, g201401_loader.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			// GameClient.getContext().startActivity(intent); 
			this.finish();
			return;
		}
		
		activity = this;

		String path = getFilesDir().getParent();
		AssetManager assetManager = getAssets();
		InputStream inputStream = null;  
        try {  
            inputStream = assetManager.open("apkVersion");  
            BufferedReader br0 =new BufferedReader(new InputStreamReader(inputStream));
            final String apkVersion = br0.readLine();
        	Log.v("cocos2dx", "apkVersion(" + apkVersion + ")");
            String markVersion = "null";
            try {
	            File file = new File(path + "/files/assets/markVersion");
	            if (file.exists()) {
	            	FileReader fr = new FileReader(file);
	            	BufferedReader br = new BufferedReader(fr);
	            	markVersion = br.readLine();
	        		Log.v("cocos2dx", "versionMark(" + markVersion + ")");
	            }
	        } catch (Exception e) {
    			Log.v("cocos2dx", "versionMark get exception");
	            e.printStackTrace();  
	        }
        	if (!markVersion.equals(apkVersion)) {
    			Log.v("cocos2dx", "apkVersion("+apkVersion+") != markVersion("+markVersion+") do removeDir");
		        try {  
					DeleteDirectory.removeDir(path + "/files/assets");
		        } catch (Exception e) {  
        			Log.v("cocos2dx", "removeDir get exception");
		            e.printStackTrace();  
		        }                  
		        try {  
					File f2 = new File(path + "/files/assets");
	                if (!f2.exists())
	                	f2.mkdir();
        			Log.v("cocos2dx", "create files/assets succeeded");
		        } catch (Exception e) {  
        			Log.v("cocos2dx", "create files/assets failed");
		            e.printStackTrace();  
		        }                  
		        try {  
		            FileWriter writer = new FileWriter(path + "/files/assets/markVersion");  
		            writer.write(apkVersion);  
		            writer.close();  
        			Log.v("cocos2dx", "write markVersion succeeded");
		        } catch (Exception e) {  
        			Log.v("cocos2dx", "write markVersion failed");
		            e.printStackTrace();  
		        }                  
			} else {
    			Log.v("cocos2dx", "ignore removeDir");
    		}
        } catch (Exception ex) {  
        	Log.v("cocos2dx", "removeDir get exception");
			ex.printStackTrace();
        } 

		try {
			if ( localClass == null ) {
				File file = new File( path + "/files/assets/g201401.jar" );
				if(file.exists()) { 
					Log.i( "cocos2dx", "load g201401.jar from assets" );
					DexClassLoader cl = new DexClassLoader(file.toString(), getFilesDir().getAbsolutePath(), null, ClassLoader.getSystemClassLoader().getParent());  
					localClass = cl.loadClass("com.d2eam.g201401.GameClient");
				} else {
					Log.i( "cocos2dx", "load raw classes.dex" );
					localClass = Class.forName("com.d2eam.g201401.GameClient");
				}
			}

			instance = localClass.newInstance();
			Method _onCreate = localClass.getDeclaredMethod("onCreate", new Class[] { Activity.class, Bundle.class });  
			_onCreate.setAccessible(true);  
			_onCreate.invoke(instance, new Object[] { this, savedInstanceState });  
		} catch (Exception ex) {     
			ex.printStackTrace();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		Log.v("cocos2dx", "onConfigurationChanged");
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		super.onConfigurationChanged(config);
		try {  
			Method _onConfigurationChanged = localClass.getDeclaredMethod("onConfigurationChanged", new Class[] { Configuration.class });  
			_onConfigurationChanged.setAccessible(true);  
			_onConfigurationChanged.invoke(instance, new Object[] { config });  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}

	@Override  
	public void onSaveInstanceState(Bundle outState) {  
		Log.v("cocos2dx", "onSaveInstanceState");
		super.onSaveInstanceState(outState);  
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method _onSaveInstanceState = localClass.getDeclaredMethod("onSaveInstanceState", new Class[] { Bundle.class });  
			_onSaveInstanceState.setAccessible(true);  
			_onSaveInstanceState.invoke(instance, new Object[] { outState });  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  

	@Override  
	protected void onStart() {  
		Log.v("cocos2dx", "onStart");
		super.onStart();  
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method start = localClass.getDeclaredMethod("onStart");  
			start.setAccessible(true);  
			start.invoke(instance);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  
  
	@Override  
	protected void onResume() {  
		Log.v("cocos2dx", "onResume");
		super.onResume();  
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method resume = localClass.getDeclaredMethod("onResume");  
			resume.setAccessible(true);  
			resume.invoke(instance);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  
  
	@Override  
	protected void onRestart() {  
		Log.v("cocos2dx", "onRestart");
		super.onRestart();  
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method restart = localClass.getDeclaredMethod("onRestart");  
			restart.setAccessible(true);  
			restart.invoke(instance);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  

	@Override  
	protected void onPause() {  
		Log.v("cocos2dx", "onPause");
		super.onPause();  
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method pause = localClass.getDeclaredMethod("onPause");  
			pause.setAccessible(true);  
			pause.invoke(instance);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}
		//这个地方特殊处理，如果是37wan平台，直接终止进程
		if(this.isFinishing() == true && GameClient.platformId == 19){
			activity = null;
			android.os.Process.killProcess(android.os.Process.myPid());
		}   
	}  
  
	@Override  
	protected void onStop() {  
		Log.v("cocos2dx", "onStop");
		super.onStop();  
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method stop = localClass.getDeclaredMethod("onStop");  
			stop.setAccessible(true);  
			stop.invoke(instance);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  
  
	@Override  
	protected void onDestroy() {  
		Log.v("cocos2dx", "onDestroy");
		super.onDestroy();  
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		activity = null;
		try {  
			Method des = localClass.getDeclaredMethod("onDestroy");  
			des.setAccessible(true);  
			des.invoke(instance);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.v("cocos2dx", "onNewIntent");
		super.onNewIntent(intent);
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method method = localClass.getDeclaredMethod("onNewIntent", new Class[] {Intent.class});  
			method.setAccessible(true);  
			method.invoke(instance, new Object[] {intent});
		} catch (Exception e) {  
			e.printStackTrace();  
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("cocos2dx", "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		if ( activity != this ) {
			Log.i("cocos2dx", "get invalid activity");
			return;
		}
		try {  
			Method method = localClass.getDeclaredMethod("onActivityResult", new Class[] { int.class, int.class,Intent.class});  
			method.setAccessible(true);  
			method.invoke(instance, new Object[] {requestCode, resultCode,data });
		} catch (Exception e) {  
			e.printStackTrace();  
		}
	}  	
}
