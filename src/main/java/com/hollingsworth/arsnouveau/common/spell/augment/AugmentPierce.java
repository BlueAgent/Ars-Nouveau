package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class AugmentPierce extends AbstractAugment {
    public static AugmentPierce INSTANCE = new AugmentPierce();

    private AugmentPierce() {
        super(GlyphLib.AugmentPierceID, "Pierce");
    }

    @Override
    public int getDefaultManaCost() {
        return 40;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ItemsRegistry.WILDEN_SPIKE;
    }

    @Override
    public String getBookDescription() {
        return "When applied to the Projectile spell, projectiles may continue through their path an additional time after hitting a mob or block. Causes certain effects to also target the block behind them, like Break. Combines with AOE to provide depth.";
    }
}
