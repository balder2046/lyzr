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



import org.cocos2dx.lib.Cocos2dxFakeActivity;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
//import com.d2eam.g201401.GameClient;

public class SdkWrapper {

	private static final String TAG = "cocos2dx";
	private static String userId = null;
	private static String timestamp = null;
	private static String sign = null;
	private static int platformId = 0;
	private static String appId = null;
	private static String serverId = "0";

	public static void initSdk(final int _platformId, final String _appId) {
		platformId = _platformId;
		appId = _appId;
		userId = null;
		timestamp = null;
		sign = null;
		serverId = "0";

		Log.v(TAG, "SpecSdkWrapper.initSdk platformId="+platformId);
	}

	public static void onRestart() {
	}

	public static void onResume() {
	}

	public static void onPause() {
	}

	public static void onDestroy() {
	}


	public static void showUserCenter() {
	}

	public static void showExitView() {
			GameClient.nativeTerminateProcess();
	}

	public static void dewallow() {
	}
}

