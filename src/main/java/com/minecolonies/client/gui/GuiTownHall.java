package com.minecolonies.client.gui;

import com.minecolonies.MineColonies;
import com.minecolonies.lib.Constants;
import com.minecolonies.tileentities.TileEntityTownHall;
import com.minecolonies.util.LanguageHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiTownHall extends GuiBase
{
    //private final int numberOfButtons = 8; //This variable is unused - Nico
    private final int idBuildTownhall = 0, idRepairTownhall = 1, idRecallCitizens = 2, idToggleSpecialization = 3, idRenameColony = 4, idInformation = 5, idActions = 6, idSettings = 7;
    private TileEntityTownHall tileEntityTownHall;
    private int buttonSpan = 4, span = 30;

    private EntityPlayer player;
    private World        world;
    private int          x, y, z;

    public GuiTownHall(TileEntityTownHall tileEntityTownHall, EntityPlayer player, World world, int x, int y, int z)
    {
        super();
        this.tileEntityTownHall = tileEntityTownHall;
        this.player = player;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected void addElements()
    {
        super.addElements();

        String currentSpec = LanguageHandler.format("com.minecolonies.gui.townhall.currentSpecialization");
        String spec = "<Industrial>"; //TODO replace with actual specialisation
        String currentTownhallName = LanguageHandler.format("com.minecolonies.gui.townhall.currTownhallName");
        String townhallName = tileEntityTownHall.getCityName();

        int y = span;

        addLabel(currentTownhallName, middleX - fontRendererObj.getStringWidth(currentTownhallName) / 2 + 3, middleY + 4);
        addLabel(townhallName, middleX - fontRendererObj.getStringWidth(townhallName) / 2 + 3, middleY + 13);
        addButton(idBuildTownhall, LanguageHandler.format("com.minecolonies.gui.townhall.build"), middleX - buttonWidth / 2, middleY + y, buttonWidth, buttonHeight);
        y += buttonHeight + buttonSpan;
        addButton(idRepairTownhall, LanguageHandler.format("com.minecolonies.gui.townhall.repair"), middleX - buttonWidth / 2, middleY + y, buttonWidth, buttonHeight);
        y += buttonHeight + buttonSpan;
        addButton(idRecallCitizens, LanguageHandler.format("com.minecolonies.gui.townhall.recall"), middleX - buttonWidth / 2, middleY + y, buttonWidth, buttonHeight);
        y += buttonHeight + buttonSpan;
        addButton(idToggleSpecialization, LanguageHandler.format("com.minecolonies.gui.townhall.togglespec"), middleX - buttonWidth / 2, middleY + y, buttonWidth, buttonHeight);

        y += buttonHeight + buttonSpan;
        addLabel(currentSpec, middleX - fontRendererObj.getStringWidth(currentSpec) / 2 + 3, middleY + y);
        addLabel(spec, middleX - fontRendererObj.getStringWidth(spec) / 2 + 3, middleY + y + 11);
        y += buttonHeight + buttonSpan;

        addButton(idRenameColony, LanguageHandler.format("com.minecolonies.gui.townhall.rename"), middleX - buttonWidth / 2, middleY + y, buttonWidth, buttonHeight);

        //Bottom navigation
        addButton(idInformation, LanguageHandler.format("com.minecolonies.gui.workerHuts.information"), middleX - 76, middleY + ySize - 34, 64, buttonHeight);
        addButton(idActions, LanguageHandler.format("com.minecolonies.gui.townhall.actions"), middleX - 10, middleY + ySize - 34, 44, buttonHeight);
        addButton(idSettings, LanguageHandler.format("com.minecolonies.gui.workerHuts.settings"), middleX + xSize / 2 - 50, middleY + ySize - 34, 46, buttonHeight);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        switch(guiButton.id)
        {
            case idBuildTownhall:
                break;
            case idRepairTownhall:
                break;
            case idRecallCitizens:
                break;
            case idToggleSpecialization:
                break;
            case idRenameColony:
                player.openGui(MineColonies.instance, Constants.Gui.RenameTown.ordinal(), world, x, y, z);
                break;
            case idInformation:
                break;
            case idActions:
                break;
            case idSettings:
                break;
        }
    }
}