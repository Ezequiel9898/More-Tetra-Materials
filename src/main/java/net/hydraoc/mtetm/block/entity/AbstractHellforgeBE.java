package net.hydraoc.mtetm.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import net.hydraoc.mtetm.recipe.HellSmeltingRecipe;
import se.mickelus.tetra.items.cell.ThermalCellItem;

//This code is edited from the AbstractFurnaceBlockEntity code from vanilla.
public abstract class AbstractHellforgeBE extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_FUEL = 1;
    protected static final int SLOT_RESULT = 2;
    public static final int DATA_LIT_TIME = 0;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{3, 2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    private final RecipeType<? extends HellSmeltingRecipe> primaryRecipeType;
    private final RecipeType<? extends AbstractCookingRecipe> secondRecipeType;
    public NonNullList<ItemStack> items;
    public int litTime;
    public int litDuration = 51200;
    public int cookingProgress;
    public int cookingTotalTime;
    protected final ContainerData dataAccess;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed;
    public final RecipeManager.CachedCheck<Container, ? extends HellSmeltingRecipe> primaryQuickCheck;
    public final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> secondaryQuickCheck;
    LazyOptional<? extends IItemHandler>[] handlers;

    protected AbstractHellforgeBE(BlockEntityType<?> p_154991_, BlockPos p_154992_, BlockState p_154993_, RecipeType<? extends HellSmeltingRecipe> recipeType1, RecipeType<? extends AbstractCookingRecipe> recipeType2) {
        super(p_154991_, p_154992_, p_154993_);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            public int get(int p_58431_) {
                switch (p_58431_) {
                    case 0 -> {return AbstractHellforgeBE.this.litTime;}
                    case 1 -> {return AbstractHellforgeBE.this.litDuration;}
                    case 2 -> {return AbstractHellforgeBE.this.cookingProgress;}
                    case 3 -> {return AbstractHellforgeBE.this.cookingTotalTime;}
                    default -> {return 0;}
                }
            }

            public void set(int p_58433_, int p_58434_) {
                switch (p_58433_) {
                    case 0 -> AbstractHellforgeBE.this.litTime = p_58434_;
                    case 1 -> AbstractHellforgeBE.this.litDuration = 51200;
                    case 2 -> AbstractHellforgeBE.this.cookingProgress = p_58434_;
                    case 3 -> AbstractHellforgeBE.this.cookingTotalTime = p_58434_;
                }

            }

            public int getCount() {
                return 4;
            }
        };
        this.recipesUsed = new Object2IntOpenHashMap();
        this.handlers = SidedInvWrapper.create(this, new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH});
        this.primaryQuickCheck = RecipeManager.createCheck(recipeType1);
        this.secondaryQuickCheck = RecipeManager.createCheck(recipeType2);
        this.primaryRecipeType = recipeType1;
        this.secondRecipeType = recipeType2;
    }

    /** @deprecated */
    @Deprecated
    public static Map<Item, Integer> getFuel() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        ItemStack cell = new ItemStack(ThermalCellItem.instance.get());
        add(map, ThermalCellItem.instance.get(), ThermalCellItem.getCharge(cell)*200);
        return map;
    }

    private static boolean isNeverAFurnaceFuel(Item p_58398_) {
        return p_58398_.builtInRegistryHolder().is(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private static void add(Map<Item, Integer> p_204303_, TagKey<Item> p_204304_, int p_204305_) {
        Iterator var3 = Registry.ITEM.getTagOrEmpty(p_204304_).iterator();

        while(var3.hasNext()) {
            Holder<Item> holder = (Holder)var3.next();
            if (!isNeverAFurnaceFuel((Item)holder.value())) {
                p_204303_.put((Item)holder.value(), p_204305_);
            }
        }

    }

    private static void add(Map<Item, Integer> p_58375_, ItemLike p_58376_, int p_58377_) {
        Item item = p_58376_.asItem();
        if (isNeverAFurnaceFuel(item)) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item.getName((ItemStack)null).getString() + " a furnace fuel. That will not work!"));
            }
        } else {
            p_58375_.put(item, p_58377_);
        }

    }

    public void load(CompoundTag p_155025_) {
        super.load(p_155025_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_155025_, this.items);
        this.litTime = p_155025_.getInt("BurnTime");
        this.cookingProgress = p_155025_.getInt("CookTime");
        this.cookingTotalTime = p_155025_.getInt("CookTimeTotal");
        this.litDuration = 51200;
        CompoundTag compoundtag = p_155025_.getCompound("RecipesUsed");
        Iterator var3 = compoundtag.getAllKeys().iterator();

        while(var3.hasNext()) {
            String s = (String)var3.next();
            this.recipesUsed.put(new ResourceLocation(s), compoundtag.getInt(s));
        }

    }

    protected void saveAdditional(CompoundTag p_187452_) {
        super.saveAdditional(p_187452_);
        p_187452_.putInt("BurnTime", this.litTime);
        p_187452_.putInt("CookTime", this.cookingProgress);
        p_187452_.putInt("CookTimeTotal", this.cookingTotalTime);
        ContainerHelper.saveAllItems(p_187452_, this.items);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((p_187449_, p_187450_) -> {
            compoundtag.putInt(p_187449_.toString(), p_187450_);
        });
        p_187452_.put("RecipesUsed", compoundtag);
    }

    public boolean isLit(Level level, AbstractHellforgeBE blockEntity) {
        if(litTime > 0 && !blockEntity.getItem(0).isEmpty() &&
                ((Recipe)blockEntity.primaryQuickCheck.getRecipeFor(blockEntity, level).orElse(null) != null || (Recipe)blockEntity.secondaryQuickCheck.getRecipeFor(blockEntity, level).orElse(null) != null)){
            return true;
        }else{
            return false;
        }
    }
    //Moved the fuel ticker outside of the serverTick method so I could tinker with it.
    public static void fuelTicker(AbstractHellforgeBE blockEntity){
        if (blockEntity.cookingProgress > 0) {
            --blockEntity.litTime;
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState blockState, AbstractHellforgeBE blockEntity) {
        double adjustX = (double)pos.getX() + 0.5;
        double adjustY = (double)pos.getY()+0.5;
        double adjustZ = (double)pos.getZ() + 0.5;
        boolean flag = blockEntity.isLit(level, blockEntity);
        boolean flag1 = false;
        fuelTicker(blockEntity);

        ItemStack itemstack = (ItemStack)blockEntity.items.get(1);
        boolean flag2 = !((ItemStack)blockEntity.items.get(0)).isEmpty();
        boolean flag3 = !itemstack.isEmpty();

        if (blockEntity.isLit(level, blockEntity)) {
            flag1 = true;
            blockState = (BlockState)blockState.setValue(AbstractFurnaceBlock.LIT, flag1);
            level.setBlock(pos, blockState, 10);
            setChanged(level, pos, blockState);
        }else{
            flag1 = false;
            blockState = (BlockState)blockState.setValue(AbstractFurnaceBlock.LIT, flag1);
            level.setBlock(pos, blockState, 10);
            setChanged(level, pos, blockState);
        }

        if (blockEntity.litTime < blockEntity.litDuration-ThermalCellItem.getCharge(itemstack) && ThermalCellItem.getCharge(itemstack) != 1){
            blockEntity.litTime = blockEntity.litTime + ThermalCellItem.getCharge(itemstack)*200;
            blockEntity.litDuration = 51200;
            ThermalCellItem.drainCharge(itemstack, ThermalCellItem.getCharge(itemstack));
            level.playLocalSound(adjustX, adjustY, adjustZ, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        if (!blockEntity.isLit(level, blockEntity) && (!flag3 || !flag2)) {
            if (!blockEntity.isLit(level, blockEntity) && blockEntity.cookingProgress > 0) {
                blockEntity.cookingProgress = Mth.clamp(blockEntity.cookingProgress - 2, 0, blockEntity.cookingTotalTime);
            }
        } else {
            Recipe recipe;
            if (flag2) {
                recipe = (Recipe)blockEntity.primaryQuickCheck.getRecipeFor(blockEntity, level).orElse(null);
                if(recipe == null){
                    recipe = (Recipe)blockEntity.secondaryQuickCheck.getRecipeFor(blockEntity, level).orElse(null);
                }
            } else {
                recipe = null;
            }

            int i = blockEntity.getMaxStackSize();

            if (blockEntity.isLit(level, blockEntity) && blockEntity.canBurn(recipe, blockEntity.items, i)) {
                ++blockEntity.cookingProgress;
                if (blockEntity.cookingProgress == blockEntity.cookingTotalTime) {
                    blockEntity.cookingProgress = 0;
                    blockEntity.cookingTotalTime = getTotalCookTime(level, blockEntity);
                    if (blockEntity.burn(recipe, blockEntity.items, i)) {
                        blockEntity.setRecipeUsed(recipe);
                    }
                    flag1 = true;
                }
            } else {
                blockEntity.cookingProgress = 0;
            }
        }

    }

    private boolean canBurn(@Nullable Recipe<AbstractHellforgeBE> recipe, NonNullList<ItemStack> itemStacks, int p_155008_) {
        if (!((ItemStack)itemStacks.get(0)).isEmpty()) {
            if(recipe == null){
                return false;
            }
            ItemStack itemstack = recipe.assemble(this);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStackOutput = (ItemStack)itemStacks.get(2);
                ItemStack itemStackByProduct = (ItemStack)itemStacks.get(3);
                if (itemStackOutput.isEmpty() && itemStackByProduct.isEmpty()) {
                    return true;
                } else if (!itemStackOutput.sameItem(itemstack)) {
                    return false;
                } else if (itemStackOutput.getCount() + itemstack.getCount() <= p_155008_ && itemStackOutput.getCount() + itemstack.getCount() <= itemStackOutput.getMaxStackSize()) {
                    return true;
                } else {
                    return itemStackOutput.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    private boolean burn(@Nullable Recipe<AbstractHellforgeBE> recipe, NonNullList<ItemStack> itemStacks, int p_155029_) {
        if (recipe != null && this.canBurn(recipe, itemStacks, p_155029_)) {
            ItemStack itemstack = (ItemStack)itemStacks.get(0);
            ItemStack itemstack1 = recipe.assemble(this);
            ItemStack itemstack2 = (ItemStack)itemStacks.get(2);
            if (itemstack2.isEmpty()) {
                itemStacks.set(2, itemstack1.copy());
            } else if (itemstack2.is(itemstack1.getItem())) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (itemstack.is(Blocks.WET_SPONGE.asItem()) && !((ItemStack)itemStacks.get(1)).isEmpty() && ((ItemStack)itemStacks.get(1)).is(Items.BUCKET)) {
                itemStacks.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    public static int getTotalCookTime(Level level, AbstractHellforgeBE blockEntity) {
        return (Integer)blockEntity.primaryQuickCheck.getRecipeFor(blockEntity, level).map(HellSmeltingRecipe::getCookingTime).orElse(50);
    }

    public static boolean isFuel(ItemStack itemStack) {
        ItemStack cell = new ItemStack(ThermalCellItem.instance.get());
        return ThermalCellItem.getCharge(cell)*10000 > 0;
    }

    public int[] getSlotsForFace(Direction dir) {
        if (dir == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return dir == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, itemStack);
    }

    public boolean canTakeItemThroughFace(int p_58392_, ItemStack itemStack, Direction dir) {
        if (dir == Direction.DOWN && p_58392_ == 1) {
            if(itemStack.getDamageValue()==128) {
                return true;
            }
        } else {
            return true;
        }
        System.out.println(itemStack.getDamageValue());
        return false;
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        Iterator var1 = this.items.iterator();

        ItemStack itemstack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemstack = (ItemStack)var1.next();
        } while(itemstack.isEmpty());

        return false;
    }

    public ItemStack getItem(int p_58328_) {
        return (ItemStack)this.items.get(p_58328_);
    }

    public ItemStack removeItem(int p_58330_, int p_58331_) {
        return ContainerHelper.removeItem(this.items, p_58330_, p_58331_);
    }

    public ItemStack removeItemNoUpdate(int p_58387_) {
        return ContainerHelper.takeItem(this.items, p_58387_);
    }

    public void setItem(int p_58333_, ItemStack p_58334_) {
        ItemStack itemstack = (ItemStack)this.items.get(p_58333_);
        boolean flag = !p_58334_.isEmpty() && ItemStack.isSameItemSameTags(itemstack, p_58334_);
        this.items.set(p_58333_, p_58334_);
        if (p_58334_.getCount() > this.getMaxStackSize()) {
            p_58334_.setCount(this.getMaxStackSize());
        }

        if (p_58333_ == 0 && !flag) {
            this.cookingTotalTime = getTotalCookTime(this.level, this);
            this.cookingProgress = 0;
            this.setChanged();
        }

    }

    public boolean stillValid(Player p_58340_) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return p_58340_.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    public boolean canPlaceItem(int p_58389_, ItemStack p_58390_) {
        if (p_58389_ == 2) {
            return false;
        } else if (p_58389_ != 1) {
            return !(p_58390_.getItem() instanceof ThermalCellItem);
        } else {
            ItemStack itemstack = (ItemStack)this.items.get(1);
            return p_58390_.getItem() instanceof ThermalCellItem;
        }
    }

    public void clearContent() {
        this.items.clear();
    }

    public void setRecipeUsed(@Nullable Recipe<?> p_58345_) {
        if (p_58345_ != null) {
            ResourceLocation resourcelocation = p_58345_.getId();
            this.recipesUsed.addTo(resourcelocation, 1);
        }

    }

    @Nullable
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    public void awardUsedRecipes(Player p_58396_, List<ItemStack> p_282202_) {
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer p_155004_) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(p_155004_.getLevel(), p_155004_.position());
        p_155004_.awardRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel p_154996_, Vec3 p_154997_) {
        List<Recipe<?>> list = Lists.newArrayList();
        ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

        while(var4.hasNext()) {
            Object2IntMap.Entry<ResourceLocation> entry = (Object2IntMap.Entry)var4.next();
            p_154996_.getRecipeManager().byKey((ResourceLocation)entry.getKey()).ifPresent((p_155023_) -> {
                list.add(p_155023_);
                try{
                    createExperience(p_154996_, p_154997_, entry.getIntValue(), ((HellSmeltingRecipe)p_155023_).getExperience());}
                catch (java.lang.ClassCastException e1){
                    try{
                        createExperience(p_154996_, p_154997_, entry.getIntValue(), ((AbstractCookingRecipe)p_155023_).getExperience());
                    }catch (java.lang.ClassCastException e2){
                        System.out.println("How did this get past both recipe checks?????");
                        e2.printStackTrace();
                    }
                }
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel p_154999_, Vec3 p_155000_, int p_155001_, float p_155002_) {
        int i = Mth.floor((float)p_155001_ * p_155002_);
        float f = Mth.frac((float)p_155001_ * p_155002_);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrb.award(p_154999_, p_155000_, i);
    }

    public void fillStackedContents(StackedContents p_58342_) {
        Iterator var2 = this.items.iterator();

        while(var2.hasNext()) {
            ItemStack itemstack = (ItemStack)var2.next();
            p_58342_.accountStack(itemstack);
        }

    }

    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.UP) {
                return this.handlers[0].cast();
            } else {
                return facing == Direction.DOWN ? this.handlers[1].cast() : this.handlers[2].cast();
            }
        } else {
            return super.getCapability(capability, facing);
        }
    }

    public void invalidateCaps() {
        super.invalidateCaps();

        for(int x = 0; x < this.handlers.length; ++x) {
            this.handlers[x].invalidate();
        }
    }

    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this, new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH});
    }
}
