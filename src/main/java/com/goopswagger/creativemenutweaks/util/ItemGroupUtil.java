package com.goopswagger.creativemenutweaks.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemGroupUtil {

    public static Identifier getGroupIdentifier(ItemGroup group) {
        if (group instanceof DummyItemGroup dummyGroup)
            return dummyGroup.getIdentifier();
        else if (Registries.ITEM_GROUP.getId(group) != null) {
            return Registries.ITEM_GROUP.getId(group);
        }
        return null;
    }

}
