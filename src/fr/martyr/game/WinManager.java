package fr.martyr.game;

import fr.martyr.Main;
import fr.martyr.player.PlayerManager;
import fr.martyr.utils.State;
import fr.martyr.world.WorldManager;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class WinManager {
   private static int time = 0;

   public static boolean checkWin() {
      if (Main.getInstance().getGame().getAlivePlayers().size() <= 1) {
         win((UUID)Main.getInstance().getGame().getAlivePlayers().get(0));
         return true;
      } else {
         return false;
      }
   }

   public static void win(UUID id) {
      if (State.isState(State.GAME)) {
         Location loc = Main.getInstance().getGame().getSpawn();
         Player winner = Bukkit.getOfflinePlayer(id).getPlayer();
         State.setState(State.FINISH);
         Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            Main.getInstance().getGame().getWorld().getWorldBorder().setSize(400.0D);
            Iterator var4 = Bukkit.getOnlinePlayers().iterator();

            while(var4.hasNext()) {
               Player pl = (Player)var4.next();
               pl.teleport(loc);
               if (pl.getUniqueId() == id) {
                  pl.setPlayerListName("§6Vainqueur " + pl.getName());
                  pl.sendTitle("§6Vous avez gagné", "§fVictoire de §a" + winner.getName());
                  pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10.0F, 10.0F);
               } else {
                  pl.sendTitle("", "§fVictoire de §a" + winner.getName());
                  pl.playSound(pl.getLocation(), Sound.ENTITY_WITHER_DEATH, 5.0F, 5.0F);
               }
            }

            Main.getInstance().getGame().setInvincibility(true);
            Bukkit.broadcastMessage("§f§m+------§c§m---------------§f§m------+");
            Bukkit.broadcastMessage("          §d✦ §ePartie terminée §d✦ ");
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§6Victoire de §3" + winner.getName() + "§6.");
            Bukkit.broadcastMessage("§6Avec un total de §7" + WorldManager.getKills(winner) + " §6kills.");
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§aTop Kills:");
            Map<String, Integer> top10 = WorldManager.getTop10();

            for(int i = 0; i < 3 && top10.size() > i; ++i) {
               String player = (String)top10.keySet().toArray()[i];
               Bukkit.broadcastMessage("§a#" + (i + 1) + ". §e" + player + " §7» §f" + top10.get(player) + " kills");
               Player p = Bukkit.getPlayer(player);
            }

            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§f§m+------§c§m---------------§f§m------+");
            launchWinFireworks();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
               Bukkit.getOnlinePlayers().forEach((p) -> {
                  (new PlayerManager(p)).kickPlayer();
               });
            }, 500L);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
            }, 540L);
         }, 10L);
      }
   }

   public static void launchWinFireworks() {
      Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
         if (State.isState(State.FINISH) && time < 40) {
            Bukkit.getOnlinePlayers().stream().filter((op) -> {
               return op.getGameMode() != GameMode.SPECTATOR;
            }).forEach((op) -> {
               Firework f = (Firework)op.getWorld().spawnEntity(op.getLocation(), EntityType.FIREWORK);
               f.detonate();
               FireworkMeta fM = f.getFireworkMeta();
               FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(Color.YELLOW).withFade(Color.ORANGE).with(Type.STAR).trail(true).build();
               fM.setPower(2);
               fM.addEffect(effect);
               f.setFireworkMeta(fM);
            });
            ++time;
         }

      }, 0L, 10L);
   }
}