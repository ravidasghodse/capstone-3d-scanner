package Capstone.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class ScannerActivity extends Activity {
    private static final String TAG                = "Sample::Activity";

    public static final int     VIEW_MODE_RGBA     = 0;
    public static final int     VIEW_MODE_GRAY     = 1;
    public static final int     VIEW_MODE_CANNY    = 2;
    public static final int     VIEW_MODE_FINDSPOTS = 5;
    public static final int     VIEW_MODE_OPENGL = 6;

    private MenuItem            mItemPreviewRGBA;
    private MenuItem            mItemPreviewGray;
    private MenuItem            mItemPreviewCanny;
    private MenuItem            mItemFindSpots;
    private MenuItem            mItemChangeView;

    public static int           viewMode           = VIEW_MODE_RGBA;

    public ScannerActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        setContentView(new View(this));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("Preview RGBA");
//        mItemPreviewGray = menu.add("Preview GRAY");
//        mItemPreviewCanny = menu.add("Canny");
        
        mItemFindSpots  = menu.add("Find spots");
        mItemChangeView = menu.add("Change view");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu Item selected " + item);
        if (item == mItemPreviewRGBA)
            viewMode = VIEW_MODE_RGBA;
//        else if (item == mItemPreviewGray)
//            viewMode = VIEW_MODE_GRAY;
//        else if (item == mItemPreviewCanny)
//            viewMode = VIEW_MODE_CANNY;
        else if (item == mItemFindSpots)
            viewMode = VIEW_MODE_FINDSPOTS;
        else if (item == mItemChangeView) {
        	Intent intent;
        	if(viewMode == VIEW_MODE_RGBA) {
        		intent = new Intent(ScannerActivity.this, OpenGLActivity.class);
        		startActivity(intent);
//        		viewMode = VIEW_MODE_OPENGL;
        	}
//        	if(viewMode == VIEW_MODE_OPENGL) {
//        		intent = new Intent(ScannerActivity.this, OpenGLActivity.class);
//        		startActivity(intent);
//        		viewMode = VIEW_MODE_RGBA;   		
//        	}
        }
        return true;
    }
}
