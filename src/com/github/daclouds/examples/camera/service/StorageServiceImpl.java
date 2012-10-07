package com.github.daclouds.examples.camera.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class StorageServiceImpl implements StorageService {

	public void saveToSdCard(String filename, byte[] data, Context context) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + filename);
			fos.write(data);
			fos.flush();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fos != null) fos.close();
		}
		
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi.isConnected()){
			saveToAppEngine(filename, data);
		}else{
//			TODO
//			Queue 에 쌓기... WIFI 연결되면 업로드
		}
	}

	public void saveToAppEngine(final String filename, byte[] data)
			throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://mgae101.appspot.com/uploadImage");
		try {
			MultipartEntity entity = new MultipartEntity();

			entity.addPart("location", new StringBody(""));
			entity.addPart("imageFile", new ByteArrayBody(data, "image/jpeg", filename));
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			Log.d(this.getClass().getSimpleName(), response.getStatusLine().toString());
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		}
	}

}
