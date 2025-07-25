package com.goopswagger.creativemenutweaks.util;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ItemGroupUtil {

    public static ItemGroup getGroup(Identifier identifier) {
        return Registries.ITEM_GROUP.get(identifier);
    }

    public static Identifier getGroupIdentifier(ItemGroup group) {
        if (group instanceof DummyItemGroup dummyGroup)
            return dummyGroup.getIdentifier();
        else if (Registries.ITEM_GROUP.getId(group) != null) {
            return Registries.ITEM_GROUP.getId(group);
        }
        return null;
    }

    public static void calculateIndex(ItemGroup group, int index) {
        ((FabricItemGroupImpl) group).fabric_setPage(MathHelper.floor(index / 10f));
        ((ItemGroupAccessor) group).setColumn(index % 5);
        ((ItemGroupAccessor) group).setRow(MathHelper.floor(index / 5f) % 2 == 0 ? ItemGroup.Row.TOP : ItemGroup.Row.BOTTOM);
    }
}
