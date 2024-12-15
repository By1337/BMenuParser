package org.by1337.bmenuparser.inv.copy;

import com.google.common.base.Joiner;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.by1337.bmenuparser.inv.ScreenUtil;
import org.by1337.bmenuparser.text.RawMessageConvertor;

import java.util.*;

public class MenuSaver {
    private final HandledScreen<?> screen;
    private final Inventory inventory;
    private final Map<String, InvItem> items = new HashMap<>();
    private final List<Map<Integer, String>> frames = new ArrayList<>();
    private final AlphabetNameGenerator alphabetNameGenerator = new AlphabetNameGenerator();
    private final UUID randomUUID = UUID.randomUUID();

    public MenuSaver(HandledScreen<?> screen, List<ScreenData> frames, Inventory inventory) {
        this.screen = screen;
        this.inventory = inventory;

        Map<InvItem, String> itemToId = new HashMap<>();

        for (ScreenData frame : frames) {
            this.frames.add(toFrameData(frame.content, itemToId));
        }
        this.frames.removeIf(Map::isEmpty);


        Map<Integer, String> matrix = new HashMap<>();

        for (Map<Integer, String> map : this.frames) {

            for (Integer i : new ArrayList<>(map.keySet())) {
                if (map.get(i).equals(matrix.get(i))) {
                    map.remove(i);
                } else {
                    matrix.put(i, map.get(i));
                }
            }
        }
        this.frames.removeIf(Map::isEmpty);
    }

    public String saveCurrentFrame() {
        items.clear();
        Map<InvItem, List<Integer>> itemToSlots = new HashMap<>();

        ScreenData data = new ScreenData(inventory);
        for (Integer i : data.content.keySet()) {
            InvItem item = data.content.get(i);
            if (item != InvItem.AIR) {
                itemToSlots.computeIfAbsent(item, k -> new ArrayList<>()).add(i);
            }
        }
        AlphabetNameGenerator generator = new AlphabetNameGenerator();
        for (InvItem item : itemToSlots.keySet()) {
            item.slots = itemToSlots.get(item);
            items.put(generator.nextName(), item);
        }
        return saveNoAnimation(true);
    }

