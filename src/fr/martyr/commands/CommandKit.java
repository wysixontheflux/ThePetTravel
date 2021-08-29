package fr.martyr.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.martyr.Main;

public class CommandKit implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		
		((Player) sender).getInventory().addItem(Main.getInstance().pets.get(((Player) sender).getUniqueId()).getPet().getStacks());
		
		return true;
	}
}
