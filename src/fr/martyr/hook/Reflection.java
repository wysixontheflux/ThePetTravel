package fr.martyr.hook;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public final class Reflection {
   private Reflection() {
   }

   public static String getVersion() {
      String name = Bukkit.getServer().getClass().getPackage().getName();
      return name.substring(name.lastIndexOf(46) + 1);
   }

   public static Class<?> getNMSClass(String className) {
      String fullName = "net.minecraft.server." + getVersion() + "." + className;
      Class clazz = null;

      try {
         clazz = Class.forName(fullName);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return clazz;
   }

   public static Class<?> getOBCClass(String className) {
      String fullName = "org.bukkit.craftbukkit." + getVersion() + "." + className;
      Class clazz = null;

      try {
         clazz = Class.forName(fullName);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return clazz;
   }

   public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
      Class[] primitiveTypes = Reflection.DataType.getPrimitive(parameterTypes);
      Constructor[] var6;
      int var5 = (var6 = clazz.getConstructors()).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         Constructor<?> constructor = var6[var4];
         if (Reflection.DataType.compare(Reflection.DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
            return constructor;
         }
      }

      throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
   }

   public static Object instantiateObject(Class<?> clazz, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
      return getConstructor(clazz, Reflection.DataType.getPrimitive(arguments)).newInstance(arguments);
   }

   public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
      Class[] primitiveTypes = Reflection.DataType.getPrimitive(parameterTypes);
      Method[] var7;
      int var6 = (var7 = clazz.getMethods()).length;

      for(int var5 = 0; var5 < var6; ++var5) {
         Method method = var7[var5];
         if (method.getName().equals(methodName) && Reflection.DataType.compare(Reflection.DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
            return method;
         }
      }

      throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
   }

   public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
      return getMethod(clazz, methodName, Reflection.DataType.getPrimitive(arguments)).invoke(instance, arguments);
   }

   public static Field getField(Class<?> clazz, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException {
      Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
      field.setAccessible(true);
      return field;
   }

   public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
      return getField(clazz, declared, fieldName).get(instance);
   }

   public static Object getValue(Object instance, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
      return getValue(instance, instance.getClass(), declared, fieldName);
   }

   public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
      getField(clazz, declared, fieldName).set(instance, value);
   }

   public static void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
      setValue(instance, instance.getClass(), declared, fieldName, value);
   }

   public static void setValue(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
      setValue(instance, true, fieldName, value);
   }

   public static void setFinalStatic(Field field, Object value) throws ReflectiveOperationException {
      field.setAccessible(true);
      Field mf = Field.class.getDeclaredField("modifiers");
      mf.setAccessible(true);
      mf.setInt(field, field.getModifiers() & -17);
      field.set((Object)null, value);
   }

   public static enum DataType {
      BYTE(Byte.TYPE, Byte.class),
      SHORT(Short.TYPE, Short.class),
      INTEGER(Integer.TYPE, Integer.class),
      LONG(Long.TYPE, Long.class),
      CHARACTER(Character.TYPE, Character.class),
      FLOAT(Float.TYPE, Float.class),
      DOUBLE(Double.TYPE, Double.class),
      BOOLEAN(Boolean.TYPE, Boolean.class);

      private static final Map<Class<?>, Reflection.DataType> CLASS_MAP = new HashMap();
      private final Class<?> primitive;
      private final Class<?> reference;

      static {
         Reflection.DataType[] var3;
         int var2 = (var3 = values()).length;

         for(int var1 = 0; var1 < var2; ++var1) {
            Reflection.DataType type = var3[var1];
            CLASS_MAP.put(type.primitive, type);
            CLASS_MAP.put(type.reference, type);
         }

      }

      private DataType(Class<?> primitive, Class<?> reference) {
         this.primitive = primitive;
         this.reference = reference;
      }

      public static Reflection.DataType fromClass(Class<?> clazz) {
         return (Reflection.DataType)CLASS_MAP.get(clazz);
      }

      public static Class<?> getPrimitive(Class<?> clazz) {
         Reflection.DataType type = fromClass(clazz);
         return type == null ? clazz : type.getPrimitive();
      }

      public static Class<?> getReference(Class<?> clazz) {
         Reflection.DataType type = fromClass(clazz);
         return type == null ? clazz : type.getReference();
      }

      public static Class<?>[] getPrimitive(Class<?>[] classes) {
         int length = classes == null ? 0 : classes.length;
         Class[] types = new Class[length];

         for(int index = 0; index < length; ++index) {
            types[index] = getPrimitive(classes[index]);
         }

         return types;
      }

      public static Class<?>[] getPrimitive(Object[] objects) {
         int length = objects == null ? 0 : objects.length;
         Class[] types = new Class[length];

         for(int index = 0; index < length; ++index) {
            types[index] = getPrimitive(objects[index].getClass());
         }

         return types;
      }

      public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
         if (primary != null && secondary != null && primary.length == secondary.length) {
            for(int index = 0; index < primary.length; ++index) {
               Class<?> primaryClass = primary[index];
               Class<?> secondaryClass = secondary[index];
               if (!primaryClass.equals(secondaryClass) && !primaryClass.isAssignableFrom(secondaryClass)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      public Class<?> getPrimitive() {
         return this.primitive;
      }

      public Class<?> getReference() {
         return this.reference;
      }
   }

   public static enum PackageType {
      MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
      CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion());

      private final String path;

      private PackageType(String path) {
         this.path = path;
      }

      private PackageType(Reflection.PackageType parent, String path) {
         this(parent + "." + path);
      }

      public static String getServerVersion() {
         return Bukkit.getServer().getClass().getPackage().getName().substring(23);
      }

      public Class<?> getClass(String className) throws ClassNotFoundException {
         return Class.forName(this + "." + className);
      }

      public String toString() {
         return this.path;
      }
   }

   public static enum PacketType {
      HANDSHAKING_IN_SET_PROTOCOL("PacketHandshakingInSetProtocol"),
      LOGIN_IN_ENCRYPTION_BEGIN("PacketLoginInEncryptionBegin"),
      LOGIN_IN_START("PacketLoginInStart"),
      LOGIN_OUT_DISCONNECT("PacketLoginOutDisconnect"),
      LOGIN_OUT_ENCRYPTION_BEGIN("PacketLoginOutEncryptionBegin"),
      LOGIN_OUT_SUCCESS("PacketLoginOutSuccess"),
      PLAY_IN_ABILITIES("PacketPlayInAbilities"),
      PLAY_IN_ARM_ANIMATION("PacketPlayInArmAnimation"),
      PLAY_IN_BLOCK_DIG("PacketPlayInBlockDig"),
      PLAY_IN_BLOCK_PLACE("PacketPlayInBlockPlace"),
      PLAY_IN_CHAT("PacketPlayInChat"),
      PLAY_IN_CLIENT_COMMAND("PacketPlayInClientCommand"),
      PLAY_IN_CLOSE_WINDOW("PacketPlayInCloseWindow"),
      PLAY_IN_CUSTOM_PAYLOAD("PacketPlayInCustomPayload"),
      PLAY_IN_ENCHANT_ITEM("PacketPlayInEnchantItem"),
      PLAY_IN_ENTITY_ACTION("PacketPlayInEntityAction"),
      PLAY_IN_FLYING("PacketPlayInFlying"),
      PLAY_IN_HELD_ITEM_SLOT("PacketPlayInHeldItemSlot"),
      PLAY_IN_KEEP_ALIVE("PacketPlayInKeepAlive"),
      PLAY_IN_LOOK("PacketPlayInLook"),
      PLAY_IN_POSITION("PacketPlayInPosition"),
      PLAY_IN_POSITION_LOOK("PacketPlayInPositionLook"),
      PLAY_IN_SET_CREATIVE_SLOT("PacketPlayInSetCreativeSlot "),
      PLAY_IN_SETTINGS("PacketPlayInSettings"),
      PLAY_IN_STEER_VEHICLE("PacketPlayInSteerVehicle"),
      PLAY_IN_TAB_COMPLETE("PacketPlayInTabComplete"),
      PLAY_IN_TRANSACTION("PacketPlayInTransaction"),
      PLAY_IN_UPDATE_SIGN("PacketPlayInUpdateSign"),
      PLAY_IN_USE_ENTITY("PacketPlayInUseEntity"),
      PLAY_IN_WINDOW_CLICK("PacketPlayInWindowClick"),
      PLAY_OUT_ABILITIES("PacketPlayOutAbilities"),
      PLAY_OUT_ANIMATION("PacketPlayOutAnimation"),
      PLAY_OUT_ATTACH_ENTITY("PacketPlayOutAttachEntity"),
      PLAY_OUT_BED("PacketPlayOutBed"),
      PLAY_OUT_BLOCK_ACTION("PacketPlayOutBlockAction"),
      PLAY_OUT_BLOCK_BREAK_ANIMATION("PacketPlayOutBlockBreakAnimation"),
      PLAY_OUT_BLOCK_CHANGE("PacketPlayOutBlockChange"),
      PLAY_OUT_CHAT("PacketPlayOutChat"),
      PLAY_OUT_CLOSE_WINDOW("PacketPlayOutCloseWindow"),
      PLAY_OUT_COLLECT("PacketPlayOutCollect"),
      PLAY_OUT_CRAFT_PROGRESS_BAR("PacketPlayOutCraftProgressBar"),
      PLAY_OUT_CUSTOM_PAYLOAD("PacketPlayOutCustomPayload"),
      PLAY_OUT_ENTITY("PacketPlayOutEntity"),
      PLAY_OUT_ENTITY_DESTROY("PacketPlayOutEntityDestroy"),
      PLAY_OUT_ENTITY_EFFECT("PacketPlayOutEntityEffect"),
      PLAY_OUT_ENTITY_EQUIPMENT("PacketPlayOutEntityEquipment"),
      PLAY_OUT_ENTITY_HEAD_ROTATION("PacketPlayOutEntityHeadRotation"),
      PLAY_OUT_ENTITY_LOOK("PacketPlayOutEntityLook"),
      PLAY_OUT_ENTITY_METADATA("PacketPlayOutEntityMetadata"),
      PLAY_OUT_ENTITY_STATUS("PacketPlayOutEntityStatus"),
      PLAY_OUT_ENTITY_TELEPORT("PacketPlayOutEntityTeleport"),
      PLAY_OUT_ENTITY_VELOCITY("PacketPlayOutEntityVelocity"),
      PLAY_OUT_EXPERIENCE("PacketPlayOutExperience"),
      PLAY_OUT_EXPLOSION("PacketPlayOutExplosion"),
      PLAY_OUT_GAME_STATE_CHANGE("PacketPlayOutGameStateChange"),
      PLAY_OUT_HELD_ITEM_SLOT("PacketPlayOutHeldItemSlot"),
      PLAY_OUT_KEEP_ALIVE("PacketPlayOutKeepAlive"),
      PLAY_OUT_KICK_DISCONNECT("PacketPlayOutKickDisconnect"),
      PLAY_OUT_LOGIN("PacketPlayOutLogin"),
      PLAY_OUT_MAP("PacketPlayOutMap"),
      PLAY_OUT_MAP_CHUNK("PacketPlayOutMapChunk"),
      PLAY_OUT_MAP_CHUNK_BULK("PacketPlayOutMapChunkBulk"),
      PLAY_OUT_MULTI_BLOCK_CHANGE("PacketPlayOutMultiBlockChange"),
      PLAY_OUT_NAMED_ENTITY_SPAWN("PacketPlayOutNamedEntitySpawn"),
      PLAY_OUT_NAMED_SOUND_EFFECT("PacketPlayOutNamedSoundEffect"),
      PLAY_OUT_OPEN_SIGN_EDITOR("PacketPlayOutOpenSignEditor"),
      PLAY_OUT_OPEN_WINDOW("PacketPlayOutOpenWindow"),
      PLAY_OUT_PLAYER_INFO("PacketPlayOutPlayerInfo"),
      PLAY_OUT_POSITION("PacketPlayOutPosition"),
      PLAY_OUT_REL_ENTITY_MOVE("PacketPlayOutRelEntityMove"),
      PLAY_OUT_REL_ENTITY_MOVE_LOOK("PacketPlayOutRelEntityMoveLook"),
      PLAY_OUT_REMOVE_ENTITY_EFFECT("PacketPlayOutRemoveEntityEffect"),
      PLAY_OUT_RESPAWN("PacketPlayOutRespawn"),
      PLAY_OUT_SCOREBOARD_DISPLAY_OBJECTIVE("PacketPlayOutScoreboardDisplayObjective"),
      PLAY_OUT_SCOREBOARD_OBJECTIVE("PacketPlayOutScoreboardObjective"),
      PLAY_OUT_SCOREBOARD_SCORE("PacketPlayOutScoreboardScore"),
      PLAY_OUT_SCOREBOARD_TEAM("PacketPlayOutScoreboardTeam"),
      PLAY_OUT_SET_SLOT("PacketPlayOutSetSlot"),
      PLAY_OUT_SPAWN_ENTITY("PacketPlayOutSpawnEntity"),
      PLAY_OUT_SPAWN_ENTITY_EXPERIENCE_ORB("PacketPlayOutSpawnEntityExperienceOrb"),
      PLAY_OUT_SPAWN_ENTITY_LIVING("PacketPlayOutSpawnEntityLiving"),
      PLAY_OUT_SPAWN_ENTITY_PAINTING("PacketPlayOutSpawnEntityPainting"),
      PLAY_OUT_SPAWN_ENTITY_WEATHER("PacketPlayOutSpawnEntityWeather"),
      PLAY_OUT_SPAWN_POSITION("PacketPlayOutSpawnPosition"),
      PLAY_OUT_STATISTIC("PacketPlayOutStatistic"),
      PLAY_OUT_TAB_COMPLETE("PacketPlayOutTabComplete"),
      PLAY_OUT_TILE_ENTITY_DATA("PacketPlayOutTileEntityData"),
      PLAY_OUT_TRANSACTION("PacketPlayOutTransaction"),
      PLAY_OUT_UPDATE_ATTRIBUTES("PacketPlayOutUpdateAttributes"),
      PLAY_OUT_UPDATE_HEALTH("PacketPlayOutUpdateHealth"),
      PLAY_OUT_UPDATE_SIGN("PacketPlayOutUpdateSign"),
      PLAY_OUT_UPDATE_TIME("PacketPlayOutUpdateTime"),
      PLAY_OUT_WINDOW_ITEMS("PacketPlayOutWindowItems"),
      PLAY_OUT_WORLD_EVENT("PacketPlayOutWorldEvent"),
      PLAY_OUT_WORLD_PARTICLES("PacketPlayOutWorldParticles"),
      STATUS_IN_PING("PacketStatusInPing"),
      STATUS_IN_START("PacketStatusInStart"),
      STATUS_OUT_PONG("PacketStatusOutPong"),
      STATUS_OUT_SERVER_INFO("PacketStatusOutServerInfo");

      private final String name;

      private PacketType(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }
}