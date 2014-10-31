package tterrag.core.common.util.blockiterators;

import java.util.Iterator;

import net.minecraft.world.World;
import tterrag.core.common.util.BlockCoord;

public abstract class AbstractBlockIterator implements Iterable<BlockCoord>, Iterator<BlockCoord>
{
    protected World world;
    protected BlockCoord base;

    protected AbstractBlockIterator(World world, BlockCoord base)
    {
        this.world = world;
        this.base = base;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("You can't remove blocks silly!");
    }
    
    @Override
    public Iterator<BlockCoord> iterator()
    {
        return this;
    }
}
