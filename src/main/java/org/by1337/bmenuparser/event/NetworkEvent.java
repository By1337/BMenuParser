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

    @FunctionalInterface
    public interface ChatPacketCallback {
        void onChatPacket(GameMessageS2CPacket packet);
    }
}
