package Capstone.Scanner;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

//filename: OpenGLTestHarnessActivity.java
public class OpenGLActivity extends Activity {
	   private TouchView mTestHarness;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        PointCloud.vertices = new float[6];
	        for (int i =0; i < 6; i++){
	        	PointCloud.vertices[i] = (float) (0.1*i + 1);
	        }
			PointCloud.xlook = 0;
			PointCloud.ylook = 0;
	        mTestHarness = new TouchView(this);
	        mTestHarness.setEGLConfigChooser(false);
	        mTestHarness.setRenderer(new CloudRenderer(this));
	        //mTestHarness.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	        mTestHarness.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	        setContentView(mTestHarness);
	    }
	    @Override
	    protected void onResume()    {
	        super.onResume();
	        mTestHarness.onResume();
	    }
	    @Override
	    protected void onPause() {
	        super.onPause();
	        mTestHarness.onPause();
	    }
	}
