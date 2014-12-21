package com.fn.reunion.app.xmpp;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by menaitech on 12/21/14.
 */
public class RosterListenerImpl implements RosterListener {

    HashMap<String, String> userStatus = new HashMap<String, String>();
    boolean dirty = true;

    public RosterListenerImpl(Roster roster) {
        super();
    }
    public void entriesAdded(Collection<String> addresses){
        dirty = true;
        System.out.println("Entry added");
        for (String string : addresses) {
            System.out.println(string);
        }
    }
    public void entriesUpdated(Collection<String> addresses){
        dirty = true;
        System.out.println("Entry update");
        for (String string : addresses) {
            System.out.println(string);
//          try {
//              roster.createEntry(string, "nicck", null);
//          } catch (XMPPException e) {
//              e.printStackTrace();
//          }
        }
    }

    public void entriesDeleted(Collection<String> addresses){
        dirty = true;
        System.out.println("Entry deleted");
        for (String string : addresses) {
            System.out.println(string);
        }
    }
    public void presenceChanged(Presence presence){
        dirty = true;
        System.out.println("Presenceof "+presence.getFrom()+" changed to"+presence.getStatus());
        userStatus.put(presence.getFrom(), presence.getStatus());
    }
    public String getPresence(String user){
        return userStatus.get(user);
    }
    public boolean isDirty(){
        return dirty;
    }
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
