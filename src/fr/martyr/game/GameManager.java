package fr.martyr.game;

import fr.martyr.Main;
import fr.martyr.utils.Scatter;
import fr.martyr.utils.State;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class GameManager {
   public static boolean isBorder = false;

   public GameManager() {
      Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
         if (State.isState(State.GAME)) {
            int timer = Main.getInstance().getGame().getTimer();
            Main.getInstance().getGame().setTimer(timer + 1);
            if (timer + 30 == Main.getInstance().getGame().getPvPTime() * 60) {
               Bukkit.broadcastMessage("§dUHCThePetTravel §8» §7PvP dans §e30 §7secondes.");
            }

            if (timer + 10 == Main.getInstance().getGame().getPvPTime() * 60) {
               Bukkit.broadcastMessage("§dUHCThePetTravel §8» §7PvP dans §e10 §7secondes.");
            }

            if (timer + 5 == Main.getInstance().getGame().getPvPTime() * 60) {
               Bukkit.broadcastMessage("§dUHCThePetTravel §8» §7PvP dans §e5 §7secondes.");
            }

            if (timer == Main.getInstance().getGame().getPvPTime() * 60 && !Main.getInstance().getGame().isTP()) {
               PvP();
            }
            
            if (timer == Main.getInstance().getGame().getBorder() * 60) {
                border();
            }
        }
      }, 0L, 20L);
   }

   public static void PvP() {
      Main.getInstance().getGame().setInvincibility(true);
      Main.getInstance().getGame().setTP(true);
      Main.getInstance().getGame().getWorld().setPVP(true);

      Bukkit.broadcastMessage("§7§m+------------------------------------+");
      Bukkit.broadcastMessage(" §f» §6Le PvP est désormais §aactivé§6.");
      Bukkit.broadcastMessage(" §f» §6Tous les joueurs ont reçu un heal final.");
      Bukkit.broadcastMessage("§7§m+------------------------------------+");
      Iterator var1 = Main.getInstance().getGame().getAlivePlayers().iterator();

      while(var1.hasNext()) {
         UUID uuid = (UUID)var1.next();
         Player player = Bukkit.getPlayer(uuid);
         if (player != null) {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.setHealth(Bukkit.getPlayer(uuid).getMaxHealth());
            player.playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.ENTITY_WOLF_GROWL, 2.0F, 2.0F);
         }
      }

      Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
         Main.getInstance().getGame().setInvincibility(false);
         World world = Bukkit.getWorld("world");
         world.setGameRuleValue("randomTickSpeed", "3");
         world.setGameRuleValue("doMobSpawning", "true");
      }, 200L);
   }

   public static void border() {
      Bukkit.broadcastMessage("§dUHCThePetTravel §8» §7Réduction de la bordure en cours.");
      Bukkit.broadcastMessage("§dUHCThePetTravel §8» §7Rapprochez vous du §ecentre §7sous peine de dégats.");
      WorldBorder wb = Main.getInstance().getGame().getWorld().getWorldBorder();
      wb.setSize((double)(Main.getInstance().getGame().getTPBorder() * 2));
      wb.setSize((double)(Main.getInstance().getGame().getFinalBorderSize() * 2), (long)(Main.getInstance().getGame().getBorderMoveTime() * 60));
      isBorder = true;
      kickOffline();
   }

   public static void kickOffline() {
      List<UUID> uuids = new ArrayList(Main.getInstance().getGame().getAlivePlayers());
      Iterator var2 = uuids.iterator();

      while(var2.hasNext()) {
         UUID uuid = (UUID)var2.next();
         if (Bukkit.getPlayer(uuid) == null) {
            Main.getInstance().getGame().getAlivePlayers().remove(uuid);
         }
      }

      Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
         Bukkit.broadcastMessage("§dUHCThePetTravel §8» §7Les joueurs déconnectés ont été §céliminés§7.");
      }, 2L);
      WinManager.checkWin();
   }
}