package fr.martyr.listeners;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import fr.martyr.Main;
import fr.martyr.PlayerPet;
import fr.martyr.commands.CommandAlliance;
import fr.martyr.utils.ItemBuilder;
import fr.martyr.utils.Scatter;
import fr.martyr.utils.State;
import net.md_5.bungee.api.ChatColor;

public class GameEvents implements Listener {
   @EventHandler
   public void onWeatherChange(WeatherChangeEvent e) {
      e.setCancelled(e.toWeatherState());
   }

   @EventHandler
   public void onPlace(BlockPlaceEvent e) {
      if (State.isState(State.GAME) && e.getBlock().getY() >= 130) {
         e.getPlayer().sendMessage("Erreur: Vous ne pouvez pas poser plus haut.");
         e.setCancelled(true);
      }

   }

   @EventHandler
   public void onDamage(EntityDamageByEntityEvent e) {
      if (e.getCause().equals(DamageCause.PROJECTILE) && e.getEntity() instanceof Player) {
         Player damaged = (Player)e.getEntity();
         if (((Projectile)e.getDamager()).getShooter() instanceof Player) {
            Player damager = (Player)((Projectile)e.getDamager()).getShooter();
            if (damaged.getHealth() - e.getFinalDamage() > 0.0D) {
               damager.sendMessage("§dUHCThePetTravel §8» §6" + damaged.getName() + " §7est à §c" + this.getPercent((int)(damaged.getHealth() - e.getFinalDamage()) * 5) + "% §7de sa vie.");
               return;
            }
         }
      }
      
      final LivingEntity sourceEntity = (LivingEntity) e.getEntity();
      final Entity damagerEntity = e.getDamager();

      if(sourceEntity == null || damagerEntity == null) return;
      
      if(sourceEntity.getType() == EntityType.PLAYER && damagerEntity.getType() == EntityType.PLAYER) {
    	  if(CommandAlliance.confirmedAlliances.containsKey(sourceEntity.getUniqueId()) && 
    			  CommandAlliance.confirmedAlliances.get(sourceEntity.getUniqueId()).equals(damagerEntity.getUniqueId())) {
    		  e.setCancelled(true);
    		  ((Player) damagerEntity).sendMessage(ChatColor.RED + "Vous ne pouvez pas taper votre alliance!");
    		  return;
    	  }
      }
      
      if(sourceEntity.getType() != EntityType.PLAYER && damagerEntity.getType() == EntityType.PLAYER) {
    	  final PlayerPet pet = Main.getInstance().pets.get(damagerEntity.getUniqueId());
    	  
    	  if(pet == null || pet.entity == null) return;
    	  
    	  if(pet.entity.getUniqueId().equals(sourceEntity.getUniqueId())) {
    		  e.setCancelled(true);
    		  ((Player) damagerEntity).sendMessage(ChatColor.RED + "Vous ne pouvez pas taper votre pet!");
    		  return;
    	  }
    	  
    	  if(!Main.getInstance().game.getWorld().getPVP()) {
    		  ((Player) damagerEntity).sendMessage(ChatColor.RED + "Le PVP est actuellement désactivé!");
    		  return;
    	  }
    	  
    	  for(PlayerPet playerPet : Main.getInstance().pets.values()) {
    		  if(playerPet.entity != null && sourceEntity.getUniqueId().equals(playerPet.entity.getUniqueId())) {
    			  if((((LivingEntity) e.getEntity()).getHealth() - e.getDamage() <= 0) && 
    					  CommandAlliance.confirmedAlliances.containsKey(playerPet.getUUID()) && 
    	    			  CommandAlliance.confirmedAlliances.get(playerPet.getUUID()).equals(((Player) damagerEntity).getUniqueId())) {
    	    		  e.setCancelled(true);
    	    		  ((Player) damagerEntity).sendMessage(ChatColor.RED + "Vous ne pouvez pas tuer le pet de votre alliance!");
    	    		  return;
    	    	  }
    		  }
    	  }
      }else if(damagerEntity.getType() != EntityType.PLAYER){
    	  final PlayerPet pet = Main.getInstance().pets.get(damagerEntity.getUniqueId());
    	  
    	  if(pet == null || pet.entity == null) return;
    	  
    	  e.setCancelled(true);
      }
   }

