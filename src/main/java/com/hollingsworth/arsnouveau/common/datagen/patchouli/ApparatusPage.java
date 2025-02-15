package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class ApparatusPage extends AbstractPage{

    public ApparatusPage(String recipe){
        this.object.addProperty("recipe", recipe);
    }

    public ApparatusPage(ItemLike itemLike){
        this(itemLike.asItem().getRegistryName().toString());
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation(ArsNouveau.MODID, "apparatus_recipe");
    }
}
