package tterrag.core.common.util.blockiterators;

import java.util.Iterator;

import net.minecraft.util.BlockPos;

public abstract class AbstractBlockIterator implements Iterable<BlockPos>, Iterator<BlockPos>
{
    protected BlockPos base;

    protected AbstractBlockIterator(BlockPos base)
    {
        this.base = base;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("You can't remove blocks silly!");
    }

    @Override
    public Iterator<BlockPos> iterator()
    {
        return this;
    }
}
