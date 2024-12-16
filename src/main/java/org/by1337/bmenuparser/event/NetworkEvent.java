package org.by1337.bmenuparser.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class NetworkEvent {
    public static final Event<ChatPacketCallback> CHAT_EVENT = EventFactory.createArrayBacked(ChatPacketCallback.class,
            (listeners) -> (packet) -> {
                for (ChatPacketCallback listener : listeners) {
                    listener.onChatPacket(packet);
                }
            }
    );
    public static final Event<SoundEventListener> SOUND_EVENT = EventFactory.createArrayBacked(SoundEventListener.class, (listeners) -> (packet) -> {
        for (SoundEventListener listener : listeners) {
            listener.on(packet);
        }
    });
    public static final Event<ParticlePacketCallback> PARTICLE = EventFactory.createArrayBacked(ParticlePacketCallback.class,
            (listeners) -> (packet) -> {
                for (ParticlePacketCallback listener : listeners) {
                    listener.onPacket(packet);
                }
            }
    );
    public static final Event<CooldownPacketCallback> COOLDOWN_UPDATE = EventFactory.createArrayBacked(CooldownPacketCallback.class,
            (listeners) -> (packet) -> {
                for (CooldownPacketCallback listener : listeners) {
                    listener.onPacket(packet);
                }
            }
    );

    @FunctionalInterface
    public interface ChatPacketCallback {
        void onChatPacket(GameMessageS2CPacket packet);
    }
}
