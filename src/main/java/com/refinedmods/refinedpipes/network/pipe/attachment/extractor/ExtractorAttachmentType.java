package com.refinedmods.refinedpipes.network.pipe.attachment.extractor;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesItems;
import com.refinedmods.refinedpipes.config.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public enum ExtractorAttachmentType {
    BASIC(1),
    IMPROVED(2),
    ADVANCED(3),
    ELITE(4),
    ULTIMATE(5);

    private final int tier;

    ExtractorAttachmentType(int tier) {
        this.tier = tier;
    }

    public static ExtractorAttachmentType get(byte b) {
        ExtractorAttachmentType[] v = values();

        if (b < 0 || b >= v.length) {
            return BASIC;
        }

        return v[b];
    }

    public int getTier() {
        return tier;
    }

    int getItemTickInterval() {
        return getConfig().getItemTickInterval();
    }

    int getFluidTickInterval() {
        return getConfig().getFluidTickInterval();
    }

    public int getItemsToExtract() {
        return getConfig().getItemsToExtract();
    }

    int getFluidsToExtract() {
        return getConfig().getFluidsToExtract();
    }

    public int getFilterSlots() {
        return getConfig().getFilterSlots();
    }

    public boolean getCanSetRedstoneMode() {
        return getConfig().getCanSetRedstoneMode();
    }

    public boolean getCanSetWhitelistBlacklist() {
        return getConfig().getCanSetWhitelistBlacklist();
    }

    public boolean getCanSetRoutingMode() {
        return getConfig().getCanSetRoutingMode();
    }

    public boolean getCanSetExactMode() {
        return getConfig().getCanSetExactMode();
    }

    private ServerConfig.ExtractorAttachment getConfig() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment();
            case ELITE:
                return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment();
            case ULTIMATE:
                return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment();
            default:
                throw new RuntimeException("?");
        }
    }

    public ResourceLocation getId() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_extractor");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_extractor");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_extractor");
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "elite_extractor");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "ultimate_extractor");
            default:
                throw new RuntimeException("?");
        }
    }

    ResourceLocation getItemId() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_extractor_attachment");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_extractor_attachment");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_extractor_attachment");
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "elite_extractor_attachment");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "ultimate_extractor_attachment");
            default:
                throw new RuntimeException("?");
        }
    }

    Item getItem() {
        switch (this) {
            case BASIC:
                return RefinedPipesItems.BASIC_EXTRACTOR_ATTACHMENT;
            case IMPROVED:
                return RefinedPipesItems.IMPROVED_EXTRACTOR_ATTACHMENT;
            case ADVANCED:
                return RefinedPipesItems.ADVANCED_EXTRACTOR_ATTACHMENT;
            case ELITE:
                return RefinedPipesItems.ELITE_EXTRACTOR_ATTACHMENT;
            case ULTIMATE:
                return RefinedPipesItems.ULTIMATE_EXTRACTOR_ATTACHMENT;
            default:
                throw new RuntimeException("?");
        }
    }

    ResourceLocation getModelLocation() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/basic");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/improved");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/advanced");
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/elite");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/ultimate");
            default:
                throw new RuntimeException("?");
        }
    }
}
