package Capstone.Scanner;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

public class TouchView extends GLSurfaceView {
	private static final float ZOOMJUMP = 100;
	private static final float ZOOMFACTOR = 0.3f;
	private static final float MOVEFACTOR = 0.6F;

	private float initialX;
	private float initialY;
	
	private float origDist;
	private boolean ignoreLastFinger;
	private float initialXLook;
	private float initialYLook;
	public TouchView(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		int action = event.getAction();
        Log.v("touch", "in onTouchEvent, action is " + event.getAction());
        
    	int action = event.getAction() & MotionEvent.ACTION_MASK;
    	if (event.getPointerCount() == 3) {
    		if (action == MotionEvent.ACTION_POINTER_DOWN)
    			PointCloud.xDist = PointCloud.yDist = PointCloud.zoomDist = 
    				PointCloud.xlook = PointCloud.ylook = 0f;
    	}
		if (event.getPointerCount() == 2) {
			switch(action) {
        	case MotionEvent.ACTION_POINTER_DOWN:
        		// Second finger down
        		origDist = calculateSeparation(event);
				initialX = calculateX(event);
				initialY = calculateY(event);
				initialXLook = PointCloud.xDist;
				initialYLook = PointCloud.yDist;
				Log.i("touch", "fuck!!!");
        		break;
        	case MotionEvent.ACTION_POINTER_UP:
        		// We're ending a pinch so prepare to
        		// ignore the last finger while it's the
        		// only one still down.
        		ignoreLastFinger  = true;
        		break;
        	case MotionEvent.ACTION_MOVE:
        		// We're in a pinch so decide if we need to change
        		// the zoom level.
        		float newSeparation = calculateSeparation(event);
        		if(Math.abs(newSeparation - origDist) > ZOOMJUMP) {
        			PointCloud.zoomDist += (newSeparation - origDist) * ZOOMFACTOR;
        			origDist = newSeparation;
        		} else {
        			PointCloud.xDist = initialXLook - (initialX - calculateX(event)) * MOVEFACTOR;
        			PointCloud.yDist = initialYLook - (initialY - calculateY(event)) * MOVEFACTOR;
        		}
        		break;
			}
		} else {
	        if(ignoreLastFinger) {
	            if(action == MotionEvent.ACTION_UP)
	            	ignoreLastFinger = false;
	        	return true;
	        }
			switch(action) {
			case MotionEvent.ACTION_DOWN:
				Log.i("touch", "DOWN");
				// Need to remember where the initial starting point
				// center is of our Dot and where our touch starts from
				initialXLook = PointCloud.xlook;
				initialYLook = PointCloud.ylook;
				initialX = event.getX();
				initialY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				PointCloud.xlook = initialXLook - (event.getX() - initialX);
				PointCloud.ylook = initialYLook - (event.getY() - initialY);
				break;
			}
		}
		


		return(true);
	}
	private float calculateSeparation(MotionEvent e) {
		float x = e.getX(0) - e.getX(1);
		float y = e.getY(0) - e.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	private float calculateX(MotionEvent e) {
		return (e.getX(0) + e.getX(1)) / 2.0f;
	}
	private float calculateY(MotionEvent e) {
		return (e.getY(0) + e.getY(1)) / 2.0f;
	}
}
