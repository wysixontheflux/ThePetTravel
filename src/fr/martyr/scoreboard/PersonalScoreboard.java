package fr.martyr.scoreboard;

import java.util.UUID;

import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.martyr.Main;
import fr.martyr.PlayerPet;
import fr.martyr.utils.State;
import fr.martyr.world.WorldManager;

public class PersonalScoreboard {
   private final UUID uuid;
   private final ObjectiveSign objectiveSign;
   private Player p;
   private Main main = Main.getInstance();

   PersonalScoreboard(Player player) {
      this.p = player;
      this.uuid = player.getUniqueId();
      this.objectiveSign = new ObjectiveSign("sidebar", "Wait");
      this.reloadData();
      this.objectiveSign.addReceiver(player);
   }

   public void reloadData() {
   }

   public void setLines(String ip) {
      this.objectiveSign.setDisplayName("§6UHCThePetTravel");
      String time = this.secondsToString(Main.getInstance().getGame().getTimer());
      WorldManager.updateHealth(this.p);
      
      if (State.isState(State.WAITING)) {
         this.objectiveSign.setLine(0, "§7");
         this.objectiveSign.setLine(1, " §c» §7Joueurs: §e" + Bukkit.getOnlinePlayers().size() + "/" + this.main.getGame().getSlot());
         this.objectiveSign.setLine(2, " §c» §7Démarrage: §b" + this.main.getGame().getTimer() + "s");
         this.objectiveSign.setLine(3, "§6");
         this.objectiveSign.updateLines();
      } else if (!State.isState(State.GAME) && !State.isState(State.TP)) {
         if (State.isState(State.FINISH)) {
            this.objectiveSign.setLine(0, "§7" + DateFormatUtils.format(System.currentTimeMillis(), "dd/MM/yyyy"));
            this.objectiveSign.setLine(1, "§5");
            this.objectiveSign.setLine(2, "§7Gagnant:");
            this.objectiveSign.setLine(3, "§f» §6" + Bukkit.getOfflinePlayer((UUID)this.main.getGame().getAlivePlayers().get(0)).getName());
            this.objectiveSign.setLine(4, "§8");
            this.objectiveSign.setLine(5, "§7Kills: §e" + WorldManager.getKills(this.p));
            this.objectiveSign.setLine(6, "§7Durée: §e" + time + "s");
            this.objectiveSign.setLine(7, "§3");
            this.objectiveSign.updateLines();
         }
      } else {
    	  final PlayerPet pet = Main.getInstance().pets.get(this.p.getUniqueId());
    	  
         this.objectiveSign.setLine(0, "§7§m+--------------+");
         this.objectiveSign.setLine(1, " §7» §eJoueurs: §a" + this.main.getGame().getAlivePlayers().size() + "/" + this.main.getGame().getSlot());
         this.objectiveSign.setLine(2, " §7» §eBordure: §b" + (int)Bukkit.getWorld("world").getWorldBorder().getSize() / 2);
         this.objectiveSign.setLine(3, " §7» §eKills: §b" + WorldManager.getKills(this.p));
         this.objectiveSign.setLine(4, "§6§9§7§m+--------------+");
         this.objectiveSign.setLine(5, " §7» §eDurée: §b" + time);
         this.objectiveSign.setLine(6, " §7» §ePVP: §b" + (this.main.getGame().isTP() ? "✔" : this.secondsToString(Main.getInstance().getGame().getPvPTime() * 60 - Main.getInstance().getGame().getTimer())));
         
         if(pet != null && pet.entity != null) {
	         final StringBuilder beforeLife = new StringBuilder(ChatColor.GREEN.toString());
	         final StringBuilder afterLife = new StringBuilder(ChatColor.RED.toString());
	         
	         final long life = Math.round(((LivingEntity) pet.entity).getHealth() / 4);
	         final long remlife = 10 - life;
	         
	         for(int io = 0; io < life; io++) beforeLife.append("-");
	         for(int io = 0; io < remlife; io++) afterLife.append("-");
	         
	         this.objectiveSign.setLine(7, " §7» §ePet: " + beforeLife.toString() + afterLife.toString());
         }
         
         this.objectiveSign.setLine((pet != null && pet.entity != null) ? 8 : 7, "§9§7§m+--------------+");
         this.objectiveSign.updateLines();
      }

   }

   private String secondsToString(int pTime) {
      return String.format("%02d:%02d", pTime / 60, pTime % 60);
   }

   public void onLogout() {
      this.objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(this.uuid));
   }
}