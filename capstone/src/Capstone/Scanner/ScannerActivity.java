package Capstone.Scanner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Activity;
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
import android.view.Window;
import android.view.WindowManager;

public class ScannerActivity extends Activity {
	private static final String TAG = "Sample::Activity";

	public static final int VIEW_MODE_RGBA = 0;
	public static final int VIEW_MODE_START = 1;
	public static final int VIEW_MODE_STOP = 2;
	public static final int VIEW_MODE_OPENGL = 6;

	private MenuItem mItemPreviewRGBA;
	private MenuItem mItemStartStop;
	private MenuItem mItemChangeView;

	Queue<Bitmap> picQueue;

	Preview preview;
	ImageProcess imageProcess;

	int num;

	Handler timerUpdateHandler;
	boolean timelapseRunning = false;
	int currentTime = 0;
	final int MSECS_BETWEEN_PHOTOS = 1;

	public static int viewMode = VIEW_MODE_RGBA;

	private Runnable timerUpdateTask = new Runnable() {
		public void run() {
			if (currentTime < MSECS_BETWEEN_PHOTOS) {
				currentTime++;
			} else {
				preview.getCamera().takePicture(null, null, pic);
				currentTime = 0;
			}

			timerUpdateHandler.postDelayed(timerUpdateTask, 1000);
			// countdownTextView.setText("" + currentTime);
		}
	};

	public ScannerActivity() {
		num = 0;
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

		PointCloud.vertices = new float[1000];
		picQueue = new LinkedList<Bitmap>();
		timerUpdateHandler = new Handler();
		imageProcess = new ImageProcess();
		preview = new Preview(this);
		setContentView(preview);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "onCreateOptionsMenu");
		mItemPreviewRGBA = menu.add("Preview RGBA");
		mItemStartStop = menu.add("Start");
		mItemChangeView = menu.add("Change view");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "Menu Item selected " + item);
		if (item == mItemPreviewRGBA)
			viewMode = VIEW_MODE_RGBA;
		else if (item == mItemStartStop) {
			if (!timelapseRunning) {
				item.setTitle("Stop");
				timelapseRunning = true;
				timerUpdateHandler.post(timerUpdateTask);
			} else {
				item.setTitle("Start");
				timelapseRunning = false;
				timerUpdateHandler.removeCallbacks(timerUpdateTask);

				// int i = 0;
				// while (!picQueue.isEmpty()) {
				// savePicture(picQueue.poll(),
				// String.format("/capstone/camera%d.jpg", i++));
				// }

			}
		} else if (item == mItemChangeView) {
			Intent intent;
			if (viewMode == VIEW_MODE_RGBA) {
				intent = new Intent(ScannerActivity.this, OpenGLActivity.class);
				startActivity(intent);
				item.setTitle("Back");
			}
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
//			saveData(mBitmap, String.format("/capstone/camera%d.dat", num));
			num++;
			imageProcess.processFrame(mBitmap);

			preview.getCamera().startPreview();

			Log.d("takephoto", "lalalalalalal!");
		}
	};

	public void saveData(Bitmap mBitmap, String filename) {
		try {
			OutputStream outputStream = new FileOutputStream(Environment
							.getExternalStorageDirectory() + filename);
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
