/* UserStateType.java
*
* Programmed By:
*     Kevin Fahy
*     
* Change Log:
*     2009-June-28, KF
*         First write.
*                  
* Known Issues:
*     none
*
* Copyright (C) 2009  Pirate Captains
*
* License: GNU General Public License version 2.
* Full license can be found in ParrotIM/LICENSE.txt.
*/

package com.fn.reunion.app.controller;

public enum UserStateType {
	
   ONLINE("Online"),
   
   CHAT("Chat"),
   
   AWAY("Away"),
   
   BUSY("Busy"),

   OFFLINE("Offline"),

   NOT_AVAILABLE("not available"),

   NOT_BE_DISTURBED("dnd"),

   INVISIBLE("Invisible"),

   BRB("Be right back"),

   PHONE("On the phone"),

   LUNCH("Lunch"),
   
   EXTENDED_AWAY("xa");

   /**
    * The name of the enumerated type for GUI output.
    */
   private String name;

   /**
    * Default constructor. Assigns a String name to each type for output on the
    * GUI.
    *
    * @param name
    */
   private UserStateType(String name) {
       this.name = name;
   }

   /**
    * Converts the enumerated type to a String.
    *
    * @return The name of the enumerated type.
    */
   public String toString() {
       return this.name;
   }
}