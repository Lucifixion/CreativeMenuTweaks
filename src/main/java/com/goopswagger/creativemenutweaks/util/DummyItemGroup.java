package com.goopswagger.creativemenutweaks.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DummyItemGroup extends ItemGroup {
    private final Identifier identifier;

    public DummyItemGroup(Identifier identifier) {
        super(null, -1, Type.CATEGORY, Text.of("itemgroup.name"), () -> new ItemStack(Items.AIR), (displayContext, entries) -> {});
        this.identifier = identifier;
    }

    @Override
    public boolean shouldDisplay() {
        return true;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }
}
