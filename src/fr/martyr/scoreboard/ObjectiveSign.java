package fr.martyr.scoreboard;

import java.util.HashMap;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.martyr.scoreboard.VObjective.RawObjective;
import fr.martyr.scoreboard.VObjective.VScore;

public class ObjectiveSign extends VObjective {
   public HashMap<Integer, String> lines = new HashMap();

   public ObjectiveSign(String name, String displayName) {
      super(name, displayName);
      this.lines = new HashMap();

      for(int i = 0; i < 19; ++i) {
         this.lines.put(i, null);
      }

   }

   public boolean addReceiver(OfflinePlayer offlinePlayer) {
      if (!offlinePlayer.isOnline()) {
         return false;
      } else {
         this.receivers.add(offlinePlayer);
         Player p = offlinePlayer.getPlayer();
         this.init(p);
         this.updateScore(p, true);
         return true;
      }
   }

   public void setLine(int nb, String line) {
      VScore remove = this.getScore((String)this.lines.get(nb));
      this.scores.remove(remove);
      VScore add = this.getScore(line);
      add.setScore(nb);
      this.lines.put(nb, line);
   }

   public void updateLines() {
      this.updateLines(true);
   }

   public void updateLines(boolean inverse) {
      String old = this.toggleName();
      this.receivers.stream().filter(OfflinePlayer::isOnline).forEach((op) -> {
         this.create(op.getPlayer());
         this.updateScore(op.getPlayer(), inverse);
         this.displayTo(op.getPlayer(), this.location.getLocation());
         RawObjective.removeObjective(op.getPlayer(), old);
      });
   }

   private void replaceScore(VScore remove, VScore add) {
      this.scores.remove(remove);
      this.receivers.stream().filter(OfflinePlayer::isOnline).forEach((op) -> {
         RawObjective.updateScoreObjective(op.getPlayer(), this, add);
         RawObjective.removeScoreObjective(op.getPlayer(), this, remove);
      });
   }
}