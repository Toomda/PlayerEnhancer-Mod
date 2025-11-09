package toomda.playerenhancer.logic;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

public final class Inventories {
    public static final int SLOT_COUNT = 5;

    public static class TargetInventory extends SimpleInventory {
        private final String ownerUuid;
        private Runnable persistDirty;

        public TargetInventory(String ownerUuid) {
            super(SLOT_COUNT);
            this.ownerUuid = ownerUuid;
        }

        public String getOwnerUuid() { return ownerUuid; }

        public void setPersistDirty(@org.jetbrains.annotations.Nullable Runnable r) {
            this.persistDirty = r;
        }

        @Override
        public void markDirty() {
            super.markDirty();
            if (persistDirty != null) {
                persistDirty.run();
            }
        }

        public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("owner", ownerUuid);

            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
            for (int i = 0; i < this.size(); i++) stacks.set(i, this.getStack(i));

            NbtCompound items = new NbtCompound();
            net.minecraft.inventory.Inventories.writeNbt(items, stacks, registries);
            nbt.put("items", items);
            return nbt;
        }

        public static TargetInventory fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
            String owner = nbt.getString("owner");
            TargetInventory inv = new TargetInventory(owner);

            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(SLOT_COUNT, ItemStack.EMPTY);
            net.minecraft.inventory.Inventories.readNbt(nbt.getCompound("items"), stacks, registries);
            for (int i = 0; i < SLOT_COUNT; i++) inv.setStack(i, stacks.get(i));
            inv.markDirty();
            return inv;
        }
    }

    private Inventories() {}
}
