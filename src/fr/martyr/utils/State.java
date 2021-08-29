package fr.martyr.utils;

public enum State {
   WAITING(true),
   TP(false),
   GAME(false),
   FINISH(false);

   private static State current = WAITING;
   private boolean canJoin;

   private State(boolean b) {
      this.canJoin = b;
   }

   public static boolean isState(State state) {
      return current == state;
   }

   public static State getState() {
      return current;
   }

   public static void setState(State state) {
      current = state;
   }

   public boolean canJoin() {
      return this.canJoin;
   }
}