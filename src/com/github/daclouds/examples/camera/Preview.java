package com.github.daclouds.examples.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {

	SurfaceHolder holder;
	Camera camera;
	
	Preview(Context context) {
		super(context);
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(width, height);
		camera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			camera.release();
			camera = null;
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			data2sd(getContext(), data, System.currentTimeMillis() + ".jpg");
		} catch (Exception e) {
			Log.e(VIEW_LOG_TAG, e.toString());
		}
		
		camera.startPreview();
	}
	
	private void data2sd(Context context, byte[] data, String filename) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + filename);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			if (fos != null) fos.close();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			camera.takePicture(null, null, this);
		}
		return true;
	}
	
}
