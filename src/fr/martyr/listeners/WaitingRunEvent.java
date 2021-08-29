package fr.martyr.listeners;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import fr.martyr.AnimalPet;
import fr.martyr.Main;
import fr.martyr.PlayerPet;
import fr.martyr.utils.State;
import net.md_5.bungee.api.ChatColor;

public class WaitingRunEvent implements Listener{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(!State.isState(State.WAITING)) return;
		
		final Player player = e.getPlayer();
		  
		if(e.hasItem() && e.getMaterial() == Material.BOOK) {
			player.openInventory(Main.getInstance().getPetInventory());
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		if(!State.isState(State.WAITING)) return;
		
		e.setCancelled(true);
		  
		final Player player = (Player) e.getWhoClicked();
		final InventoryView invView = e.getView();
		final ItemStack stack = e.getCurrentItem();
		  
		if(invView.getTitle().equals(YELLOW + "" + BOLD + "Selecteur de Pets") && stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			final AnimalPet pet = AnimalPet.fromItemName(stack.getItemMeta().getDisplayName());
			  
			if(pet != null) {
				player.sendMessage(ChatColor.YELLOW + "Vous avez choisi le pet: " + pet.getItemName());
				Main.getInstance().pets.put(player.getUniqueId(), new PlayerPet(player.getUniqueId(), pet));
			}
			  
			return;
		}
	}	  
}
