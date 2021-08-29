package fr.martyr.scoreboard;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;

public class ScoreboardTeam {
   private String name;
   private String prefix;

   public ScoreboardTeam(String name, String prefix) {
      this.name = name;
      this.prefix = prefix;
   }

   public static PacketPlayOutPlayerInfo updateDisplayName(EntityPlayer player) {
      PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, new EntityPlayer[]{player});
      player.playerConnection.sendPacket(packet);
      return packet;
   }

   public PacketPlayOutScoreboardTeam createTeam() {
      return this.createPacket(0);
   }

   private PacketPlayOutScoreboardTeam createPacket(int mode) {
      PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
      this.setField(packet, "a", this.name);
      this.setField(packet, "h", mode);
      this.setField(packet, "b", "");
      this.setField(packet, "c", this.prefix);
      this.setField(packet, "d", "");
      this.setField(packet, "i", 0);
      this.setField(packet, "e", "always");
      this.setField(packet, "f", 0);
      return packet;
   }

   private void setField(Object edit, String fieldName, Object value) {
      try {
         Field field = edit.getClass().getDeclaredField(fieldName);
         field.setAccessible(true);
         field.set(edit, value);
      } catch (IllegalAccessException | NoSuchFieldException var5) {
         var5.printStackTrace();
      }

   }

   public PacketPlayOutScoreboardTeam updateTeam() {
      return this.createPacket(2);
   }

   public PacketPlayOutScoreboardTeam removeTeam() {
      PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
      this.setField(packet, "a", this.name);
      this.setField(packet, "h", 1);
      return packet;
   }

   public PacketPlayOutScoreboardTeam setFriendlyFire(boolean v) {
      PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
      this.setField(packet, "i", v ? 1 : 0);
      return packet;
   }

   public PacketPlayOutScoreboardTeam addOrRemovePlayer(int mode, String playerName) {
      PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
      this.setField(packet, "a", this.name);
      this.setField(packet, "h", mode);

      try {
         Field f = packet.getClass().getDeclaredField("g");
         f.setAccessible(true);
         ((List)f.get(packet)).add(playerName);
      } catch (IllegalAccessException | NoSuchFieldException var5) {
         var5.printStackTrace();
      }

      return packet;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}