package toomda.playerenhancer.logic;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.Map;

public class TargetAugmentState extends PersistentState {
    private final Map<String, Inventories.TargetInventory> byPlayer = new Object2ObjectOpenHashMap<>();

    private static final PersistentState.Type<TargetAugmentState> TYPE =
            new PersistentState.Type<>(
                    TargetAugmentState::new,
                    TargetAugmentState::fromNbt,
                    null
            );

    public static TargetAugmentState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(TYPE, "playeraugment_state");
    }

    public static TargetAugmentState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        TargetAugmentState s = new TargetAugmentState();
        NbtList list = nbt.getList("entries", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            var inv = Inventories.TargetInventory.fromNbt(list.getCompound(i), registries);
            s.byPlayer.put(inv.getOwnerUuid(), inv);
        }
        return s;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtList list = new NbtList();
        for (var inv : byPlayer.values()) list.add(inv.toNbt(registries));
        nbt.put("entries", list);
        return nbt;
    }

    public Inventories.TargetInventory getOrCreate(String uuid) {
        return byPlayer.computeIfAbsent(uuid, id -> {
            var inv = new Inventories.TargetInventory(id);
            inv.setPersistDirty(this::markDirty);
            return inv;
        });
    }
}
