package com.hollingsworth.arsnouveau.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;

public class VortexParticleData implements ParticleProvider<ColorParticleTypeData> {
    private final SpriteSet spriteSet;
    public static final String NAME = "vortex";

    public VortexParticleData(SpriteSet sprite) {
        this.spriteSet = sprite;
    }

    @Override
    public Particle createParticle(ColorParticleTypeData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return new ParticleVortex(worldIn, x,y,z,xSpeed, ySpeed, zSpeed, data.color.getRed(), data.color.getGreen(), data.color.getBlue(), 1.0f, .25f, 36, this.spriteSet);
    }

    public static ParticleOptions createData(ParticleColor color) {
        return new ColorParticleTypeData(ModParticles.VORTEX_TYPE, color, false);
    }

}