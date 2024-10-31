package org.by1337.bmenuparser.inv.copy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvItem {
    public static final InvItem AIR = new InvItem(new NbtCompound(), new ItemStack(net.minecraft.item.Items.AIR));
    public final NbtCompound nbt;
    public final ItemStack itemStack;
    private final String data;
    public List<Integer> slots = new ArrayList<>();

    public InvItem(NbtCompound nbt, ItemStack itemStack) {
        this.nbt = nbt;
        this.data = nbt.toString();
        this.itemStack = itemStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvItem item = (InvItem) o;
        return Objects.equals(data, item.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
