package com.gliesestudio.mc.quickgui.model;

import com.gliesestudio.mc.quickgui.enums.CommandExecutor;
import com.gliesestudio.mc.quickgui.enums.ItemStackType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiItem {

    private Material item;
    private String displayName;
    private List<String> lore;
    private String command;
    private int row;
    private int column;
    private ItemStackType itemStackType;
    private CommandExecutor commandExecutor;

    public GuiItem() {
    }

    public GuiItem(Material item, String displayName, List<String> lore, String command, int row, int column,
                   ItemStackType itemStackType, CommandExecutor commandExecutor) {
        this.item = item;
        this.displayName = displayName;
        this.lore = lore;
        this.command = command;
        this.row = row;
        this.column = column;
        this.itemStackType = itemStackType;
        this.commandExecutor = commandExecutor;
    }

    public static GuiItem createSystemFiller(Material item, int row, int column) {
        return new GuiItem()
                .item(item)
                .row(row)
                .column(column)
                .itemStackType(ItemStackType.SYSTEM_FILLER)
                .commandExecutor(CommandExecutor.SERVER);
    }

    // GuiItem builders
    public GuiItem item(Material item) {
        this.item = item;
        return this;
    }

    public GuiItem displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public GuiItem lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public GuiItem command(String command) {
        this.command = command;
        return this;
    }

    public GuiItem row(int row) {
        this.row = row;
        return this;
    }

    public GuiItem commandExecutor(CommandExecutor executor) {
        this.commandExecutor = executor;
        return this;
    }

    public GuiItem column(int column) {
        this.column = column;
        return this;
    }

    public GuiItem itemStackType(ItemStackType itemStackType) {
        this.itemStackType = itemStackType;
        return this;
    }

    // Getters
    public Material getItem() {
        return item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public @NotNull List<String> getLore() {
        return lore != null ? lore : new ArrayList<>();
    }

    public String getCommand() {
        return command;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }


    public ItemStackType getItemStackType() {
        return itemStackType;
    }


    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    @Override
    public String toString() {
        return "GuiItem{" +
                "item=" + item +
                ", displayName='" + displayName + '\'' +
                ", command='" + command + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", itemStackType=" + itemStackType +
                '}';
    }
}
