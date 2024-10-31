package org.by1337.bmenuparser;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.by1337.bmenuparser.event.GameMessageS2CPacketAccessor;
import org.by1337.bmenuparser.event.NetworkEvent;
import org.by1337.bmenuparser.text.RawMessageConvertor;

public class BMenuParser implements ClientModInitializer {
	public static final String MOD_ID = "bmenuparser";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		NetworkEvent.CHAT_EVENT.register(packet -> {
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