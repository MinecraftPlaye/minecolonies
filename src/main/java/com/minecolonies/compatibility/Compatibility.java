package com.minecolonies.compatibility;

import com.minecolonies.compatibility.tinkers.ToolCheck;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