   private String getPercent(int s) {
      if (s < 6) {
         return "§4" + s;
      } else if (s < 16) {
         return "§c" + s;
      } else if (s < 51) {
         return "§e" + s;
      } else {
         return s < 101 ? "§a" + s : "§a" + s;
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
 	  final Entity entity = event.getEntity();

      if (entity instanceof Player) {
         final Player p = (Player)event.getEntity();
         
         if (p.getGameMode() == GameMode.ADVENTURE || p.getGameMode() == GameMode.SPECTATOR || Main.getInstance().getGame().isInvincibility()) {
            event.setCancelled(true);
         }
      }else if(event.getCause() != DamageCause.ENTITY_ATTACK){    	  
    	  for(PlayerPet playerPet : Main.getInstance().pets.values()) {
    		  if(playerPet.entity != null && entity.getUniqueId().equals(playerPet.entity.getUniqueId())) {
    			  event.setCancelled(true);
    		  }
    	  }
      }
   }

   @EventHandler
   public void onHungerMeterChange(FoodLevelChangeEvent event) {
      if (State.isState(State.WAITING) || State.isState(State.FINISH)) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onDrop(PlayerDropItemEvent e) {
      if (State.isState(State.WAITING)) {
         e.setCancelled(true);
      }
   }

   @EventHandler
   public void onDeath(EntityDeathEvent event) {
      int i;
      if (event.getEntity() instanceof Chicken) {
         i = Main.getInstance().getGame().getFeatherRate();
         event.getDrops().add(new ItemStack(Material.FEATHER, i));
      }

      if (event.getEntity() instanceof Spider || event.getEntity() instanceof CaveSpider) {
         i = Main.getInstance().getGame().getStringRate();
         event.getDrops().add(new ItemStack(Material.STRING, i));
      }

   }

   @EventHandler
   public void onEntitySpawn(EntitySpawnEvent event) {
      if (event.getEntityType() == EntityType.WITCH || event.getEntityType() == EntityType.GUARDIAN || event.getEntityType() == EntityType.BAT) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onServerList(ServerListPingEvent e) {
      if (State.isState(State.GAME)) {
         e.setMotd("§cEn cours");
      } else if (State.isState(State.WAITING)) {
         e.setMotd("§bEn attente");
      } else if (State.isState(State.FINISH)) {
         e.setMotd("§6Fin de la partie");
      } else if (State.isState(State.TP)) {
         e.setMotd("§aTéléportation");
      }
   }

   @EventHandler
   public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
      Player p = event.getPlayer();
      if (p.getGameMode() == GameMode.SPECTATOR && event.getRightClicked().getType() == EntityType.PLAYER) {
         Player target = (Player)event.getRightClicked();
         Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, target.getName());

         for(int i = 0; i < 36; ++i) {
            if (target.getInventory().getItem(i) != null) {
               inv.setItem(i, target.getInventory().getItem(i));
            }
         }

         DecimalFormat df2 = new DecimalFormat("#.#");
         ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
         SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
         skullMeta.setDisplayName("§a" + target.getName());
         skullMeta.setOwner(target.getName());
         ItemStack t = (new ItemBuilder(skull)).setSkullOwner(target.getName()).setName("§a" + target.getName()).addLoreLine("§eVie: §c" + df2.format(target.getHealth() / 2.0D) + " �?�").addLoreLine("§eNourriture: §d" + df2.format((long)target.getFoodLevel()) + "/§d20").addLoreLine("§eNiveau: §d" + target.getLevel()).toItemStack();
         List<String> lore = new ArrayList();
         Iterator var11 = target.getActivePotionEffects().iterator();

         while(var11.hasNext()) {
            PotionEffect effect = (PotionEffect)var11.next();
            int eff = effect.getDuration();
            String time = (new SimpleDateFormat("mm:ss")).format(eff * 50);
            String var14;
            switch((var14 = effect.getType().getName()).hashCode()) {
            case -1481449460:
               if (var14.equals("INCREASE_DAMAGE")) {
                  lore.add("§eForce " + (effect.getAmplifier() + 1) + " §7(" + time + " min)");
               }
               break;
            case -944915573:
               if (var14.equals("REGENERATION")) {
                  lore.add("§eRégénération " + (effect.getAmplifier() + 1) + " §7(" + time + " min)");
               }
               break;
            case -774622513:
               if (var14.equals("ABSORPTION")) {
                  lore.add("§eAbsorption §7(" + time + " min)");
               }
               break;
            case 2288686:
               if (var14.equals("JUMP")) {
                  lore.add("§eJump boost " + (effect.getAmplifier() + 1) + " §7(" + time + " min)");
               }
               break;
            case 79104039:
               if (var14.equals("SPEED")) {
                  lore.add("§eVitesse " + (effect.getAmplifier() + 1) + " §7(" + time + " min)");
               }
               break;
            case 428830473:
               if (var14.equals("DAMAGE_RESISTANCE")) {
                  lore.add("§eRésistance " + (effect.getAmplifier() + 1) + " §7(" + time + " min)");
               }
               break;
            case 1073139170:
               if (var14.equals("FIRE_RESISTANCE")) {
                  lore.add("§eRésistance au feu §7(" + time + " min)");
               }
            }
         }

         ItemStack info = (new ItemBuilder(Material.BREWING_STAND)).setName("§6Effets de potions").setLore((List)(lore.isEmpty() ? Arrays.asList("§f» §7Aucun") : lore)).toItemStack();
         inv.setItem(46, info);
         ItemStack glass = (new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)).setName("§f").toItemStack();
         inv.setItem(45, t);
         inv.setItem(36, glass);
         inv.setItem(37, glass);
         inv.setItem(38, glass);
         inv.setItem(39, glass);
         inv.setItem(40, glass);
         inv.setItem(41, glass);
         inv.setItem(42, glass);
         inv.setItem(43, glass);
         inv.setItem(44, glass);
         inv.setItem(48, target.getInventory().getHelmet());
         inv.setItem(49, target.getInventory().getChestplate());
         inv.setItem(50, target.getInventory().getLeggings());
         inv.setItem(51, target.getInventory().getBoots());
         p.openInventory(inv);
      }
   }
   
