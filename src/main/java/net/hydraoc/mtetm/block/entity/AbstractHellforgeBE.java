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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class AbstractHellforgeBE extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_FUEL = 1;
    protected static final int SLOT_RESULT = 2;
    public static final int DATA_LIT_TIME = 0;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    public NonNullList<ItemStack> items;
    public int litTime;
    public int litDuration = 40000;
    public int cookingProgress;
    public int cookingTotalTime;
    protected final ContainerData dataAccess;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed;
    public final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;
    LazyOptional<? extends IItemHandler>[] handlers;

    protected AbstractHellforgeBE(BlockEntityType<?> p_154991_, BlockPos p_154992_, BlockState p_154993_, RecipeType<? extends AbstractCookingRecipe> p_154994_) {
        super(p_154991_, p_154992_, p_154993_);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
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
                    case 1 -> AbstractHellforgeBE.this.litDuration = 40000;
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
        this.quickCheck = RecipeManager.createCheck(p_154994_);
        this.recipeType = p_154994_;
    }

    /** @deprecated */
    @Deprecated
    public static Map<Item, Integer> getFuel() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        add(map, (ItemLike)Items.LAVA_BUCKET, 20000);
        add(map, (ItemLike)Blocks.COAL_BLOCK, 16000);
        add(map, (ItemLike)Items.BLAZE_ROD, 2400);
        add(map, (ItemLike)Items.COAL, 1600);
        add(map, (ItemLike)Items.CHARCOAL, 1600);
        add(map, (TagKey)ItemTags.LOGS, 300);
        add(map, (TagKey)ItemTags.BAMBOO_BLOCKS, 300);
        add(map, (TagKey)ItemTags.PLANKS, 300);
        add(map, (ItemLike)Blocks.BAMBOO_MOSAIC, 300);
        add(map, (TagKey)ItemTags.WOODEN_STAIRS, 300);
        add(map, (ItemLike)Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        add(map, (TagKey)ItemTags.WOODEN_SLABS, 150);
        add(map, (ItemLike)Blocks.BAMBOO_MOSAIC_SLAB, 150);
        add(map, (TagKey)ItemTags.WOODEN_TRAPDOORS, 300);
        add(map, (TagKey)ItemTags.WOODEN_PRESSURE_PLATES, 300);
        add(map, (TagKey)ItemTags.WOODEN_FENCES, 300);
        add(map, (TagKey)ItemTags.FENCE_GATES, 300);
        add(map, (ItemLike)Blocks.NOTE_BLOCK, 300);
        add(map, (ItemLike)Blocks.BOOKSHELF, 300);
        add(map, (ItemLike)Blocks.CHISELED_BOOKSHELF, 300);
        add(map, (ItemLike)Blocks.LECTERN, 300);
        add(map, (ItemLike)Blocks.JUKEBOX, 300);
        add(map, (ItemLike)Blocks.CHEST, 300);
        add(map, (ItemLike)Blocks.TRAPPED_CHEST, 300);
        add(map, (ItemLike)Blocks.CRAFTING_TABLE, 300);
        add(map, (ItemLike)Blocks.DAYLIGHT_DETECTOR, 300);
        add(map, (TagKey)ItemTags.BANNERS, 300);
        add(map, (ItemLike)Items.BOW, 300);
        add(map, (ItemLike)Items.FISHING_ROD, 300);
        add(map, (ItemLike)Blocks.LADDER, 300);
        add(map, (TagKey)ItemTags.SIGNS, 200);
        add(map, (TagKey)ItemTags.HANGING_SIGNS, 800);
        add(map, (ItemLike)Items.WOODEN_SHOVEL, 200);
        add(map, (ItemLike)Items.WOODEN_SWORD, 200);
        add(map, (ItemLike)Items.WOODEN_HOE, 200);
        add(map, (ItemLike)Items.WOODEN_AXE, 200);
        add(map, (ItemLike)Items.WOODEN_PICKAXE, 200);
        add(map, (TagKey)ItemTags.WOODEN_DOORS, 200);
        add(map, (TagKey)ItemTags.BOATS, 1200);
        add(map, (TagKey)ItemTags.WOOL, 100);
        add(map, (TagKey)ItemTags.WOODEN_BUTTONS, 100);
        add(map, (ItemLike)Items.STICK, 100);
        add(map, (TagKey)ItemTags.SAPLINGS, 100);
        add(map, (ItemLike)Items.BOWL, 100);
        add(map, (TagKey)ItemTags.WOOL_CARPETS, 67);
        add(map, (ItemLike)Blocks.DRIED_KELP_BLOCK, 4001);
        add(map, (ItemLike)Items.CROSSBOW, 300);
        add(map, (ItemLike)Blocks.BAMBOO, 50);
        add(map, (ItemLike)Blocks.DEAD_BUSH, 100);
        add(map, (ItemLike)Blocks.SCAFFOLDING, 50);
        add(map, (ItemLike)Blocks.LOOM, 300);
        add(map, (ItemLike)Blocks.BARREL, 300);
        add(map, (ItemLike)Blocks.CARTOGRAPHY_TABLE, 300);
        add(map, (ItemLike)Blocks.FLETCHING_TABLE, 300);
        add(map, (ItemLike)Blocks.SMITHING_TABLE, 300);
        add(map, (ItemLike)Blocks.COMPOSTER, 300);
        add(map, (ItemLike)Blocks.AZALEA, 100);
        add(map, (ItemLike)Blocks.FLOWERING_AZALEA, 100);
        add(map, (ItemLike)Blocks.MANGROVE_ROOTS, 300);
        return map;
    }

    private static boolean isNeverAFurnaceFuel(Item p_58398_) {
        return p_58398_.builtInRegistryHolder().is(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private static void add(Map<Item, Integer> p_204303_, TagKey<Item> p_204304_, int p_204305_) {
        Iterator var3 = BuiltInRegistries.ITEM.getTagOrEmpty(p_204304_).iterator();

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

    public boolean isLit() {
        return this.litTime > 0;
    }

    public void load(CompoundTag p_155025_) {
        super.load(p_155025_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_155025_, this.items);
        this.litTime = p_155025_.getInt("BurnTime");
        this.cookingProgress = p_155025_.getInt("CookTime");
        this.cookingTotalTime = p_155025_.getInt("CookTimeTotal");
        this.litDuration = 40000;
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

    public static void fuelTicker(AbstractHellforgeBE blockEntity){
        if (blockEntity.cookingProgress > 0) {
            --blockEntity.litTime;
        }else{

        }
    }

    public static void serverTick(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, AbstractHellforgeBE p_155017_) {
        boolean flag = p_155017_.isLit();
        boolean flag1 = false;
        fuelTicker(p_155017_);

        ItemStack itemstack = (ItemStack)p_155017_.items.get(1);
        boolean flag2 = !((ItemStack)p_155017_.items.get(0)).isEmpty();
        boolean flag3 = !itemstack.isEmpty();
        if (!p_155017_.isLit() && (!flag3 || !flag2)) {
            if (!p_155017_.isLit() && p_155017_.cookingProgress > 0) {
                p_155017_.cookingProgress = Mth.clamp(p_155017_.cookingProgress - 2, 0, p_155017_.cookingTotalTime);
            }
        } else {
            Recipe recipe;
            if (flag2) {
                recipe = (Recipe)p_155017_.quickCheck.getRecipeFor(p_155017_, p_155014_).orElse(null);
            } else {
                recipe = null;
            }

            int i = p_155017_.getMaxStackSize();
            if ((p_155017_.litTime < p_155017_.litDuration-p_155017_.getBurnDuration(itemstack))&& p_155017_.canBurn(p_155014_.registryAccess(), recipe, p_155017_.items, i)) {
                    p_155017_.litTime = p_155017_.litTime + p_155017_.getBurnDuration(itemstack);
                    p_155017_.litDuration = 40000;
                if (p_155017_.isLit()) {
                    flag1 = true;
                    if (itemstack.hasCraftingRemainingItem()) {
                        p_155017_.items.set(1, itemstack.getCraftingRemainingItem());
                    } else if (flag3) {
                        Item item = itemstack.getItem();
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            p_155017_.items.set(1, itemstack.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (p_155017_.isLit() && p_155017_.canBurn(p_155014_.registryAccess(), recipe, p_155017_.items, i)) {
                ++p_155017_.cookingProgress;
                if (p_155017_.cookingProgress == p_155017_.cookingTotalTime) {
                    p_155017_.cookingProgress = 0;
                    p_155017_.cookingTotalTime = getTotalCookTime(p_155014_, p_155017_);
                    if (p_155017_.burn(p_155014_.registryAccess(), recipe, p_155017_.items, i)) {
                        p_155017_.setRecipeUsed(recipe);
                    }

                    flag1 = true;
                }
            } else {
                p_155017_.cookingProgress = 0;
            }
        }

        if (flag != p_155017_.isLit()) {
            flag1 = true;
            p_155016_ = (BlockState)p_155016_.setValue(AbstractFurnaceBlock.LIT, p_155017_.isLit());
            p_155014_.setBlock(p_155015_, p_155016_, 3);
        }

        if (flag1) {
            setChanged(p_155014_, p_155015_, p_155016_);
        }

    }

    public boolean canBurn(RegistryAccess p_266924_, @Nullable Recipe<AbstractHellforgeBE> p_155006_, NonNullList<ItemStack> p_155007_, int p_155008_) {
        if (!((ItemStack)p_155007_.get(0)).isEmpty() && p_155006_ != null) {
            ItemStack itemstack = p_155006_.assemble(this, p_266924_);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = (ItemStack)p_155007_.get(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItem(itemstack1, itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= p_155008_ && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    public boolean burn(RegistryAccess regAccess, @Nullable Recipe<AbstractHellforgeBE> recipe, NonNullList<ItemStack> list, int p_267157_) {
        if (this.canBurn(regAccess, recipe, list, p_267157_)) {
            ItemStack itemstack = (ItemStack)list.get(0);
            ItemStack itemstack1 = recipe.assemble(this, regAccess);
            ItemStack itemstack2 = (ItemStack)list.get(2);
            if (itemstack2.isEmpty()) {
                list.set(2, itemstack1.copy());
            } else if (itemstack2.is(itemstack1.getItem())) {
                itemstack2.grow(itemstack1.getCount());
            }

            itemstack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    public int getBurnDuration(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        } else {
            Item item = itemStack.getItem();
            return ForgeHooks.getBurnTime(itemStack, this.recipeType);
        }
    }

    public static int getTotalCookTime(Level level, AbstractHellforgeBE blockEntity) {
        return (Integer)blockEntity.quickCheck.getRecipeFor(blockEntity, level).map(AbstractCookingRecipe::getCookingTime).orElse(200);
    }

    public static boolean isFuel(ItemStack itemStack) {
        return ForgeHooks.getBurnTime(itemStack, (RecipeType)null) > 0;
    }

    public int[] getSlotsForFace(Direction dir) {
        if (dir == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return dir == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int p_58336_, ItemStack itemStack, @Nullable Direction dir) {
        return this.canPlaceItem(p_58336_, itemStack);
    }

    public boolean canTakeItemThroughFace(int p_58392_, ItemStack itemStack, Direction dir) {
        if (dir == Direction.DOWN && p_58392_ == 1) {
            return itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
        } else {
            return true;
        }
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
        return Container.stillValidBlockEntity(this, p_58340_);
    }

    public boolean canPlaceItem(int p_58389_, ItemStack p_58390_) {
        if (p_58389_ == 2) {
            return false;
        } else if (p_58389_ != 1) {
            return true;
        } else {
            ItemStack itemstack = (ItemStack)this.items.get(1);
            return ForgeHooks.getBurnTime(p_58390_, this.recipeType) > 0 || p_58390_.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
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
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(p_155004_.serverLevel(), p_155004_.position());
        p_155004_.awardRecipes(list);
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            Recipe<?> recipe = (Recipe)var3.next();
            if (recipe != null) {
                p_155004_.triggerRecipeCrafted(recipe, this.items);
            }
        }

        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel p_154996_, Vec3 p_154997_) {
        List<Recipe<?>> list = Lists.newArrayList();
        ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

        while(var4.hasNext()) {
            Object2IntMap.Entry<ResourceLocation> entry = (Object2IntMap.Entry)var4.next();
            p_154996_.getRecipeManager().byKey((ResourceLocation)entry.getKey()).ifPresent((p_155023_) -> {
                list.add(p_155023_);
                createExperience(p_154996_, p_154997_, entry.getIntValue(), ((AbstractCookingRecipe)p_155023_).getExperience());
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

