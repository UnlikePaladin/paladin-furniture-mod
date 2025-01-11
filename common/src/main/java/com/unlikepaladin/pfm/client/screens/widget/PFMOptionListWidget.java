package com.unlikepaladin.pfm.client.screens.widget;

import com.google.common.collect.ImmutableList;
import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.client.screens.PFMConfigScreen;
import com.unlikepaladin.pfm.client.screens.PFMGeneratingOverlay;
import com.unlikepaladin.pfm.config.option.AbstractConfigOption;
import com.unlikepaladin.pfm.config.option.BooleanConfigOption;
import com.unlikepaladin.pfm.config.option.Side;
import com.unlikepaladin.pfm.runtime.PFMAssetGenerator;
import com.unlikepaladin.pfm.runtime.PFMDataGenerator;
import com.unlikepaladin.pfm.runtime.PFMRuntimeResources;
import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;

import java.util.*;
import java.util.function.Consumer;

public class PFMOptionListWidget extends ElementListWidget<PFMOptionListWidget.Entry> {
    final PFMConfigScreen parent;
    int maxKeyNameLength;
    public BitSet hasChanges;
    public Map<AbstractConfigOption, Boolean> newConfigValues;
    public Map<AbstractConfigOption, Integer> configOptionToIndexForHasChanges;
    public PFMOptionListWidget(PFMConfigScreen parent, MinecraftClient client) {
        super(client, parent.width + 125, parent.height, 43, parent.height - 32, 20);
        this.parent = parent;
        String string = null;
        int index = 0;
        hasChanges = new BitSet(PaladinFurnitureMod.getPFMConfig().options.size());
        newConfigValues = new HashMap<>(PaladinFurnitureMod.getPFMConfig().options.size());
        configOptionToIndexForHasChanges = new HashMap<>(PaladinFurnitureMod.getPFMConfig().options.size());
        for(Map.Entry<String, AbstractConfigOption> configOptionEntry : PaladinFurnitureMod.getPFMConfig().options.entrySet()) {
            Text text;
            int i;
            String configOptionCategory = configOptionEntry.getValue().getCategory();
            if (!configOptionCategory.equals(string)) {
                string = configOptionCategory;
                this.addEntry(new CategoryEntry(new TranslatableText(configOptionCategory)));
            }
            if ((i = client.textRenderer.getWidth(text = configOptionEntry.getValue().getTitle())) > this.maxKeyNameLength) {
                this.maxKeyNameLength = i;
            }
            if (configOptionEntry.getValue().getType() == Boolean.class) {
                PFMOptionListWidget.this.newConfigValues.put(configOptionEntry.getValue(), (Boolean) configOptionEntry.getValue().getValue());
                this.addEntry(new BooleanEntry((BooleanConfigOption)configOptionEntry.getValue(), text, index));
            } else {
                PaladinFurnitureMod.GENERAL_LOGGER.warn("Unsupported Config Type!");
            }
            configOptionToIndexForHasChanges.put(configOptionEntry.getValue(), index);
            index++;
        }
        this.addEntry(new CategoryEntry(new LiteralText("")));
        this.addEntry(new ButtonEntry(Side.CLIENT, new TranslatableText("pfm.option.regenAssets"), new TranslatableText("pfm.config.regen"), new TranslatableText("pfm.option.regenAssets.tooltip"), button -> {
            PFMFileUtil.deleteDir(PFMRuntimeResources.getAssetPackDirectory().toFile());
            PFMAssetGenerator.FROZEN = false;
            PFMRuntimeResources.prepareAndRunAssetGen(true);
            MinecraftClient.getInstance().reloadResourcesConcurrently();
        }));
        ButtonEntry entry = new ButtonEntry(Side.SERVER, new TranslatableText("pfm.option.regenData"), new TranslatableText("pfm.config.regen"), new TranslatableText("pfm.option.regenData.tooltip"), button -> {
            PFMFileUtil.deleteDir(PFMRuntimeResources.getDataPackDirectory().toFile());
            PFMDataGenerator.FROZEN = false;
            PFMRuntimeResources.prepareAndRunDataGen(true);
        });
        entry.button.active = !PFMConfigScreen.isOnServer;
        this.addEntry(entry);
    }

