package fr.martyr.world;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import fr.martyr.Main;

public class Manager {
   private Main main = Main.getInstance();

   public void deleteWorld(File path) {
      System.out.println("Deleting... " + path.getName());
      World world = Bukkit.getWorld(path.getName());
      Bukkit.unloadWorld(world, false);

      try {
         FileUtils.forceDeleteOnExit(path);
         System.out.println("World deleted: " + path.getName());
      } catch (IOException var4) {
         System.out.println("Error while deleting world (" + path.getAbsolutePath() + ")\n" + var4.getMessage());
      }

   }

   public void createWorlds() {
      System.out.print("Setup du monde world.");
      Iterator var2 = Bukkit.getWorlds().iterator();

      while(var2.hasNext()) {
         World world = (World)var2.next();
         world.setDifficulty(Difficulty.NORMAL);
         world.setGameRuleValue("naturalRegeneration", "false");
         world.setGameRuleValue("doFireTick", "false");
         world.setGameRuleValue("sendCommandFeedback", "false");
         world.setGameRuleValue("doDaylightCycle", "false");
         world.setTime(1000L);
         world.setPVP(false);
         world.setStorm(false);
         world.setThundering(false);
         world.setSpawnLocation(0, 200, 0);
         WorldBorder wb = world.getWorldBorder();
         wb.setSize(2000.0D);
         wb.setCenter(0.0D, 0.0D);
         wb.setWarningDistance(10);
         wb.setWarningTime(10);
         wb.setDamageAmount(1.0D);
         wb.setDamageBuffer(1.0D);
         this.main.getServer().setSpawnRadius(0);
         Main.getInstance().getGame().setWorld(Bukkit.getWorld("world"));
         Main.getInstance().getGame().setSpawn(new Location(Main.getInstance().getGame().getWorld(), 0.0D, 200.0D, 0.0D));
      }

      Bukkit.setWhitelist(false);
      boolean regen = Main.getInstance().getConfig().getBoolean("regen");
      System.out.println("Staut de la pregn: " + regen);
      //StructureLoader.load("spawn");}

   }
}