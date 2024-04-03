package com.goopswagger.creativemenutweaks.mixin;

import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeInventoryScreen.class, priority = 1001)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	@Shadow private static ItemGroup selectedTab;
	@Unique private static ItemGroup hoveredTab;

	public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Shadow protected abstract int getTabX(ItemGroup group);
	@Shadow protected abstract int getTabY(ItemGroup group);

	@Inject(method = "init", at = @At(value = "TAIL"))
	private void init(CallbackInfo ci) {
		if (DataItemGroupManager.update) {
			while (((CreativeGuiExtensions) this).fabric_currentPage() > 0)
				((CreativeGuiExtensions) this).fabric_previousPage();
			DataItemGroupManager.update = false;
		}
	}

	@Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;setSelectedTab(Lnet/minecraft/item/ItemGroup;)V", shift = At.Shift.AFTER))
	private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	@Inject(method = "renderTabTooltipIfHovered", at = @At(value = "HEAD"))
	private void renderTabTooltipIfHovered(DrawContext context, ItemGroup group, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
		hoveredTab = this.isPointWithinBounds(this.getTabX(group) + 3, this.getTabY(group) + 3, 21, 27, mouseX, mouseY) ? group : null;
	}

	@WrapOperation(method = "renderTabTooltipIfHovered", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;II)V"))
	private void injected(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, Operation<Void> original, @Local ItemGroup group) {
		int x2 = this.getTabX(group);
		int y2 = this.getTabY(group);
		int offset = 0;
		if (group == selectedTab)
			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 3;
		instance.drawTooltip(this.textRenderer, text, this.x + x2 - (this.textRenderer.getWidth(text)/2), this.y + y2 + (group.getRow() == ItemGroup.Row.TOP ? 2 : 32 + 12) + offset);
	}

	@WrapOperation(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void injected(DrawContext instance, Identifier texture, int x, int y, int width, int height, Operation<Void> original, @Local ItemGroup group) {
		int offset = 0;
		if (group != selectedTab && group == hoveredTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 1;
		}
//		instance.drawTexture(texture, x, y + offset, u, v, width, height - offset);
		original.call(instance, texture, x, y+offset, width, height);
	}

	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;II)V"), index = 2)
	private int injected(int y, @Local ItemGroup group) {
		int offset = 0;
		if (group == selectedTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -2 : 4;
		} else if (group == hoveredTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 1;
		}
		y += offset;
		return y;
	}

	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"), index = 3)
	private int renderTabIcon(int y, @Local ItemGroup group) {
		int offset = 0;
		if (group == selectedTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -2 : 4;
		} else if (group == hoveredTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 1;
		}
		y += offset;
		return y;
	}
}