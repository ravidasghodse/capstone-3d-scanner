package Capstone.Scanner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ScannerActivity extends Activity {
	private static final String TAG = "Sample::Activity";

	private static final int DIALOG_PAUSED_ID = 0;

	private MenuItem mItemModelPreview;
	private MenuItem mItemExit;
	
	private Preview preview;
	private ImageProcess imageProcess;

	private int num;
	Handler timerUpdateHandler;
	boolean timelapseRunning = false;
	int currentTime = 0;
	final int MSECS_BETWEEN_PHOTOS = 1;

	private Runnable timerUpdateTask = new Runnable() {
		public void run() {
			if (currentTime < MSECS_BETWEEN_PHOTOS) {
				currentTime++;
			} else {
				preview.getCamera().takePicture(null, null, pic);
				currentTime = 0;
			}

			timerUpdateHandler.postDelayed(timerUpdateTask, 900);
		}
	};

	public ScannerActivity() {
		num = 0;
		PointCloud.curLine = 0;
		
		timerUpdateHandler = new Handler();
		imageProcess = new ImageProcess();
		
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

		preview = new Preview(this);
		preview.setClickable(true);
		preview.setOnClickListener(onClick);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(preview);
	}

	View.OnClickListener onClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.i(null, "screen clicked");
			if (!timelapseRunning) {
				Toast.makeText(getApplicationContext(), "Begin scanning",
						Toast.LENGTH_SHORT).show();
				timelapseRunning = true;
				timerUpdateHandler.post(timerUpdateTask);
			} else {
				// showDialog(DIALOG_PAUSED_ID);
				Toast.makeText(getApplicationContext(), "Stop scanning",
						Toast.LENGTH_SHORT).show();
				timelapseRunning = false;
				timerUpdateHandler.removeCallbacks(timerUpdateTask);
			}
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "onCreateOptionsMenu");
		mItemModelPreview = menu.add("Model Preview");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "Menu Item selected " + item);

		if (item == mItemModelPreview) {
			Intent intent;
			intent = new Intent(ScannerActivity.this, OpenGLActivity.class);
			startActivity(intent);
		} else if (item == mItemExit) {
			finish();
		}
		
		return true;
	}

	public Camera.PictureCallback pic = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("takephoto", "lalalalalalal!");

			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inDither = true;
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opt.inSampleSize = 2;
			Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0,
					data.length, opt);

			Log.d("config", mBitmap.getConfig().toString());
			Log.d("bitmap", String.format("col: %d  row: %d\n",
					mBitmap.getWidth(), mBitmap.getHeight()));

			// picQueue.add(mBitmap);
			savePicture(mBitmap, String.format("/capstone/camera%d.jpg", num));
			// saveData(mBitmap, String.format("/capstone/camera%d.dat", num));
			num++;
			imageProcess.processFrame(mBitmap);

			preview.getCamera().startPreview();

			Log.d("takephoto", "lalalalalalal!");
		}
	};

	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_PAUSED_ID:
			// do the work to define the pause Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure to stop scanning?")
					.setCancelable(false)
					.setPositiveButton("resume",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							})
					.setNegativeButton("stop",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									timelapseRunning = false;
									timerUpdateHandler
											.removeCallbacks(timerUpdateTask);
								}
							});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	public void saveData(Bitmap mBitmap, String filename) {
		try {
			OutputStream outputStream = new FileOutputStream(
					Environment.getExternalStorageDirectory() + filename);
			Mat mRgba = Utils.bitmapToMat(mBitmap);

			byte[] data = new byte[(int) mRgba.total()];
			mRgba.get(0, 0, data);

			outputStream.write(data);
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void savePicture(Bitmap mBitmap, String filename) {
		File imagefile = new File(
				android.os.Environment.getExternalStorageDirectory() + filename);
		try {
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(imagefile));
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
