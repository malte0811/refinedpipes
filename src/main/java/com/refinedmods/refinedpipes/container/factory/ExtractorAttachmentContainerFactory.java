package com.refinedmods.refinedpipes.container.factory;

import com.refinedmods.refinedpipes.container.ExtractorAttachmentContainerMenu;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.*;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public class ExtractorAttachmentContainerFactory implements IContainerFactory<ExtractorAttachmentContainerMenu> {
    @Override
    public ExtractorAttachmentContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf buf) {
        return new ExtractorAttachmentContainerMenu(
            windowId,
            inv.player,
            buf.readBlockPos(),
            DirectionUtil.safeGet(buf.readByte()),
            RedstoneMode.get(buf.readByte()),
            BlacklistWhitelist.get(buf.readByte()),
            RoutingMode.get(buf.readByte()),
            buf.readInt(),
            buf.readBoolean(),
            ExtractorAttachmentType.get(buf.readByte()),
            ExtractorAttachment.createItemFilterInventory(null),
            ExtractorAttachment.createFluidFilterInventory(null),
            buf.readBoolean()
        );
    }
}
