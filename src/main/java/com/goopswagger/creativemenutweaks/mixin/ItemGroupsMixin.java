package com.goopswagger.creativemenutweaks.mixin;

//import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.event.AddItemGroupEvent;
import com.goopswagger.creativemenutweaks.util.DummyItemGroup;
import com.goopswagger.creativemenutweaks.util.ItemGroupUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(ItemGroups.class)
public abstract class ItemGroupsMixin {
    @Shadow
    public static List<ItemGroup> getGroupsToDisplay() {
        return null;
    }

    @Inject(method = {"getGroupsToDisplay", "getGroups"}, at = @At(value = "TAIL"), cancellable = true)
    private static void getGroups(CallbackInfoReturnable<List<ItemGroup>> cir) {
        List<ItemGroup> standardItemGroups = new ArrayList<>();
        List<ItemGroup> specialItemGroups = new ArrayList<>();

        List<ItemGroup> defaultItemGroups = new ArrayList<>(cir.getReturnValue());
        defaultItemGroups.addAll(DataItemGroupManager.getItemGroups());

        for (ItemGroup itemGroup : defaultItemGroups) {
            if (AddItemGroupEvent.EVENT.invoker().add(MinecraftClient.getInstance().player, itemGroup)) {
                if (!itemGroup.isSpecial()) {
                    ItemGroupUtil.calculateIndex(itemGroup, standardItemGroups.size());
                    standardItemGroups.add(itemGroup);
                } else {
                    specialItemGroups.add(itemGroup);
                }
            }
        }

        standardItemGroups.addAll(specialItemGroups);
        cir.setReturnValue(standardItemGroups);
    }

    @Inject(method = "getDefaultTab", at = @At(value = "TAIL"), cancellable = true)
    private static void getDefaultTab(CallbackInfoReturnable<ItemGroup> cir) {
        List<ItemGroup> groups = getGroupsToDisplay();
        if (groups != null && !groups.isEmpty()) {
            ItemGroup group = groups.get(0);
            cir.setReturnValue(group);
        }
    }
}
