package tterrag.core.common.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCoord
{
    public int x, y, z;

    public BlockCoord(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BlockCoord(Entity e)
    {
        this(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY), MathHelper.floor_double(e.posZ));
    }
    
    public BlockCoord(TileEntity te)
    {
        this(te.xCoord, te.yCoord, te.zCoord);
    }
    
    public BlockCoord(BlockCoord other)
    {
        this(other.x, other.y, other.z);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BlockCoord)
        {
            BlockCoord coord = (BlockCoord) obj;

            return coord.x == this.x && coord.y == this.y && coord.z == this.z;
        }

        return false;
    }

    public Block getBlock(World world)
    {
        return world.getBlock(x, y, z);
    }

    public void writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("blockCoordx", x);
        tag.setInteger("blockCoordy", y);
        tag.setInteger("blockCoordz", z);
    }

    public static BlockCoord readFromNBT(NBTTagCompound tag)
    {
        int x = tag.getInteger("blockCoordx");
        int y = tag.getInteger("blockCoordy");
        int z = tag.getInteger("blockCoordz");

        return new BlockCoord(x, y, z);
    }

    public void setPosition(double x, double y, double z)
    {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }
    
    @Override
    public String toString()
    {
        return "X: " + x + "  Y: " + y + "  Z: " + z;
    }
}