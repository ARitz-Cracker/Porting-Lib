package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.minecraft.world.item.ItemStack;

public class ItemHandlerHelper {
	// left for drop-in compat
	public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
		return ItemStack.isSameItemSameTags(first, second);
	}

	public static ItemStack copyStackWithSize(ItemStack stack, int size) {
		if (size == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}

	public static ItemStack growCopy(ItemStack stack, int amount) {
		return copyStackWithSize(stack, stack.getCount() + amount);
	}
}