    public String saveNoAnimation(boolean setSlots) {
        StringBuilder sb = new StringBuilder();
        sb.append("# #######################################################\n");
        sb.append("# Конфиг меню для плагина https://github.com/By1337/BMenu\n");
        sb.append("# Автор мода и плагина https://github.com/By1337\n");
        sb.append("# Телеграм канал https://t.me/by_bdev\n");
        sb.append("# Дискорд сервер https://discord.gg/bdev\n");
        sb.append("# #######################################################\n");

        sb.append("id: auto_gen:").append(randomUUID).append("\n");
        sb.append("provider: default\n");

        sb.append("type: ").append(ScreenUtil.getBukkitType(screen)).append("\n");
        sb.append("size: ").append(inventory.size()).append("\n");


        sb.append("title: ")
                .append(quoteAndEscape(fromRaw(Text.Serializer.toJson(screen.getTitle())))).append("\n");

        sb.append("items:\n");
        for (String id : items.keySet()) {
            InvItem item = items.get(id);
            if (item.itemStack.isEmpty()) {
                continue;
            }
            sb.append("  ").append(id).append(":\n");
            boolean isBaseHead = false;
            NbtCompound tag = item.itemStack.getTag();
            if (setSlots && !item.slots.isEmpty()) {
                if (item.slots.size() == 1) {
                    sb.append("    slot: ").append(item.slots.get(0)).append("\n");
                } else {
                    sb.append("    slots: [ ").append(Joiner.on(", ").join(item.slots)).append(" ]\n");
                }
            }
            if (tag != null) {
                if (tag.contains("display")) {
                    if (tag.contains("display")) {
                        NbtCompound display = tag.getCompound("display");
                        if (display.contains("Name")) {
                            String s = display.getString("Name");
                            sb.append("    display_name: ").append(quoteAndEscape(fromRaw(s))).append("\n");
                        }
                        if (display.contains("Lore")) {
                            sb.append("    lore:\n");
                            NbtList lore = display.getList("Lore", NbtType.STRING);
                            for (NbtElement nbtElement : lore) {
                                sb.append("      - ").append(quoteAndEscape(fromRaw(nbtElement.asString()))).append("\n");
                            }
                        }
                        if (display.contains("color")) {
                            int rgb = display.getInt("color");
                            int BIT_MASK = 0xff;
                            sb.append("    color: '#").append(String.format(
                                    "%02X%02X%02X",
                                    rgb >> 16 & BIT_MASK,
                                    rgb >> 8 & BIT_MASK,
                                    rgb >> 0 & BIT_MASK
                            )).append("'\n");
                        }
                    }
                }
                if (tag.contains("CustomPotionColor")){
                    int rgb = tag.getInt("CustomPotionColor");
                    int BIT_MASK = 0xff;
                    sb.append("    color: '#").append(String.format(
                            "%02X%02X%02X",
                            rgb >> 16 & BIT_MASK,
                            rgb >> 8 & BIT_MASK,
                            rgb >> 0 & BIT_MASK
                    )).append("'\n");
                }
                if (tag.contains("Enchantments")) {
                    NbtList enchantments = tag.getList("Enchantments", NbtType.COMPOUND);
                    sb.append("    enchantments:\n");

                    for (NbtElement obj : enchantments) {
                        NbtCompound enchantment = (NbtCompound) obj;
                        sb.append("      - ").append(enchantment.getString("id").split(":")[1]).append(";").append(enchantment.getInt("lvl")).append("\n");
                    }
                }
                if (tag.contains("HideFlags")) {
                    int flags = tag.getInt("HideFlags");
                    sb.append("    item_flags:");

                    boolean has = false;

                    for (ItemFlag value : ItemFlag.values()) {
                        byte bitModifier = ((byte) (1 << value.ordinal()));
                        if ((flags & bitModifier) == bitModifier) {
                            if (!has) {
                                sb.append("\n");
                                has = true;
                            }
                            sb.append("      - ").append(value.name()).append("\n");
                        }
                    }
                    if (!has) {
                        sb.append(" [ ]");
                    }
                }
                if (tag.contains("SkullOwner")) {
                    NbtCompound skullOwner = tag.getCompound("SkullOwner");
                    if (skullOwner.contains("Properties")) {
                        NbtCompound properties = skullOwner.getCompound("Properties");
                        if (properties.contains("textures")) {
                            NbtList lore = properties.getList("textures", NbtType.COMPOUND);
                            if (!lore.isEmpty()) {
                                NbtCompound val = (NbtCompound) lore.get(0);
                                sb.append("    ").append("material: ").append(quoteAndEscape("basehead-" + val.getString("Value"))).append("\n");
                                isBaseHead = true;
                            }
                        }
                    }
                }
            }
            if (item.itemStack.getCount() != 1) {
                sb.append("    ").append("amount: ").append(item.itemStack.getCount()).append("\n");
            }
            if (!isBaseHead) {
                String material = Registry.ITEM.getKey(item.itemStack.getItem()).get().getValue().getPath().toUpperCase(Locale.ENGLISH);
                sb.append("    ").append("material: ").append(material).append("\n");
            }
            if (item.itemStack.getDamage() != 0) {
                sb.append("    ").append("damage: ").append(item.itemStack.getDamage()).append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public String save() {
        StringBuilder sb = new StringBuilder(saveNoAnimation(false));
        Set<String> airs = new HashSet<>();
        for (String id : items.keySet()) {
            if (items.get(id).itemStack.isEmpty()) {
                airs.add(id);
            }
        }

        Map<Integer, String> nonAirSlots = new HashMap<>();
        sb.append("animation:\n");
        int tick = 0;
        StringBuilder frameBuilder = new StringBuilder();
        for (Map<Integer, String> frame : frames) {
            frameBuilder.append("  - tick: ").append(tick++).append("\n");
            frameBuilder.append("    opcodes:\n");

            Map<String, List<Integer>> inverse = new HashMap<>();
            frame.forEach((k, v) -> inverse.computeIfAbsent(v, ignore -> new ArrayList<>()).add(k));

            boolean isEmpty = true;

            for (String item : inverse.keySet()) {
                List<Integer> slots = inverse.get(item);
                //if (slots.size() != 1) {
                    if (airs.contains(item)) {
                        slots.removeIf(s -> !nonAirSlots.containsKey(s));
                        if (slots.isEmpty()) continue;
                        frameBuilder.append("      - remove: ");
                        slots.forEach(nonAirSlots::remove);
                        isEmpty = false;
                    } else {
                        frameBuilder.append("      - set: ").append(item).append(" ");
                        isEmpty = false;
                    }
                    frameBuilder.append(Joiner.on(",").join(slots)).append("\n");
                //}
            }

//
//            for (Integer i : frame.keySet()) {
//                String item = frame.get(i);
//                if (airs.contains(item)) {
//                    if (nonAirSlots.containsKey(i)) {
//                        frameBuilder.append("      - remove: ").append(i).append("\n");
//                        nonAirSlots.remove(i);
//                        isEmpty = false;
//                    }
//
//                } else {
//                    frameBuilder.append("      - set: ").append(frame.get(i)).append(" ").append(i).append("\n");
//                    nonAirSlots.put(i, item);
//                    isEmpty = false;
//                }
//
//            }
            if (!isEmpty) {
                sb.append(frameBuilder);
            } else {
                tick--;
            }
            frameBuilder.setLength(0);
        }
        return sb.toString();
    }

    public UUID getRandomUUID() {
        return randomUUID;
    }

    public enum ItemFlag {
        HIDE_ENCHANTS,
        HIDE_ATTRIBUTES,
        HIDE_UNBREAKABLE,
        HIDE_DESTROYS,
        HIDE_PLACED_ON,
        HIDE_POTION_EFFECTS,
        HIDE_DYE;
    }

    public static String fromRaw(String json) {
        return RawMessageConvertor.convertToLegacy(json);
    }


    public static String quoteAndEscape(String raw) {
        StringBuilder result = new StringBuilder(" ");
        int quoteChar = 0;
        for (int i = 0; i < raw.length(); ++i) {
            char currentChar = raw.charAt(i);
            switch (currentChar) {
                case '\\':
                    result.append("\\\\");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\"':
                case '\'':
                    if (quoteChar == 0) {
                        quoteChar = currentChar == '\"' ? '\'' : '\"';
                    }
                    if (quoteChar == currentChar) {
                        result.append('\\');
                    }
                    result.append(currentChar);
                    break;
                default:
                    result.append(currentChar);
            }
        }
        if (quoteChar == 0) {
            quoteChar = '\"';
        }
        result.setCharAt(0, (char) quoteChar);
        result.append((char) quoteChar);
        return result.toString();
    }

    private Map<Integer, String> toFrameData(Map<Integer, InvItem> content, Map<InvItem, String> itemToId) {
        Map<Integer, String> frameData = new HashMap<>();
        for (Integer slot : content.keySet()) {
            InvItem item = content.get(slot);
            String itemId = itemToId.get(item);
            if (itemId == null) {
                itemId = alphabetNameGenerator.nextName();
                items.put(itemId, item);
                itemToId.put(item, itemId);
            }
            frameData.put(slot, itemId);
        }
        return frameData;
    }


    private static class AlphabetNameGenerator {
        private static final List<String> alphabet;

        static {
            alphabet = new ArrayList<>();
            alphabet.add("a");
            alphabet.add("b");
            alphabet.add("c");
            alphabet.add("d");
            alphabet.add("e");
            alphabet.add("f");
            alphabet.add("g");
            alphabet.add("h");
            alphabet.add("i");
            alphabet.add("j");
            alphabet.add("k");
            alphabet.add("l");
            alphabet.add("m");
            alphabet.add("n");
            alphabet.add("o");
            alphabet.add("p");
            alphabet.add("q");
            alphabet.add("r");
            alphabet.add("s");
            alphabet.add("t");
            alphabet.add("u");
            alphabet.add("v");
            alphabet.add("w");
            alphabet.add("x");
            alphabet.add("y");
            alphabet.add("z");
        }

        private static final Object lock = new Object();
        private final char[] symbols = Joiner.on("").join(alphabet).toCharArray();
        private long currentPosition = 0;

        public String nextName() {
            synchronized (lock) {
                StringBuilder combination = new StringBuilder();
                long position = currentPosition;
                for (int i = 0; i < symbols.length; i++) {
                    int charIndex = (int) ((position + i) % symbols.length);
                    combination.append(symbols[charIndex]);
                    position /= symbols.length;
                    if (position <= 0) break;
                }
                currentPosition++;
                return combination.toString();
            }
        }
    }
}
