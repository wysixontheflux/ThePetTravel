package fr.martyr.commands;

import fr.martyr.Main;
import fr.martyr.game.PreGameManager;
import fr.martyr.game.WinManager;
import fr.martyr.utils.ActionBar;
import fr.martyr.utils.State;
import fr.martyr.world.WorldManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
   private Main main = Main.getInstance();
   ActionBar actionBar = new ActionBar("");

   public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
      if (sender instanceof Player && cmd.getName().equalsIgnoreCase("game") && args.length >= 1) {
    	  if (args[0].equals("checkwin")) {
              sender.sendMessage("§dLancement du test . . .");
              WinManager.checkWin();
              return true;
           }

           if (args[0].equals("start")) {
              if (State.isState(State.WAITING)) {
                 if (!this.main.getGame().isForcestart()) {
                    this.main.getGame().setForcestart(true);
                    sender.sendMessage("§dUHCThePetTravel §8» §aVous avez activé le force start.");
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage("§dUHCThePetTravel §8» §aThe Pet Travel, un concept de la Salad'Corp ! Protégez votre PET au péril de votre vie!");
                    Bukkit.broadcastMessage(" ");
                    Bukkit.getOnlinePlayers().forEach((all) -> {
                       all.playSound(all.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 4.0F);
                    });
                    new PreGameManager();
                 } else {
                    sender.sendMessage("§cErreur: Le redémarrage a déjà été forcé.");
                 }
              } else {
                 sender.sendMessage("§cErreur: La partie a déjà commencée");
              }
           }
      }

      return false;
   }
}