package fr.martyr.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.martyr.AnimalPet;
import fr.martyr.Main;
import fr.martyr.PetMaker;
import fr.martyr.PlayerPet;

public class Scatter extends BukkitRunnable {
   public static List<Block> blocks = new ArrayList();
   public static Map<UUID, Location> stayLocs = new HashMap();
   private List<Player> players = new ArrayList();
   private static int b;
   private boolean j;
   private boolean start;

   public Scatter(Boolean start, Integer b) {
      Scatter.b = b;
      this.j = true;
      this.start = start;
   }

   public void run() {
      if (this.j) {
         Main.getInstance().getGame().getAlivePlayers().forEach((uuid) -> {
            this.players.add(Bukkit.getPlayer(uuid));
         });
         this.j = false;
      }

      if (this.players.size() == 0) {
         this.cancel();
      } else {
         Random random = new Random();
         Player playerToTp = (Player)this.players.get(random.nextInt(this.players.size()));
         
         if (playerToTp != null && playerToTp.getGameMode() != GameMode.SPECTATOR) {
            playerToTp.setGameMode(GameMode.SURVIVAL);
            
            final Location loc = this.randomLocation();
            
            playerToTp.teleport(loc);
            
            if(!Main.getInstance().pets.containsKey(playerToTp.getUniqueId())) {
            	Main.getInstance().pets.put(playerToTp.getUniqueId(), new PlayerPet(playerToTp.getUniqueId(), AnimalPet.random()));
            }
            
            final PlayerPet pet = spawnEntity(playerToTp, loc);
            playerToTp.getInventory().addItem(pet.getPet().getStacks());
            
            playerToTp.setVelocity(new Vector(0, 2, 0));
            
            if (this.start) {
               playerToTp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1, false, false));
               playerToTp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 9, false, false));
               this.setSpawnSpot(playerToTp);
            }
         }

         this.players.remove(playerToTp);
      }
   }

   public static PlayerPet spawnEntity(Player player, Location loc) {
	   final PlayerPet pet = Main.getInstance().pets.get(player.getUniqueId());
   		final LivingEntity entity = (LivingEntity) player.getLocation().getWorld().spawnEntity(loc, pet.getPet().getEntity());
       entity.setCustomNameVisible(true);
       entity.setCustomName(ChatColor.YELLOW + "Pet de " + ChatColor.GREEN + player.getName());
       entity.setRemoveWhenFarAway(false);
       
       if(pet.health != -1) {
    	   entity.setMaxHealth(40.0D);
    	   entity.setHealth(pet.health);
       }else {
    	   entity.setMaxHealth(40.0D);
    	   entity.setHealth(40.0D);
       }
       
       if(entity instanceof Ageable) {
           ((Ageable) entity).setBaby();
       }
       
       PetMaker.makePet(entity, player);
       Main.getInstance().pets.get(player.getUniqueId()).entity = entity;
       return pet;
   }
   
   public static Location randomLocation() {
      Random random = new Random();
      int x = (random.nextInt(2) == 0 ? 1 : -1) * random.nextInt(Scatter.b / 2);
      int z = (random.nextInt(2) == 0 ? 1 : -1) * random.nextInt(Scatter.b / 2);
      Location location = Main.getInstance().getGame().getWorld().getHighestBlockAt(x, z).getLocation();
      location.setY(location.getY() + 35.0D);
      if (!location.getChunk().isLoaded()) {
         location.getChunk().load();
      }

      return location;
   }

   private void setSpawnSpot(Player player) {
      for(int x = -3; x < 3; ++x) {
         for(int z = -3; z < 3; ++z) {
            Block block = player.getLocation().clone().add((double)x, -6.0D, (double)z).getBlock();
            block.setType(Material.WHITE_STAINED_GLASS);
            if (x == -3 || x == 2 || z == -3 || z == 2) {
                block.setType(Material.BLUE_STAINED_GLASS);
            }

            blocks.add(block);
         }
      }

      stayLocs.put(player.getUniqueId(), player.getLocation().clone().add(0.0D, -4.0D, 0.0D));
   }
}