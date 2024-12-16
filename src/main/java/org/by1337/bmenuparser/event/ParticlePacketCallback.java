package org.by1337.bmenuparser.event;

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;

@FunctionalInterface
public interface ParticlePacketCallback {
    void onPacket(ParticleS2CPacket packet);
}