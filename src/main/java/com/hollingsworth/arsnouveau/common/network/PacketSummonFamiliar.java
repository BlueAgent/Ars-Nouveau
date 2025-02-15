package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.event.FamiliarSummonEvent;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSummonFamiliar {

    String familiarID;
    int entityID;

    public PacketSummonFamiliar(String id, int entityID){
        this.familiarID = id;
        this.entityID = entityID;
    }

    //Decoder
    public PacketSummonFamiliar(FriendlyByteBuf buf){
        familiarID = buf.readUtf(32767);
        entityID = buf.readInt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeUtf(familiarID);
        buf.writeInt(entityID);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ctx.get().getSender() != null){
                IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(ctx.get().getSender()).orElse(null);
                if(cap == null)
                    return;


                Entity owner = ctx.get().getSender().level.getEntity(entityID);

                if(owner instanceof LivingEntity && ((LivingEntity) owner).hasEffect(ModPotions.FAMILIAR_SICKNESS_EFFECT)){
                    PortUtil.sendMessage(owner, new TranslatableComponent("ars_nouveau.familiar.sickness"));
                    return;
                }

                IFamiliar familiarEntity = cap.getFamiliarData(familiarID).getEntity(ctx.get().getSender().level);
                familiarEntity.setOwnerID(owner.getUUID());
                familiarEntity.getThisEntity().setPos(owner.getX(), owner.getY(), owner.getZ());

                FamiliarSummonEvent summonEvent = new FamiliarSummonEvent(familiarEntity.getThisEntity(), owner);
                MinecraftForge.EVENT_BUS.post(summonEvent);

                if(!summonEvent.isCanceled()) {
                    owner.level.addFreshEntity(familiarEntity.getThisEntity());
                    ParticleUtil.spawnPoof((ServerLevel) owner.level, familiarEntity.getThisEntity().blockPosition());
                    if (owner instanceof LivingEntity) {
                        ((LivingEntity) owner).addEffect(new MobEffectInstance(ModPotions.FAMILIAR_SICKNESS_EFFECT, 20 * 300, 0, false, false, true));
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
