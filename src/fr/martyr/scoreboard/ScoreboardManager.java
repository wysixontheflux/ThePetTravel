package fr.martyr.scoreboard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.martyr.Main;

public class ScoreboardManager {
   private final Map<UUID, PersonalScoreboard> scoreboards = new HashMap();
   private final ScheduledFuture glowingTask;
   private final ScheduledFuture reloadingTask;
   private int ipCharIndex = 0;
   private int cooldown = 0;

   public ScoreboardManager() {
      this.glowingTask = Main.getInstance().getScheduledExecutorService().scheduleAtFixedRate(() -> {
         String ip = this.colorIpAt();
         Iterator var3 = this.scoreboards.values().iterator();

         while(var3.hasNext()) {
            PersonalScoreboard scoreboard = (PersonalScoreboard)var3.next();
            Main.getInstance().getExecutorMonoThread().execute(() -> {
               scoreboard.setLines(ip);
            });
         }

      }, 80L, 80L, TimeUnit.MILLISECONDS);
      this.reloadingTask = Main.getInstance().getScheduledExecutorService().scheduleAtFixedRate(() -> {
         Iterator var2 = this.scoreboards.values().iterator();

         while(var2.hasNext()) {
            PersonalScoreboard scoreboard = (PersonalScoreboard)var2.next();
            Main.getInstance().getExecutorMonoThread().execute(scoreboard::reloadData);
         }

      }, 1L, 1L, TimeUnit.SECONDS);
   }

   private String colorIpAt() {
      String ip = "lifecraftv2.fr";
      if (this.cooldown > 0) {
         --this.cooldown;
         return ChatColor.YELLOW + ip;
      } else {
         StringBuilder formattedIp = new StringBuilder();
         if (this.ipCharIndex > 0) {
            formattedIp.append(ip.substring(0, this.ipCharIndex - 1));
            formattedIp.append(ChatColor.GOLD).append(ip.substring(this.ipCharIndex - 1, this.ipCharIndex));
         } else {
            formattedIp.append(ip.substring(0, this.ipCharIndex));
         }

         formattedIp.append(ChatColor.RED).append(ip.charAt(this.ipCharIndex));
         if (this.ipCharIndex + 1 < ip.length()) {
            formattedIp.append(ChatColor.GOLD).append(ip.charAt(this.ipCharIndex + 1));
            if (this.ipCharIndex + 2 < ip.length()) {
               formattedIp.append(ChatColor.YELLOW).append(ip.substring(this.ipCharIndex + 2));
            }

            ++this.ipCharIndex;
         } else {
            this.ipCharIndex = 0;
            this.cooldown = 50;
         }

         return ChatColor.YELLOW + formattedIp.toString();
      }
   }

   public void onDisable() {
      this.scoreboards.values().forEach(PersonalScoreboard::onLogout);
   }

   public void onLogin(Player player) {
      if (!this.scoreboards.containsKey(player.getUniqueId())) {
         this.scoreboards.put(player.getUniqueId(), new PersonalScoreboard(player));
      }
   }

   public void onLogout(Player player) {
      if (this.scoreboards.containsKey(player.getUniqueId())) {
         ((PersonalScoreboard)this.scoreboards.get(player.getUniqueId())).onLogout();
         this.scoreboards.remove(player.getUniqueId());
      }

   }

   public void update(Player player) {
      if (this.scoreboards.containsKey(player.getUniqueId())) {
         ((PersonalScoreboard)this.scoreboards.get(player.getUniqueId())).reloadData();
      }

   }
}