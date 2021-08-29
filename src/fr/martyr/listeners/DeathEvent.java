package fr.martyr.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.martyr.Main;
import fr.martyr.PlayerPet;
import fr.martyr.game.WinManager;
import fr.martyr.player.PlayerManager;
import fr.martyr.utils.Scatter;
import fr.martyr.utils.State;

public class DeathEvent implements Listener {
   @EventHandler
   public void onEntityDeathEvent(EntityDeathEvent event) {
	  if(State.isState(State.WAITING) || event.getEntityType() == EntityType.PLAYER) return;
	  
      final Entity entity = event.getEntity();
      final Player p = Main.getInstance().getPlayerFromPetUUID(entity.getUniqueId());
      
      if(p == null) return;
      
      p.setHealth(20.0D);
      (new PlayerManager(p)).setSpec();
      p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 5.0F, 2.0F);
      p.getWorld().strikeLightningEffect(p.getLocation());
      
      Main.getInstance().getGame().getAlivePlayers().remove(p.getUniqueId());
      
      if (p.getKiller() != null) {
         p.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 400, 1));
      }

      Bukkit.broadcastMessage("§aUHC §8» §c" + p.getName() + " §7vient d'être éliminé car son pet vient de mourir!");
      
      Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
         WinManager.checkWin();
      }, 10L);
   }
   
   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent event) {
	   final Player player = event.getPlayer();
	   final PlayerPet pet = Main.getInstance().pets.get(player.getUniqueId());
	   
	   if(pet == null || pet.entity == null) {
		   return;
	   }
	   
	   final Location loc = Scatter.randomLocation();
	   loc.setY(loc.getWorld().getHighestBlockYAt(loc));
	   event.setRespawnLocation(loc);
	   
	   player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
	   
	   pet.health = ((LivingEntity) pet.entity).getHealth();
	   pet.entity.remove();
	   pet.entity = null;
	   
	   Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
           Scatter.spawnEntity(player, loc);
	   }, 5*20L);
	   
	   Bukkit.broadcastMessage("§aUHC §8» §c" + player.getName() + " §7est mort!");
   }
}