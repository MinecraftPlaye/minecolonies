package com.minecolonies.compatibility.tinkers;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the fallback for when tinkers is not present!
 */
public class ToolProxy
{
    /**
     * This is the fallback for when tinkers is not present!
     * @param item the item.
     * @return if the item is a tinkers weapon.
     */
    public boolean checkForTinkersWeapon(@NotNull final Item item)
    {
        return false;
    }

    public List<AttributeModifier> checkModifiersOfWeapon(@NotNull final ItemStack stack, @NotNull final EntityEquipmentSlot slot)
    {
        List<AttributeModifier> effects = new ArrayList<AttributeModifier>();
        return effects;
    }
}
