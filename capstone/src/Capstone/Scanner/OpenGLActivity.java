package Capstone.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//filename: OpenGLTestHarnessActivity.java
public class OpenGLActivity extends Activity {
	private TouchView mTestHarness;

	private MenuItem mItemPreviewRGBA;
	private MenuItem mItemSave;
	private MenuItem mItemExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PointCloud.readModel("newpoint_1.txt");

		PointCloud.xlook = 0;
		PointCloud.ylook = 0;
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
		mItemPreviewRGBA = menu.add("Preview");
		mItemSave = menu.add("Save");
		mItemExit = menu.add("Exit");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("OpenGL", "Menu Item selected " + item);

		if (item == mItemPreviewRGBA) {
			Intent intent;
			intent = new Intent(OpenGLActivity.this, ScannerActivity.class);
			startActivity(intent);
		} else if (item == mItemExit) {
			// TODO
		} else if (item == mItemSave) {
			PointCloud.saveModel("model.txt");
		}
		return true;
	}
}
