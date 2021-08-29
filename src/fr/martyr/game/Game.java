package fr.martyr.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Game {
   private boolean start = false;
   private World world = Bukkit.getWorld("world");
   private List<UUID> alivePlayers = new ArrayList();
   private int slot = 999;
   private int pvp = 20;
   private int border = 90;
   private int bordermovetime = 68;
   private int timer = 30;
   private int initborder = 800;
   private int tpborder = 800;
   private int finalborder = 30;
   private int featherrate = 1;
   private int stringrate = 1;
   private boolean invincibility = true;
   private boolean isBorder = true;
   private boolean isTP = false;
   private boolean forcestart = false;
   private Location spawn = new Location(this.getWorld(), 0.0D, 200.0D, 0.0D);
   private int size = 800;

   public World getWorld() {
      return this.world;
   }

   public void setWorld(World world) {
      this.world = world;
   }

   public List<UUID> getAlivePlayers() {
      return this.alivePlayers;
   }

   public int getBorder() {
      return this.border;
   }

   public int getPvPTime() {
      return this.pvp;
   }

   public int getBorderTime() {
      return this.border;
   }

   public int getFeatherRate() {
      return this.featherrate;
   }

   public int getStringRate() {
      return this.stringrate;
   }

   public boolean isInvincibility() {
      return this.invincibility;
   }

   public void setInvincibility(boolean invincibility) {
      this.invincibility = invincibility;
   }

   public boolean isBorder() {
      return this.isBorder;
   }

   public void setBorder(boolean isBorder) {
      this.isBorder = isBorder;
   }

   public int getTimer() {
      return this.timer;
   }

   public void setTimer(int timer) {
      this.timer = timer;
   }

   public int getFinalBorderSize() {
      return this.finalborder;
   }

   public int getInitborderSize() {
      return this.initborder;
   }

   public int getBorderMoveTime() {
      return this.bordermovetime;
   }

   public int getSize() {
      return this.size;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public boolean getStart() {
      return this.start;
   }

   public void setStart(boolean start) {
      this.start = start;
   }

   public Location getSpawn() {
      return this.spawn;
   }

   public void setSpawn(Location spawn) {
      this.spawn = spawn;
   }

   public boolean isTP() {
      return this.isTP;
   }

   public void setTP(boolean isTP) {
      this.isTP = isTP;
   }

   public int getTPBorder() {
      return this.tpborder;
   }

   public void setTPBorder(int tpborder) {
      this.tpborder = tpborder;
   }

   public boolean isForcestart() {
      return this.forcestart;
   }

   public void setForcestart(boolean forcestart) {
      this.forcestart = forcestart;
   }

   public int getSlot() {
      return this.slot;
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }
}