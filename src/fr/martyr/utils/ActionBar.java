package fr.martyr.utils;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar {
   private String text;

   public ActionBar(String text) {
      this.text = text;
   }

   public void sendToPlayer(Player p) {
	   p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
   }

   public void sendToAll() {
      Iterator var2 = Bukkit.getServer().getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         ((Player)var2.next()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
      }
   }
}