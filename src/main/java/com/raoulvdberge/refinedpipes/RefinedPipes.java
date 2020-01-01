package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.item.group.MainItemGroup;
import com.raoulvdberge.refinedpipes.setup.ClientSetup;
import com.raoulvdberge.refinedpipes.setup.CommonSetup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RefinedPipes.ID)
public class RefinedPipes {
    public static final String ID = "refinedpipes";
    public static final ItemGroup MAIN_GROUP = new MainItemGroup();

    public RefinedPipes() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientSetup::new);

        CommonSetup commonSetup = new CommonSetup();

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, commonSetup::onRegisterBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, commonSetup::onRegisterItems);
    }
}
