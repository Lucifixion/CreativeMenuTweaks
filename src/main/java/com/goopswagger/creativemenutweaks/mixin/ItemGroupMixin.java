package com.goopswagger.creativemenutweaks.mixin;

import com.goopswagger.creativemenutweaks.event.GetItemGroupIconEvent;
import com.goopswagger.creativemenutweaks.event.GetItemGroupNameEvent;
import com.goopswagger.creativemenutweaks.event.PopulateItemGroupEvent;
import com.goopswagger.creativemenutweaks.util.ItemGroupUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {
    @Inject(method = {"getDisplayStacks", "getSearchTabStacks"}, at = @At(value = "TAIL"), cancellable = true)
    private void getDisplayStacks(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        ItemGroup group = (((ItemGroup) (Object) this));
        cir.setReturnValue(PopulateItemGroupEvent.EVENT.invoker().populate(MinecraftClient.getInstance().player, group, new ArrayList<>(cir.getReturnValue())));
    }

    @Inject(method = "getDisplayName", at = @At(value = "RETURN"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir) {
        ItemGroup group = (((ItemGroup) (Object) this));
        cir.setReturnValue(GetItemGroupNameEvent.EVENT.invoker().getName(MinecraftClient.getInstance().player, group, cir.getReturnValue()));
    }

    @Inject(method = "getIcon", at = @At(value = "RETURN"), cancellable = true)
    private void getIcon(CallbackInfoReturnable<ItemStack> cir) {
        ItemGroup group = (((ItemGroup) (Object) this));
        cir.setReturnValue(GetItemGroupIconEvent.EVENT.invoker().getIcon(MinecraftClient.getInstance().player, group, cir.getReturnValue()));
    }
}
