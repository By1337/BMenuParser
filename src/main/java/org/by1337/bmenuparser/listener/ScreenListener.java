package org.by1337.bmenuparser.listener;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.by1337.bmenuparser.gui.CustomButtonWidget;
import org.by1337.bmenuparser.inv.ScreenUtil;
import org.by1337.bmenuparser.inv.copy.MenuSaver;
import org.by1337.bmenuparser.inv.copy.ScreenAnimationParser;
import org.by1337.bmenuparser.text.RawMessageConvertor;
import org.by1337.bmenuparser.text.StringUtil;
import org.by1337.bmenuparser.toast.CustomToast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenListener {

    public void register() {
        final int buttonWidth = 58;
        final int buttonHeight = 16;

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof HandledScreen) {
                HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
                Inventory inventory = ScreenUtil.getInventory(handledScreen);
                if (inventory != null) {
                    final ScreenAnimationParser parser = new ScreenAnimationParser(inventory, screen);
                    Screens.getButtons(screen).add(
                            new CustomButtonWidget(
                                    (width / 2) - (buttonWidth / 2) + 58 - 3, (height / 2) - (buttonHeight / 2) - (103 + buttonHeight), buttonWidth, buttonHeight, Text.of("save anim"), (btn) -> {
                                MenuSaver menuSaver = new MenuSaver(
                                        handledScreen, parser.getFrameCreator().frames, inventory
                                );
                                saveToFile(menuSaver.save(), generateSaveName(menuSaver, handledScreen));

                            })
                    );
                    Screens.getButtons(screen).add(
                            new CustomButtonWidget(
                                    (width / 2) - (buttonWidth / 2) + 58 - 3 - buttonWidth, (height / 2) - (buttonHeight / 2) - (103 + buttonHeight), buttonWidth, buttonHeight, Text.of("save"), (btn) -> {
                                MenuSaver menuSaver = new MenuSaver(
                                        handledScreen, parser.getFrameCreator().frames, inventory
                                );

                                saveToFile(menuSaver.saveCurrentFrame(), generateSaveName(menuSaver, handledScreen));
                            })
                    );
                }
            }
        });
    }

    private String generateSaveName(MenuSaver menuSaver, HandledScreen<?> screen) {
        Path menuFolder = FabricLoader.getInstance().getGameDir().resolve("mods/menus");

        if (screen.getTitle() instanceof LiteralText) {
            String json = Text.Serializer.toJson(screen.getTitle());
            String title = RawMessageConvertor.convertToLegacy(json, true);
            String s = StringUtil.removeIf(
                    title,
                    c ->
                            (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')) &&
                                    (!(c >= 'а' && c <= 'я') && !(c >= 'А' && c <= 'Я')) &&
                                    (!(c >= '0' && c <= '9')) &&
                                    c != ' '
            ).trim();

            String resultPath = s.replace(" ", "-");
            int x = 0;
            while (menuFolder.resolve(resultPath + ".yml").toFile().exists()) {
                resultPath = s + "(" + ++x + ")";
            }
            return resultPath + ".yml";
        }
        return menuSaver.getRandomUUID() + ".yml";
    }

    private void saveToFile(String data, String fileName) {
        Path modsFolderPath = FabricLoader.getInstance().getGameDir().resolve("mods");

        Path folder = modsFolderPath.resolve("menus");
        folder.toFile().mkdirs();

        try {
            Files.write(folder.resolve(fileName), data.getBytes(StandardCharsets.UTF_8));

            CustomToast customToast = new CustomToast(
                    new LiteralText("./mods/menus/" + fileName),
                    new LiteralText("Сохранил!"),
                    new ItemStack(Items.LIME_DYE)
            );
            MinecraftClient.getInstance().getToastManager().add(customToast);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
