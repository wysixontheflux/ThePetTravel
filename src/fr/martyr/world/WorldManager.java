package fr.martyr.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.martyr.Main;

public class WorldManager {
   public static World WORLD = Bukkit.getWorld("world");
   public static Location SPAWN;
   private static final List<Entity> entityList;

   static {
      SPAWN = new Location(WORLD, 0.5D, 231.0D, 0.5D, 100.0F, 0.0F);
      entityList = new ArrayList<Entity>();
   }

   public static void registerObjectives() {
      onDisable(false);
      ScoreboardManager sbm = Bukkit.getScoreboardManager();
      Scoreboard mainScoreboard = sbm.getMainScoreboard();
      Objective health = mainScoreboard.registerNewObjective("health", "health");
      health.setRenderType(RenderType.HEARTS);
      health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
      Objective healthBellow = mainScoreboard.registerNewObjective("showhealth", "dummy");
      healthBellow.setDisplaySlot(DisplaySlot.BELOW_NAME);
      healthBellow.setDisplayName("%");
      mainScoreboard.registerNewObjective("pkills", "playerKillCount");
   }

   public static void onDisable(boolean resetWhitelist) {
      if (resetWhitelist) {
         Main.getInstance().getServer().getWhitelistedPlayers().forEach((op) -> {
            op.setWhitelisted(false);
         });
      }

      if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health") != null) {
         Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health").unregister();
      }

      if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("pkills") != null) {
         Bukkit.getScoreboardManager().getMainScoreboard().getObjective("pkills").unregister();
      }

      if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("showhealth") != null) {
         Bukkit.getScoreboardManager().getMainScoreboard().getObjective("showhealth").unregister();
      }

   }

   public static Map<String, Integer> getTop10() {
      Scoreboard scoreboard = Main.getInstance().getServer().getScoreboardManager().getMainScoreboard();
      Objective objective = scoreboard.getObjective("pkills");
      Map<String, Integer> stats = new HashMap<String, Integer>();
      scoreboard.getEntries().forEach((playerName) -> {
         stats.put(playerName, objective.getScore(playerName).getScore());
      });
      Map<String, Integer> sortedByCount = (Map<String, Integer>)stats.entrySet().stream().sorted().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> {
         return e1;
      }, LinkedHashMap::new));
      return sortedByCount;
   }

   public static int getKills(Player player) {
      return Main.getInstance().getServer().getScoreboardManager().getMainScoreboard().getObjective("pkills").getScore(player.getName()).getScore();
   }

   public static void updateHealth(Player player) {
      Objective showhealth = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("showhealth");
      if (showhealth != null) {
         double newPHealth = player.getHealth();
         showhealth.getScore(player.getName()).setScore((int)newPHealth * 5);
      }
   }

   public static void spawnArmorStand(Location location, String name) {
      ArmorStand as = (ArmorStand)WORLD.spawnEntity(location, EntityType.ARMOR_STAND);
      as.setVisible(false);
      as.setCustomNameVisible(true);
      as.setCustomName(name);
      as.setGravity(false);
      entityList.add(as);
   }

   public static void clearAllCustomEntities() {
      entityList.forEach(Entity::remove);
      entityList.clear();
   }
}