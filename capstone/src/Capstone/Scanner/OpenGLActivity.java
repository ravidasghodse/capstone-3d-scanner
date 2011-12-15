package Capstone.Scanner;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

//filename: OpenGLTestHarnessActivity.java
public class OpenGLActivity extends Activity {
	private TouchView mTestHarness;

//	private MenuItem mItemBack;
	private MenuItem mItemSave;
	private MenuItem mItemCloudRender;
	private MenuItem mItemLineRender;
	private MenuItem mItemSurfaceRender;
	
	public static final int MODE_CLOUD = 0;
	public static final int MODE_LINE = 1;
	public static final int MODE_SURFACE = 2;
	public static int renderMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		PointCloud.readModel("newpoint_1.txt");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		PointCloud.xlook = 0;
		PointCloud.ylook = 0;
		renderMode = MODE_CLOUD;
//		cloudRender = new CloudRenderer(this);
//		lineRender = new LineRenderer(this);
//		surfaceRender = new SurfaceRenderer(this);
		
		mTestHarness = new TouchView(this);
		mTestHarness.setEGLConfigChooser(false);
		mTestHarness.setRenderer(new CloudRenderer(this));
		// mTestHarness.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		mTestHarness.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		setContentView(mTestHarness);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mTestHarness.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mTestHarness.onPause();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("OpenGL", "onCreateOptionsMenu");
		mItemCloudRender = menu.add("Cloud");
		mItemLineRender = menu.add("Line");
		mItemSurfaceRender = menu.add("Surface");
		mItemSave = menu.add("Save");
//		mItemExit = menu.add("Back");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("OpenGL", "Menu Item selected " + item);

		if (item == mItemCloudRender) {
			renderMode = MODE_CLOUD;
		} else if(item == mItemLineRender) {
			renderMode = MODE_LINE;
		} else if(item == mItemSurfaceRender) {
			renderMode = MODE_SURFACE;
		} else if (item == mItemSave) {
			PointCloud.saveModel("model.txt");
		}
		return true;
	}
}
