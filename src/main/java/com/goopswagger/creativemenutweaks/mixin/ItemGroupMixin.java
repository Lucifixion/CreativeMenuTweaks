package com.goopswagger.creativemenutweaks.mixin;

import com.goopswagger.creativemenutweaks.data.DataItemGroup;
import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.util.ItemGroupUtil;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {
    @Inject(method = {"getDisplayStacks", "getSearchTabStacks"}, at = @At(value = "TAIL"), cancellable = true)
    private void getDisplayStacks(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        ItemGroup group = (((ItemGroup) (Object) this));
        Identifier identifier = ItemGroupUtil.getGroupIdentifier(group);
        if (DataItemGroupManager.groupData.containsKey(identifier)) {
            DataItemGroup dataItemGroup = DataItemGroupManager.groupData.get(identifier);
            if (dataItemGroup.replace())
                cir.setReturnValue(dataItemGroup.entries());
            else
                cir.getReturnValue().addAll(dataItemGroup.entries());
        }
    }

    @Inject(method = "getDisplayName", at = @At(value = "RETURN"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir) {
        ItemGroup group = (((ItemGroup) (Object) this));
        Identifier identifier = ItemGroupUtil.getGroupIdentifier(group);
        if (DataItemGroupManager.groupData.containsKey(identifier))
            DataItemGroupManager.groupData.get(identifier).optionalName().ifPresent(name -> cir.setReturnValue(Text.translatable(name)));
    }

    @Inject(method = "getIcon", at = @At(value = "RETURN"), cancellable = true)
    private void getIcon(CallbackInfoReturnable<ItemStack> cir) {
        ItemGroup group = (((ItemGroup) (Object) this));
        Identifier identifier = ItemGroupUtil.getGroupIdentifier(group);
        if (DataItemGroupManager.groupData.containsKey(identifier))
            DataItemGroupManager.groupData.get(identifier).optionalIcon().ifPresent(cir::setReturnValue);
    }
}
