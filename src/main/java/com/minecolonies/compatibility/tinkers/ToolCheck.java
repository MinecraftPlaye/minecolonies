package com.minecolonies.compatibility.tinkers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tools.melee.TinkerMeleeWeapons;

/**
 * This class is to store a check to see if a tinker's tool is broken.
 */
public final class ToolCheck extends ToolProxy
{
    private static final String STATS  = "Stats";
    private static final String BROKEN = "Broken";

    /**
     * Checks to see if STACK is a tinker's tool, and if it is, it checks it's NBT tags to see if it's broken.
     *
     * @param stack the item in question.
     * @return boolean whether the stack is broken or not.
     */
    public static boolean checkTinkersBroken(@Nullable final ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            final NBTTagCompound tags = stack.getTagCompound();
            if (tags.hasKey(STATS))
            {
                final NBTTagCompound stats = tags.getCompoundTag(STATS);
                if (stats.getBoolean(BROKEN))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if item is tinkers weapon.
     *
     * @param item the item.
     * @return if the item is tinkers weapon.
     */
    public static boolean isTinkersWeapon(@NotNull final Item item)
    {
        return new ToolCheck().checkForTinkersWeapon(item);
    }

    /**
     * Check if item is tinkers weapon.
     *
     * @param item the item.
     * @return if the item is tinkers weapon.
     */
    @Override
    @Optional.Method(modid = "tconstruct")
    public boolean checkForTinkersWeapon(@NotNull final Item item)
    {
        return item == TinkerMeleeWeapons.longSword
                 || item == TinkerMeleeWeapons.rapier
                 || item == TinkerMeleeWeapons.fryPan
                 || item == TinkerMeleeWeapons.dagger
                 || item == TinkerMeleeWeapons.battleAxe
                 || item == TinkerMeleeWeapons.battleSign
                 || item == TinkerMeleeWeapons.broadSword
                 || item == TinkerMeleeWeapons.cleaver
                 || item == TinkerMeleeWeapons.cutlass;
    }
}
