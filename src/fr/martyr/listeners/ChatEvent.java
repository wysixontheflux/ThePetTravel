package fr.martyr.listeners;

import fr.martyr.utils.State;
import fr.martyr.world.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {
   @EventHandler
   public void onChat(AsyncPlayerChatEvent e) {
      Player p = e.getPlayer();
      if (!State.isState(State.GAME)) {
    	  e.setFormat("§7%1$s§7: §7%2$s");
      } else {
         String message = e.getMessage();
         if (p.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
            Bukkit.getOnlinePlayers().stream().filter((spec) -> {
               return spec.getGameMode() == GameMode.SPECTATOR;
            }).forEach((spec) -> {
               spec.sendMessage("§f[Spec] §7" + p.getDisplayName() + " §f» §7" + message);
            });
         } else {
            e.setFormat("§e" + "%1$s" + "§7: §f" + "%2$s");
         }
      }

   }
}