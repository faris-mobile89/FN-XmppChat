package com.fn.reunion.app.controller;


import java.text.DecimalFormat;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;

/**
 * The XMPPFileManager is responsible for receiving files from Smack users.
 * 
 */

public class XMPPFileManager {

    /**
     * The Requester.
     */
    private String requester;

    /**
     * XMPP Protocol.
     */

    private XMPPConnection connection;

    /**
     * Handles the Incoming File.
     */
    private FileTransferManager fileTransferManager;

    /**
     * Represents the Progress of File Receiving.
     */
    private DecimalFormat progressBar;

    /**
     * This is the constructor of the XMPPFileManager.
     * 
     * @param XMPPConnection
     * @param FileTransferManager
     * @param DecimalFormat
     */

    public XMPPFileManager(XMPPConnection con, FileTransferManager ftm,
            DecimalFormat progress) {
        this.connection = con;
        FileTransferNegotiator.setServiceEnabled(connection, true);
        this.fileTransferManager = ftm;
        this.progressBar = progress;
    }

    /**
     * Responsible for receiving file from a user
     * 
     * @param contact
     */

    public void fileReceiver(final String contact) {
        this.requester = contact;
        fileTransferManager
                .addFileTransferListener(new FileTransferRequestListener());
    }

    /**
     * This is an File Transfer Listener class that is responsible for handling
     * user's file receiving.
     */

    private class FileTransferRequestListener implements FileTransferListener {

		@Override
		public void fileTransferRequest(FileTransferRequest arg0) {
			
		}

        /**
         * Handles File Transfer Request.
         * 
         * @param request
         * 
         */

 /*       public void fileTransferRequest(FileTransferRequest request) {

            if (request.getRequestor().contains(requester)) {
            	int confirmation = 1;
                int confirmation =
                        JOptionPane.showConfirmDialog(null, "From: "
                                + requester
                                + "\nDescription: Sending a file"
                                + "\nFile Name: "
                                + request.getFileName()
                                + " \nFile Size: "
                                + progressBar
                                        .format(request.getFileSize() / 1024)
                                + " KB");

                if (confirmation == 1) {

                    IncomingFileTransfer transfer = request.accept();
                    File file = new File(request.getFileName());
                    //JFileChooser chooser = new JFileChooser();
                   // chooser.setSelectedFile(file);
                  //  int confirmedLocation = chooser.showOpenDialog(null);

                   // String filePath = chooser.getSelectedFile().getPath();

                    //if (confirmedLocation == JFileChooser.APPROVE_OPTION) {

                        try {
                            transfer.recieveFile(new File(filePath));

                          //  while (!transfer.isDone() && ProgressMonitorScreen.counter != 100) {

                                if (transfer.getStatus().equals(Status.error)) {
                                    JOptionPane
                                            .showMessageDialog(
                                                    null,
                                                    "Cannot receive the file because an error occured during the process.",
                                                    "Failed",
                                                    JOptionPane.ERROR_MESSAGE);
                                    return;
                                } else {
//                                    System.out
//                                            .println("Transferring Progress: "
//                                                    + progressBar
//                                                            .format(transfer
//                                                                    .getProgress() * 100)
//                                                    + "% transferred.");
                                }

                                try {
                                    Thread.sleep(1500);
                                    ProgressMonitorScreen.counter = (int) (transfer.getProgress() * 100);
                                }

                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
//                            System.out.println(request.getFileName()
//                                    + "has been successfully received.");

                        } catch (XMPPException e1) {
                            e1.printStackTrace();
                        }

                    }

                }*/

              //  else {
                   // request.reject();
               // }
           // }

       // }

    }

}
