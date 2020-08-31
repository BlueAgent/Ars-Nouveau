package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaBloomCrop;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EffectHarvest extends AbstractEffect {

    public EffectHarvest() {
        super(ModConfig.EffectHarvestID, "Harvest");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockRayTraceResult ray = (BlockRayTraceResult) rayTraceResult;
            if(world.isRemote)
                return;
            for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, ray.getPos(), ray, getBuffCount(augments, AugmentAOE.class))){
                BlockState state = world.getBlockState(blockpos);
                if(isTree(state)){
                    HashSet<BlockPos> list = getTree(world, ray.getPos().getX(), ray.getPos().getY(), ray.getPos().getZ(), true, new HashSet<>());
                    list.forEach(listPos -> {
                        System.out.println(listPos);
                        if (hasBuff(augments, AugmentExtract.class)) {
                            world.getBlockState(listPos).getDrops(LootUtil.getSilkContext((ServerWorld) world, listPos,  shooter)).forEach(i -> world.addEntity(new ItemEntity(world,listPos.getX(), listPos.getY(), listPos.getZ(), i )));
                            world.destroyBlock(listPos, false);
                        } else if (hasBuff(augments, AugmentFortune.class)) {
                            world.getBlockState(listPos).getDrops(LootUtil.getFortuneContext((ServerWorld) world, listPos, shooter, getBuffCount(augments, AugmentFortune.class))).forEach(i -> world.addEntity(new ItemEntity(world,listPos.getX(), listPos.getY(), listPos.getZ(),i )));;
                            world.destroyBlock(listPos, false);
                        } else {
                            world.destroyBlock(listPos, true);
                        }
                        world.notifyBlockUpdate(listPos, world.getBlockState(listPos), world.getBlockState(listPos), 3);
                    });
                }

                if(state.getBlock() instanceof FarmlandBlock || world.getBlockState(blockpos.up()).getBlock() instanceof CropsBlock){
                    blockpos = blockpos.up();
                    state = world.getBlockState(blockpos);
                }

                if(!(state.getBlock() instanceof CropsBlock))
                    continue;
                CropsBlock cropsBlock = (CropsBlock)world.getBlockState(blockpos).getBlock();

                if(!cropsBlock.isMaxAge(state) || !(world instanceof ServerWorld))
                    continue;

                List<ItemStack> cropDrops = CropsBlock.getDrops(state, (ServerWorld)world, blockpos, world.getTileEntity(blockpos));

                if(hasBuff(augments, AugmentFortune.class)){
                    cropDrops = state.getDrops(LootUtil.getFortuneContext((ServerWorld) world, blockpos, shooter, getBuffCount(augments, AugmentFortune.class)));
                }
                BlockPos finalBlockpos = blockpos;
                boolean noSeed = false;
                cropDrops.forEach(d -> {
                    if(d.getItem() == BlockRegistry.MANA_BLOOM_CROP.asItem()){
                        return;
                    }
                    world.addEntity(new ItemEntity(world, finalBlockpos.getX(), finalBlockpos.getY(), finalBlockpos.getZ(), d));
                });
                world.setBlockState(blockpos,cropsBlock.withAge(1));
            }
        }
    }

    public boolean isTree(BlockState blockstate){
        return blockstate.getBlock() instanceof LogBlock || blockstate.getBlock() instanceof LeavesBlock;
    }

    public HashSet<BlockPos> getTree(World world, int x, int y, int z, boolean fromTree, HashSet<BlockPos> blocks){
        if(blocks.size() > 500)
            return blocks;
        BlockPos pos = new BlockPos(x, y, z);
        boolean isTree = isTree(world.getBlockState(pos));
        if(isTree && !blocks.contains(pos)){
            blocks.add(pos);
        }else if(fromTree){
            return blocks;
        }
        getTree(world, pos.getX() +1, pos.getY(), pos.getZ(), isTree, blocks);
        getTree(world, pos.getX() - 1, pos.getY(), pos.getZ(), isTree, blocks);
        getTree(world, pos.getX(), pos.getY() + 1, pos.getZ(), isTree, blocks);
        getTree(world, pos.getX(), pos.getY() - 1, pos.getZ(), isTree, blocks);
        getTree(world, pos.getX(), pos.getY(), pos.getZ() + 1, isTree, blocks);
        getTree(world, pos.getX(), pos.getY(), pos.getZ() - 1, isTree, blocks);
        return blocks;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.DIAMOND_AXE;
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    protected String getBookDescription() {
        return "Harvests grown crops and trees. When used on grown crops, this spell will obtain the fully grown product without destroying the plant.";
    }
}
