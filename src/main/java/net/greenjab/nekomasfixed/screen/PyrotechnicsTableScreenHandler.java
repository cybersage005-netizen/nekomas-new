package net.greenjab.nekomasfixed.screen;

import net.greenjab.nekomasfixed.registry.registries.OtherRegistry;
import net.greenjab.nekomasfixed.registry.registries.ScreenHandlerRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class PyrotechnicsTableScreenHandler extends ScreenHandler {

    private final Inventory input = new SimpleInventory(7);
    private final Inventory output = new SimpleInventory(1);
    private final Property selectedPattern = Property.create();

    public PyrotechnicsTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }



    public PyrotechnicsTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ScreenHandlerRegistry.PYROTECHNICS_TABLE_HANDLER, syncId);
        this.addProperty(selectedPattern);
        this.addSlot(new Slot(this.input, 0, 8, 16));
        this.addSlot(new Slot(this.input, 1, 8, 35));
        this.addSlot(new Slot(this.input, 2, 8, 54));
        this.addSlot(new Slot(this.input, 3, 8, 73));
        this.addSlot(new Slot(this.input, 5, 29, 59));
        this.addSlot(new Slot(this.input, 6, 46, 59));

        this.addSlot(new Slot(this.output, 0, 143, 70) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 104 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 162));
        }
    }

    @Override
    public void sendContentUpdates(){
        if(input.getStack(2).isEmpty()){
            output.setStack(0, ItemStack.EMPTY);
        }
        super.sendContentUpdates();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (slotIndex < 5) {
                if (!this.insertItem(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.getItem() instanceof DyeItem || stack.isIn(OtherRegistry.ANCIENT_DYES)) {
                    if (!this.insertItem(stack, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.isOf(Items.FEATHER) || stack.isOf(Items.GOLD_NUGGET) || stack.isOf(Items.FIRE_CHARGE) || stack.isIn(ItemTags.SKULLS)) {
                    if (!this.insertItem(stack, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.isOf(Items.GUNPOWDER)) {
                    if (!this.insertItem(stack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if(stack.isOf(Items.DIAMOND)){
                    if (!this.insertItem(stack, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if(stack.isOf(Items.GLOWSTONE_DUST)){
                    if (!this.insertItem(stack, 5, 6, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return result;
    }

    private FireworkExplosionComponent.Type getPatternForItemStack(ItemStack stack){
        if(stack.isOf(Items.FEATHER))return FireworkExplosionComponent.Type.BURST;
        else if(stack.isOf(Items.GOLD_NUGGET)) return FireworkExplosionComponent.Type.STAR;
        else if(stack.isIn(ItemTags.SKULLS)) return FireworkExplosionComponent.Type.CREEPER;
        else if(stack.isOf(Items.FIRE_CHARGE))return FireworkExplosionComponent.Type.LARGE_BALL;
        else return FireworkExplosionComponent.Type.SMALL_BALL;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public int getSelectedPattern() {
        return this.selectedPattern.get();
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if(input.getStack(2).isEmpty()){
            output.setStack(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < 7){
            this.selectedPattern.set(id);
            this.updateOutput();
            return true;
        }

        if (id == 99) {
            this.sendContentUpdates();
            this.updateOutput();
            return true;
        }

        return false;
    }

    private void updateOutput() {
        ItemStack dye1 = input.getStack(0);
        ItemStack dye2 = input.getStack(1);
        ItemStack paper = input.getStack(2);
        ItemStack gunpowder = input.getStack(3);

        if (dye1.isEmpty() || paper.isEmpty() || gunpowder.isEmpty()) {
            output.setStack(0, ItemStack.EMPTY);
            this.sendContentUpdates();
            return;
        }

        ItemStack result = new ItemStack(Items.FIREWORK_STAR);

        int color1 = ((DyeItem)dye1.getItem()).getColor().getFireworkColor();
        int color2 = dye2.isEmpty() ? color1 : ((DyeItem)dye2.getItem()).getColor().getFireworkColor();

        IntArrayList colors = new IntArrayList();
        colors.add(color1);
        colors.add(color2);

        FireworkExplosionComponent explosion = new FireworkExplosionComponent(getPatternForItemStack(input.getStack(2)), colors, new IntArrayList(), input.getStack(5).isOf(Items.DIAMOND), input.getStack(6).isOf(Items.GLOWSTONE_DUST));

        result.set(DataComponentTypes.FIREWORK_EXPLOSION, explosion);
        if(!input.getStack(2).isEmpty()){output.setStack(0, result);}else{output.setStack(0, ItemStack.EMPTY);}

        this.sendContentUpdates();
    }

    @Override
    public void setStackInSlot(int slot, int rev, ItemStack stack) {
        super.setStackInSlot(slot, rev, stack);
        this.updateOutput();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.dropInventory(player, this.input);
    }
}
