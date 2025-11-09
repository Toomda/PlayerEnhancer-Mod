// AugmentScreen.java
package toomda.playerenhancer.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import toomda.playerenhancer.PlayerEnhancer;

public class AugmentScreen extends HandledScreen<AugmentScreenHandler> {
    private static final Identifier TEX =
            Identifier.of(PlayerEnhancer.MOD_ID, "textures/gui/augment.png");

    private static final int BOX_LEFT   = 44;
    private static final int BOX_TOP    = 20;
    private static final int BOX_RIGHT  = 78;
    private static final int BOX_BOTTOM = 59;
    private static final int BOX_PADDING = 4;
    private static final int BOX_EXTRA_TOP    = 2;
    private static final int BOX_EXTRA_BOTTOM = 6;
    private static final int BOX_OFFSET_Y     = -1;

    private static final boolean LOCK_HEAD_FORWARD = true;
    private static final int CLIP_EXPAND = 6;

    private static final boolean FOLLOW_MOUSE  = true;
    private static final float   FOLLOW_FACTOR = 0.70f;

    private static final float GUI_SCALE = 1.15f;

    public AugmentScreen(AugmentScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 172;
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        ctx.drawTexture(TEX, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // suppress default "Inventory" label
    }

    private int scaledOriginX() {
        int scaledW = Math.round(this.backgroundWidth * GUI_SCALE);
        return (this.width - scaledW) / 2;
    }

    private int scaledOriginY() {
        int scaledH = Math.round(this.backgroundHeight * GUI_SCALE);
        return (this.height - scaledH) / 2;
    }

    private int toScaledMouseX(int mouseX, int originX) {
        return (int) ((mouseX - originX) / GUI_SCALE) + this.x;
    }

    private int toScaledMouseY(int mouseY, int originY) {
        return (int) ((mouseY - originY) / GUI_SCALE) + this.y;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        int originX = scaledOriginX();
        int originY = scaledOriginY();

        int sMouseX = toScaledMouseX(mouseX, originX);
        int sMouseY = toScaledMouseY(mouseY, originY);

        var matrices = ctx.getMatrices();
        matrices.push();
        matrices.translate(originX, originY, 0);
        matrices.scale(GUI_SCALE, GUI_SCALE, 1.0f);
        matrices.translate(-this.x, -this.y, 0);

        super.render(ctx, sMouseX, sMouseY, delta);

        var client = MinecraftClient.getInstance();
        var world = client.world;
        if (world != null) {
            PlayerEntity target = getClientPlayerByUuid(client, this.handler.getTargetUuid());
            if (target != null) {
                int vLeft   = this.x + BOX_LEFT   + (BOX_PADDING + 2);
                int vTop    = this.y + BOX_TOP    - BOX_EXTRA_TOP    + BOX_OFFSET_Y;
                int vRight  = this.x + BOX_RIGHT  - (BOX_PADDING + 2);
                int vBottom = this.y + BOX_BOTTOM + BOX_EXTRA_BOTTOM + BOX_OFFSET_Y;

                int cLeft   = vLeft   - CLIP_EXPAND;
                int cTop    = vTop    - CLIP_EXPAND;
                int cRight  = vRight  + CLIP_EXPAND;
                int cBottom = vBottom + CLIP_EXPAND;

                int innerW = Math.max(1, vRight - vLeft);
                int innerH = Math.max(1, vBottom - vTop);
                int base   = Math.min(innerW, innerH);
                int scale  = Math.max(8, (int)(base * 0.82f));

                float cx = vLeft + innerW / 2f;
                float cy = vTop  + innerH / 2f;
                float mx = FOLLOW_MOUSE ? (cx + (sMouseX - cx) * FOLLOW_FACTOR) : cx;
                float my = FOLLOW_MOUSE ? (cy + (sMouseY - cy) * FOLLOW_FACTOR) : cy;

                int sLeft   = Math.round(originX + (cLeft   - this.x) * GUI_SCALE);
                int sTop    = Math.round(originY + (cTop    - this.y) * GUI_SCALE);
                int sRight  = Math.round(originX + (cRight  - this.x) * GUI_SCALE);
                int sBottom = Math.round(originY + (cBottom - this.y) * GUI_SCALE);

                var sb = world.getScoreboard();
                var team = sb.getTeam("pe_hide_nametags");
                if (team == null) {
                    team = sb.addTeam("pe_hide_nametags");
                    team.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.NEVER);
                }
                String holder = target.getGameProfile().getName();
                boolean addedTemp = false;
                var current = sb.getScoreHolderTeam(holder);
                if ((current == null || current.getPlayerList().isEmpty()) || current != team) {
                    sb.addScoreHolderToTeam(holder, team);
                    addedTemp = true;
                }

                float oldBodyYaw = target.bodyYaw;
                float oldYaw     = target.getYaw();
                float oldPitch   = target.getPitch();
                float oldHeadYaw = target.headYaw;
                float oldPrevHead= target.prevHeadYaw;

                try {
                    if (!FOLLOW_MOUSE && LOCK_HEAD_FORWARD) {
                        target.bodyYaw = 0.0f;
                        target.setYaw(0.0f);
                        target.setPitch(0.0f);
                        target.headYaw = 0.0f;
                        target.prevHeadYaw = 0.0f;
                    }

                    ctx.enableScissor(sLeft, sTop, sRight, sBottom);

                    InventoryScreen.drawEntity(
                            ctx,
                            cLeft, cTop, cRight, cBottom,
                            scale,
                            0.0f,
                            mx, my,
                            target
                    );

                    ctx.disableScissor();
                } finally {
                    target.bodyYaw = oldBodyYaw;
                    target.setYaw(oldYaw);
                    target.setPitch(oldPitch);
                    target.headYaw = oldHeadYaw;
                    target.prevHeadYaw = oldPrevHead;
                    if (addedTemp) {
                        sb.removeScoreHolderFromTeam(holder, team);
                    }
                }
            }
        }

        this.drawMouseoverTooltip(ctx, sMouseX, sMouseY);
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int originX = scaledOriginX();
        int originY = scaledOriginY();
        int sX = toScaledMouseX((int) mouseX, originX);
        int sY = toScaledMouseY((int) mouseY, originY);
        return super.mouseClicked(sX, sY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int originX = scaledOriginX();
        int originY = scaledOriginY();
        int sX = toScaledMouseX((int) mouseX, originX);
        int sY = toScaledMouseY((int) mouseY, originY);
        return super.mouseReleased(sX, sY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        int originX = scaledOriginX();
        int originY = scaledOriginY();
        int sX = toScaledMouseX((int) mouseX, originX);
        int sY = toScaledMouseY((int) mouseY, originY);
        return super.mouseDragged(sX, sY, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horiz, double vert) {
        int originX = scaledOriginX();
        int originY = scaledOriginY();
        int sX = toScaledMouseX((int) mouseX, originX);
        int sY = toScaledMouseY((int) mouseY, originY);
        return super.mouseScrolled(sX, sY, horiz, vert);
    }

    private PlayerEntity getClientPlayerByUuid(MinecraftClient client, java.util.UUID uuid) {
        ClientWorld world = client.world;
        if (world == null || uuid == null) return null;
        return world.getPlayerByUuid(uuid);
    }
}
