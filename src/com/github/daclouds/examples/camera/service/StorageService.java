package com.github.daclouds.examples.camera.service;

import java.io.IOException;

import android.content.Context;

public interface StorageService {

	public void saveToSdCard(String filename, byte[] data, Context context) throws IOException;
	
}
