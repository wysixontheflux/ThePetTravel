package fr.martyr.hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;

import fr.martyr.Main;

public class SlotPatcher {
   private Main main = Main.getInstance();

   public void changeSlots(int slots) throws ReflectiveOperationException {
      Method serverGetHandle = this.main.getServer().getClass().getDeclaredMethod("getHandle");
      Object playerList = serverGetHandle.invoke(this.main.getServer());
      Field maxPlayersField = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");
      maxPlayersField.setAccessible(true);
      maxPlayersField.set(playerList, slots);
   }

   public void updateServerProperties() throws Throwable {
      Properties properties = new Properties();
      File propertiesFile = new File("server.properties");

      try {
         Throwable var3 = null;
         Throwable var4 = null;

         FileInputStream is;
         try {
            is = new FileInputStream(propertiesFile);

            try {
               properties.load(is);
            } finally {
               if (is != null) {
                  is.close();
               }

            }
         } catch (Throwable var30) {
            if (var3 == null) {
               var3 = var30;
            } else if (var3 != var30) {
               var3.addSuppressed(var30);
            }

            throw var3;
         }

         String maxPlayers = Integer.toString(this.main.getServer().getMaxPlayers());
         if (properties.getProperty("max-players").equals(maxPlayers)) {
            return;
         }

         this.main.getLogger().info("Saving max players to server.properties...");
         properties.setProperty("max-players", maxPlayers);
         var4 = null;
         is = null;

         try {
            FileOutputStream os = new FileOutputStream(propertiesFile);

            try {
               properties.store(os, "Minecraft server properties");
            } finally {
               if (os != null) {
                  os.close();
               }

            }
         } catch (Throwable var28) {
            if (var4 == null) {
               var4 = var28;
            } else if (var4 != var28) {
               var4.addSuppressed(var28);
            }

            throw var4;
         }
      } catch (IOException var31) {
         this.main.getLogger().log(Level.SEVERE, "An error occurred while updating the server properties", var31);
      }

   }
}