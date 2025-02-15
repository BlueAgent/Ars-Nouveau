package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ScribesTile extends ModdedTile implements IAnimatable, ITickable {

    public ItemEntity entity; // For rendering
    public ItemStack stack;
    public int frames;


    public ScribesTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCRIBES_TABLE_TILE, pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        stack = ItemStack.of((CompoundTag)compound.get("itemStack"));
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if(stack != null) {
            CompoundTag reagentTag = new CompoundTag();
            stack.save(reagentTag);
            compound.put("itemStack", reagentTag);
        }
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(2);
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

}
