package com.goopswagger.creativemenutweaks.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Consumer;

@Mixin(LeafEntry.class)
public interface LeafEntryAccessor {
    @Invoker("generateLoot")
    void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context);
}
