package net.plat12.itemdiscovery.mixin.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.plat12.itemdiscovery.screen.NameItemScreen;
import net.plat12.itemdiscovery.util.packet.client.ClientPayloadHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Unique
    private static final String BASE_KEY = "item.itemdiscovery.unknown_";
    @Unique
    private static final String BLOCK_KEY = "block";
    @Unique
    private static final String ITEM_KEY = "item";

    @Inject(method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
    public void getName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.hasInfiniteMaterials()
                || stack.has(DataComponents.CUSTOM_NAME)) return;
        Item item = stack.getItem();
        String name = ClientPayloadHandler.ClientStorage.getName(item);
        Component nameComponent;
        if (name != null) {
            nameComponent = Component.literal(name);
        } else {
            String key = item instanceof BlockItem ? BLOCK_KEY : ITEM_KEY;
            nameComponent = Component.translatable(BASE_KEY + key);
        }
        cir.setReturnValue(nameComponent);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.hasInfiniteMaterials()
                || stack.has(DataComponents.CUSTOM_NAME)) return;
        Item item = stack.getItem();
        if (ClientPayloadHandler.ClientStorage.getName(item) == null) {
            Minecraft.getInstance().setScreen(new NameItemScreen(item));
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

}
