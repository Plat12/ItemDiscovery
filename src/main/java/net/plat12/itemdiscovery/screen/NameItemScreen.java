package net.plat12.itemdiscovery.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.plat12.itemdiscovery.util.packet.ServerPayloadHandler;
import org.lwjgl.glfw.GLFW;


public class NameItemScreen extends Screen {
    private static final Component PROMPT_TEXT_ITEM = Component.translatable("screen.itemdiscovery.name_item");
    private static final Component PROMPT_TEXT_BLOCK = Component.translatable("screen.itemdiscovery.name_block");
    private static final Component NAME_LABEL = Component.translatable("screen.itemdiscovery.name");
    private final Item item;
    private Button confirmButton;
    private String itemName = "";

    public NameItemScreen(Item item) {
        super(Component.empty());
        this.item = item;
    }

    @Override
    protected void init() {
        EditBox nameBox = new EditBox(this.font, this.width / 2 - 100, this.height / 2 + 20, 200, 20, NAME_LABEL);
        nameBox.setMaxLength(144);
        nameBox.setValue(this.itemName);
        nameBox.setResponder((text) -> {
            this.itemName = text;
            this.confirmButton.active = isNameValid();
        });
        this.addRenderableWidget(nameBox);

        this.confirmButton = Button.builder(CommonComponents.GUI_DONE, this::onConfirm)
                .bounds(this.width / 2 - 155, this.height / 2 + 60, 150, 20).build();
        this.addRenderableWidget(this.confirmButton);

        Button cancelButton = Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
            this.onClose();
        }).bounds(this.width / 2 + 5, this.height / 2 + 60, 150, 20).build();
        this.addRenderableWidget(cancelButton);


        this.setInitialFocus(nameBox);
    }

    private boolean isNameValid() {
        return !this.itemName.isBlank();
    }

    private void onConfirm(Button button) {
        if (!button.isActive()) return;
        ClientPacketDistributor.sendToServer(
                new ServerPayloadHandler.ItemNamePacket(this.item, this.itemName));
        this.onClose();
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.confirmButton.active = isNameValid();
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, -1);

        Component promptText = item instanceof BlockItem ? PROMPT_TEXT_BLOCK : PROMPT_TEXT_ITEM;
        graphics.drawCenteredString(this.font, promptText, this.width / 2, this.height / 2 - 60, -1);
        graphics.drawString(this.font, NAME_LABEL, this.width / 2 - 100, this.height / 2 + 8, -1);

        ItemStack itemStack = new ItemStack(this.item);

        graphics.pose().scale(4);
        graphics.pose().translate(
                (float) this.width / 2 / 4 - 8,
                (float) this.height / 2 / 4 - 12
        );
        graphics.renderItem(itemStack, 0, 0);

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            this.onConfirm(this.confirmButton);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
