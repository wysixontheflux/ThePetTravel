package fr.martyr.game;

import fr.martyr.Main;
import fr.martyr.listeners.DeathEvent;
import fr.martyr.listeners.StackEvent;
import fr.martyr.utils.ActionBar;
import fr.martyr.utils.Scatter;
import fr.martyr.utils.State;
import fr.martyr.world.WorldManager;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PreGameManager {
   private Main main = Main.getInstance();
   public int timer;
   private int task;

   public PreGameManager() {
      this.timer = this.main.getAutostarttime();
      Main.getInstance().getGame().setStart(true);
      this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.main, () -> {
         --this.timer;
         this.main.getGame().setTimer(this.main.getGame().getTimer() - 1);
         if (this.timer == 0) {
            if (Bukkit.getOnlinePlayers().size() < this.main.getAutostart() && !this.main.getGame().isForcestart()) {
               Bukkit.broadcastMessage("§dUHCThePetTravel §8» §cIl n'y a pas assez de joueurs pour démarrer.");
               Bukkit.getScheduler().cancelTask(this.task);
               this.main.getGame().setTimer(this.main.getAutostarttime());
               this.timer = this.main.getAutostarttime();
               Main.getInstance().getGame().setStart(false);
               return;
            }

            State.setState(State.TP);
            Bukkit.setWhitelist(false);
            World world = Main.getInstance().getGame().getWorld();
            world.setPVP(false);
            world.getWorldBorder().setSize((double)(this.main.getGame().getInitborderSize() * 2));
            Iterator var3 = Bukkit.getOnlinePlayers().iterator();

            while(var3.hasNext()) {
               Player players = (Player)var3.next();
               players.setLevel(0);
               players.playSound(players.getLocation(), Sound.ENTITY_DONKEY_EAT, 3.0F, 3.0F);
               (new ActionBar("§7Téléportation...")).sendToAll();
               players.getInventory().clear();
            }

            (new Scatter(true, (int)this.main.getGame().getWorld().getWorldBorder().getSize() - 5)).runTaskTimer(Main.getInstance(), 0L, 2L);
         }

         if (this.timer == -7) {
            WorldManager.clearAllCustomEntities();
         }

         if (this.timer < -8 && this.timer > -12) {
            (new ActionBar("§7≫ §eDémarrage dans §f" + (this.timer + 12) + "s§e.")).sendToAll();
         }

         if (this.timer == -12) {
            (new ActionBar("§7≫ §eQue le meilleur gagne !")).sendToAll();
            Bukkit.getScheduler().cancelTask(this.task);
            
            State.setState(State.GAME);
            
            WorldManager.registerObjectives();
            
            Scatter.stayLocs.clear();
            Scatter.blocks.forEach((block) -> {
               block.setType(Material.AIR);
            });
            Scatter.blocks.clear();
            
            Iterator var5 = Main.getInstance().getGame().getAlivePlayers().iterator();

            while(var5.hasNext()) {
               UUID uuid = (UUID)var5.next();
               Player player = Bukkit.getPlayer(uuid);
               if (player != null) {
                  player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 5.0F);
                  player.getActivePotionEffects().clear();
                  player.teleport(player.getLocation().clone().add(0.0D, 2.0D, 0.0D));
                  player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 3, 1, false, false));
                  player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, 0, false, false));
                  player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, false, false));
                  player.setWalkSpeed(0.2F);
                  player.setGameMode(GameMode.SURVIVAL);
               }
            }

            this.main.getGame().setTimer(0);
            Bukkit.broadcastMessage("§7§m+---------------------------------------+");
            Bukkit.broadcastMessage(" §8» §7Les alliances entre joueurs sont " + ChatColor.GREEN + "autorisées§7.");
            Bukkit.broadcastMessage(" §8» §7Vous êtes invincible pendant §e30 §7secondes.");
            Bukkit.broadcastMessage("§7§m+---------------------------------------+");
            Bukkit.getPluginManager().registerEvents(new DeathEvent(), this.main);
            Bukkit.getPluginManager().registerEvents(new StackEvent(3), this.main);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
               List<Block> blocks = new ArrayList();

               for(int x = -30; x < 30; ++x) {
                  for(int y = 205; y > 195; --y) {
                     for(int z = -30; z < 30; ++z) {
                        Block block = (new Location(WorldManager.WORLD, (double)x, (double)y, (double)z)).getBlock();
                        if (block.getType() != Material.AIR) {
                           if (block.getType() != Material.WATER) {
                              blocks.add(block);
                           } else {
                              block.setType(Material.AIR);
                           }
                        }
                     }
                  }
               }

               this.removeBlocks(blocks);
            }, 200L);
            new GameManager();
            (new BukkitRunnable() {
               public void run() {
                  Main.getInstance().getGame().setInvincibility(false);
                  Bukkit.broadcastMessage("§dUHCThePetTravel §7» §eVous êtes désormais vulnérables aux §9dégâts§e.");
                  CreatureSpawnEvent.getHandlerList();
               }
            }).runTaskLaterAsynchronously(Main.getInstance(), 600L);
         }

      }, 0L, 20L);
   }

   private void removeBlocks(List<Block> blocks) {
      final List<Block> toRemove = new ArrayList(blocks);
      (new BukkitRunnable() {
         public void run() {
            for(int i = 0; i < 200; ++i) {
               if (toRemove.isEmpty()) {
                  this.cancel();
               } else {
                  Block block = (Block)toRemove.get(0);
                  toRemove.remove(block);
                  block.setType(Material.AIR);
               }
            }

         }
      }).runTaskTimer(Main.getInstance(), 20L, 2L);
   }
}