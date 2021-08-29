package fr.martyr;

import static org.bukkit.Material.BEE_SPAWN_EGG;
import static org.bukkit.Material.CHICKEN_SPAWN_EGG;
import static org.bukkit.Material.COW_SPAWN_EGG;
import static org.bukkit.Material.FOX_SPAWN_EGG;
import static org.bukkit.Material.HORSE_SPAWN_EGG;
import static org.bukkit.Material.LLAMA_SPAWN_EGG;
import static org.bukkit.Material.MOOSHROOM_SPAWN_EGG;
import static org.bukkit.Material.OCELOT_SPAWN_EGG;
import static org.bukkit.Material.PIG_SPAWN_EGG;
import static org.bukkit.Material.POLAR_BEAR_SPAWN_EGG;
import static org.bukkit.Material.SHEEP_SPAWN_EGG;
import static org.bukkit.Material.SPIDER_SPAWN_EGG;
import static org.bukkit.Material.VILLAGER_SPAWN_EGG;
import static org.bukkit.Material.WOLF_SPAWN_EGG;
import static org.bukkit.Material.ZOMBIFIED_PIGLIN_SPAWN_EGG;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.martyr.utils.ItemBuilder;

public enum AnimalPet {
	PIG(PIG_SPAWN_EGG, EntityType.PIG, ChatColor.RED + "Cochon",
			new ItemBuilder(Material.COOKED_BEEF).toItemStack()),
	
	HORSE_BABY(HORSE_SPAWN_EGG, EntityType.HORSE, ChatColor.BLUE + "Cheval",
			new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0)).toItemStack(),
			new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0)).toItemStack(),
			new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0)).toItemStack(),
			new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0)).toItemStack(),
			new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0)).toItemStack()),
	
	COW(COW_SPAWN_EGG, EntityType.COW, ChatColor.AQUA + "Vache",
			new ItemBuilder(Material.LEATHER_HELMET).toItemStack(),
			new ItemBuilder(Material.LEATHER_CHESTPLATE).toItemStack(),
			new ItemBuilder(Material.LEATHER_LEGGINGS).toItemStack(),
			new ItemBuilder(Material.LEATHER_BOOTS).toItemStack(),
			new ItemBuilder(Material.COOKED_BEEF, 5).toItemStack()),
	
	RENARD(FOX_SPAWN_EGG, EntityType.FOX, ChatColor.GOLD + "Renard",
			new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1800, 0)).toItemStack(),
			new ItemBuilder(Material.FLINT_AND_STEEL).toItemStack()),
	
	CAT(OCELOT_SPAWN_EGG, EntityType.OCELOT, ChatColor.WHITE + "Ocelot",
			new ItemBuilder(Material.SALMON, 20).toItemStack(),
			new ItemBuilder(Material.ENDER_PEARL, 2).toItemStack()),
	
	WOLF(WOLF_SPAWN_EGG, EntityType.WOLF, ChatColor.DARK_GRAY + "Loup",
			new ItemBuilder(Material.STONE_SWORD).addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1).toItemStack(),
			new ItemBuilder(Material.STONE_PICKAXE).toItemStack()),
	
	ZOMBIE_PIGMAN(ZOMBIFIED_PIGLIN_SPAWN_EGG, EntityType.ZOMBIFIED_PIGLIN, ChatColor.DARK_PURPLE + "Pigman",
			new ItemBuilder(Material.CREEPER_SPAWN_EGG, 2).toItemStack(),
			new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1800, 0)).toItemStack()),
	
	CHICKEN(CHICKEN_SPAWN_EGG, EntityType.CHICKEN, ChatColor.RED + "Poulet",
			new ItemBuilder(Material.CAKE, 3).toItemStack(),
			new ItemBuilder(Material.BOW).toItemStack(),
			new ItemBuilder(Material.FEATHER, 20).toItemStack()),
	
	VILLEGEOIS(VILLAGER_SPAWN_EGG, EntityType.VILLAGER, ChatColor.WHITE + "Villageois",
			new ItemBuilder(Material.TNT, 5).toItemStack(),
			new ItemBuilder(Material.GUNPOWDER, 20).toItemStack(),
			new ItemBuilder(Material.MUSIC_DISC_11).toItemStack()),
	
	SPIDER(SPIDER_SPAWN_EGG, EntityType.SPIDER, ChatColor.DARK_PURPLE + "Araignée",
			new ItemBuilder(Material.STRING, 20).toItemStack(),
			new ItemBuilder(Material.BOW).addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 0).toItemStack()),

	CHAMPIMEUUH(MOOSHROOM_SPAWN_EGG, EntityType.MUSHROOM_COW, ChatColor.RED + "Champimeeuh",
			new ItemBuilder(Material.ARROW, 20).toItemStack(),
			new ItemBuilder(Material.BONE, 10).toItemStack()),
	
	SHEEP(SHEEP_SPAWN_EGG, EntityType.SHEEP, ChatColor.YELLOW + "Mouton",
			new ItemBuilder(Material.WHITE_WOOL, 20).toItemStack(),
			new ItemBuilder(Material.COOKED_BEEF, 20).toItemStack()),
	
	ALPAGA(LLAMA_SPAWN_EGG, EntityType.LLAMA, ChatColor.GRAY + "Alpaga",
			new ItemBuilder(Material.EMERALD, 32).toItemStack()),
	
	BEAR(POLAR_BEAR_SPAWN_EGG, EntityType.POLAR_BEAR, ChatColor.WHITE + "Ours",
			new ItemBuilder(Material.SNOWBALL, 16).toItemStack(),
			new ItemBuilder(Material.IRON_BOOTS).addUnsafeEnchantment(Enchantment.FROST_WALKER, 1).toItemStack()),
			
	BEE(BEE_SPAWN_EGG, EntityType.BEE, ChatColor.YELLOW + "Abeille"),
	
	;
	
	private final Material egg;
	private final EntityType entity;
	private final String itemName;
	private final ItemStack[] stacks;
	
	AnimalPet(Material egg, EntityType entity, String itemName, ItemStack... stacks){
		this.egg = egg;
		new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 0, 600));
		this.entity = entity;
		this.itemName = itemName;
		this.stacks = stacks;
	}
	
	public Material getEgg() {
		return this.egg;
	}
	
	public EntityType getEntity() {
		return this.entity;
	}
	
	public String getItemName() {
		return this.itemName;
	}
	
	public ItemStack[] getStacks() {
		return this.stacks;
	}
	
	public static AnimalPet fromItemName(String itemName) {
		for(AnimalPet pet : values()) {
			if(pet.getItemName().equals(itemName)) {
				return pet;
			}
		}
		
		return null;
	}
	
	public static AnimalPet random() {
		final AnimalPet[] array = AnimalPet.values();
		return array[new Random().nextInt(array.length)];	
	}
}
