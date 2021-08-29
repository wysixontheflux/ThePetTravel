package fr.martyr.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.martyr.Main;
import fr.martyr.PlayerPet;
import fr.martyr.utils.State;

public class CommandPetLocate implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player) || State.isState(State.WAITING)) return true;
		
		final Player player = (Player) sender;
		final PlayerPet pet = Main.getInstance().pets.get(player.getUniqueId());
		
		if(pet == null) return true;
		
		final Location loc = pet.entity.getLocation();
		player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Votre pet se situe en " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
		
		return true;
	}
}
