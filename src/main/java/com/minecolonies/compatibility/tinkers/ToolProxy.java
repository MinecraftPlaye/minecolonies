package com.minecolonies.compatibility.tinkers;

import net.minecraft.item.Item;
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

    public String[] getEffectsOfWeapon(@NotNull final Item item)
    {
        String[] effects = {};
        return effects;
    }
}
