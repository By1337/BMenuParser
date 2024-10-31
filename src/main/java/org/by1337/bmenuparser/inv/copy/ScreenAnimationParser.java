package org.by1337.bmenuparser.inv.copy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import org.by1337.bmenuparser.toast.CustomToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScreenAnimationParser {
    private final Inventory inventory;
    private final Screen screen;
    private volatile boolean stop;
    private final FrameCreator frameCreator = new FrameCreator();

    public ScreenAnimationParser(Inventory inventory, Screen screen) {
        this.screen = screen;
        this.inventory = inventory;
        frameCreator.screenshot();
        new Thread(() -> {
            while (!stop) {
                if (MinecraftClient.getInstance().currentScreen != this.screen) {
                    CustomToast customToast = new CustomToast(
                            new LiteralText("Вы закрыли меню раньше :("),
                            new LiteralText("Неудача!"),
                            new ItemStack(Items.BARRIER)
                    );
                    MinecraftClient.getInstance().getToastManager().add(customToast);
                    return;
                }
                if (frameCreator.noDiff > 150) {
                    frameCreator.end();
                    stop = true;
                    CustomToast customToast = new CustomToast(
                            new LiteralText("Скопировано " + frameCreator.frames.size() + " кадров!"),
                            new LiteralText("Скопировано!"),
                            new ItemStack(Items.LIME_DYE)
                    );
                    MinecraftClient.getInstance().getToastManager().add(customToast);
                    return;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                frameCreator.screenshot();
            }
        }).start();
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }


    public boolean isStop() {
        return stop;
    }

    public FrameCreator getFrameCreator() {
        return frameCreator;
    }

    public class FrameCreator {
        private long lastFrame;
        private int noDiff;
        public List<ScreenData> frames = new ArrayList<>();
        private ScreenData last;

        public void end() {
            if (last != null) {
                frames.add(last);
            }
        }

        public void screenshot() {
            ScreenData current = new ScreenData(inventory);
            if (last == null) {
                last = current;
                frames.add(last);
            } else {
                Map<Integer, InvItem> diff = last.getDiff(current);
                if (diff.isEmpty()) {
                    noDiff++;
                } else {
                    last = current;
                    frames.add(last);
                }
            }
        }
    }
}
