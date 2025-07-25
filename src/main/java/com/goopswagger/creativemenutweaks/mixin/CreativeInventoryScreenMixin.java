package com.goopswagger.creativemenutweaks.mixin;

import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.util.ItemGroupUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(value = CreativeInventoryScreen.class, priority = 1001)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	@Shadow private static ItemGroup selectedTab;
	@Unique private static ItemGroup hoveredTab;

	public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Shadow protected abstract int getTabX(ItemGroup group);
	@Shadow protected abstract int getTabY(ItemGroup group);

	@Shadow protected abstract void setSelectedTab(ItemGroup group);

	@Inject(method = "init", at = @At(value = "TAIL"))
	private void init(CallbackInfo ci) {
		if (DataItemGroupManager.dirty) {
			((FabricCreativeInventoryScreen) this).switchToPage(0);
			selectedTab = ItemGroups.getDefaultTab();
			setSelectedTab(selectedTab);

			DataItemGroupManager.dirty = false;
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
	private void injected(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, Operation<Void> original, @Local(argsOnly = true) ItemGroup group) {
		int textLength = this.textRenderer.getWidth(text);
		int x2 = this.getTabX(group);
		int y2 = this.getTabY(group);
		int offset = 0;
		if (group == selectedTab)
			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 3;
		List<Text> texts = new ArrayList<>();
		texts.add(text);
		if (MinecraftClient.getInstance().options.advancedItemTooltips) {
			Identifier identifier = ItemGroupUtil.getGroupIdentifier(group);
			if (identifier != null) {
				Text identifierText = Text.literal(identifier.toString()).formatted(Formatting.GRAY);
				texts.add(identifierText);

				int identifierTextLength = this.textRenderer.getWidth(identifierText);
				if (identifierTextLength > textLength)
					textLength = identifierTextLength;
			}
		}
		instance.drawTooltip(this.textRenderer, texts, this.x + x2 - (textLength / 2), this.y + y2 + (group.getRow() == ItemGroup.Row.TOP ? MinecraftClient.getInstance().options.advancedItemTooltips ? -10 : 2 : 32 + 12) + offset);
	}

	@WrapOperation(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"))
	private void injected(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height, Operation<Void> original, @Local(argsOnly = true) ItemGroup group) {
		int offset = 0;
		if (group != selectedTab && group == hoveredTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 1;
		}
//		instance.drawTexture(texture, x, y + offset, width, height, width, height - offset);
		original.call(instance, renderLayers, sprite, x, y + offset, width, height);
	}

	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;II)V"), index = 2)
	private int injected(int y, @Local(argsOnly = true) ItemGroup group) {
		int offset = 0;
		if (group == selectedTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -2 : 4;
		} else if (group == hoveredTab) {
			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 1;
		}
		y += offset;

		if (group.getRow() == ItemGroup.Row.BOTTOM) {
			y--;
		}

		return y;
	}

//	@ModifyArg(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"), index = 3)
//	private int renderTabIcon(int y, @Local(argsOnly = true) ItemGroup group) {
//		int offset = 0;
//		if (group == selectedTab) {
//			offset = group.getRow() == ItemGroup.Row.TOP ? -2 : 4;
//		} else if (group == hoveredTab) {
//			offset = group.getRow() == ItemGroup.Row.TOP ? -1 : 1;
//		}
//		y += offset;
//		return y;
//	}
}