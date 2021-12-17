package com.refinedmods.refinedpipes.network.pipe.shape;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeShapeProps {
    public static final VoxelShape CORE_SHAPE = Block.box(4, 4, 4, 12, 12, 12);
    public static final VoxelShape NORTH_EXTENSION_SHAPE = Block.box(4, 4, 0, 12, 12, 4);
    public static final VoxelShape EAST_EXTENSION_SHAPE = Block.box(12, 4, 4, 16, 12, 12);
    public static final VoxelShape SOUTH_EXTENSION_SHAPE = Block.box(4, 4, 12, 12, 12, 16);
    public static final VoxelShape WEST_EXTENSION_SHAPE = Block.box(0, 4, 4, 4, 12, 12);
    public static final VoxelShape UP_EXTENSION_SHAPE = Block.box(4, 12, 4, 12, 16, 12);
    public static final VoxelShape DOWN_EXTENSION_SHAPE = Block.box(4, 0, 4, 12, 4, 12);

    public static final VoxelShape NORTH_ATTACHMENT_SHAPE = Block.box(3, 3, 0, 13, 13, 3);
    public static final VoxelShape EAST_ATTACHMENT_SHAPE = Block.box(13, 3, 3, 16, 13, 13);
    public static final VoxelShape SOUTH_ATTACHMENT_SHAPE = Block.box(3, 3, 13, 13, 13, 16);
    public static final VoxelShape WEST_ATTACHMENT_SHAPE = Block.box(0, 3, 3, 3, 13, 13);
    public static final VoxelShape UP_ATTACHMENT_SHAPE = Block.box(3, 13, 3, 13, 16, 13);
    public static final VoxelShape DOWN_ATTACHMENT_SHAPE = Block.box(3, 0, 3, 13, 3, 13);
}
