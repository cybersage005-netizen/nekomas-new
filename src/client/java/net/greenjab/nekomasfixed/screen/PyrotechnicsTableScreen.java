package net.greenjab.nekomasfixed.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class PyrotechnicsTableScreen extends HandledScreen<PyrotechnicsTableScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of("nekomasfixed", "textures/gui/container/pyrotechnics_table.png");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/loom/scroller");
    private static final Identifier PATTERN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/loom/pattern_highlighted");

    private static final Identifier DYE_ICON = Identifier.ofVanilla("container/slot/dye");
    private static final Identifier DYE2_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/dye2");
    private static final Identifier BIG_BALL_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/big_ball_slot");
    private static final Identifier CREEPER_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/creeper_slot");
    private static final Identifier DIAMOND_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/diamond_slot");
    private static final Identifier DUST_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/dust_slot");
    private static final Identifier FEATHER_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/feather_slot");
    private static final Identifier SMALL_BALL_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/small_ball_slot");
    private static final Identifier STAR_ICON = Identifier.of("nekomasfixed", "container/pyrotechnics/star_slot");
    private static final Identifier PAPER_ICON = Identifier.ofVanilla("container/slot/banner_pattern");
    private static final Identifier GUNPOWDER_ICON = Identifier.ofVanilla("container/slot/redstone_dust");

    private static final Map<Integer, Identifier> TEXTURE_VALUES = new HashMap<>();
    private FireworkRocketEntity previewRocket;
    private boolean showPreview = false;

    static {
        TEXTURE_VALUES.put(0, SMALL_BALL_ICON);
        TEXTURE_VALUES.put(1, BIG_BALL_ICON);
        TEXTURE_VALUES.put(2, STAR_ICON);
        TEXTURE_VALUES.put(3, CREEPER_ICON);
        TEXTURE_VALUES.put(4, FEATHER_ICON);
        TEXTURE_VALUES.put(5, DIAMOND_ICON);
        TEXTURE_VALUES.put(6, DUST_ICON);
    }

    @Override
    protected void handledScreenTick() {
        if (this.showPreview && this.previewRocket != null) {
            this.previewRocket.tick();

            // loop instead of hard reset spam
            if (this.previewRocket.age > 40) {
                this.previewRocket.age = 0;
            }
        }

        this.handler.updateToClient();
    }

    private static int getSlotForStack(ItemStack stack){
        int returnVal = 0;
        if(stack.isIn(ItemTags.SKULLS))returnVal = 3;
        else if(stack.isOf(Items.FEATHER))returnVal = 4;
        else if(stack.isOf(Items.FIRE_CHARGE))returnVal = 1;
        else if(stack.isOf(Items.DIAMOND))returnVal = 5;
        else if(stack.isOf(Items.GLOWSTONE_DUST))returnVal = 6;
        else if(stack.isOf(Items.GOLD_NUGGET))returnVal = 2;
        return returnVal;
    }

    private boolean scrollbarClicked;

    public PyrotechnicsTableScreen(PyrotechnicsTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 186;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderMain(context, mouseX, mouseY, deltaTicks);
        this.renderCursorStack(context, mouseX, mouseY);
        this.renderLetGoTouchStack(context);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }



    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
        renderGhostIcon(context, 0, DYE_ICON, i + 8, j + 16);
        renderGhostIcon(context, 1, DYE2_ICON, i + 8, j + 35);
        renderGhostIcon(context, 2, PAPER_ICON, i + 8, j + 54);
        renderGhostIcon(context, 3, GUNPOWDER_ICON, i + 8, j + 73);

        int selected = this.handler.getSelectedPattern();
        ItemStack stack = this.handler.slots.get(2).getStack();
        int topX = i + 28;
        int topY = j + 20;
        int midY = topY + 16;




        for (int k = 0; k < 4; k++) {
            int bx = topX + k * 12;
            int by = topY;

            Identifier tex = TEXTURE_VALUES.get(k);
            if (tex != null && stack.isEmpty()) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, tex, bx+1 , by +1, 12, 12);
            }else{
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_VALUES.get(getSlotForStack(stack)), bx+1, by+1, 12, 12);
                if (selected == k ) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PATTERN_HIGHLIGHTED_TEXTURE, bx, by, 12, 12);
                }
                break;
            }

            if (selected == k ) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PATTERN_HIGHLIGHTED_TEXTURE, bx, by, 12, 12);
            }
        }

        Identifier midTex = TEXTURE_VALUES.get(4);
        if (midTex != null && stack.isEmpty()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, midTex, topX + 1, midY + 1, 12, 12);
        }

        if (selected == 4 && stack.isEmpty()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PATTERN_HIGHLIGHTED_TEXTURE, topX, midY, 14, 14);
        }


    }

    private void renderGhostIcon(DrawContext context, int slotId, Identifier icon, int x, int y) {
        Slot slot = this.handler.slots.get(slotId);
        if (!slot.hasStack()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, icon, x, y, 16, 16);
        }
    }



    @Override
    public boolean mouseClicked(Click click, boolean doubled) {

        int left = this.x;
        int top = this.y;
        int btnX = left + 86;
        int btnY = top + 80;
        int btnW = 16;
        int btnH = 16;

        if (click.x() >= btnX && click.x() < btnX + btnW && click.y() >= btnY && click.y() < btnY + btnH) {
            if (this.client != null && this.client.interactionManager != null) {
                this.client.interactionManager.clickButton(this.handler.syncId, 99);
            }

            this.showPreview = !this.showPreview;
            return true;
        }
        int topX = left + 30;
        int topY = top + 17;

        for (int k = 0; k < 4; k++) {
            double dx = click.x() - (topX + k * 12);
            double dy = click.y() - topY;

            if (dx >= 0 && dy >= 0 && dx < 12 && dy < 12) {
                if (this.client != null && this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(this.handler.syncId, k);
                }
                return true;
            }
        }

        int midX = topX;
        int midY = topY + 16;

        double dxMid = click.x() - midX;
        double dyMid = click.y() - midY;

        if (dxMid >= 0 && dyMid >= 0 && dxMid < 12 && dyMid < 12) {
            if (this.client != null && this.client.interactionManager != null) {
                this.client.interactionManager.clickButton(this.handler.syncId, 4);
            }
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        int totalPatterns = 20;
        int totalRows = MathHelper.ceilDiv(totalPatterns, 4);
        int hiddenRows = Math.max(0, totalRows - 4);

        if (this.scrollbarClicked && hiddenRows > 0) {
            int topY = this.y + 17;
            int bottomY = topY + 56;
            float scrollPosition = ((float) click.y() - (float) topY - 7.5F) / ((float) (bottomY - topY) - 15.0F);
            scrollPosition = MathHelper.clamp(scrollPosition, 0.0F, 1.0F);
            int visibleTopRow = (int) (scrollPosition * hiddenRows + 0.5F);
            visibleTopRow = MathHelper.clamp(visibleTopRow, 0, hiddenRows);
            return true;
        }

        return super.mouseDragged(click, offsetX, offsetY);
    }
}
