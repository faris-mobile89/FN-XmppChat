/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fn.reunion.app.xmpp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;
import org.jivesoftware.smackx.ChatStateManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Column;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.AttentionExtension;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.Nick;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.HeaderProvider;
import org.jivesoftware.smackx.provider.HeadersProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider;
import org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.FormNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.RetractEventProvider;
import org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.fn.reunion.app.controller.BadConnectionException;
import com.fn.reunion.app.controller.GenericConnection;
import com.fn.reunion.app.controller.UserStateType;
import com.fn.reunion.app.model.FriendTempData;
import com.fn.reunion.app.model.TypingStateType;
import com.fn.reunion.app.ui.base.AppBaseActivity;
import com.fn.reunion.app.ui.pages.BuddiesListPage;
import com.fn.reunion.app.utility.SessionManager;
import de.duenndns.ssl.MemorizingTrustManager;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Faris Nemer (faris.it.cs@gmail.com)
 */
public class XmppManager implements GenericConnection {

	private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);

	private static final String XMPP_RESOURCE_NAME = "Smack";
	
	private static final String tag = "XmppManager";
	
	private Context context;

	private NotificationService.TaskSubmitter taskSubmitter;

	private NotificationService.TaskTracker taskTracker;

	private SharedPreferences sharedPrefs;

	private String xmppHost;

	private int xmppPort;

	private XMPPConnection connection;

	private String username,password;

	private ConnectionListener connectionListener;

	private PacketListener notificationPacketListener;
	
	private FileTransferManager fileManager;

	private Handler handler;

	private List<Runnable> taskList;

	private boolean running = false;

	private Future<?> futureTask;

	private Thread reconnection;
	
    private ArrayList<Chat> chats;

    private Chat lastChat;

    private String domain;

    private VCard vcard;

	/**
	 * XmppManager
	 *
	 * @param notificationService
	 */
    
	public XmppManager(NotificationService notificationService) {
		initService(notificationService);
	}
	
	private void initService(NotificationService notificationService){
		
	if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	    
		this.chats = new ArrayList<Chat>();
	    vcard = new VCard();
	    
		context = notificationService;
		taskSubmitter = notificationService.getTaskSubmitter();
		taskTracker = notificationService.getTaskTracker();
		sharedPrefs = notificationService.getSharedPreferences();

		//xmppHost = sharedPrefs.getString(Constants.XMPP_HOST,"192.168.43.254");
		xmppHost = Constants.XMPP_HOST_IP; // at menaIT
		//xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
		xmppPort = Integer.parseInt(Constants.XMPP_HOST_PORT);
		
		username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
		password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");

		Log.i(LOGTAG, "init username&password = " + username + "@" + password);

		connectionListener = new PersistentConnectionListener(this);
		notificationPacketListener = new NotificationPacketListener(this);

		handler = new Handler();
		taskList = new ArrayList<Runnable>();
		/**
         */
		reconnection = new ReconnectionThread(this);
	}

	public Context getContext() {
		return context;
	}

	public void connect() {
		Log.d(LOGTAG, "connect()...");
		submitLoginTask();
	}

	public void disconnect() {
        Log.d(LOGTAG, "disconnect()...");
        terminatePersistentConnection();
    }

	/**
     */
	public void terminatePersistentConnection() {
		Log.d(LOGTAG, "terminatePersistentConnection()...");
		Runnable runnable = new Runnable() {

			final XmppManager xmppManager = XmppManager.this;

			public void run() {
				if (xmppManager.isConnected()) {
					Log.d(LOGTAG, "terminatePersistentConnection()... run()");
					xmppManager.getConnection().removePacketListener(
							xmppManager.getNotificationPacketListener());
					xmppManager.getConnection().disconnect();
				}
				xmppManager.runTask();
			}
		};
		addTask(runnable);
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		XMPPLogic.getInstance().setConnection(connection);
	}
	
	public FileTransferManager getFileTransferManager(){
		return fileManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	private void submitConnectTask() {
		Log.d(LOGTAG, "submitConnectTask()...");
		addTask(new ConnectTask());
	}

	private void submitRegisterTask() {
		Log.d(LOGTAG, "submitRegisterTask()...");
		submitConnectTask();
		addTask(new RegisterTask());
	}

	private void submitLoginTask() {
		Log.d(LOGTAG, "submitLoginTask()...");
		submitRegisterTask();
		addTask(new LoginTask());
	}
	
	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	public PacketListener getNotificationPacketListener() {
		return notificationPacketListener;
	}

	/**
     */
	public void startReconnectionThread() {
		synchronized (reconnection) {
			if (!reconnection.isAlive()) {
				reconnection.setName("Xmpp Reconnection Thread");
				reconnection.start();
			}
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void reregisterAccount() {
		removeAccount();
		submitLoginTask();
		runTask();
	}

	public List<Runnable> getTaskList() {
		return taskList;
	}

	public Future<?> getFutureTask() {
		return futureTask;
	}

	/**
    */
	public void runTask() {
		Log.i(LOGTAG, "runTask()...");
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		Log.d(LOGTAG, "runTask()...done");
	}

	/**
	 * @return
	 */
	private String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}
	
	/**
	 * 
	 * @return  true if registered
	 */
	
	private boolean isRegistered() {

		String username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
		String password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");

		if (username.length() > 0 && password.length() > 0) {
			return true;
		}
		
		return false;
		
//		return sharedPrefs.contains(Constants.XMPP_USERNAME) && 
//				sharedPrefs.contains(Constants.XMPP_PASSWORD);
	}
	
	private boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}

	private void addTask(Runnable runnable) {
		Log.d(LOGTAG, "addTask(runnable)... running: " + running);
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {
				Log.d(LOGTAG,
						"addTask(runnable)... taskList.isEmpty() && !running");
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				taskList.add(runnable);
			}
		}
		Log.d(LOGTAG, "addTask(runnable)... done");
	}

	private void removeAccount() {
		Editor editor = sharedPrefs.edit();
		editor.remove(Constants.XMPP_USERNAME);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.commit();
	}

	/**
	 * A runnable task to connect the server. connection.connect();
	 */
	private class ConnectTask implements Runnable {

		final XmppManager xmppManager;

		private ConnectTask() {
			this.xmppManager = XmppManager.this;
			Log.i(LOGTAG, "ConnectTask.run()...");

		}

		public void run() {
			Log.i(LOGTAG, "ConnectTask.run()...");

			if (!xmppManager.isConnected()) {
				// Create the configuration for this new connection
				/**
            	 */
				ConnectionConfiguration connConfig = new ConnectionConfiguration(xmppHost, xmppPort);
				connConfig.setCompressionEnabled(false);
				connConfig.setSecurityMode(SecurityMode.enabled);
				connConfig.setSocketFactory(new XmppSocketFactory());
				connConfig.setDebuggerEnabled(true);
				
		        try {
		            SSLContext sc = SSLContext.getInstance("TLS");
		            sc.init(null, MemorizingTrustManager.getInstanceList(context), new SecureRandom());
		            connConfig.setCustomSSLContext(sc);
		        } catch (NoSuchAlgorithmException e) {
		        	Log.e(tag, "NoSuchAlgorithmException"+e.getMessage());
		            throw new IllegalStateException(e);
		        } catch (KeyManagementException e) {
		            throw new IllegalStateException(e);
		        }
		        
				XMPPConnection connection = new XMPPConnection(connConfig);
				xmppManager.setConnection(connection);

				try {
					
					// Connect to the server
					connection.connect();
					Log.i(LOGTAG, "XMPP connected successfully");

					// packet provider
					/** 
					 * adding Configuration
                     */
			      configureProviderManager(connection);

				} catch (XMPPException e) {
					Log.e(LOGTAG, "XMPP connection failed", e);
					running = false;
				}

				xmppManager.runTask();

			} else {
				Log.i(LOGTAG, "XMPP connected already");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to register a new user onto the server.
	 */
	private class RegisterTask implements Runnable {
		final XmppManager xmppManager;

		private RegisterTask() {
			xmppManager = XmppManager.this;
		}

		public void run() {
			Log.i(LOGTAG, "RegisterTask.run()...");
			if (!xmppManager.isRegistered()) {

				final String newUsername = new SessionManager(context).getUserDetails().getPhone();
				final String newPassword = newRandomUUID();
				Registration registration = new Registration();

				PacketFilter packetFilter = new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class));

				PacketListener packetListener = new PacketListener() {
					public void processPacket(Packet packet) {
						Log.d("RegisterTask.PacketListener","processPacket().....");
						Log.d("RegisterTask.PacketListener","packet=" + packet.toXML());

						if (packet instanceof IQ) {
							IQ response = (IQ) packet;
							if (response.getType() == IQ.Type.ERROR) {
								if (!response.getError().toString()
										.contains("409")) {
									Log.e(LOGTAG,
											"Unknown error while registering XMPP account! "
													+ response.getError()
															.getCondition());
								}
							} else if (response.getType() == IQ.Type.RESULT) {
								xmppManager.setUsername(newUsername);
								xmppManager.setPassword(newPassword);
								Log.d(LOGTAG, "username=" + newUsername);
								Log.d(LOGTAG, "password=" + newPassword);

								Editor editor = sharedPrefs.edit();
								editor.putString(Constants.XMPP_USERNAME,newUsername);
								editor.putString(Constants.XMPP_PASSWORD,newPassword);
								editor.commit();
								Log.i(LOGTAG, "Account registered successfully");
								xmppManager.runTask();
							}
						}
					}
				};
				connection.addPacketListener(packetListener, packetFilter);
				registration.setType(IQ.Type.SET);
				
				/* PLESSE REMOVE THIS COMMENTS */
				registration.addAttribute("username", newUsername);
				registration.addAttribute("password", newPassword);
				registration.addAttribute("imsi", "460000001232300");
				registration.addAttribute("imei", "324234343434434");
				
				
				connection.sendPacket(registration);
			} else {
				Log.i(LOGTAG, "Account registered already");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * /** A runnable task to log into the server.
	 */
	private class LoginTask implements Runnable {

		final XmppManager xmppManager;

		private LoginTask() {
			this.xmppManager = XmppManager.this;
			Log.i(LOGTAG, "********LoginTask.Started********");
		}

		public void run() {
			
			Log.i(LOGTAG, "******** Login Process ******");
			
			if (!xmppManager.isAuthenticated()) {
				String username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
				String password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
				
				xmppManager.setUsername(username);
				xmppManager.setPassword(password);
				Log.d(LOGTAG, "username=" + username);
				Log.d(LOGTAG, "password=" + password);
				
				Log.i(LOGTAG,"trying login user : " + xmppManager.getUsername());

				try {
					xmppManager.getConnection().
					    login(
							xmppManager.getUsername(),
							xmppManager.getPassword(),
							XMPP_RESOURCE_NAME);
					
					Log.d(LOGTAG, "Loggedn in successfully to host "+ connection.getHost());
					
					// connection listener
					/**
                     */
					if (xmppManager.getConnectionListener() != null) {
						xmppManager.getConnection().addConnectionListener(
								xmppManager.getConnectionListener());
					}
					
					//fileTransfer Listener 
					addFileListener();

					// packet filter
					/**
                     */
					PacketFilter packetFilter = new PacketTypeFilter(
							NotificationIQ.class);
					
					PacketFilter filter = new MessageTypeFilter(
							Message.Type.chat);

					// packet listener
					/**
                     */
					connection.addPacketListener(new MessagePacketListener(getContext(),getConnection()),new MessagePacketFilter());
					PacketListener packetListener = xmppManager.getNotificationPacketListener();
					connection.addPacketListener(packetListener, filter);
					connection.getRoster().addRosterListener(new BuddyListener(getContext()));
					xmppManager.runTask();

				} catch (XMPPException e) {
					Log.e(LOGTAG, "LoginTask.run()... xmpp error");
					Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					/**
                     */
					String INVALID_CREDENTIALS_ERROR_CODE = "401";
					String errorMessage = e.getMessage();
					/**
                     */
					if (errorMessage != null
							&& errorMessage
									.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
						xmppManager.reregisterAccount();
						return;
					}
					xmppManager.startReconnectionThread();

				} catch (Exception e) {
					Log.e(LOGTAG, "LoginTask.run()... other error");
					Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					xmppManager.startReconnectionThread();
				}

			} else {
				Log.i(LOGTAG, "Logged in already");
				xmppManager.runTask();
			}

		}
	}

	/*****************************************************
	 * 
	 *****************************************************
	 */
	
   public void addFriend(String userID,String userNickname) {
    	
        Roster roster = null;
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);    	
        String nickname = userNickname;
        //nickname = StringUtils.parseBareAddress(userID);
        roster = connection.getRoster();
        if (!roster.contains(userID)) {
                try {
					roster.createEntry(userID, nickname, null);
				} catch (XMPPException e) {
					e.printStackTrace();
				}
        }

        return;
    }

   public void login(String userID, String password) throws BadConnectionException {
	   
	
    	// Create a connection
		ConnectionConfiguration connConfig = new ConnectionConfiguration(xmppHost, xmppPort, XMPP_RESOURCE_NAME);
		connConfig.setSASLAuthenticationEnabled(true);
		connConfig.setCompressionEnabled(false);
		connConfig.setSecurityMode(SecurityMode.enabled);
		connConfig.setSocketFactory(new XmppSocketFactory());
		
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, MemorizingTrustManager.getInstanceList(context), new SecureRandom());
            connConfig.setCustomSSLContext(sc);
        } catch (NoSuchAlgorithmException e) {
        	Log.e(tag, "NoSuchAlgorithmException"+e.getMessage());
            throw new IllegalStateException(e);
        } catch (KeyManagementException e) {
            throw new IllegalStateException(e);
        }
        
		
	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			connConfig.setTruststoreType("AndroidCAStore");
			connConfig.setTruststorePassword(null);
			connConfig.setTruststorePath(null);
			} else {
				connConfig.setTruststoreType("BKS");
			    String path = System.getProperty("javax.net.ssl.trustStore");
			    if (path == null)
			        path = System.getProperty("java.home") + File.separator + "etc"
			            + File.separator + "security" + File.separator
			            + "cacerts.bks";
			    connConfig.setTruststorePath(path);
			}*/
		
		configure(ProviderManager.getInstance());
		this.connection = new XMPPConnection(connConfig);
		
		try {
			this.connection.connect();
			Log.i(tag,"Connected to " + connection.getHost());
		} catch (XMPPException ex) {
			Log.e(tag, "Failed to connect to "+ connection.getHost());
			Log.e(tag, ex.toString());
		}
		
		try {
			this.connection.login(userID, password);
			Log.i(tag,"Logged in as " + connection.getUser());
		} catch (XMPPException e1) {
			e1.printStackTrace();
            Log.e(tag, "Error signing into Jabber!\nUser name and password do not match." );
		}
        // Setup the listeners for messages and buddy changes
        connection.addPacketListener(new MessagePacketListener(getContext(),getConnection()),new MessagePacketFilter());
        connection.getRoster().addRosterListener(new BuddyListener(getContext()));

        return;
    }

    public boolean removeFriend(String userID) throws BadConnectionException {
        boolean removed = false; // Default return value
        Roster roster = this.connection.getRoster();

        for (RosterEntry r : roster.getEntries()) {
            if (r.getUser().equalsIgnoreCase(userID)) {
                try {
                    roster.removeEntry(r);
                } catch (XMPPException e) {
                    throw new BadConnectionException();
                }
                removed = true;
                break;
            }
        }

        return removed;
    }

    public void changeStatus(UserStateType state, String status) {
        Presence presence = new Presence(Presence.Type.available);
        if (state == UserStateType.ONLINE) {
            presence.setMode(Presence.Mode.available);
        } else if (state == UserStateType.AWAY) {
            presence.setMode(Presence.Mode.away);
        } else if (state == UserStateType.BUSY) {
            presence.setMode(Presence.Mode.dnd);
        } else {
            presence.setMode(Presence.Mode.chat);
        }
        presence.setStatus(status);
        connection.sendPacket(presence);

        return;
    }

    public String retrieveStatus(String userID) {
        String userStatus = ""; // default return value

        try {
            userStatus = this.connection.getRoster().getPresence(userID).getStatus();
        } catch (NullPointerException e) {
         //Invalid connection or user status
            userStatus = "";
        }
        // Server may set their status to null; we want empty string
        if (userStatus == null) {
            userStatus = "";
        }

        return userStatus;
    }
    
    public VCard getUserVCard( String userId ){
    	
    	 VCard vCard = new VCard();
    	 try {
			vCard.load(connection, userId);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
    	 return vCard;
    }
    
    public VCard getVcard(){
    	 
	    try {
	        vcard.load(connection);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	     return vcard; 
     }
    
    public void getRosterPresence(){
    	
    	Roster roster = null;
        roster = this.connection.getRoster();
    	for(RosterEntry r:roster.getEntries()) {
            Presence presence = roster.getPresence(r.getUser());
            System.out.println(presence.toXML());
        }
    }

    
     /**
      * Save user information.
     * @throws XMPPException 
      */
     public void setUserInfo (String firstName,String lastName){
    	 
    	 try {
    	     vcard.load(connection);
		} catch (XMPPException e1) {
			Log.e(tag, e1.getMessage());
		}
    	 
    	 vcard.setFirstName(firstName);
    	 vcard.setLastName(lastName);
    	// vcard.setAddressFieldHome("STREET", "Some street");
    	// vcard.setAddressFieldWork("CTRY", "US");
    	 //vcard.setPhoneWork("FAX", "3443233");
    	 
    	 try {
    		 Log.d(tag, "saving vCard...");
			vcard.save(connection);
		} catch (XMPPException e) {
			Log.e(tag, e.getMessage());
		}

     }
     
     
     
     /**
      * Set the avatar for the VCard by specifying the url to the image.
      *
      * @param avatarURL
      *            the url to the image(png,jpeg,gif,bmp)
      * @throws XMPPException
      */
     public void setAvatarPicture(URL avatarURL) throws XMPPException {
        
     	byte[] bytes = new byte[0];
         try {
             bytes = getBytes(avatarURL);
         } catch (IOException e) {
             e.printStackTrace();
         }

         setAvatarPicture(bytes);
     }

    /**
     * Specify the bytes for the avatar to use.
     *
     * @param file
     *            the bytes of the avatar.
     * @throws XMPPException
     */
    public void setAvatarPicture(File file) throws XMPPException {
        vcard = new VCard();
        vcard.load(connection);

        // Otherwise, add to mappings.
        byte[] bytes;
        try {
            bytes = getFileBytes(file);
            String encodedImage = StringUtils.encodeBase64(bytes);
            vcard.setAvatar(bytes, encodedImage);
            vcard.setEncodedImage(encodedImage);
            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
                    + encodedImage + "</BINVAL>", true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        vcard.save(connection);
    }

    /**
     * Specify the bytes for the avatar to use.
     *
     * @param bytes
     *            the bytes of the avatar.
     * @throws XMPPException
     */
    public void setAvatarPicture(byte[] bytes) throws XMPPException {
    	
        vcard.load(connection);
        String encodedImage = StringUtils.encodeBase64(bytes);
        vcard.setField("Avatar", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage + "</BINVAL>", true);
        vcard.save(connection);
    }

    /**
     * Common code for getting the bytes of a url.
     *
     * @param url
     *            the url to read.
     */
    public byte[] getBytes(URL url) throws IOException {
        final String path = url.getPath();
        final File file = new File(path);
        if (file.exists()) {
            return getFileBytes(file);
        }

        return null;
    }

    private byte[] getFileBytes(File file) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int bytes = (int) file.length();
            byte[] buffer = new byte[bytes];
            int readBytes = bis.read(buffer);
            if (readBytes != buffer.length) {
                throw new IOException("Entire file not read");
            }
            return buffer;
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    public byte[] getAvatarPicture(String userID) throws XMPPException {
    	    	
         byte[] avatarBytes;
            vcard.load(connection, userID); // load someone's VCard
            Log.i("MyVCard", vcard.toXML().toString());
        	avatarBytes  = vcard.getAvatar();
          
        return avatarBytes;
    }

    public UserStateType retrieveState(String userID) {
    	
        UserStateType userState = UserStateType.OFFLINE; // default return value
        Presence userFromServer = null;
        Mode userStateFromServer = null;
        
        try {
            userFromServer = this.connection.getRoster().getPresence(userID);
            userStateFromServer = userFromServer.getMode();

            if (userStateFromServer == Presence.Mode.dnd) {
                userState = UserStateType.BUSY;
            } else if (userStateFromServer == Presence.Mode.away
                    || userStateFromServer == Presence.Mode.xa) {
                userState = UserStateType.AWAY;
            } else if (userFromServer.isAvailable()) {
                userState = UserStateType.ONLINE;
            } else { // user is offline
                userState = UserStateType.OFFLINE;
            }
        } catch (NullPointerException e) {
            // Invalid connection or user in the retrieve state
            userState = UserStateType.OFFLINE;
        }

        return userState;
    }

    public ArrayList<FriendTempData> retrieveFriendList() {
    	
        ArrayList<FriendTempData> friends = new ArrayList<FriendTempData>();
        FriendTempData friendToAdd = null;
        String userID = null;
        Roster roster = null;

        roster = this.connection.getRoster();

        for (RosterEntry r : roster.getEntries()) {
            userID = r.getUser();
            
            if (r.getName().length() < 1) {
				r.setName(userID);
			}
            friendToAdd =
                    new FriendTempData(userID, r.getName(), this
                            .retrieveStatus(userID),
                            this.retrieveState(userID), false);
            friends.add(friendToAdd);
        }
        return friends;
    }

    public void sendMessage(String toUserID, String message)
    
            throws BadConnectionException {
        Chat ourChat = null;

        for (Chat c : this.chats) {
            if (c.getParticipant().equalsIgnoreCase(toUserID)) {
                ourChat = c;
                break;
            }
        }

        if (ourChat == null) {
            ourChat =
                    connection.getChatManager().createChat(toUserID,
                            new MessageListener() {
                                public void processMessage(Chat chat,
                                        Message message) {
                                    // Do nothing

                                    return;
                                }
                            });
        }

        try {
            ourChat.sendMessage(message);
        } catch (XMPPException e) {
            // Error in sending the message
            throw new BadConnectionException();
        }

        return;
    }

    // Section
    // Listeners
    private class MessagePacketFilter implements PacketFilter {
        public boolean accept(Packet packet) {
            // TODO Is this the source of the name is null bug? check
            // if we are receiving packets that aren't messages that we need
            // to deal with.
            return (packet instanceof Message);
        }
    }


    @Override
    public int hashCode() {
        int hash = 7;

        hash = hash * 31 + "Google".hashCode();
        hash = hash * 31 + this.connection.hashCode();

        return hash;
    }

    /**
     *
     * set typing state
     *
     * @param state
     *            int that represents different state 1 = active 2 = composing 3
     *            = gone 4 = inactive 5 = paused
     */
    public void setTypingState(int state, String userID)
            throws BadConnectionException, XMPPException {
        ChatStateManager curState = ChatStateManager.getInstance(connection);
        if (lastChat == null) {
            lastChat =
                    connection.getChatManager().createChat(userID,
                            new DefaultChatStateListener());
        }
        
        if (state == 1) {
            curState.setCurrentState(ChatState.active, lastChat);
        } else if (state == 2) {
            curState.setCurrentState(ChatState.composing, lastChat);

        } else if (state == 3) {
            curState.setCurrentState(ChatState.gone, lastChat);
        } else if (state == 4) {
            curState.setCurrentState(ChatState.inactive, lastChat);
        } else if (state == 5) {
            curState.setCurrentState(ChatState.paused, lastChat);
        }
    }

    private class DefaultChatStateListener implements ChatStateListener {
    	
        public void stateChanged(Chat user, ChatState event) {
            String state = event.name();
            TypingStateType typingState = null;
            if (state.equals("active")) {
                typingState = TypingStateType.ACTIVE;
            } else if (state.equals("composing")) {
                typingState = TypingStateType.TYPING;
            } else if (state.equals("paused")) {
                typingState = TypingStateType.PAUSED;
            } else if (state.equals("inactive")) {
                typingState = TypingStateType.INACTIVE;
            } else if (state.equals("gone")) {
                typingState = TypingStateType.GONE;
            }
           // controller.typingStateUpdated(genericConnection, typingState, user.getParticipant().toString());
        }

        public void processMessage(Chat arg0, Message arg1) {
            // Do nothing
        }
    }
    
    /**
     * 
     * @param keyword
     * @param byUsername true if search by username
     * @param byName  true if search by name
     * @throws XMPPException
     * 
     */
    public void SearchUser(String keyword , boolean byUsername,boolean byName) throws XMPPException{
    	
    	 UserSearchManager search = new UserSearchManager(connection);
         Form searchForm = search.getSearchForm("search." + connection.getServiceName());

         Form answerForm = searchForm.createAnswerForm();
         answerForm.setAnswer("search", keyword);
         if (byUsername) {
        	 answerForm.setAnswer("Username", true);
		}else if (byName) {
			answerForm.setAnswer("Name", true);
		}
        
         ReportedData data = search.getSearchResults(answerForm, "search." + connection.getServiceName());

         System.out.println("\nThe jids from our each of our hits:");

         Iterator<Row> rows = data.getRows();
         while (rows.hasNext()) 
         {
            Row row = rows.next();
            Log.i(tag,row.toString());
            Iterator<String> jids = row.getValues("jid");
            while (jids.hasNext()) {
               Log.i(tag,jids.next());
            }
         }
    }
    
    public Boolean checkIfUserExists(String user) throws XMPPException{
    	
        UserSearchManager search = new UserSearchManager(connection);  
        Form searchForm = search.getSearchForm("search."+connection.getServiceName());
        Form answerForm = searchForm.createAnswerForm();  
        answerForm.setAnswer("Username", true);  
        answerForm.setAnswer("search", user);  
        ReportedData data = search.getSearchResults(answerForm,"search."+connection.getServiceName());  
        if (data.getRows() != null) {
            Iterator<Row> it = data.getRows();
            while (it.hasNext()) {
                Row row = it.next();
                Iterator iterator = row.getValues("jid");
                if (iterator.hasNext()) {
                   // String value = iterator.next().toString();
                    //Log.i(tag,"Iteartor values...... " + value);
                    return true;
                }
            }
        }
        return false;
    }
    
    public void sampleSearch() throws XMPPException{
    	
    	ProviderManager.getInstance().addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
    	Log.i(tag,"sampleSearch ..."+connection.getServiceName());
        UserSearchManager search = new UserSearchManager(connection);
        Form searchForm = search.getSearchForm("search." + connection.getServiceName());
        
        Log.i(tag,"Available search fields:");
        Iterator<FormField> fields = searchForm.getFields();
        
        while (fields.hasNext()) {
           FormField field = fields.next();
          Log.i(tag,field.getVariable() + " : " + field.getType());
        }
        
        Form answerForm = searchForm.createAnswerForm();
        Log.i(tag,"search"+ "a");
        answerForm.setAnswer("Email", true);
        
        ReportedData data = search.getSearchResults(answerForm, "search." + connection.getServiceName());
        
        Log.i(tag,"\nColumns that are included as part of the search results:");
        Iterator<Column> columns = data.getColumns();
        while (columns.hasNext()) {
        	Log.i(tag,columns.next().getVariable());
        }
        
        Log.i(tag,"\nThe jids from our each of our hits:");
        Iterator<Row> rows = data.getRows();
        while (rows.hasNext()) {
           Row row = rows.next();
           
           Iterator<String> jids = row.getValues("jid");
           while (jids.hasNext()) {
        	   Log.i(tag,jids.next());
           }
        }
   }

    public String getUserEmailHome() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserEmailWork() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserFirstName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserLastName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserMiddleName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserNickName() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserOrganization() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserOrganizationUnit() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserPhoneHome() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUserPhoneWork() throws XMPPException {
        // TODO Auto-generated method stub
        return null;
    }

    public void load(String userID) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void load() throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserEmailHome(String email) throws XMPPException {
    	vcard.load(connection);
    	vcard.setEmailHome(email);
    	vcard.save(connection);
    	

    }

    public void setUserEmailWork(String name) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserFirstName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setFirstName(name);
    	vcard.save(connection);

    }

    public void setUserLastName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setLastName(name);
    	vcard.save(connection);

    }

    public void setUserMiddleName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setMiddleName(name);
    	vcard.save(connection);

    }

    public void setUserNickName(String name) throws XMPPException {
    	vcard.load(connection);
    	vcard.setNickName(name);
    	vcard.save(connection);
    }

    public void setUserOrganization(String name) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserOrganizationUnit(String name) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void setUserPhoneHome(String phone) throws XMPPException {
    	vcard.load(connection);
    	vcard.setField("Home", phone);
    	vcard.save(connection);

    }

    public void setUserPhoneWork(String phone) throws XMPPException {
    	vcard.load(connection);
    	vcard.setField("Work", phone);
    	vcard.save(connection);

    }
    public void setField(String name , String data) throws XMPPException {
    	vcard.load(connection);
    	vcard.setField(name, data);
    	vcard.save(connection);

    }
   
    public void addFileListener() {
    	
    	fileManager = new FileTransferManager(connection);
    	fileManager.addFileTransferListener(new FileTransferListener() {
		public void fileTransferRequest(final FileTransferRequest request) {
	
				new Thread() {
					@Override
					public void run() {
						  File externalFileDir = Environment.
		    	            		getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
						
						IncomingFileTransfer transfer = request.accept();
						final File saveTo = new File(externalFileDir,request.getFileName());
						Log.i(tag,"File transfer: " + saveTo.getName() + " - "
				    	    		+ request.getFileSize() / 1024 + " KB");
						 try {
		    	    			transfer.recieveFile(saveTo);
		    	    			Log.i(tag,"File transfer: " + saveTo.getName() + " - "
		    	    					+ transfer.getStatus());
		    	    			double percents = 0.0;
		    	    			while (!transfer.isDone()) {
		    	    				if (transfer.getStatus().equals(Status.in_progress)) {
		    	    					percents = ((int) (transfer.getProgress() * 10000)) / 100.0;
		    	    					Log.d(tag,"File transfer: " + saveTo.getName() + " - "
		    	    							+ percents + "%");
		    	    				} else if (transfer.getStatus().equals(Status.error)) {
		    	    					Log.e(tag,returnAndLogError(transfer));
		    	    					return;
		    	    				}
		    	    				Thread.sleep(1000);
		    	    			}
		    	    			if (transfer.getStatus().equals(Status.complete)) {
		    	    				Log.d(tag,"File transfer complete. File saved as "
		    	    						+ saveTo.getAbsolutePath());
		    	    				
		    	    				/**
		    	    				 * Notify MessageActivity
		    	    				 */
		    	    				FileRecieveNotfication notifier = new FileRecieveNotfication();
		    	    				notifier.checkdata(getContext());
		    	    				notifier.notifyMessageUI(request.getRequestor(), saveTo.getAbsolutePath());
		    	    			} else {
		    	    				Log.e(tag,returnAndLogError(transfer));
		    	    			}
		    	    		} catch (Exception ex) {
		    	    			String message = "Cannot receive the file because an error occured during the process."
		    	    					+ ex;
		    	    			Log.e(tag, message, ex);
		    	    			Log.i(tag,message);
		    	    		}
					};
				}.start();
			}
		});
    	
    	
	}
    
    public String returnAndLogError(FileTransfer transfer) {
		String message = "Cannot process the file because an error occured during the process.";

		if (transfer.getError() != null) {
			message += transfer.getError();
		}
		if (transfer.getException() != null) {
			message += transfer.getException();
		}
		return message;
	}

    public void sendFile(File mfile, String userID) throws XMPPException {
        
    	String fileDescription="this is test file";
        
    	ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
    	ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
    	ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
	    ProviderManager.getInstance().addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());
        ProviderManager.getInstance().addIQProvider("open","http://jabber.org/protocol/ibb", new OpenIQProvider());
        ProviderManager.getInstance().addIQProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());
        ProviderManager.getInstance().addIQProvider("close","http://jabber.org/protocol/ibb", new CloseIQProvider());
       
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        if (sdm == null)
        sdm = new ServiceDiscoveryManager(connection);
    	
        FileTransferManager manager = new FileTransferManager(connection);
    	FileTransferNegotiator.setServiceEnabled(connection, true);
    	OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(userID);
    	File file = mfile;
    	try {
    	   transfer.sendFile(file, fileDescription);
    	} catch (XMPPException e) {
    	   e.printStackTrace();
    	}
    	while(!transfer.isDone()) {
    	   if(transfer.getStatus().equals(Status.error)) {
    	      Log.e(tag,"ERROR!!! " + transfer.getError());
    	   } else if (transfer.getStatus().equals(Status.cancelled) || transfer.getStatus().equals(Status.refused)) {
    		   Log.e(tag,"Cancelled!!! " + transfer.getError());
    	   }
    	   try {
    	      Thread.sleep(1000L);
    	   } catch (InterruptedException e) {
    	      e.printStackTrace();
    	   }
    	}
    	if(transfer.getStatus().equals(Status.refused) || transfer.getStatus().equals(Status.error)
    	 || transfer.getStatus().equals(Status.cancelled)){
    		Log.e(tag,"refused cancelled error " + transfer.getError());
    	} else {
    		Log.i(tag,"file sent successfuly");
    	}
    	
    	/*
    	 * <error code="503" type="cancel"><service-unavailable xmlns="urn:ietf:params:xml:ns:xmpp-stanzas"/></error>  
    	 *  Solution: Pass full jabber id:username@domain/resource. In my case i have changed 
    	 *  
    	 *  http://stackoverflow.com/questions/12153795/file-transfer-using-xmpp-extension-xep-0065
    	 */
    	
    }
    
	public void sendFile2(String friendId , String userId , File f) {
		
		   Log.d(tag, "sendFile2 called ...");
		    ProviderManager.getInstance().addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());
	        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
	        ProviderManager.getInstance().addIQProvider("open","http://jabber.org/protocol/ibb", new OpenIQProvider());
	        ProviderManager.getInstance().addIQProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());
	        ProviderManager.getInstance().addIQProvider("close","http://jabber.org/protocol/ibb", new CloseIQProvider());
	        ProviderManager.getInstance().addExtensionProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());
		 
	      ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
            if (sdm == null)
            sdm = new ServiceDiscoveryManager(connection);
 
		FileTransferManager manager = new FileTransferManager(connection);
		FileTransferNegotiator.setServiceEnabled(connection, true);
		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(friendId); 
        Log.d(tag, "sending file ...");
		//File file = new File("the-file-name.txt");
		File file = f;
		if (file.exists()) {
			Log.i(tag,"File Exist");
		} else {
			Log.e(tag,"File Not Exist");
			return;
		}
		
