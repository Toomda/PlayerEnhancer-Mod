package toomda.playerenhancer.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import toomda.playerenhancer.logic.Inventories;
import toomda.playerenhancer.logic.TargetAugmentState;

import java.util.UUID;

public class AugmentScreenHandler extends ScreenHandler {
    private final Inventories.TargetInventory inv;
    private final UUID targetUuid;

    public static final int DEFENSE_SLOT = 0;
    public static final int ATTACK_SLOT  = 1;
    public static final int RADAR_SLOT   = 2;
    public static final int SPEECH_SLOT  = 3;
    public static final int MISC_SLOT    = 4;

    private static final int PLAYER_INV_TOP_Y = 90;
    private static final int PLAYER_INV_LEFT_X = 8;

    public AugmentScreenHandler(int syncId, PlayerInventory playerInv, UUID targetUuid) {
        super(AugmentScreenHandlerType.TYPE, syncId);
        this.targetUuid = targetUuid;
        this.inv = new Inventories.TargetInventory("client");
        setupSlots(playerInv);
    }
    public AugmentScreenHandler(int syncId, PlayerInventory playerInv,
                                Inventories.TargetInventory inv, UUID targetUuid) {
        super(AugmentScreenHandlerType.TYPE, syncId);
        this.inv = inv;
        this.targetUuid = targetUuid;
        setupSlots(playerInv);
    }

    private void setupSlots(PlayerInventory playerInv) {
        int[][] SLOT_POS = {
                {96, 10},  // Defense
                {120, 10}, // Attack
                {108, 32}, // Radar
                {96, 54},  // Speech
                {120, 54}  // ?
        };

        for (int i = 0; i < 5; i++) {
            final int idx = i;
            this.addSlot(new Slot(inv, i, SLOT_POS[i][0], SLOT_POS[i][1]) {
                @Override public boolean canInsert(ItemStack stack) {
                    return switch (idx) {
                        case DEFENSE_SLOT -> stack.isOf(Items.WOLF_ARMOR);
                        case ATTACK_SLOT  -> stack.isOf(Items.NETHER_BRICK);
                        default -> true;
                    };
                }
                @Override public int getMaxItemCount() { return 1; }
            });
        }

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInv, col + row * 9 + 9,
                        PLAYER_INV_LEFT_X + col * 18,
                        PLAYER_INV_TOP_Y + row * 18));
        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInv, col,
                    PLAYER_INV_LEFT_X + col * 18,
                    PLAYER_INV_TOP_Y + 58));
    }

    @Override
    public void onContentChanged(Inventory changed) {
        super.onContentChanged(changed);
        if (changed == inv) this.sendContentUpdates();
    }

    @Override public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!player.getWorld().isClient) {
            var sw = (net.minecraft.server.world.ServerWorld) player.getWorld();
            TargetAugmentState.get(sw).markDirty();
        }
    }

    @Override public boolean canUse(PlayerEntity player) { return true; }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack out = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasStack()) return out;

        ItemStack stack = slot.getStack();
        out = stack.copy();

        int containerSlots = 5;
        if (index < containerSlots) {
            if (!this.insertItem(stack, containerSlots, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!this.insertItem(stack, 0, containerSlots, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (!player.getWorld().isClient) {
            inv.markDirty();
        }

        return out;
    }

    public UUID getTargetUuid() { return targetUuid; }
}
