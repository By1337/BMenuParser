package org.by1337.bmenuparser.event;

import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;

@FunctionalInterface
public interface CooldownPacketCallback {
    void onPacket(CooldownUpdateS2CPacket packet);
}