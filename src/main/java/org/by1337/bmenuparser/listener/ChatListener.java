package org.by1337.bmenuparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.by1337.bmenuparser.event.GameMessageS2CPacketAccessor;
import org.by1337.bmenuparser.event.NetworkEvent;
import org.by1337.bmenuparser.text.RawMessageConvertor;

public class ChatListener {
    private boolean enabled = true;

    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//chat_click_to_copy")
                .executes(ctx -> {
                    enabled = !enabled;
                    if (enabled) {
                        ctx.getSource().sendFeedback(new LiteralText("Сообщения теперь можно копировать!"));
                    } else {
                        ctx.getSource().sendFeedback(new LiteralText("Сообщения теперь нельзя копировать!"));
                    }
                    return 1;
                })
        );

        NetworkEvent.CHAT_EVENT.register(packet -> {
            if (!enabled || !Thread.currentThread().getName().contains("Netty Client IO")) return;
            if (packet.getMessage() instanceof LiteralText) {
                LiteralText literalText = (LiteralText) packet.getMessage();
                final String raw = RawMessageConvertor.convertToLegacy(Text.Serializer.toJson(packet.getMessage()));

                literalText = (LiteralText) literalText.styled(s -> s.withClickEvent(new ClickEvent(
                        ClickEvent.Action.COPY_TO_CLIPBOARD, raw
                )));
                literalText = (LiteralText) literalText.styled(s -> s.withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy")
                )));
                ((GameMessageS2CPacketAccessor) packet).setMessage(literalText);
            }
        });
    }
}
