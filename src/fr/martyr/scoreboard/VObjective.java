package fr.martyr.scoreboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_16_R3.ScoreboardServer;

public class VObjective {
   protected String name;
   protected String displayName;
   protected EnumScoreboardHealthDisplay format;
   protected VObjective.ObjectiveLocation location;
   protected List<OfflinePlayer> receivers;
   protected ConcurrentLinkedQueue<VObjective.VScore> scores;

   public VObjective(String name, String displayName) {
      this.format = EnumScoreboardHealthDisplay.INTEGER;
      this.location = VObjective.ObjectiveLocation.SIDEBAR;
      this.receivers = new ArrayList();
      this.scores = new ConcurrentLinkedQueue();
      this.name = name;
      this.displayName = displayName;
   }

   public boolean addReceiver(OfflinePlayer offlinePlayer) {
      if (!offlinePlayer.isOnline()) {
         return false;
      } else {
         this.receivers.add(offlinePlayer);
         Player p = offlinePlayer.getPlayer();
         this.init(p);
         this.updateScore(p);
         return true;
      }
   }

   public void init(Player receiver) {
      this.create(receiver);
      this.displayTo(receiver, this.location.getLocation());
   }

   protected void updateScore(Player p) {
      VObjective.RawObjective.updateScoreObjective(p, this, false);
   }

   protected void create(Player receiver) {
      VObjective.RawObjective.createObjective(receiver, this);
   }

   protected void displayTo(Player receiver, int location) {
      VObjective.RawObjective.displayObjective(receiver, this.getName(), location);
   }

   public String getName() {
      return this.name;
   }

   public void removeReceiver(OfflinePlayer offlinePlayer) {
      this.receivers.remove(offlinePlayer);
      if (offlinePlayer.isOnline()) {
         this.remove(offlinePlayer.getPlayer());
      }

   }

   protected void remove(Player receiver) {
      VObjective.RawObjective.removeObjective(receiver, this);
   }

   public void updateScore(String score) {
      this.updateScore(this.getScore(score));
   }

   protected void updateScore(VObjective.VScore score) {
      this.receivers.stream().filter(OfflinePlayer::isOnline).forEach((op) -> {
         VObjective.RawObjective.updateScoreObjective(op.getPlayer(), this, score);
      });
   }

   public VObjective.VScore getScore(String player) {
      Iterator var3 = this.scores.iterator();

      VObjective.VScore score;
      while(var3.hasNext()) {
         score = (VObjective.VScore)var3.next();
         if (score.getPlayerName().equals(player)) {
            return score;
         }
      }

      score = new VObjective.VScore(player, 0);
      this.scores.add(score);
      return score;
   }

   public VObjective.ObjectiveLocation getLocation() {
      return this.location;
   }

   public void setLocation(VObjective.ObjectiveLocation location) {
      this.location = location;
   }

   protected void updateScore(Player p, boolean inverse) {
      VObjective.RawObjective.updateScoreObjective(p, this, inverse);
   }

   public void updateScore(boolean forceRefresh) {
      if (forceRefresh) {
         String old = this.toggleName();
         this.receivers.stream().filter(OfflinePlayer::isOnline).forEach((op) -> {
            this.create(op.getPlayer());
            VObjective.RawObjective.updateScoreObjective(op.getPlayer(), this, false);
            this.displayTo(op.getPlayer(), this.location.getLocation());
            VObjective.RawObjective.removeObjective(op.getPlayer(), old);
         });
      } else {
         this.receivers.stream().filter(OfflinePlayer::isOnline).forEach((op) -> {
            VObjective.RawObjective.updateScoreObjective(op.getPlayer(), this, false);
         });
      }

   }

   protected String toggleName() {
      String old = this.name;
      if (this.name.endsWith("1")) {
         this.name = this.name.substring(0, this.name.length() - 1);
      } else {
         this.name = this.name + "1";
      }

      return old;
   }

   protected void update() {
      this.receivers.stream().filter(OfflinePlayer::isOnline).forEach((op) -> {
         VObjective.RawObjective.updateObjective(op.getPlayer(), this);
      });
   }

   public void removeScore(String score) {
      VObjective.VScore score1 = this.getScore(score);
      this.removeScore(score1);
   }

   public void removeScore(VObjective.VScore score) {
      this.scores.remove(score);
      this.receivers.stream().filter(OfflinePlayer::isOnline).forEach((op) -> {
         VObjective.RawObjective.removeScoreObjective(op.getPlayer(), this, score);
      });
   }

   public void clearScores() {
      this.scores.clear();
   }

   public ConcurrentLinkedQueue<VObjective.VScore> getScores() {
      return this.scores;
   }

