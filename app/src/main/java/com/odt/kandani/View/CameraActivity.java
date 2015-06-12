package com.odt.kandani.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.odt.kandani.Components.CameraPreview;
import com.odt.kandani.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity {
	private Camera mCamera;
	private CameraPreview mPreview;
	private PictureCallback mPicture;
	private FloatingActionButton capture, switchCamera;
	private Context myContext;
	private LinearLayout cameraPreview;
	private boolean cameraFront = false;
	private String picName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		myContext = this;
		initialize();
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}

	private int findBackFacingCamera() {
		int cameraId = -1;
		//Search for the back facing camera
		//get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		//for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	public void onResume() {
		super.onResume();
		if (!hasCamera(myContext)) {
			Toast toast = Toast.makeText(myContext, "PUNTEN! HAPE SIA GORENG, EWEUH KAMERAAN!", Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		if (mCamera == null) {
			//if the front facing camera does not exist
			//if the front facing camera does not exist
			if (findFrontFacingCamera() < 0) {
				Toast.makeText(this, "HAPE SIA EWEUH KAMERA HAREUPNA! TONG SOSOAN!", Toast.LENGTH_LONG).show();
				switchCamera.setVisibility(View.GONE);
			}			
			mCamera = Camera.open(findBackFacingCamera());
			mPicture = getPictureCallback();
			mPreview.refreshCamera(mCamera);
		}
	}

	public void initialize() {
		cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
		mPreview = new CameraPreview(myContext, mCamera);
		cameraPreview.addView(mPreview);

		capture = (FloatingActionButton) findViewById(R.id.button_capture);
		capture.setOnClickListener(captrureListener);

		switchCamera = (FloatingActionButton) findViewById(R.id.button_ChangeCamera);
		switchCamera.setOnClickListener(switchCameraListener);
	}

	OnClickListener switchCameraListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//get the number of cameras
			int camerasNumber = Camera.getNumberOfCameras();
			if (camerasNumber > 1) {
				//release the old camera instance
				//switch camera, from the front and the back and vice versa
				
				releaseCamera();
				chooseCamera();
			} else {
				Toast toast = Toast.makeText(myContext, "PUNTEN PISAN IEU MAH! KAMERA MANEH NGAN HIJI", Toast.LENGTH_LONG);
				toast.show();
			}
		}
	};

	public void chooseCamera() {
		//if the camera preview is the front
		if (cameraFront) {
			int cameraId = findBackFacingCamera();
			if (cameraId >= 0) {
				//open the backFacingCamera
				//set a picture callback
				//refresh the preview
				
				mCamera = Camera.open(cameraId);
				mPicture = getPictureCallback();			
				mPreview.refreshCamera(mCamera);
			}
		} else {
			int cameraId = findFrontFacingCamera();
			if (cameraId >= 0) {
				//open the backFacingCamera
				//set a picture callback
				//refresh the preview
				
				mCamera = Camera.open(cameraId);
				mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//when on Pause, release camera in order to be used from other applications
		releaseCamera();
	}

	private boolean hasCamera(Context context) {
		//check if the device has camera
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	private PictureCallback getPictureCallback() {
		PictureCallback picture = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				//make a new picture file
				File pictureFile = getOutputMediaFile();
				
				if (pictureFile == null) {
					return;
				}
				try {
					//write the file
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					//Toast toast = Toast.makeText(myContext, "ALUS SIA BISA MOTO GEUNING! TAH GEUS DI TEUNDEUN DI: " + pictureFile.getName(), Toast.LENGTH_LONG);
					//toast.show();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}

				Intent i = new Intent(CameraActivity.this, ReminderAddActivity.class);
				i.putExtra("tipe","Capture");
				i.putExtra("capture",pictureFile.getName());
				startActivity(i);
				finish();
				//mPreview.refreshCamera(mCamera);


			}
		};
		return picture;
	}

	OnClickListener captrureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCamera.autoFocus(myAutoFocusCallback);
			mCamera.takePicture(null, null, mPicture);
		}
	};

	Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			// TODO Auto-generated method stub
			capture.setEnabled(true);
		}};
	//make picture and save to a folder
	private static File getOutputMediaFile() {
		//make a new file directory inside the "sdcard" folder
		File mediaStorageDir = new File("/sdcard/", "Kandani Software");
		
		//if this "JCGCamera folder does not exist
		if (!mediaStorageDir.exists()) {
			//if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		
		//take the current timeStamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		//and make a media file:
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		
		return mediaFile;
	}

	private void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
}