    public void save() {
        for (Map.Entry<AbstractConfigOption, Boolean> entry : newConfigValues.entrySet()) {
            if (entry.getKey().getType() == Boolean.class)
                entry.getKey().setValue(entry.getValue());
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 15;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    @Environment(value= EnvType.CLIENT)
    public class CategoryEntry
            extends Entry {
        final MutableText text;
        private final int textWidth;

        public CategoryEntry(MutableText text) {
            this.text = text;
            this.textWidth = PFMOptionListWidget.this.client.textRenderer.getWidth(this.text);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            PFMOptionListWidget.this.client.textRenderer.draw(matrices, this.text.setStyle(Style.EMPTY.withBold(true)), (float)((PFMOptionListWidget.this.client.currentScreen.width / 2 - this.textWidth / 2)), (float)(y + entryHeight - (PFMOptionListWidget.this).client.textRenderer.fontHeight - 1), 0xFFFFFF);
        }

        @Override
        public boolean changeFocus(boolean lookForwards) {
            return false;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(new Selectable(){

                @Override
                public Selectable.SelectionType getType() {
                    return Selectable.SelectionType.HOVERED;
                }

                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    builder.put(NarrationPart.TITLE, PFMOptionListWidget.CategoryEntry.this.text);
                }
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class BooleanEntry
            extends Entry {
        private final BooleanConfigOption configOption;
        private final Text optionName;
        private final ButtonWidget valueButton;
        private final ButtonWidget resetButton;

        private final ButtonWidget.TooltipSupplier supplier;
        int index;
        boolean hasChanges = false;
        BooleanEntry(final BooleanConfigOption configOption, final Text optionName, int index) {
            this.configOption = configOption;
            this.optionName = optionName;
            this.index = index;
            this.supplier = new ButtonWidget.TooltipSupplier() {
                final MutableText sideText = configOption.getSide() == Side.CLIENT ? new TranslatableText("pfm.option.client").setStyle(Style.EMPTY.withItalic(false).withBold(true).withColor(0xf77f34)) : new TranslatableText("pfm.option.server").setStyle((Style.EMPTY.withItalic(false).withBold(true).withColor(0xf77f34)));
                final MutableText styledTooltip = ((MutableText)configOption.getToolTip()).setStyle(Style.EMPTY.withItalic(true));
                final MutableText combinedText = new LiteralText("").append(sideText).append(new LiteralText("\n")).append(styledTooltip);
                @Override
                public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                    PFMOptionListWidget.this.parent.renderOrderedTooltip(matrices, PFMOptionListWidget.this.client.textRenderer.wrapLines(combinedText, Math.max(PFMOptionListWidget.this.width / 2 - 43, 170)), mouseX, mouseY);
                }
                @Override
                public void supply(Consumer<Text> consumer) {
                    consumer.accept(combinedText);
                    ButtonWidget.TooltipSupplier.super.supply(consumer);
                }
            };

            this.valueButton = new ButtonWidget(0, 0, 75, 20, optionName, button -> {
                PFMOptionListWidget.this.parent.focusedConfigOption = configOption;
                PFMOptionListWidget.this.newConfigValues.put(configOption, !PFMOptionListWidget.this.newConfigValues.get(configOption));
                hasChanges = !hasChanges;
                PFMOptionListWidget.this.hasChanges.set(index, hasChanges);
            }, this.supplier);

            this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), button -> {
                PFMOptionListWidget.this.newConfigValues.put(configOption, configOption.getDefaultValue());
                hasChanges = true;
                PFMOptionListWidget.this.hasChanges.set(index, true);
            }){

                @Override
                protected MutableText getNarrationMessage() {
                    return new TranslatableText("narrator.controls.reset", optionName);
                }
            };
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            PFMOptionListWidget.this.client.textRenderer.draw(matrices, this.optionName, (float)(x + 90 - PFMOptionListWidget.this.maxKeyNameLength), (float)(y + entryHeight / 2 - PFMOptionListWidget.this.client.textRenderer.fontHeight / 2), 0xFFFFFF);
            this.resetButton.x = x + 190;
            this.resetButton.y = y;
            this.resetButton.active = this.configOption.getSide() == Side.SERVER ? !PFMConfigScreen.isOnServer && !(this.configOption.getDefaultValue() == PFMOptionListWidget.this.newConfigValues.get(configOption)) : !(this.configOption.getDefaultValue() == PFMOptionListWidget.this.newConfigValues.get(configOption));;
            this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
            this.valueButton.x = x + 105;
            this.valueButton.y = y;
            this.valueButton.setMessage(PFMOptionListWidget.this.newConfigValues.get(configOption) ? ScreenTexts.YES : ScreenTexts.NO);
            this.valueButton.active = this.configOption.getSide() != Side.SERVER || !PFMConfigScreen.isOnServer;
            this.valueButton.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.valueButton, this.resetButton);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(this.valueButton, this.resetButton);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.valueButton.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return this.resetButton.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.valueButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class ButtonEntry
            extends Entry {
        private final Text optionName;
        private final ButtonWidget button;

        private final ButtonWidget.TooltipSupplier supplier;
        private final Side side;
        ButtonEntry(Side side, final Text optionName, Text buttonText, Text tooltip, ButtonWidget.PressAction pressAction) {
            this.optionName = optionName;
            this.side = side;
            this.supplier = new ButtonWidget.TooltipSupplier() {
                final MutableText sideText = side == Side.CLIENT ? new TranslatableText("pfm.option.client").setStyle(Style.EMPTY.withItalic(false).withBold(true).withColor(0xf77f34)) : new TranslatableText("pfm.option.server").setStyle((Style.EMPTY.withItalic(false).withBold(true).withColor(0xf77f34)));
                final MutableText styledTooltip = ((MutableText)tooltip).setStyle(Style.EMPTY.withItalic(true));
                final MutableText combinedText = new LiteralText("").append(sideText).append(new LiteralText("\n")).append(styledTooltip);
                @Override
                public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                    PFMOptionListWidget.this.parent.renderOrderedTooltip(matrices, PFMOptionListWidget.this.client.textRenderer.wrapLines(combinedText, Math.max(PFMOptionListWidget.this.width / 2 - 43, 170)), mouseX, mouseY);
                }
                @Override
                public void supply(Consumer<Text> consumer) {
                    consumer.accept(combinedText);
                    ButtonWidget.TooltipSupplier.super.supply(consumer);
                }
            };

            this.button = new ButtonWidget(0, 0, 135, 20, buttonText, pressAction, supplier){
                @Override
                protected MutableText getNarrationMessage() {
                    return (MutableText) optionName;
                }
            };
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            PFMOptionListWidget.this.client.textRenderer.draw(matrices, this.optionName, (float)(x + 90 - PFMOptionListWidget.this.maxKeyNameLength), (float)(y + entryHeight / 2 - PFMOptionListWidget.this.client.textRenderer.fontHeight / 2), 0xFFFFFF);
            this.button.x = x + 105;
            this.button.y = y;
            this.button.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(this.button);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(this.button);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.button.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.button.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry extends ElementListWidget.Entry<Entry> {
    }
}

