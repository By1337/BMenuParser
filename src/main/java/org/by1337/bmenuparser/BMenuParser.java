package org.by1337.bmenuparser;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.by1337.bmenuparser.listener.*;
import org.by1337.bmenuparser.schem.SchemSelector;

public class BMenuParser implements ClientModInitializer {
    public static final String MOD_ID = "bmenuparser";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        new ScreenListener().register();
        new ChatListener().register();
        new SoundListener().register();
        new ParticleListener().register();
        new CooldownListener().register();
        new SchemSelector().register();
    }
}