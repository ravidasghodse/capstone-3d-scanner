package com.Photo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class PhotoActivity extends Activity {
    /** Called when the activity is first created. */
	private PreView pv = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pv = new PreView(this);
        setContentView(pv);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return pv.onKeyDown(keyCode, event);
	}
    
}
