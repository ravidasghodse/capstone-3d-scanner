package Capstone.Scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.opencv.core.Point3;

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
	   
	    private MenuItem            mItemPreviewRGBA;
	    private MenuItem            mItemSave;
	    private MenuItem            mItemExit;
	   
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
//	        PointCloud.vertices = new float[3];
//	        for (int i = 0; i < 3; i++){
//	        	PointCloud.vertices[i] = 0;
//	        }
//	        PointCloud.cur = 3;
	        
			PointCloud.xlook = 0;
			PointCloud.ylook = 0;
	        mTestHarness = new TouchView(this);
	        mTestHarness.setEGLConfigChooser(false);
	        mTestHarness.setRenderer(new CloudRenderer(this));
//	        mTestHarness.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
	        } else if(item == mItemExit) {
	        	this.finish();
	        } else if(item == mItemSave) {
	        	File file = new File(android.os.Environment.getExternalStorageDirectory()
	    				+ "/capstone/" + "model.txt");
	        	
	    		try {
	    			if(!file.exists()){
	    				file.createNewFile();
	    			}
	    			
	    			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	    			for(int i = 0; i < PointCloud.cur; i+=3)
	    				writer.write(String.format("%.3f %.3f %.3f\n", PointCloud.vertices[i], PointCloud.vertices[i+1], PointCloud.vertices[i+2]));
	    			writer.flush();
	    			writer.close();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	        }
	        return true;
	    }
	}
