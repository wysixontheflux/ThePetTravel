package fr.martyr;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_16_R3.PathfinderGoalSelector;

public class PetMaker {

    private static Field gsa;
    private static Field goalSelector;
    private static Field targetSelector;

    static {
        try {
            gsa = PathfinderGoalSelector.class.getDeclaredField("b");
            gsa.setAccessible(true);

            goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
            goalSelector.setAccessible(true);

            targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
            targetSelector.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makePet(Entity entity, Player owner) {
        EntityLiving entityLiving = ((CraftLivingEntity)entity).getHandle();
        if (entityLiving instanceof EntityInsentient) {
            EntityInsentient entityInsentient = (EntityInsentient)entityLiving;
            entityInsentient.goalSelector = new PathfinderGoalSelector(entityInsentient.world.getMethodProfilerSupplier());
            entityInsentient.targetSelector = new PathfinderGoalSelector(entityInsentient.world.getMethodProfilerSupplier());
            entityInsentient.goalSelector.a(0, new PathfinderGoalFloat(entityInsentient));
            entityInsentient.goalSelector.a(1, new PathfinderGoalFollowPlayer(entityInsentient, owner, 2D, 5.0F));
        } else {
            throw new IllegalArgumentException(entityLiving.getClass().getSimpleName() + " is not an instance of an EntityInsentient.");
        }
    }

    public static class PathfinderGoalFollowPlayer extends PathfinderGoal {
        private EntityInsentient entity;
        private EntityPlayer owner;
        private double speed;
        private float distanceSquared;

        public PathfinderGoalFollowPlayer(EntityInsentient entity, Player owner, double speed, float distance) {
            this.entity = entity;
            this.owner = ((CraftPlayer)owner).getHandle();
            this.speed = speed;
            this.distanceSquared = distance * distance;
        }

        @Override
        public boolean a() {
            return (owner != null && owner.isAlive() && this.entity.h(owner) > (double)distanceSquared);
        }

        @Override
        public void d() {
            this.entity.getNavigation().n();
        }

        @Override
        public void c() {
            this.entity.getNavigation().a(owner, this.speed);
        }
    }
}
