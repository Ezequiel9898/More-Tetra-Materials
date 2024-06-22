package net.hydraoc.mtetm.menus;

import net.hydraoc.mtetm.block.ModBlocks;
import net.hydraoc.mtetm.block.entity.HellforgeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class HellforgeMenu extends AbstractContainerMenu {
    public final HellforgeBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public HellforgeMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
    }

    public HellforgeMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.HELLFORGE_MENU.get(), pContainerId);
        checkContainerSize(inv, 6);
        blockEntity = ((HellforgeBlockEntity) entity);
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 31, 30)); //Input Slot
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 144, 56)); //Fuel Slot
            this.addSlot(new SlotItemHandler(iItemHandler, 2, 91,30)); //Output Slot
            this.addSlot(new SlotItemHandler(iItemHandler, 3, -91,-55)); //By-product Slot
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean hasFuel(){
        return data.get(1) > 0;
    }

    //Returns the crafting progress
    public int getBurnProgress() {
        int i = this.data.get(2);
        int j = this.data.get(3);
        return j != 0 && i != 0 ? i * 22 / j : 0;
    }

    //Returns the fuel bar
    public int getLitProgress() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }
        return this.data.get(0) * 36 / i;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INV_ROWS = 3;
    private static final int PLAYER_INV_COLUMNS = 9;
    private static final int PLAYER_INV_SLOTS = PLAYER_INV_COLUMNS * PLAYER_INV_ROWS;
    private static final int VANILLA_SLOTS = HOTBAR_SLOT_COUNT + PLAYER_INV_SLOTS;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INV_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOTS;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INV_SLOTS = 4;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOTS) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INV_FIRST_SLOT_INDEX, TE_INV_FIRST_SLOT_INDEX
                    + TE_INV_SLOTS, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INV_FIRST_SLOT_INDEX + TE_INV_SLOTS) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.HELLFORGE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 17, 85 + row * 17));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 17, 140));
        }
    }
}
