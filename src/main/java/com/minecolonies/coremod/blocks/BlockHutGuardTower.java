package com.minecolonies.coremod.blocks;

import org.jetbrains.annotations.NotNull;

/**
 * Block of the GuardTower hut.
 */
public class BlockHutGuardTower extends AbstractBlockHut
{
    /**
     * Default constructor.
     */
    protected BlockHutGuardTower()
    {
        //No different from Abstract parent
        super();
    }

    public String getJobName()
    {
        return "Guard";
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockHutGuardTower";
    }
}
