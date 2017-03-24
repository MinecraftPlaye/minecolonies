package com.minecolonies.coremod.network.messages;

import com.minecolonies.coremod.configuration.Configurations;
import com.minecolonies.coremod.MineColonies;
import com.minecolonies.coremod.colony.ColonyManager;
import com.minecolonies.coremod.colony.Structures;
import com.minecolonies.coremod.util.*;
import com.minecolonies.structures.helpers.Structure;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Save Schematic Message.
 */
public class SchematicSaveMessage implements IMessage, IMessageHandler<SchematicSaveMessage, IMessage>
{
    private byte [] data = null;
    final private int MAX_TOTAL_SIZE = 32767;


    /**
     * Public standard constructor.
     */
    public SchematicSaveMessage()
    {
        super();
    }

    /**
     * Send a schematic compound to the client.
     *
     * @param data byte array of the schematic.
     * @param name name of the schematic ex: huts/stone/builder1.
     */
    public SchematicSaveMessage(final byte[] data)
    {
            this.data = data;
    }

    @Override
    public void fromBytes(@NotNull final ByteBuf buf)
    {
        Log.getLogger().info("fromBytes: maxCapacity=" + buf.maxCapacity());
        Log.getLogger().info("fromBytes: readableBytes=" + buf.readableBytes());
        int length = buf.readInt();
        Log.getLogger().info("fromBytes: length = " + length);
        final byte[] compressedData = new byte [length];
        Log.getLogger().info("fromBytes: compressedData.length = " + compressedData.length);
        buf.readBytes(compressedData);
        data = Structure.uncompress(compressedData);
        Log.getLogger().info("fromBytes: data.length = " + data.length);
    }

    @Override
    public void toBytes(@NotNull final ByteBuf buf)
    {
        Log.getLogger().info("toBytes: maxCapacity=" + buf.maxCapacity());
        Log.getLogger().info("toBytes: capacity=" + buf.capacity());
        Log.getLogger().info("toBytes: writableBytes=" + buf.writableBytes());
        Log.getLogger().info("toBytes: data.length=" + data.length);
        Log.getLogger().info("toBytes: buf.writerIndex=" + buf.writerIndex());

        final byte[] compressedData = Structure.compress(data);
        Log.getLogger().info("toBytes: compressedData.length=" + compressedData.length);
        Log.getLogger().info("toBytes: buf.writerIndex=" + buf.writerIndex());
        buf.capacity(compressedData.length + buf.writerIndex());
        Log.getLogger().info("toBytes: buf.capacity()" + buf.capacity());
        final int MAX_SIZE = MAX_TOTAL_SIZE - Integer.SIZE / Byte.SIZE;
        if (compressedData.length > MAX_SIZE)
        {
            buf.writeInt(0);
            if (MineColonies.isClient())
            {
                ClientStructureWrapper.sendMessageSchematicTooBig(MAX_SIZE);
            }
            else
            {
                Log.getLogger().error("SchematicSaveMessage: schematic size too big, can not be bigger than " + MAX_SIZE + " bytes");
            }
        }
        else
        {
            buf.writeInt(compressedData.length);
            buf.writeBytes(compressedData);
        }
    }

    @Nullable
    @Override
    public IMessage onMessage(@NotNull final SchematicSaveMessage message, final MessageContext ctx)
    {
        if (!MineColonies.isClient() && !Configurations.allowPlayerSchematics)
        {
            Log.getLogger().info("SchematicSaveMessage: custom schematic is not allowed on this server.");
            if (ctx.side.isServer())
            {
                ctx.getServerHandler().playerEntity.sendMessage(new TextComponentString("The server does not allow custom schematic!"));
            }
            return null;
        }

        boolean schematicSent=false;
        if (message.data == null)
        {
            Log.getLogger().error("Received empty schematic file");
            schematicSent = false;
        }
        else
        {
            schematicSent = Structures.handleSaveSchematicMessage(message.data);
        }

        if (ctx.side.isServer())
        {
            if (schematicSent)
            {
                ctx.getServerHandler().playerEntity.sendMessage(new TextComponentString("Schematic successfully sent!"));
            }
            else
            {
                ctx.getServerHandler().playerEntity.sendMessage(new TextComponentString("Failed to send the Schematic!"));
            }
        }
        return null;
    }
}
