package fr.martyr;

import java.util.UUID;

import org.bukkit.entity.Entity;

public class PlayerPet {
	private final UUID uuid;
	private final AnimalPet pet;
	
	public Entity entity;
	public double health = -1;
	
	public PlayerPet(UUID uuid, AnimalPet pet) {
		this.uuid = uuid;
		this.pet = pet;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public AnimalPet getPet() {
		return this.pet;
	}
}
