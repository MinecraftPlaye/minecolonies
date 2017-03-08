package com.minecolonies.compatibility;

import com.minecolonies.compatibility.tinkers.ToolCheck;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class is to store the methods that call the methods to check for miscellaneous compatibility problems.
 */
public final class Compatibility
{

    private Compatibility()
    {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * This method checks to see if STACK is able to mine anything.
     * It goes through all compatibility checks.
     *
     * @param stack the item in question.
     * @param tool  the name of the tool.
     * @return boolean whether the stack can mine or not.
     */
    public static boolean getMiningLevelCompatibility(@Nullable final ItemStack stack, @Nullable final String tool)
    {
        if (ToolCheck.checkTinkersBroken(stack))
        {
            return false;
        }
        return true;
    }

    public static boolean isTinkersWeapon(@NotNull final Item item)
    {
        return ToolCheck.isTinkersWeapon(item);
    }

    public static double getModifier(@NotNull final ItemStack stack, @NotNull final EntityEquipmentSlot slot)
    {
        final List<AttributeModifier> effects = ToolCheck.getModifiersOfWeapon(stack, slot);
        int effectDamage = 0;
        if (!effects.isEmpty())
        {
            for (int i = 1; i <= effects.size(); i++)
            {
                //placeholder.
            }
        }
        return effectDamage;
    }
}
