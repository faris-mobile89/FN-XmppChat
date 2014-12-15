package com.fn.reunion.app.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.fn.reunion.app.model.MMessage;
import com.fn.reunion.app.model.UserInfo;

public class FNSerializableManager {
	
	
    public static void saveObject(Object ob , String fileName){
		  try
		 {
		            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("/sdcard/fn/"+fileName+".bin"))); //Select where you wish to save the file...
		            oos.writeObject(ob); // write the class as an 'object'
		            oos.flush(); // flush the stream to insure all of the information was written to 'file_path.bin'
		            oos.close();// close the stream
		         }
		         catch(Exception ex)
		         {
		            Log.v("Serialization Save Error : ",ex.getMessage());
		            ex.printStackTrace();
		         }
		 }
    
	public static void wirteObject(Context context , Object ob , String fileName){
		
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			Log.v("Writing Error : ",e.getMessage());
		}
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(fos);
			os.writeObject(ob);
			os.close();
		} catch (IOException e) {
			Log.v("Writing Error : ",e.getMessage());
		}
	
	}
    
    public static Object loadSerializedObject(File f)
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Object o = ois.readObject();
            return o;
        }
        catch(Exception ex)
        {
        Log.v("Serialization Read Error : ",ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
    
    public static List<UserInfo> loadSerializedUserInfo(Context context,String fileName)
	    {
	        try
	        {
	        	FileInputStream fis  = context.openFileInput(fileName);
	
	        	ObjectInputStream ois = new ObjectInputStream(fis);
	        	
	            @SuppressWarnings("unchecked")
				ArrayList<UserInfo> usersList =   (ArrayList<UserInfo>) ois.readObject();
	            ois.close();
	            return usersList;
	        }
	        catch(Exception ex)
	        {
	        Log.v("Serialization Read Error : ",ex.getMessage());
	            ex.printStackTrace();
	        }
	        return null;
	    }
    
    public static void deleteChatFile(String fileName){
    	File file = new File(fileName);
    	if (file.exists()) {
    	   	file.delete();
    	   	Log.i("FNSerializableManager", "chat deleted");
		}
    }
    
    public static ArrayList<MMessage> loadSerializedMessages(Context context,String fileName)
    {
        try
        {
        	FileInputStream fis  = context.openFileInput(fileName);

        	ObjectInputStream ois = new ObjectInputStream(fis);
        	
            @SuppressWarnings("unchecked")
            ArrayList<MMessage> msgs =  (ArrayList<MMessage>) ois.readObject();
            ois.close();
            return msgs;
        }
        catch(Exception ex)
        {
        Log.v("Serialization Read Error : ",ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
	
	public static UserInfo getObject (Context context,String fileName){
		
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(fileName);
		} catch (FileNotFoundException e) {
			Log.v("Serialization Read Error : ",e.getMessage());
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(fis);
		} catch (StreamCorruptedException e) {
			Log.v("Serialization Read Error : ",e.getMessage());
		} catch (IOException e) {
			Log.v("Serialization Read Error : ",e.getMessage());
		}
		UserInfo userInfo = null;
		try {
			userInfo = (UserInfo) ois.readObject();
		} catch (OptionalDataException e) {
			Log.v("Serialization Read Error : ",e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.v("Serialization Read Error : ",e.getMessage());
		} catch (IOException e) {
			Log.v("Serialization Read Error : ",e.getMessage());

		}
		try {
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userInfo;
	}
}
