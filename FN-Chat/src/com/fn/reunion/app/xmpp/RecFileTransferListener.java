package com.fn.reunion.app.xmpp;

import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.util.Log;

public class RecFileTransferListener implements FileTransferListener {
private String tag ="RecFileTransferListener";
	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		Log.i(tag, ".....");
		final IncomingFileTransfer inTransfer = request.accept();
		final String fileName = request.getFileName();
		long length = request.getFileSize();
		Log.i(tag, ":" + length + "  " + request.getRequestor());
		Log.i(tag, "" + request.getMimeType());
		try {

			final File file = new File("/sdcard/" + fileName);
			Log.i(tag, file.getAbsolutePath());
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						inTransfer.recieveFile(file);
						while (!inTransfer.getStatus().equals(Status.complete)) {
							if (inTransfer.getStatus().equals(Status.error)) {
								Log.e("error","ERROR!!! " + inTransfer.getError());
								inTransfer.cancel();
								inTransfer.recieveFile(file);
							} else {
								Log.i(tag, "status:" + inTransfer.getStatus());
								Log.i(tag, "process:" + inTransfer.getProgress());
							}
						}
						if (inTransfer.isDone()) {
							Log.i(tag, "Done+status:" + inTransfer.getStatus());
							Log.i(tag,
									"Done+process:" + inTransfer.getProgress());
						}
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}