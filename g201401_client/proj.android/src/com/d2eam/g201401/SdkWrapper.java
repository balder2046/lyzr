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

import cn.SpecSdkWrapper;
import cn.xxwan.sdkall.frame.eneity.BelostInfo;
import cn.xxwan.sdkall.frame.eneity.XXLoginCallbackInfo;
import cn.xxwan.sdkall.frame.eneity.XXPaymentCallbackInfo;
import cn.xxwan.sdkall.frame.eneity.XXSdkQuitInfo;
import cn.xxwan.sdkall.frame.eneity.XXWanSDKGameInfo;
import cn.xxwan.sdkall.frame.eneity.XXWanSDKLoginInfo;
import cn.xxwan.sdkall.frame.eneity.XXWanSDKPayInfo;
import cn.xxwan.sdkall.frame.listener.OnXXwanAPiListener;

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
		SpecSdkWrapper.initSdk(GameClient.getContext(), platformId, new OnXXwanAPiListener() {

			@Override
			public void onSuccess(Object t, int code) {
				// TODO Auto-generated method stub
				//可以不作处理
				Log.v(TAG, "SpecSdkWrapper.initSdk onSuccess");
			}
			
			@Override
			public void onFial(String msg, int code) {
				// TODO Auto-generated method stub
				Log.v(TAG, "SpecSdkWrapper.initSdk onFial");
				//context.finish();
				GameClient.nativeTerminateProcess();
			}
		});
		
		//设置各个接口的监听
		SpecSdkWrapper.getInstance(GameClient.getContext()).setMexitListener(exitListener);
		SpecSdkWrapper.getInstance(GameClient.getContext()).setMloginListener(loginlistener);
		SpecSdkWrapper.getInstance(GameClient.getContext()).setMpaymentListener(payListener);
		SpecSdkWrapper.getInstance(GameClient.getContext()).setMLogoutListener(logoutlistener);		
	}

	public static void onRestart() {
		SpecSdkWrapper.getInstance(GameClient.getContext()).showPauseView(GameClient.getContext(), pauselistener);
	}

	public static void onResume() {
		SpecSdkWrapper.getInstance(GameClient.getContext()).handlerToolFloat(GameClient.getContext(), true);
	}

	public static void onPause() {
		SpecSdkWrapper.getInstance(GameClient.getContext()).handlerToolFloat(GameClient.getContext(), false);
	}

	public static void onDestroy() {
		SpecSdkWrapper.getInstance(GameClient.getContext()).doDestoryOut(GameClient.getContext());
	}

	public static OnXXwanAPiListener beloseListener = new OnXXwanAPiListener() {

		@Override
		public void onSuccess(Object t, int code) {
			BelostInfo loseinfo = (BelostInfo) t;
			if (loseinfo != null) {
				Log.v(TAG, loseinfo.desc + ", beloseListener onSuccess ,code = " + code);
			}
			switch (loseinfo.statusCode) {
			case 1://该用户未成年 在线时长超过3小时，且该接口 30分钟回调一次
				//提醒用户并将用户获取资源的减半
				// Toast.makeText(GameClient.getContext(), loseinfo.desc,
				// 		Toast.LENGTH_SHORT).show();
				break;

			case 2://该用户未成年 在线时长超过5小，  且该接口15分钟回调一次
				//提醒用户 且 用户随后不会获得额外奖励
				// Toast.makeText(GameClient.getContext(), loseinfo.desc,
				// 		Toast.LENGTH_SHORT).show();
			    break;
			case 3://该用户未实名注册 进行实名注册 弹出实名注册框
				// Toast.makeText(GameClient.getContext(), loseinfo.desc,
				// 		Toast.LENGTH_SHORT).show();
			    break;
			case 4://该用户完成实名注册 实名注册框消失
				// Toast.makeText(GameClient.getContext(), loseinfo.desc,
				// 		Toast.LENGTH_SHORT).show();
				//该接口只是说明完成注册 是否注册成功未说明   
				//建议再次调用防沉迷接口  查询是否进行了实名注册          
			    break;
			}
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", beloseListener onFial ,code = " + code);
			//ToastShow(msg, 1000);
		}
	};

	public static OnXXwanAPiListener pauselistener = new OnXXwanAPiListener() {

		@Override
		public void onSuccess(Object t, int code) {
			String msg = (String) t;
			if (msg != null) {
				Log.v(TAG, msg + ", pauselistener onSuccess,code = " + code);
				//ToastShow(msg, 1000);
			}
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", beloseListener onFial ,code = " + code);
			//ToastShow(msg, 1000);

		}
	};

	public static OnXXwanAPiListener switchlistener = new OnXXwanAPiListener() {

		@Override
		public void onSuccess(Object t, int code) {
			XXLoginCallbackInfo msg = (XXLoginCallbackInfo) t;
			if (msg != null) {
				Log.v(TAG, msg + ", switchlistener onSuccess ,code = " + code);
			}
			// logininfo.setText(msg.toString());
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", switchlistener onFial ,code = " + code);
			//ToastShow(msg, 1000);
		}
	};

	public static OnXXwanAPiListener payListener = new OnXXwanAPiListener() {
		@Override
		public void onSuccess(Object t, int code) {
			switch (code) {
			case 0:
				XXPaymentCallbackInfo msg = (XXPaymentCallbackInfo) t;
				if (msg != null) {
					Log.v(TAG, msg.toString() + ", payListener onSuccess 0 , code = " + code);
					//ToastShow(msg.toString(), 1000);
					// payresultinfo.setText(msg.toString());
				}
				break;

			case 1:
				XXLoginCallbackInfo callbackInfo = (XXLoginCallbackInfo) t;
				if (callbackInfo != null) {
					Log.v(TAG, callbackInfo.toString() + ", payListener onSuccess 1, code = " + code);
					//ToastShow(callbackInfo.toString(), 1000);
					// logininfo.setText(callbackInfo.toString());
					// TODO 更新用户信息 更新时间戳 签名伐 这里一定要更新 亲~
					// TODO 如果没有设置 istimeoutpayment的话， 这里需要再次调用支付。
				}
				break;
			}
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", payListener onFial, code = " + code);
			//ToastShow(msg, 1000);
			// TODO 支付失败 处理 。。。
		}
	};

	public static OnXXwanAPiListener logoutlistener = new OnXXwanAPiListener() {

		@Override
		public void onSuccess(Object t, int code) {
			String msg = (String) t;
			if (msg != null) {
				Log.v(TAG, msg + ", logoutlistener onSuccess ,code = " + code);
				//ToastShow(msg, 1000);
				// org.cocos2dx.lib.Cocos2dxHelper.appRestart();
				// GameClient.nativeTerminateProcess();
				// Intent intent = new Intent(GameClient.getContext(), g201401_loader.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// GameClient.getContext().startActivity(intent); 
				userId = null;
				timestamp = null;
				sign = null;
				serverId = "0";
				GameClient.returnToLogin();
			}
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", logoutlistener onFial ,code = " + code);
			//ToastShow(msg, 1000);
		}
	};

	/**
	 * 退出监听
	 */
	public static OnXXwanAPiListener exitListener = new OnXXwanAPiListener() {

		@Override
		public void onSuccess(Object t, int code) {
			XXSdkQuitInfo msg = (XXSdkQuitInfo) t;
			if (msg != null) {
				Log.v(TAG, msg + ", exitListener onSuccess ,code = " + code);
				//ToastShow("盖世英雄", 1000);
			}
			if (msg.which == 2) {
				//getContext().finish();
				GameClient.nativeTerminateProcess();
			}
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", exitListener onFial ,code = " + code);
			//ToastShow(msg.toString(), 1000);
		}
	};

	/**
	 * 用户中心监听
	 */
	static public OnXXwanAPiListener userCenter = new OnXXwanAPiListener() {

		@Override
		public void onSuccess(Object t, int code) {
			String msg = (String) t;
			if (msg != null) {
				Log.v(TAG, msg + ", userCenter onSuccess ,code = " + code);
				//ToastShow(msg, 1000);
			}
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", userCenter onFial ,code = " + code);
			//ToastShow(msg, 1000);
		}
	};

	     /**
	     * 登录回调
	     */
	static public OnXXwanAPiListener loginlistener = new OnXXwanAPiListener() {

		@Override
		public void onSuccess(Object t, int code) {
			XXLoginCallbackInfo msg = (XXLoginCallbackInfo) t;
			if (msg != null) {
				Log.v(TAG, msg + ", loginlistener onSuccess ,code = " + code);
				if (msg.statusCode == 0) {
					userId = msg.userId;
					timestamp = msg.timestamp;
					sign = msg.sign;
				}
				GameClient.nativeOnLoginResult(msg.statusCode, msg.userId, "xxwan&"+platformId+"&"+msg.timestamp+"&"+msg.sign);
				SpecSdkWrapper.OnLoginSucceeded(GameClient.getContext());
			} else {
				GameClient.nativeOnLoginResult(-1, null, null);
			}
		}

		@Override
		public void onFial(String msg, int code) {
			Log.v(TAG, msg + ", loginlistener onFial ,code = " + code);
			GameClient.nativeOnLoginResult(code, null, msg);
		}
	};	

	public static void loginUseSdk() {
		if (userId != null && timestamp != null && sign != null)
			GameClient.nativeOnLoginResult(0, userId, "xxwan&"+platformId+"&"+timestamp+"&"+sign);
		else {
			XXWanSDKLoginInfo dkInitInfo = new XXWanSDKLoginInfo();
			dkInitInfo.setGameId(appId);
			dkInitInfo.setServerId(serverId);
			SpecSdkWrapper.getInstance(GameClient.getContext()).showLoginView(GameClient.getContext(), dkInitInfo);
		}
	}	

	public static void showPayView(int amount, long roleId, String roleName, String callbackInfo, String chargeUrl ) {
		if ( userId != null && timestamp != null && sign != null ) {
			XXWanSDKPayInfo payInfo = new XXWanSDKPayInfo();
			payInfo.setAmount(amount);
			payInfo.setCallBackInfo(callbackInfo);
			payInfo.setCallbackURL(chargeUrl+"/xxwan/"+platformId+"/payresult");
			payInfo.setFixed(true);
			payInfo.setRoleId(String.valueOf(roleId));
			payInfo.setRoleName(roleName);
			payInfo.setServerId(serverId);
			Log.v(TAG, "showPayView : " + "amount="+amount + " roleId="+roleId + " roleName="+roleName + 
				" fixed=true" + " serverId="+serverId + " callbackInfo="+callbackInfo + " chargeUrl="+chargeUrl+"/xxwan/"+platformId+"/payresult");
			SpecSdkWrapper.getInstance(GameClient.getContext()).showPayView(GameClient.getContext(), payInfo);
		}
	}

	public static void submitGameInfo(int infoType, int _serverId, String serverName, long roleId, String roleName, int roleLevel) {
		serverId = String.valueOf(_serverId);
		XXWanSDKGameInfo gameInfo = new XXWanSDKGameInfo();
		gameInfo.setInfoType(infoType);
		gameInfo.setServerId(serverId);
		gameInfo.setServerName(serverName);		
		gameInfo.setRoleId(String.valueOf(roleId));
		gameInfo.setRoleLevel(String.valueOf(roleLevel));
		gameInfo.setRoleName(roleName);
		Log.v(TAG, "submitGameInfo : " + "infoType="+infoType + " serverId="+serverId + " serverName="+serverName + 
			" roleId="+roleId + " roleName="+roleName + " roleLevel="+roleLevel);
		SpecSdkWrapper.getInstance(GameClient.getContext()).submitgameinfo(gameInfo);
	}

	public static void showUserCenter() {
		if ( userId != null && timestamp != null && sign != null )
			SpecSdkWrapper.getInstance(GameClient.getContext()).showUserCenter(GameClient.getContext());
	}

	public static void showExitView() {
		if ( userId != null && timestamp != null && sign != null )
			SpecSdkWrapper.getInstance(GameClient.getContext()).showExitView(GameClient.getContext());
		else
			GameClient.nativeTerminateProcess();
	}

	public static void dewallow() {
		SpecSdkWrapper.getInstance(GameClient.getContext()).beaddicted(GameClient.getContext(), beloseListener);
	} 
}

