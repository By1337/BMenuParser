package org.by1337.bmenuparser.event;

import net.minecraft.text.Text;

public interface GameMessageS2CPacketAccessor {
    void bMenuParser$setMessage(Text message);

    default void setMessage(Text message) {
        bMenuParser$setMessage(message);
    }
}
