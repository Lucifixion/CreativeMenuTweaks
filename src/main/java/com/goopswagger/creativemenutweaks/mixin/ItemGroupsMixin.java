package com.goopswagger.creativemenutweaks.mixin;

import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.util.DummyItemGroup;
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
    private static Stream<ItemGroup> stream() {
        return null;
    }

    @Inject(method = {"getGroupsToDisplay", "getGroups"}, at = @At(value = "TAIL"), cancellable = true)
    private static void getGroups(CallbackInfoReturnable<List<ItemGroup>> cir) {
        List<ItemGroup> groups = new ArrayList<>(cir.getReturnValue());
        final int[] offset = {0};
        DataItemGroupManager.getCustomGroups().forEach((entry) -> {
            DummyItemGroup group = entry.getValue().getDummyItemGroup();
            if (!groups.contains(group)) {
                group.adjust(stream(), offset[0]);
                groups.add(group);
                offset[0]++;
            }
        });
        cir.setReturnValue(groups);
    }
}
