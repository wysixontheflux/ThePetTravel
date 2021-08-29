package fr.martyr.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAlliance implements CommandExecutor{
	// BOTH
	public static HashMap<UUID, UUID> confirmedAlliances = new HashMap<UUID, UUID>();
	
	// RECEIVER => Array<SENDER>
	public static HashMap<UUID, ArrayList<UUID>> waitingAlliances = new HashMap<UUID, ArrayList<UUID>>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		
		final Player player = (Player) sender;
		
		if(args.length != 1) {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "La syntaxe de la commande est '/alliance <PSEUDO>'!");
			return true;
		}
		
		if(player.getName().equals(args[0])) {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Ce pseudo est le vôtre!");
			return true;
		}
		
		final String targetName = args[0];
		final Player targetPlayer = Bukkit.getPlayer(targetName);
		
		if(targetPlayer == null || !targetPlayer.isOnline()) {
			sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Ce pseudo n'est pas dans la partie!");
			return true;
		}
		
		if(confirmedAlliances.containsKey(player.getUniqueId())) {
			final Player allyPlayer = Bukkit.getPlayer(confirmedAlliances.get(player.getUniqueId()));
			
			if(!allyPlayer.getUniqueId().equals(targetPlayer.getUniqueId())) {
				player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Vous devez quitter votre ancienne alliance");
				player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "avec '/alliance " + allyPlayer.getName() + "'!");
			}else {
				confirmedAlliances.remove(player.getUniqueId());
				confirmedAlliances.remove(allyPlayer.getUniqueId());
				
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Vous venez d'annuler votre alliance avec " + allyPlayer.getName() + "!");
				allyPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " vient d'annuler votre alliance avec lui!");
			}
		}else if(confirmedAlliances.containsKey(targetPlayer.getUniqueId())){
			player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Ce joueur possède déjà une alliance avec un joueur!");
		}else if(getAllWaiting(player.getUniqueId()).contains(targetPlayer.getUniqueId())){
			waitingAlliances.getOrDefault(player.getUniqueId(), new ArrayList<UUID>()).clear();
			waitingAlliances.getOrDefault(targetPlayer.getUniqueId(), new ArrayList<UUID>()).clear();
			confirmedAlliances.put(player.getUniqueId(), targetPlayer.getUniqueId());
			confirmedAlliances.put(targetPlayer.getUniqueId(), player.getUniqueId());
			
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Vous obtenez une alliance avec " + targetPlayer.getName() + "!");
			targetPlayer.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Vous obtenez une alliance avec " + player.getName() + "!");
		}else {
			sendAllyRequest(player.getUniqueId(), targetPlayer.getUniqueId());
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Demande d'alliance envoyée à " + targetPlayer.getName() + "!");
			targetPlayer.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Demande d'alliance reçue de " + player.getName() + "!");
		}
		
		return true;
	}
	
	public void sendAllyRequest(UUID sender, UUID receiver) {		
		if(waitingAlliances.containsKey(receiver)) {
			waitingAlliances.get(receiver).add(sender);
		}else {
			final ArrayList<UUID> array = new ArrayList<UUID>();
			array.add(sender);
			waitingAlliances.put(receiver, array);
		}
	}
	
	public ArrayList<UUID> getAllWaiting(UUID player){		
		if(waitingAlliances.containsKey(player)) {
			return waitingAlliances.get(player);
		}
		
		return new ArrayList<UUID>();
	}
}
