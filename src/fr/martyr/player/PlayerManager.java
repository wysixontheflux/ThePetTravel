package fr.martyr.player;

import static org.bukkit.ChatColor.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.martyr.Main;
import fr.martyr.utils.ItemBuilder;

public class PlayerManager {
   private Player p;

   public PlayerManager(Player player) {
      this.p = player;
   }

   public void kickPlayer() {
      if(p.isOnline()) {
    	  p.kickPlayer("Partie terminée!");
      }
   }

   public void setJoinInventory() {
      this.setPlayInventory();
      this.p.getInventory().setItem(4, new ItemBuilder(Material.BOOK).setName(YELLOW + "" + BOLD + "Selecteur de Pets").toItemStack());
      this.p.teleport(Main.getInstance().getGame().getSpawn());
      this.p.setGameMode(GameMode.ADVENTURE);
   }

   public void setPlayInventory() {
      this.p.getInventory().clear();
      this.p.getInventory().setArmorContents((ItemStack[])null);
      this.p.getActivePotionEffects().clear();
      this.p.setMaxHealth(20.0D);
      this.p.setHealth(20.0D);
      this.p.setFoodLevel(20);
      this.p.setExp(0.0F);
      this.p.setLevel(0);
      this.p.setFireTicks(0);
   }

   public void setSpec() {
      this.p.closeInventory();
      this.p.setGameMode(GameMode.SPECTATOR);
   }
}