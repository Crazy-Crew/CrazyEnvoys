package me.badbones69.envoy.multisupport;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_10_R1.NBTTagCompound;

public class NMS_v1_10_R1 {
	
	@SuppressWarnings("deprecation")
	public static ItemStack getSpawnEgg(EntityType type, int amount) {
		ItemStack item = new ItemStack(Material.MONSTER_EGG, amount);
		net.minecraft.server.v1_10_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagCompound = stack.getTag();
		if(tagCompound == null) {
			tagCompound = new NBTTagCompound();
		}
		NBTTagCompound id = new NBTTagCompound();
		id.setString("id", type.getName());
		tagCompound.set("EntityTag", id);
		stack.setTag(tagCompound);
		return CraftItemStack.asBukkitCopy(stack);
	}
	
	public static ItemStack addUnbreaking(ItemStack item) {
		net.minecraft.server.v1_10_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if(!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if(tag == null) {
			tag = nmsStack.getTag();
		}
		tag.setBoolean("Unbreakable", true);
		tag.setInt("HideFlags", 4);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}
	
}