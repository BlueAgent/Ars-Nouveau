package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class PotionMelderRenderer  extends GeoBlockRenderer<PotionMelderTile> {

    public PotionMelderRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, new PotionMelderModel());
    }

    public static GenericItemRenderer getISTER(){
        return new GenericItemRenderer(new PotionMelderModel());
    }
}

