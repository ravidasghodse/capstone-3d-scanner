package capstone.scanner;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

public class ScannerActivity extends Activity {
    /** Called when the activity is first created. */
	private PreView pv = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //TextView tv = new TextView(this);
        //tv.setText("Hello world!");
        Log.i("TAG", Integer.toString(Camera.getNumberOfCameras()));
        pv = new PreView(this);
        setContentView(pv);
    }

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		return pv.onKeyDown(keyCode, event);
//	}
//    
}
