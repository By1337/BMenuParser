package org.by1337.bmenuparser.listener;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import org.by1337.bmenuparser.event.CooldownPacketCallback;
import org.by1337.bmenuparser.event.NetworkEvent;

import java.util.Locale;
import java.util.UUID;

public class CooldownListener {
    private boolean enabled = false;

    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//cooldown_log")
                .executes(ctx -> {
                    enabled = !enabled;
                    if (enabled) {
                        ctx.getSource().sendFeedback(new LiteralText("Кд на предметы теперь логируются"));
                    } else {
                        ctx.getSource().sendFeedback(new LiteralText("Кд на предметы теперь не логируются"));
                    }
                    return 1;
                })
        );

        NetworkEvent.COOLDOWN_UPDATE.register(new CooldownPacketCallback() {
            @Override
            public void onPacket(CooldownUpdateS2CPacket packet) {
                if (!enabled || !Thread.currentThread().getName().contains("Netty Client IO")) return;
                String material = Registry.ITEM.getKey(packet.getItem()).get().getValue().getPath().toUpperCase(Locale.ENGLISH);
                LiteralText text = new LiteralText("Кд: на: ");
                text.append(new LiteralText(material)
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, material))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(material)))
                        )
                );
                text.append(new LiteralText(" тиков: "));
                String cd = String.valueOf(packet.getCooldown());
                text.append(new LiteralText(cd)
                        .styled(s -> s
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, cd))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(cd)))
                        )
                );
                MinecraftClient.getInstance().inGameHud.addChatMessage(net.minecraft.network.MessageType.CHAT, text, UUID.randomUUID());
            }
        });
    }
}
