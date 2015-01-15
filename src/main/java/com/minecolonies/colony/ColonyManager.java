package com.minecolonies.colony;

import com.minecolonies.MineColonies;
import com.minecolonies.colony.buildings.Building;
import com.minecolonies.colony.permissions.Permissions;
import com.minecolonies.configuration.Configurations;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ColonyManager {
    private static Map<Integer, Colony>       colonies        = new HashMap<Integer, Colony>();
    private static Map<Integer, List<Colony>> coloniesByWorld = new HashMap<Integer, List<Colony>>();
    private static int                        topColonyId     = 0;

    private static Map<Integer, ColonyView>   colonyViews     = new HashMap<Integer, ColonyView>();

    private static int numWorldsLoaded;    //  Used to trigger loading/unloading colonies

    private final static String FILENAME_MINECOLONIES_PATH = "minecolonies";
    private final static String FILENAME_MINECOLONIES = "colonies.dat";

    private final static String TAG_COLONIES = "colonies";

    public static void init()
    {
    }

    /**
     * Create a new Colony in the given world and at that location
     *
     * @param w
     * @param coord
     * @return
     */
    public static Colony createColony(
            World w,
            ChunkCoordinates coord)
    {
        Colony colony = new Colony(++topColonyId, w, coord);
        colonies.put(colony.getID(), colony);

        if (!coloniesByWorld.containsKey(colony.getDimensionId()))
        {
            coloniesByWorld.put(colony.getDimensionId(), new ArrayList<Colony>());
        }

        coloniesByWorld.get(colony.getDimensionId()).add(colony);
        return colony;
    }

    /**
     * Get Colony by UUID
     *
     * @param id UUID of colony
     * @return
     */
    public static Colony getColonyById(int id) { return colonies.get(id); }

    /**
     * Get Colony that contains a given ChunkCoordinates
     *
     * @param w
     * @param coord
     * @return
     */
    public static Colony getColonyByCoord(World w, ChunkCoordinates coord)
    {
        return getColonyByCoord(w, coord.posX, coord.posY, coord.posZ);
    }


    public static Colony getColonyByCoord(World w, int x, int y, int z)
    {
        List<Colony> coloniesInWorld = coloniesByWorld.get(w.provider.dimensionId);
        if (coloniesInWorld == null) return null;

        for (Colony c : coloniesInWorld)
        {
            if (c.isCoordInColony(w, x, y, z)) return c;
        }

        return null;
    }

    /**
     * Get closest colony by ChunkCoordinate
     *
     * @param w
     * @param coord
     * @return
     */
    public static Colony getClosestColony(World w, ChunkCoordinates coord)
    {
        return getClosestColony(w, coord.posX, coord.posY, coord.posZ);
    }

    /**
     * Get closest colony by x,y,z
     *
     * @param w
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Colony getClosestColony(World w, int x, int y, int z)
    {
        List<Colony> coloniesInWorld = coloniesByWorld.get(w.provider.dimensionId);
        if (coloniesInWorld == null) return null;

        Colony closestColony = null;
        float closestDist = Float.MAX_VALUE;

        for (Colony c : coloniesInWorld)
        {
            if (c.getDimensionId() == w.provider.dimensionId)
            {
                float dist = c.getDistanceSquared(x, y, z);
                if (dist < closestDist)
                {
                    closestColony = c;
                    closestDist = dist;
                }
            }
        }

        return closestColony;
    }

    public static List<Colony> getColoniesByOwner(UUID owner)
    {
        List<Colony> results = new ArrayList<Colony>();

        for (Colony c : colonies.values())
        {
            if (c.getPermissions().getOwner().equals(owner))//TODO is this what we want? Also improve
            {
                results.add(c);
            }
        }

        return results;
    }

    /**
     * Get a Building by a World and coordinates
     *
     * @param w
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Building getBuilding(World w, int x, int y, int z)
    {
        ChunkCoordinates coords = new ChunkCoordinates(x, y, z);
        Colony colony = getColonyByCoord(w, coords);
        if (colony != null)
        {
            Building building = colony.getBuilding(coords);
            if (building != null)
            {
                return building;
            }
        }

        //  Fallback - there might be a Building for this block, but it's outside of it's owning colony's radius
        if (coloniesByWorld.containsKey(w.provider.dimensionId))
        {
            for (Colony otherColony : coloniesByWorld.get(w.provider.dimensionId))
            {
                Building building = otherColony.getBuilding(coords);
                if (building != null)
                {
                    return building;
                }
            }
        }

        return null;
    }

    /**
     * Get a Building by a World and coordinates
     *
     * @param w
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Building.View getBuildingView(World w, int x, int y, int z)
    {
        //  On client we will just check all known views
        ChunkCoordinates coords = new ChunkCoordinates(x, y, z);
        for (ColonyView colony : colonyViews.values())
        {
            Building.View building = colony.getBuilding(coords);
            if (building != null)
            {
                return building;
            }
        }

        return null;
    }

    /**
     * Get ColonyView by UUID
     *
     * @param id UUID of colony
     * @return
     */
    public static ColonyView getColonyView(int id)
    {
        return colonyViews.get(id);
    }

    /**
     * Get Colony that contains a given ChunkCoordinates
     *
     * @param w
     * @param coord
     * @return
     */
    public static ColonyView getColonyView(World w, ChunkCoordinates coord)
    {
        return getColonyView(w, coord.posX, coord.posY, coord.posZ);
    }


    public static ColonyView getColonyView(World w, int x, int y, int z)
    {
        for (ColonyView c : colonyViews.values())
        {
            if (c.isCoordInColony(w, x, y, z)) return c;
        }

        return null;
    }

    public static ColonyView getClosestColonyView(World w, int x, int y, int z)
    {
        ColonyView closestColony = null;
        float closestDist = Float.MAX_VALUE;

        for (ColonyView c : colonyViews.values())
        {
            if (c.getDimensionId() == w.provider.dimensionId)
            {
                float dist = c.getDistanceSquared(x, y, z);
                if (dist < closestDist)
                {
                    closestColony = c;
                    closestDist = dist;
                }
            }
        }

        return closestColony;
    }

    public static List<ColonyView> getColonyViewsOwnedByPlayer(EntityPlayer player)
    {
        List<ColonyView> results = new ArrayList<ColonyView>();

        for (ColonyView c : colonyViews.values())
        {
            if (c.getPlayers().get(player.getGameProfile().getId()).equals(Permissions.Rank.OWNER))//TODO update for permissions
            {
                results.add(c);
            }
        }

        return results;
    }

    public static double getMinimumDistanceBetweenTownHalls()
    {
        //  [Townhall](Radius)+(Padding)+(Radius)[TownHall]
        return (2 * Configurations.workingRangeTownhall) + Configurations.townhallPadding;
    }

    /**
     * On server tick, tick every Colony
     * NOTE: Review this for performance
     *
     * @param event
     */
    public static void onServerTick(
            TickEvent.ServerTickEvent event)
    {
        for (Colony c : colonies.values())
        {
            c.onServerTick(event);
        }
    }

    public static void onClientTick(
            TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (Minecraft.getMinecraft().theWorld == null && !colonyViews.isEmpty())
            {
                //  Player has left the game, clear the Colony View cache
                colonyViews.clear();
            }
        }
    }

    /**
     * On world tick, tick every Colony in that world
     * NOTE: Review this for performance
     *
     * @param event
     */
    public static void onWorldTick(
            TickEvent.WorldTickEvent event)
    {
        for (Colony c : colonies.values())
        {
            if (c.getDimensionId() == event.world.provider.dimensionId)
            {
                c.onWorldTick(event);
            }
        }
    }

    /**
     * Read Colonies from saved NBT data
     *
     * @param compound
     */
    public static void readFromNBT(
            NBTTagCompound compound)
    {
        NBTTagList colonyTags = compound.getTagList(TAG_COLONIES, NBT.TAG_COMPOUND);
        for (int i = 0; i < colonyTags.tagCount(); ++i)
        {
            Colony colony = Colony.loadColony(colonyTags.getCompoundTagAt(i));
            colonies.put(colony.getID(), colony);

            if (!coloniesByWorld.containsKey(colony.getDimensionId()))
            {
                coloniesByWorld.put(colony.getDimensionId(), new ArrayList<Colony>());
            }
            coloniesByWorld.get(colony.getDimensionId()).add(colony);

            topColonyId = Math.max(topColonyId, colony.getID());
        }
    }

    /**
     * Write colonies to NBT data for saving
     *
     * @param compound
     */
    public static void writeToNBT(
            NBTTagCompound compound)
    {
        NBTTagList colonyTagList = new NBTTagList();
        for(Colony colony : colonies.values())
        {
            NBTTagCompound colonyTagCompound = new NBTTagCompound();
            colony.writeToNBT(colonyTagCompound);
            colonyTagList.appendTag(colonyTagCompound);
        }
        compound.setTag(TAG_COLONIES, colonyTagList);
    }

    /**
     * Get save location for Minecolonies data, from the world/save directory
     *
     * @param world
     * @return
     */
    private static File getSaveLocation(
            World world)
    {
        File saveDir = new File(DimensionManager.getWorld(0).getSaveHandler().getWorldDirectory(), FILENAME_MINECOLONIES_PATH);
        return new File(saveDir, FILENAME_MINECOLONIES);
    }

    /**
     * Load a file and return the data as an NBTTagCompound
     *
     * @param file The path to the file
     * @return the data from the file as an NBTTagCompound, or null
     */
    private static NBTTagCompound loadNBTFromPath(
            File file)
    {
        try
        {
            if (file != null && file.exists())
            {
                return CompressedStreamTools.read(file);
//                return CompressedStreamTools.readCompressed(new FileInputStream(file));
            }
        }
        catch (IOException exception)
        {
            MineColonies.logger.error("Exception when loading ColonyManger", exception);
        }
        return null;
    }

    /**
     * Save an NBTTagCompound to a file.  Does so in a safe manner using an intermediate tmp file
     *
     * @param file The destination file to write the data to
     * @param compound The NBTTagCompound to write to the file
     */
    private static void saveNBTToPath(
            File file,
            NBTTagCompound compound)
    {
        try
        {
            if (file != null)
            {
                file.getParentFile().mkdir();
                CompressedStreamTools.safeWrite(compound, file);

//                File tempFile = new File(file.getAbsolutePath() + "_tmp");
//                tempFile.delete();
//
//                CompressedStreamTools.writeCompressed(compound, new DataOutputStream(new FileOutputStream(tempFile)));
//
//                file.delete();
//                tempFile.renameTo(file);
            }
        }
        catch (IOException exception)
        {
            MineColonies.logger.error("Exception when saving ColonyManager", exception);
        }
    }

    /**
     * When a world is loaded, Colonies in that world need to grab the reference to the World
     * Additionally, when loading the first world, load all colonies.
     *
     * @param world
     */
    public static void onWorldLoad(World world)
    {
        if (!world.isRemote)
        {
            if (numWorldsLoaded == 0)
            {
                File file = getSaveLocation(world);
                NBTTagCompound data = loadNBTFromPath(file);
                if (data != null)
                {
                    readFromNBT(data);
                }
            }
            ++numWorldsLoaded;

            List<Colony> worldColonies = coloniesByWorld.get(world.provider.dimensionId);
            if (worldColonies != null)
            {
                for (Colony c : worldColonies)
                {
                    c.onWorldLoad(world);
                }
            }
        }
        else
        {
            for (ColonyView v : colonyViews.values())
            {
                v.onWorldLoad(world);
            }
        }
    }

    public static void onWorldSave(World world)
    {
        if (!world.isRemote &&
            world.provider.dimensionId == 0)    //  For now, save when 0 saves...
        {
            NBTTagCompound compound = new NBTTagCompound();
            writeToNBT(compound);

            File file = getSaveLocation(world);
            saveNBTToPath(file, compound);
        }
    }

    /**
     * When a world unloads, all colonies in that world are informed
     * Additionally, when the last world is unloaded, delete all colonies
     *
     * @param world
     */
    public static void onWorldUnload(World world)
    {
        if (!world.isRemote)
        {
            List<Colony> worldColonies = coloniesByWorld.get(world.provider.dimensionId);
            if (worldColonies != null)
            {
                for (Colony c : worldColonies)
                {
                    c.onWorldUnload(world);
                }
            }

            --numWorldsLoaded;
            if (numWorldsLoaded == 0)
            {
                colonies.clear();
                coloniesByWorld.clear();
            }
        }
    }

    /**
     *
     * @param colonyId
     * @param colonyData
     */
    static public IMessage handleColonyViewMessage(int colonyId, ByteBuf colonyData, boolean isNewSubscription)
    {
        ColonyView view = getColonyView(colonyId);
        if (view == null)
        {
            view = ColonyView.createFromNetwork(colonyId);
            colonyViews.put(colonyId, view);
        }

        return view.handleColonyViewMessage(colonyData, isNewSubscription);
    }

    public static IMessage handlePermissionsViewMessage(int colonyID, ByteBuf data)
    {
        ColonyView view = getColonyView(colonyID);
        if(view != null)
        {
            return view.handlePermissionsViewMessage(data);
        }
        else
        {
            MineColonies.logger.error(String.format("Colony view does not exist for ID #%d", colonyID));
            return null;
        }
    }

    /**
     *
     * @param colonyId
     * @param citizenId
     * @param buf
     */
    static public IMessage handleColonyViewCitizensMessage(int colonyId, int citizenId, ByteBuf buf)
    {
        ColonyView view = getColonyView(colonyId);
        if (view != null)
        {
            return view.handleColonyViewCitizensMessage(citizenId, buf);
        }

        return null;
    }

    /**
     *
     * @param colonyId
     * @param citizenId
     */
    static public IMessage handleColonyViewRemoveCitizenMessage(int colonyId, int citizenId)
    {
        ColonyView view = getColonyView(colonyId);
        if (view != null)
        {
            //  Can legitimately be NULL, because (to keep the code simple and fast), it is
            //  possible to receive a 'remove' notice before receiving the View
            return view.handleColonyViewRemoveCitizenMessage(citizenId);
        }

        return null;
    }

    /**
     *
     * @param colonyId The ID of the colony
     * @param buf      The building data, or null if it was removed
     */
    static public IMessage handleColonyBuildingViewMessage(int colonyId, ChunkCoordinates buildingId, ByteBuf buf)
    {
        ColonyView view = getColonyView(colonyId);
        if (view != null)
        {
            return view.handleColonyBuildingViewMessage(buildingId, buf);
        }
        else
        {
            MineColonies.logger.error(String.format("Colony view does not exist for ID #%d", colonyId));
            return null;
        }
    }

    /**
     *
     * @param colonyId
     * @param buildingId
     */
    static public IMessage handleColonyViewRemoveBuildingMessage(int colonyId, ChunkCoordinates buildingId)
    {
        ColonyView view = getColonyView(colonyId);
        if (view != null)
        {
            //  Can legitimately be NULL, because (to keep the code simple and fast), it is
            //  possible to receive a 'remove' notice before receiving the View
            return view.handleColonyViewRemoveBuildingMessage(buildingId);
        }

        return null;
    }
}