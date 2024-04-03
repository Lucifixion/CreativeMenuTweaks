package com.goopswagger.creativemenutweaks.util;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Stream;

public class DummyItemGroup extends ItemGroup implements FabricItemGroup {
    private static final int TABS_PER_PAGE = FabricItemGroup.TABS_PER_PAGE;
    private final Identifier identifier;

    private int page;

    public DummyItemGroup(Identifier identifier) {
        super(null, -1, Type.CATEGORY, Text.of("itemgroup.name"), () -> new ItemStack(Items.AIR), (displayContext, entries) -> {});
        this.identifier = identifier;
    }

    public void adjust(Stream<ItemGroup> stream, int i) {
        final List<ItemGroup> sortedItemGroups = stream
                .filter(group -> group.getType() == Type.CATEGORY && !group.isSpecial())
                .filter(ItemGroup::shouldDisplay)
                .toList();

        int count = sortedItemGroups.size() + i;
        this.page = ((count / TABS_PER_PAGE));
        int pageIndex = count % TABS_PER_PAGE;
        ItemGroup.Row row = pageIndex < (TABS_PER_PAGE / 2) ? ItemGroup.Row.TOP : ItemGroup.Row.BOTTOM;
        this.row = row;
        this.column = (row == ItemGroup.Row.TOP ? pageIndex % TABS_PER_PAGE : (pageIndex - TABS_PER_PAGE / 2) % (TABS_PER_PAGE));
    }

    @Override
    public boolean shouldDisplay() {
        return true;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {

    }
}
