package fr.martyr.commands;

import java.util.IllegalFormatException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.martyr.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandSetTimer implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		
		if(args.length == 0) {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "/settimer <VALEUR>");
			return true;
		}
		
		try {
			Main.getInstance().game.setTimer(Integer.parseInt(args[0]));
			sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Valeur définie!");
		}catch(IllegalFormatException ex) {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Po un nombre!");
		}
		
		return true;
	}
}
