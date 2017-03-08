package com.minecolonies.compatibility.tinkers;

import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

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

    public PotionEffect[] getEffectsOfWeapon(@NotNull final Item item)
    {
        PotionEffect[] effects = {};
        return effects;
    }
}
