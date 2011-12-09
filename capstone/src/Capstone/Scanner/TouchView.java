package Capstone.Scanner;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class TouchView extends GLSurfaceView {
	private float initialX;
	private float initialY;
	private float offsetX;
	private float offsetY;

	public TouchView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.i("touch", "DOWN");
			// Need to remember where the initial starting point
			// center is of our Dot and where our touch starts from
			initialX = PointCloud.xlook;
			initialY = PointCloud.ylook;
			offsetX = event.getX();
			offsetY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			PointCloud.xlook = initialX + event.getX() - offsetX;
			PointCloud.ylook = initialY + event.getY() - offsetY;
			break;
		}
		return (true);
	}
}