   public boolean containsScore(String player) {
      Iterator var3 = this.scores.iterator();

      while(var3.hasNext()) {
         VObjective.VScore score = (VObjective.VScore)var3.next();
         if (score.getPlayerName().equals(player)) {
            return true;
         }
      }

      return false;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public Object getFormat() {
      return this.format;
   }

   public static enum ObjectiveLocation {
      LIST(0),
      SIDEBAR(1),
      BELOWNAME(2);

      private final int location;

      private ObjectiveLocation(int location) {
         this.location = location;
      }

      public int getLocation() {
         return this.location;
      }
   }

   public static class RawObjective {
      public static void createObjective(Player p, VObjective objective) {
         Reflection.sendPacket(p, makeScoreboardObjectivePacket(0, objective.getName(), objective.getDisplayName(), objective.getFormat()));
      }

      public static PacketPlayOutScoreboardObjective makeScoreboardObjectivePacket(int action, String objectiveName, String objectiveDisplayName, Object format) {
         try {
            PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
            Reflection.setValue(packet, "a", objectiveName);
            Reflection.setValue(packet, "b", ChatSerializer.a("{\"text\":\"" + objectiveDisplayName + "\"}"));
            Reflection.setValue(packet, "c", format);
            Reflection.setValue(packet, "d", action);
            return packet;
         } catch (ReflectiveOperationException var5) {
            var5.printStackTrace();
            return null;
         }
      }

      public static void updateObjective(Player p, VObjective objective) {
         Reflection.sendPacket(p, makeScoreboardObjectivePacket(2, objective.getName(), objective.getDisplayName(), objective.getFormat()));
      }

      public static void removeObjective(Player p, VObjective objective) {
         Reflection.sendPacket(p, makeScoreboardObjectivePacket(1, objective.getName(), objective.getDisplayName(), objective.getFormat()));
      }

      public static void removeObjective(Player p, String name) {
         Reflection.sendPacket(p, makeScoreboardObjectivePacket(1, name, "", EnumScoreboardHealthDisplay.INTEGER));
      }

      public static void displayObjective(Player p, String name, int location) {
         Reflection.sendPacket(p, makeScoreboardDisplayPacket(name, location));
      }

      public static PacketPlayOutScoreboardDisplayObjective makeScoreboardDisplayPacket(String objectiveName, int location) {
         try {
            PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
            Reflection.setValue(packet, "a", location);
            Reflection.setValue(packet, "b", objectiveName);
            return packet;
         } catch (ReflectiveOperationException var3) {
            var3.printStackTrace();
            return null;
         }
      }

      public static void createScoreObjective(Player p, VObjective objective) {
         updateScoreObjective(p, objective, false);
      }

      public static void updateScoreObjective(Player p, VObjective objective, boolean inverse) {
         VObjective.VScore score;
         Iterator var4;
         if (!inverse) {
            var4 = objective.getScores().iterator();

            while(var4.hasNext()) {
               score = (VObjective.VScore)var4.next();
               updateScoreObjective(p, objective, score);
            }

         } else {
            var4 = objective.getScores().iterator();

            while(var4.hasNext()) {
               score = (VObjective.VScore)var4.next();
               updateScoreObjective(p, objective, score, objective.getScores().size() - score.getScore() - 1);
            }

         }
      }

      public static void updateScoreObjective(Player p, VObjective objective, VObjective.VScore score) {
         Reflection.sendPacket(p, makeScoreboardScorePacket(objective.getName(), ScoreboardServer.Action.CHANGE, score.getPlayerName(), score.getScore()));
      }

      public static void updateScoreObjective(Player p, VObjective objective, VObjective.VScore score, int scoreValue) {
         Reflection.sendPacket(p, makeScoreboardScorePacket(objective.getName(), ScoreboardServer.Action.CHANGE, score.getPlayerName(), scoreValue));
      }

      public static PacketPlayOutScoreboardScore makeScoreboardScorePacket(String objectiveName, Object action, String scoreName, int scoreValue) {
         if (objectiveName == null) {
            objectiveName = "";
         }

         try {
            PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
            Reflection.setValue(packet, "a", scoreName);
            Reflection.setValue(packet, "b", objectiveName);
            Reflection.setValue(packet, "c", scoreValue);
            Reflection.setValue(packet, "d", action);
            return packet;
         } catch (ReflectiveOperationException var5) {
            var5.printStackTrace();
            return null;
         }
      }

      public static void createScoreObjective(Player p, VObjective objective, VObjective.VScore score) {
         updateScoreObjective(p, objective, score);
      }

      public static void removeScoreObjective(Player p, VObjective objective) {
         Iterator var3 = objective.getScores().iterator();

         while(var3.hasNext()) {
            VObjective.VScore score = (VObjective.VScore)var3.next();
            removeScoreObjective(p, objective, score);
         }

      }

      public static void removeScoreObjective(Player p, VObjective objective, VObjective.VScore score) {
         Reflection.sendPacket(p, makeScoreboardScorePacket(objective.getName(), ScoreboardServer.Action.REMOVE, score.getPlayerName(), 0));
      }
   }

   public class VScore {
      private final String playerName;
      private int score;

      public VScore(String player, int score) {
         this.playerName = player;
         this.score = score;
      }

      public void removeScore(int score) {
         this.setScore(this.getScore() - score);
      }

      public int getScore() {
         return this.score;
      }

      public void setScore(int score) {
         this.score = score;
      }

      public void incrementScore() {
         this.setScore(this.getScore() + 1);
      }

      public void addScore(int score) {
         this.setScore(this.getScore() + score);
      }

      public String getPlayerName() {
         return this.playerName;
      }
   }
}