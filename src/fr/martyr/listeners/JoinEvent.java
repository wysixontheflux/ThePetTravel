package fr.martyr.listeners;

import fr.martyr.Main;
import fr.martyr.game.PreGameManager;
import fr.martyr.player.PlayerManager;
import fr.martyr.utils.ActionBar;
import fr.martyr.utils.State;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.scoreboard.Team;

public class JoinEvent implements Listener {
   private Main main = Main.getInstance();
   private static List<UUID> decoPlayers = new ArrayList();

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      Player p = e.getPlayer();
      e.setJoinMessage((String)null);

      this.main.getScoreboardManager().onLogin(p);
      Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("91");
      team.addEntry(p.getName());
      World world = this.main.getGame().getWorld();
      PlayerManager playerManager;
      
      if (State.isState(State.TP) || State.isState(State.GAME)) {
         if (decoPlayers.contains(p.getUniqueId())) {
            (new ActionBar("§dUHCThePetTravel §8» §2" + p.getName() + " §7est revenu dans la partie.")).sendToAll();
            //Main.getInstance().getGame().getAlivePlayers().add(p.getUniqueId());
            decoPlayers.remove(p.getUniqueId());
            Main.getInstance().pets.get(p.getUniqueId()).entity.teleport(p);
            return;
         }

         playerManager = new PlayerManager(p);
         playerManager.setPlayInventory();
         p.teleport(new Location(world, 0.0D, (double)world.getHighestBlockYAt(0, 0), 0.0D));
         playerManager.setSpec();
      } else if (!State.isState(State.WAITING)) {
         if (this.main.getGame().isTP() && decoPlayers.contains(p.getUniqueId()) && !State.isState(State.FINISH)) {
            decoPlayers.remove(p.getUniqueId());
            p.setGameMode(GameMode.SURVIVAL);
            (new ActionBar("§dUHCThePetTravel §8» §b" + p.getName() + " §7est revenu dans la partie.")).sendToAll();
            return;
         }

         playerManager = new PlayerManager(p);
         playerManager.setPlayInventory();
         p.teleport(new Location(world, 0.0D, (double)world.getHighestBlockYAt(0, 0), 0.0D));
         playerManager.setSpec();
      } else if (State.isState(State.WAITING)) {
         (new PlayerManager(p)).setJoinInventory();
         (new ActionBar("§a+ " + p.getName() + " §ba rejoint. §6(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")")).sendToAll();
         Main.getInstance().getGame().getAlivePlayers().add(p.getUniqueId());
         if (Bukkit.getOnlinePlayers().size() >= this.main.getAutostart() && !Main.getInstance().getGame().getStart()) {
            new PreGameManager();
            return;
         }
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      Player player = e.getPlayer();
      e.setQuitMessage((String)null);
      Main.getInstance().getScoreboardManager().onLogout(player);

      if (State.isState(State.WAITING)) {
         Main.getInstance().getGame().getAlivePlayers().remove(player.getUniqueId());
         (new ActionBar("§c- §e" + player.getName() + " §aa quitté la partie. §6(" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + Main.getInstance().getGame().getSlot() + ")")).sendToAll();
      }

      if ((State.isState(State.TP) || State.isState(State.GAME)) && Main.getInstance().getGame().getAlivePlayers().contains(player.getUniqueId())) {
         if (Main.getInstance().getGame().getTimer() < Main.getInstance().getGame().getBorderTime() * 60) {
            decoPlayers.add(player.getUniqueId());
            (new ActionBar("§aUHCThePetTravel §8» §b" + player.getName() + " §7a quitté la partie.")).sendToAll();
            return;
         }

         player.setHealth(0.0D);
      }
   }

   @EventHandler
   public void onLogin(PlayerLoginEvent e) {
      Player p = e.getPlayer();
      if (State.isState(State.WAITING) && Bukkit.getOnlinePlayers().size() >= Main.getInstance().getGame().getSlot() && !p.isOp()) {
         e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, "§cLe serveur est plein.");
      }

   }
}