   @EventHandler
   public void onEntityRegen(EntityRegainHealthEvent event) {
	   if(event.getEntityType() != EntityType.PLAYER) {
		   event.setCancelled(true);
	   }
   }
   
   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) {	   
	   final Player player = event.getPlayer();
	   final PlayerPet pet = Main.getInstance().pets.get(player.getUniqueId());
	   
	   if(pet == null || pet.entity == null) return;
	   
	   if(player.getLocation().getWorld() != pet.entity.getLocation().getWorld() || player.getLocation().distance(pet.entity.getLocation()) > 10) {
		   pet.entity.teleport(player.getLocation());
	   }
   }
   
   @EventHandler
   public void onEntityPortal(EntityPortalEvent event) {
	   final Entity entity = event.getEntity();
	   
	   if(entity == null) return;
	   
	   if(entity.getType() != EntityType.PLAYER) {
		   event.setCancelled(true);
	   }
   }
   
   @EventHandler
   public void onPlayerTeleport(PlayerTeleportEvent event) {
	   final Player player = event.getPlayer();
	   final PlayerPet pet = Main.getInstance().pets.get(player.getUniqueId());
	   
	   if(pet == null || pet.entity == null) return;
	   
	   pet.health = ((LivingEntity) pet.entity).getHealth();
	   pet.entity.remove();
	   pet.entity = null;
	   
	   Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
           Scatter.spawnEntity(player, event.getTo());
	   }, 3*20L);
   }
   
   @EventHandler
   public void craftItem(PrepareItemCraftEvent e) {       
       if(e.getRecipe().getResult().getType() == Material.SHIELD || e.getRecipe().getResult().getType() == Material.FISHING_ROD) {
    	   e.getInventory().setResult(new ItemStack(Material.AIR));
       }
   }
}