package net.plat12.itemdiscovery.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.plat12.itemdiscovery.util.packet.client.ClientPayloadHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.plat12.itemdiscovery.util.ModUtils.shouldDisplayCustomNames;

@Mixin(ClientLanguage.class)
public class ClientLanguageMixin {

    @Unique
    private static final String BASE_KEY = "item.itemdiscovery.unknown_";
    @Unique
    private static final String BLOCK_KEY = "block";
    @Unique
    private static final String ITEM_KEY = "item";
    @Shadow
    @Final
    private Map<String, String> storage;

    /**
     * Intercept the getOrDefault method to provide custom translations
     */
    @Inject(method = "getOrDefault(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
            at = @At("HEAD"), cancellable = true)
    private void interceptGetOrDefault(String key, String defaultValue, CallbackInfoReturnable<String> cir) {
        if (!shouldDisplayCustomNames()) return;
        if (ClientPayloadHandler.ClientStorage.TRANSLATION_OVERRIDES.containsKey(key)) {
            cir.setReturnValue(ClientPayloadHandler.ClientStorage.TRANSLATION_OVERRIDES.get(key));
            return;
        }

        if (key.startsWith("item.") || key.startsWith("block.")) {
            Item item = getItemFromTranslationKey(key);
            if (item != null) {
                String unknownKey = item instanceof BlockItem ?
                        BASE_KEY + BLOCK_KEY :
                        BASE_KEY + ITEM_KEY;

                String unknownTranslation = storage.getOrDefault(unknownKey,
                        item instanceof BlockItem ? "Unknown Block" : "Unknown Item");
                cir.setReturnValue(unknownTranslation);
            }

        }
    }

    /**
     * Intercept the has method to account for our custom translations
     */
    @Inject(method = "has(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void interceptHas(String key, CallbackInfoReturnable<Boolean> cir) {
        if (ClientPayloadHandler.ClientStorage.TRANSLATION_OVERRIDES.containsKey(key)) {
            cir.setReturnValue(true);
            return;
        }


        if (key.startsWith("item.") || key.startsWith("block.")) {
            Player player = Minecraft.getInstance().player;
            if (player == null || !player.hasInfiniteMaterials()) {
                Item item = getItemFromTranslationKey(key);
                if (item != null) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    /**
     * Helper method to get Item from translation key
     */
    @Unique
    private Item getItemFromTranslationKey(String translationKey) {
        try {
            String prefix;
            if (translationKey.startsWith("item.")) {
                prefix = "item.";
            } else if (translationKey.startsWith("block.")) {
                prefix = "block.";
            } else {
                return null;
            }

            String withoutPrefix = translationKey.substring(prefix.length());

            String resourceLocationString = withoutPrefix.replace(".", ":");
            ResourceLocation resourceLocation = ResourceLocation.parse(resourceLocationString);

            if (prefix.equals("item.")) {
                return BuiltInRegistries.ITEM.get(resourceLocation).map(Holder.Reference::value).orElse(null);
            } else {
                Block block = BuiltInRegistries.BLOCK.get(resourceLocation).map(Holder.Reference::value).orElse(null);
                return block != null ? block.asItem() : null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}