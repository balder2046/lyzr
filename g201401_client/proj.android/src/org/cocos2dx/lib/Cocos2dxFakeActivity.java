/****************************************************************************
Copyright (c) 2010-2013 cocos2d-x.org

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
package org.cocos2dx.lib;

import org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.FrameLayout;
import android.view.View;
import android.view.Gravity;

public abstract class Cocos2dxFakeActivity implements Cocos2dxHelperListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String TAG = Cocos2dxFakeActivity.class.getSimpleName();

	// ===========================================================
	// Fields
	// ===========================================================

	private Cocos2dxGLSurfaceView mGLSurfaceView;
	private Cocos2dxHandler mHandler;
	private static Activity sContext = null;
	private static View sContentView = null;
	public static int mScreenDir = 0;

	public static Activity getContext() {
		return sContext;
	}

	public static View getContentView() {
		return sContentView;
	}

	// ===========================================================
	// Constructors
	// ===========================================================
	protected void onCreate(final Activity content, final Bundle savedInstanceState) {
		sContext = content;
		this.mHandler = new Cocos2dxHandler(content);

		this.init();

		Cocos2dxHelper.init(content, this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	private static native void nativeOnDeviceOrientationChanged(final int dir);
	public static native void nativeOnScreenOffsetChanged(final int offset);

	protected void onConfigurationChanged(Configuration config) {
		Log.v(TAG, "the onConfigurationChanged");

		mScreenDir = sContext.getWindowManager().getDefaultDisplay().getRotation();
		Cocos2dxFakeActivity.nativeOnDeviceOrientationChanged(mScreenDir);
	}

	protected void onResume() {
		Cocos2dxHelper.onResume();
		this.mGLSurfaceView.onResume();
	}

	protected void onPause() {
		Cocos2dxHelper.onPause();
		this.mGLSurfaceView.onPause();
	}

	protected void onDestroy() {
		Cocos2dxHelper.onDestroy();
	}

	@Override
	public void showDialog(final String pTitle, final String pMessage) {
		Message msg = new Message();
		msg.what = Cocos2dxHandler.HANDLER_SHOW_DIALOG;
		msg.obj = new Cocos2dxHandler.DialogMessage(pTitle, pMessage);
		this.mHandler.sendMessage(msg);
	}

	@Override
	public void showEditTextDialog(final String pTitle, final String pContent, final int pInputMode, final int pInputFlag, final int pReturnType, final int pMaxLength) {
		Message msg = new Message();
		msg.what = Cocos2dxHandler.HANDLER_SHOW_EDITBOX_DIALOG;
		msg.obj = new Cocos2dxHandler.EditBoxMessage(pTitle, pContent, pInputMode, pInputFlag, pReturnType, pMaxLength);
		this.mHandler.sendMessage(msg);
	}

	@Override
	public void runOnGLThread(final Runnable pRunnable) {
		this.mGLSurfaceView.queueEvent(pRunnable);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void init() {

		// FrameLayout
		ViewGroup.LayoutParams framelayout_params =
		    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
		                               ViewGroup.LayoutParams.FILL_PARENT);
		FrameLayout framelayout = new FrameLayout(sContext);
		framelayout.setLayoutParams(framelayout_params);

		// Cocos2dxEditText layout
		FrameLayout.LayoutParams edittext_layout_params =
		    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
		                               FrameLayout.LayoutParams.WRAP_CONTENT);
		Cocos2dxEditText edittext = new Cocos2dxEditText(sContext);
		edittext_layout_params.gravity = Gravity.BOTTOM;
		edittext.setLayoutParams(edittext_layout_params);

		// ...add to FrameLayout
		framelayout.addView(edittext);

		// Cocos2dxGLSurfaceView
		this.mGLSurfaceView = this.onCreateView();

		// ...add to FrameLayout
		framelayout.addView(this.mGLSurfaceView);

		// Switch to supported OpenGL (ARGB888) mode on emulator
		if (isAndroidEmulator())
			this.mGLSurfaceView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);

		mScreenDir = sContext.getWindowManager().getDefaultDisplay().getRotation();
		this.mGLSurfaceView.setCocos2dxRenderer(new Cocos2dxRenderer());
		this.mGLSurfaceView.setCocos2dxEditText(edittext);

		// Set framelayout as the content view
		sContext.setContentView(framelayout);
		sContentView = framelayout;
	}

	protected Cocos2dxGLSurfaceView onCreateView() {
		return new Cocos2dxGLSurfaceView(sContext);
	}

	private final static boolean isAndroidEmulator() {
		String model = Build.MODEL;
		Log.d(TAG, "model=" + model);
		String product = Build.PRODUCT;
		Log.d(TAG, "product=" + product);
		boolean isEmulator = false;

		if (product != null) {
			isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
		}

		Log.d(TAG, "isEmulator=" + isEmulator);
		return isEmulator;
	}

	public void queueEventGLView( Runnable event ) {
		mGLSurfaceView.queueEvent( event );
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
