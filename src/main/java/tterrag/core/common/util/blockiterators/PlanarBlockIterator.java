package tterrag.core.common.util.blockiterators;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PlanarBlockIterator extends CubicBlockIterator
{
    public static enum Orientation
    {
        EAST_WEST, NORTH_SOUTH, HORIZONTAL;

        public static Orientation perpendicular(EnumFacing dir)
        {
            switch (dir)
            {
            case DOWN:
            case UP:
                return HORIZONTAL;
            case NORTH:
            case SOUTH:
                return EAST_WEST;
            case EAST:
            case WEST:
                return NORTH_SOUTH;
            default:
                return null;
            }
        }
    }

    private Orientation orientation;

    public PlanarBlockIterator(BlockPos base, Orientation orientation, int radius)
    {
        super(base, radius);

        this.orientation = orientation;
    }

    @Override
    public BlockPos next()
    {
    	BlockPos coord = new BlockPos(curX, curY, curZ);
        switch (orientation)
        {
        case EAST_WEST:
            curY = curY == maxY ? minY : curY + 1;
            curX = curY == minY ? curX + 1 : curX;
        case NORTH_SOUTH:
            curY = curY == maxY ? minY : curY + 1;
            curZ = curY == minY ? curZ + 1 : curZ;
        case HORIZONTAL:
            curX = curX == maxX ? minX : curX + 1;
            curZ = curX == minX ? curZ + 1 : curZ;
        }
        return coord;
    }

    @Override
    public boolean hasNext()
    {
        return curX <= maxX && curY <= maxY && curZ <= maxZ;
    }
}
