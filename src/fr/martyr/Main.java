package fr.martyr;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.YELLOW;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.martyr.commands.CommandAlliance;
import fr.martyr.commands.CommandKit;
import fr.martyr.commands.CommandPetLocate;
import fr.martyr.commands.CommandSetTimer;
import fr.martyr.commands.Commands;
import fr.martyr.game.Game;
import fr.martyr.hook.SlotPatcher;
import fr.martyr.listeners.ChatEvent;
import fr.martyr.listeners.GameEvents;
import fr.martyr.listeners.JoinEvent;
import fr.martyr.listeners.NoMoveEvent;
import fr.martyr.listeners.WaitingRunEvent;
import fr.martyr.scoreboard.ScoreboardManager;
import fr.martyr.world.Manager;
import fr.martyr.world.WorldManager;

public class Main extends JavaPlugin implements Listener {
   public static Main instance;
   public boolean isPregen = true;
   private int autostart;
   private int autostarttime;
   public Game game = new Game();
   private ScoreboardManager scoreboardManager;
   private ScheduledExecutorService executorMonoThread;
   private ScheduledExecutorService scheduledExecutorService;

   public HashMap<UUID, PlayerPet> pets = new HashMap<UUID, PlayerPet>();
   
   public Player getPlayerFromPetUUID(UUID uuid) {
	   for(PlayerPet pet : pets.values()) {
		   if(pet.entity.getUniqueId().equals(uuid)) {
			   final Player pplayer = Bukkit.getPlayer(pet.getUUID());
			   
			   if(pplayer.isOnline()) {
				   return pplayer;
			   }
			   
			   break;
		   }
	   }
	   
	   return null;
   }
   
   private Inventory petInventory;
   
   public Inventory getPetInventory() {
	   return this.petInventory;
   }
   
   public void onEnable() {
      instance = this;
      
      this.getCommand("alliance").setExecutor(new CommandAlliance());
      this.getCommand("petlocate").setExecutor(new CommandPetLocate());
      this.getCommand("settimer").setExecutor(new CommandSetTimer());
      this.getCommand("kit").setExecutor(new CommandKit());
      
      this.registerListener();
      this.saveDefaultConfig();
      
      final int[] petSlotsInventory = new int[100];
      
      int count = 0;
      
      for(int io = 0; io < 3; io++) {
    	  for(int ioo = 0; ioo < 7; ioo++) {
    		  petSlotsInventory[count] = 10 + (9*io) + ioo;
    		  count++;
    	  }
      }
      
      this.petInventory = Bukkit.createInventory(null, 45, YELLOW + "" + BOLD + "Selecteur de Pets");
      
      for(int io = 0; io < AnimalPet.values().length; io++) {
    	  final AnimalPet pet = AnimalPet.values()[io];
    	  
    	  if(pet == AnimalPet.BEE) continue;
    	  
    	  final ItemStack monsterStack = new ItemStack(pet.getEgg());
    	  final ItemMeta monsterStackMeta = monsterStack.getItemMeta();
    	  monsterStackMeta.setDisplayName(pet.getItemName());
    	  monsterStack.setItemMeta(monsterStackMeta);
    	  this.petInventory.setItem(petSlotsInventory[io], monsterStack);
      }
      
      final AnimalPet pet = AnimalPet.BEE;	  
	  final ItemStack monsterStack = new ItemStack(pet.getEgg());
	  final ItemMeta monsterStackMeta = monsterStack.getItemMeta();
	  monsterStackMeta.setDisplayName(pet.getItemName());
	  monsterStack.setItemMeta(monsterStackMeta);
      this.petInventory.setItem(31, monsterStack);
      
      this.setAutostart(this.getConfig().getInt("autostart"));
      this.setAutostarttime(this.getConfig().getInt("autostarttime"));
      this.getGame().setSlot(this.getConfig().getInt("slot"));

      try {
         (new SlotPatcher()).changeSlots(this.getGame().getSlot());
      } catch (ReflectiveOperationException var2) {
         var2.printStackTrace();
      }

      try {
		(new SlotPatcher()).updateServerProperties();
	} catch (Throwable e) {
		e.printStackTrace();
	}
      Bukkit.getScheduler().runTaskLater(this, new Runnable() {
         public void run() {
            Main.this.getGame().setSize(Main.this.getConfig().getInt("size"));
            Main.this.registerCommands();
            Main.this.manageTab();
            Main.this.scheduledExecutorService = Executors.newScheduledThreadPool(16);
            Main.this.executorMonoThread = Executors.newScheduledThreadPool(1);
            Main.this.scoreboardManager = new ScoreboardManager();
            (new Manager()).createWorlds();
         }
      }, 1L);
   }

   public void onDisable() {
      Iterator var2 = Bukkit.getScoreboardManager().getMainScoreboard().getTeams().iterator();

      while(var2.hasNext()) {
         Team teams = (Team)var2.next();
         teams.unregister();
      }

      WorldManager.onDisable(true);
      this.getScoreboardManager().onDisable();
      (new Manager()).deleteWorld(new File("world"));
      (new Manager()).deleteWorld(new File("world_nether"));
      try {
		(new SlotPatcher()).updateServerProperties();
	} catch (Throwable e) {
		e.printStackTrace();
	}
   }

   public void registerCommands() {
      this.getCommand("game").setExecutor(new Commands());
   }

   public void registerListener() {
      PluginManager pm = Bukkit.getPluginManager();
      List<Listener> listeners = new ArrayList();
      listeners.add(new ChatEvent());
      listeners.add(new JoinEvent());
      listeners.add((new NoMoveEvent()).listener);
      listeners.add(new GameEvents());
      listeners.add(new WaitingRunEvent());
      Iterator var4 = listeners.iterator();

      while(var4.hasNext()) {
         Listener listener = (Listener)var4.next();
         pm.registerEvents(listener, this);
      }

   }

   public static Main getInstance() {
      return instance;
   }

   public Game getGame() {
      return this.game;
   }

   public ScoreboardManager getScoreboardManager() {
      return this.scoreboardManager;
   }

   public ScheduledExecutorService getExecutorMonoThread() {
      return this.executorMonoThread;
   }

   public ScheduledExecutorService getScheduledExecutorService() {
      return this.scheduledExecutorService;
   }

   public static Location stringToLoc(String string) {
      String[] args = string.split(", ");
      return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
   }

   public void manageTab() {
      System.out.print("Gestion du tab...");
      Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
      if (scoreboard.getTeam("91") == null) {
         Team team = scoreboard.registerNewTeam("91");
         team.setPrefix("§7");
      }

   }

   public int getAutostart() {
      return this.autostart;
   }

   public void setAutostart(int autostart) {
      this.autostart = autostart;
   }

   public int getAutostarttime() {
      return this.autostarttime;
   }

   public void setAutostarttime(int autostarttime) {
      this.autostarttime = autostarttime;
   }
}