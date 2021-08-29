package fr.martyr.listeners;

import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class StackEvent implements Listener {
   private int radius;

   public StackEvent(int radius) {
      this.radius = radius;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onItemSpawn(ItemSpawnEvent event) {
      if (event.getEntityType() == EntityType.DROPPED_ITEM) {
         Item newEntity = event.getEntity();
         int maxSize = newEntity.getItemStack().getMaxStackSize();
         List<Entity> entityList = newEntity.getNearbyEntities((double)this.radius, 1.0D, (double)this.radius);
         Iterator var6 = entityList.iterator();

         while(var6.hasNext()) {
            Entity anEntityList = (Entity)var6.next();
            if (anEntityList instanceof Item) {
               Item curEntity = (Item)anEntityList;
               if (!curEntity.isDead() && curEntity.getItemStack().getType().equals(newEntity.getItemStack().getType()) && curEntity.getItemStack().getData().getData() == newEntity.getItemStack().getData().getData() && curEntity.getItemStack().getDurability() == newEntity.getItemStack().getDurability() && Math.abs(curEntity.getLocation().getX() - newEntity.getLocation().getX()) <= (double)this.radius && Math.abs(curEntity.getLocation().getY() - newEntity.getLocation().getY()) <= (double)this.radius && Math.abs(curEntity.getLocation().getZ() - newEntity.getLocation().getZ()) <= (double)this.radius) {
                  int newAmount = newEntity.getItemStack().getAmount();
                  int curAmount = curEntity.getItemStack().getAmount();
                  int more = Math.min(newAmount, maxSize - curAmount);
                  curAmount += more;
                  newAmount -= more;
                  curEntity.getItemStack().setAmount(curAmount);
                  newEntity.getItemStack().setAmount(newAmount);
                  if (newAmount <= 0) {
                     event.setCancelled(true);
                  }

                  return;
               }
            }
         }

      }
   }
}