//		long length = file.length();

		try {
			transfer.sendFile(file, userId);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
		while (!transfer.isDone()) {
			if (transfer.getStatus().equals(Status.error)) {
				Log.e(tag,"ERROR!!! " + transfer.getError());
			} else if (transfer.getStatus().equals(Status.cancelled)
					|| transfer.getStatus().equals(Status.refused)) {
				Log.e(tag,"Cancelled!!! " + transfer.getError());
			}
			try {
				Thread.sleep(2000L);

				Log.i(tag,"transfer file " + "sending file status :- "
						+ transfer.getStatus() + " " + "progress:-"
						+ transfer.getProgress());

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (transfer.getStatus().equals(Status.refused)
				|| transfer.getStatus().equals(Status.error)
				|| transfer.getStatus().equals(Status.cancelled)) {
			Log.e(tag,"refused cancelled error " + transfer.getError());
		} else {
			Log.d(tag,"Success");
		}
	}
	
	public void setSubscription(){
		
		//Roster roster = connection.getRoster();
        //roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        //roster.addRosterListener(new RosterListenerImpl());
       // PacketFilter subscribeFilter = new SubscribeFilter();
        //PacketListener subscribePacketListener = new SubscribePacketListener();
        //connection.addPacketListener(subscribePacketListener, subscribeFilter);
	}


    public boolean isValidUserID(String userID) {
        // TODO Auto-generated method stub
        return false;
    }

    public void createRoom(String room) throws XMPPException {
        // TODO Auto-generated method stub

    }

    public void inviteFriend(String userID, String roomName)
            throws XMPPException {
        // TODO Auto-generated method stub

    }

    public boolean isConferenceChat() {
        return false;
    }

    public void sendMultMessage(String message, String roomName)
            throws BadConnectionException {
        // TODO Auto-generated method stub

    }

    public boolean doesExist(String userID) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFollowing(String userID) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isConnected(){
    	
    	return connection != null && connection.isConnected();
    }
    
    public void configure(ProviderManager pm) {
	 
    	pm.addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
       
    	//  Private Data Storage
        pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        //  Time
        try {
            pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        //  Roster Exchange
        pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
        //  Message Events
        pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());
        
        //  Chat State
        pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        
        //  XHTML
        pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
        
        //  Group Chat Invitations
        pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());

        //  Service Discovery # Items    
      //  pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        //  Service Discovery # Info
      //  pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        //  Data Forms
        pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());

        //  MUC User
        pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());

        //  MUC Admin    
        pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

        //  MUC Owner    
        pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        //  Delayed Delivery
       // pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());

        //  Version
        try {
            pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            //  Not sure what's happening here.
        }

        //  VCard
        pm.addIQProvider("vCard","vcard-temp", new VCardProvider());
 
        //  Offline Message Requests
        pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
       
        //  Offline Message Indicator
        pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        
        //  Last Activity
        pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());

        //  User Search
        pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
 
        //  SharedGroupsInfo
        pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        //  JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

        //   FileTransfer
        //pm.addIQProvider("open","http://jabber.org/protocol/ibb", new IBBProviders.Open());
       // pm.addIQProvider("close","http://jabber.org/protocol/ibb", new IBBProviders.Close());
      //  pm.addExtensionProvider("data","http://jabber.org/protocol/ibb", new IBBProviders.Data());
        
     // //File transfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si",new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",new BytestreamsProvider());
        pm.addIQProvider("open", "http://jabber.org/protocol/ibb",new OpenIQProvider());
        pm.addIQProvider("close", "http://jabber.org/protocol/ibb",new CloseIQProvider());
        pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb",new DataPacketProvider());
        
       	ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
    	ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
    	ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
    	
        //  Privacy
        pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    
    }
    
    public void configureProviderManager(XMPPConnection connection) {

		  ProviderManager.getInstance().addIQProvider("notification","chatAPP:iq:notification",new NotificationIQProvider());

        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        ProviderManager.getInstance().addIQProvider("query",
                "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        ProviderManager.getInstance().addIQProvider("query",
                "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());
        ProviderManager.getInstance().addIQProvider("query",
                "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        if (sdm == null)
            sdm = new ServiceDiscoveryManager(connection);

        //sdm.addFeature("http://jabber.org/protocol/disco#info");
        //sdm.addFeature("http://jabber.org/protocol/disco#item");
        //sdm.addFeature("jabber:iq:privacy");

        //Search 
    	ProviderManager.getInstance().addIQProvider("query","jabber:iq:search", new UserSearch.Provider());

        // The order is the same as in the smack.providers file

        //  Private Data Storage
        ProviderManager.getInstance().addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
        //  Time
        try {
        	ProviderManager.getInstance().addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            System.err.println("Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        //  Roster Exchange
        ProviderManager.getInstance().addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
        //  Message Events
        ProviderManager.getInstance().addExtensionProvider("x","jabber:x:event", new MessageEventProvider());
        //  Chat State
        ProviderManager.getInstance().addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.getInstance().addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.getInstance().addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.getInstance().addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        ProviderManager.getInstance().addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        //  XHTML
        ProviderManager.getInstance().addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        //  Group Chat Invitations
        ProviderManager.getInstance().addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());
        //  Service Discovery # Items
        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        //  Service Discovery # Info
        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        //  Data Forms
        ProviderManager.getInstance().addExtensionProvider("x","jabber:x:data", new DataFormProvider());
        //  MUC User
        ProviderManager.getInstance().addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());
        //  MUC Admin
        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        //  MUC Owner
        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        //  Delayed Delivery
        ProviderManager.getInstance().addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());
        ProviderManager.getInstance().addExtensionProvider("delay", "urn:xmpp:delay", new DelayInformationProvider());
        //  Version
        try {
        	ProviderManager.getInstance().addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            System.err.println("Can't load class for org.jivesoftware.smackx.packet.Version");
        }
        //  VCard
        ProviderManager.getInstance().addIQProvider("vCard","vcard-temp", new VCardProvider());
        //  Offline Message Requests
        ProviderManager.getInstance().addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        //  Offline Message Indicator
        ProviderManager.getInstance().addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        //  Last Activity
        ProviderManager.getInstance().addIQProvider("query","jabber:iq:last", new LastActivity.Provider());
        //  User Search
        ProviderManager.getInstance().addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
        //  SharedGroupsInfo
        ProviderManager.getInstance().addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        //  JEP-33: Extended Stanza Addressing
        ProviderManager.getInstance().addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

        //   FileTransfer
        ProviderManager.getInstance().addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());
        ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        ProviderManager.getInstance().addIQProvider("open","http://jabber.org/protocol/ibb", new OpenIQProvider());
        ProviderManager.getInstance().addIQProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());
        ProviderManager.getInstance().addIQProvider("close","http://jabber.org/protocol/ibb", new CloseIQProvider());
        ProviderManager.getInstance().addExtensionProvider("data","http://jabber.org/protocol/ibb", new DataPacketProvider());
        
        //  Privacy
        ProviderManager.getInstance().addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());

        // SHIM
        ProviderManager.getInstance().addExtensionProvider("headers", "http://jabber.org/protocol/shim", new HeadersProvider());
        ProviderManager.getInstance().addExtensionProvider("header", "http://jabber.org/protocol/shim", new HeaderProvider());

        // PubSub
        ProviderManager.getInstance().addIQProvider("pubsub", "http://jabber.org/protocol/pubsub", new PubSubProvider());
        ProviderManager.getInstance().addExtensionProvider("create", "http://jabber.org/protocol/pubsub", new SimpleNodeProvider());
        ProviderManager.getInstance().addExtensionProvider("items", "http://jabber.org/protocol/pubsub", new ItemsProvider());
        ProviderManager.getInstance().addExtensionProvider("item", "http://jabber.org/protocol/pubsub", new ItemProvider());
        ProviderManager.getInstance().addExtensionProvider("subscriptions", "http://jabber.org/protocol/pubsub", new SubscriptionsProvider());
        ProviderManager.getInstance().addExtensionProvider("subscription", "http://jabber.org/protocol/pubsub", new SubscriptionProvider());
        ProviderManager.getInstance().addExtensionProvider("affiliations", "http://jabber.org/protocol/pubsub", new AffiliationsProvider());
        ProviderManager.getInstance().addExtensionProvider("affiliation", "http://jabber.org/protocol/pubsub", new AffiliationProvider());
        ProviderManager.getInstance().addExtensionProvider("options", "http://jabber.org/protocol/pubsub", new FormNodeProvider());
        // PubSub owner
        ProviderManager.getInstance().addIQProvider("pubsub", "http://jabber.org/protocol/pubsub#owner", new PubSubProvider());
        ProviderManager.getInstance().addExtensionProvider("configure", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
        ProviderManager.getInstance().addExtensionProvider("default", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
        // PubSub event
        ProviderManager.getInstance().addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", new EventProvider());
        ProviderManager.getInstance().addExtensionProvider("configuration", "http://jabber.org/protocol/pubsub#event", new ConfigEventProvider());
        ProviderManager.getInstance().addExtensionProvider("delete", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());
        ProviderManager.getInstance().addExtensionProvider("options", "http://jabber.org/protocol/pubsub#event", new FormNodeProvider());
        ProviderManager.getInstance().addExtensionProvider("items", "http://jabber.org/protocol/pubsub#event", new ItemsProvider());
        ProviderManager.getInstance().addExtensionProvider("item", "http://jabber.org/protocol/pubsub#event", new ItemProvider());
        ProviderManager.getInstance().addExtensionProvider("retract", "http://jabber.org/protocol/pubsub#event", new RetractEventProvider());
        ProviderManager.getInstance().addExtensionProvider("purge", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());

        // Nick Exchange
        ProviderManager.getInstance().addExtensionProvider("nick", "http://jabber.org/protocol/nick", new Nick.Provider());

        // Attention
        ProviderManager.getInstance().addExtensionProvider("attention", "urn:xmpp:attention:0", new AttentionExtension.Provider());

        //input
        ProviderManager.getInstance().addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        ProviderManager.getInstance().addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        ProviderManager.getInstance().addIQProvider("open", "http://jabber.org/protocol/ibb", new OpenIQProvider());
        ProviderManager.getInstance().addIQProvider("close", "http://jabber.org/protocol/ibb",new CloseIQProvider());
        ProviderManager.getInstance().addExtensionProvider("data", "http://jabber.org/protocol/ibb",new DataPacketProvider());

    }

}
