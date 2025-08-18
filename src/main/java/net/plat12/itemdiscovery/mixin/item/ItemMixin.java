package net.plat12.itemdiscovery.mixin.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.plat12.itemdiscovery.screen.NameItemScreen;
import net.plat12.itemdiscovery.util.packet.client.ClientPayloadHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.plat12.itemdiscovery.util.ModUtils.shouldDisplayCustomNames;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (!shouldDisplayCustomNames()
                || stack.has(DataComponents.CUSTOM_NAME)) return;
        Item item = stack.getItem();
        if (ClientPayloadHandler.ClientStorage.TRANSLATION_OVERRIDES.get(item.getDescriptionId()) == null) {
            Minecraft.getInstance().setScreen(new NameItemScreen(item));
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

}
