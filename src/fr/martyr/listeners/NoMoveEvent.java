package fr.martyr.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.martyr.utils.Scatter;
import fr.martyr.utils.State;

public class NoMoveEvent implements Listener {
   public final Listener listener = new Listener() {
      @EventHandler
      public void onMove(PlayerMoveEvent event) {
         if (State.isState(State.TP) && Scatter.stayLocs.containsKey(event.getPlayer().getUniqueId()) && event.getTo().distanceSquared((Location)Scatter.stayLocs.get(event.getPlayer().getUniqueId())) > 40.0D) {
            event.getPlayer().teleport((Location)Scatter.stayLocs.get(event.getPlayer().getUniqueId()));
         } else {
            if (State.isState(State.GAME)) {
               event.getHandlers().unregister(NoMoveEvent.this.listener);
            }

         }
      }
   };
}