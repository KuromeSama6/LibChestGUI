package moe.ku6.libchestgui.gui;

import moe.ku6.libchestgui.util.IntPair;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class FormBuilder {
    private final Form form = new Form();
    private Activity editingActivity;

    public FormBuilder(String title, int rows) {
        form.setTitle(title);
        form.setLocation(InventoryType.CHEST);
        form.setRows(rows);
    }

    public FormBuilder(String title, InventoryType type) {
        if (type == InventoryType.CHEST)
            throw new IllegalArgumentException("InventoryType.CHEST is not supported, use FormBuilder(String title, int rows) instead");

        form.setTitle(title);
        form.setLocation(type);
    }

    public FormBuilder Title(String title) {
        form.setTitle(title);
        return this;
    }

    public FormBuilder Location(InventoryType type) {
        form.setLocation(type);
        return this;
    }

    public FormBuilder Rows(int rows) {
        form.setRows(rows);
        return this;
    }

    public FormBuilder Activity(int startX, int startY, int endX, int endY) {
        return Activity(InventoryType.CHEST, startX, startY, endX, endY);
    }
    public FormBuilder Activity(InventoryType type, int startX, int startY, int endX, int endY) {
        return Activity(0, type, startX, startY, endX, endY);
    }
    public FormBuilder Activity(int layer, InventoryType type, int startX, int startY, int endX, int endY) {
        var activity = new Activity(layer, type, startX, startY, endX, endY);
        return Activity(activity);
    }
    public FormBuilder Activity(Activity activity) {
        editingActivity = activity;
        form.getActivities().add(activity);
        return this;
    }
    public FormBuilder Mutable(boolean mutable) {
        EnsureEditingActivity();
        editingActivity.setDefaultHandler(mutable ? IActionHandler.Allow() : IActionHandler.Deny());
        return this;
    }
    public FormBuilder DefaultHandler(IActionHandler handler) {
        EnsureEditingActivity();
        editingActivity.setDefaultHandler(handler);
        return this;
    }

    public FormBuilder Item(ItemStack... items) {
        EnsureEditingActivity();
        editingActivity.Add(items);
        return this;
    }

    public FormBuilder Item(ItemStack item, IActionHandler handler) {
        EnsureEditingActivity();
        editingActivity.Add(item, handler);
        return this;
    }

    public FormBuilder Item(int x, int y, ItemStack item) {
        return Item(x, y, item, IActionHandler.Deny());
    }
    public FormBuilder Item(int x, int y, ItemStack item, IActionHandler handler) {
        EnsureEditingActivity();
        editingActivity.Set(new IntPair(x, y), item, handler);
        return this;
    }

    public FormBuilder Item(int x, int y, int offset, ItemStack item) {
        return Item(x, y, offset, item, IActionHandler.Deny());
    }
    public FormBuilder Item(int x, int y, int offset, ItemStack item, IActionHandler handler) {
        EnsureEditingActivity();
        var slot = editingActivity.GetSlot(new IntPair(x, y));
        editingActivity.Set(slot + offset, item, handler);
        return this;
    }

    public FormBuilder Fill(int startX, int startY, int endX, int endY, ItemStack item) {
        EnsureEditingActivity();
        editingActivity.Fill(new IntPair(startX, startY), new IntPair(endX, endY), item);
        return this;
    }
    public FormBuilder Fill(int startX, int startY, int endX, int endY, ItemStack item, IActionHandler handler) {
        EnsureEditingActivity();
        editingActivity.Fill(new IntPair(startX, startY), new IntPair(endX, endY), item, handler);
        return this;
    }
    public FormBuilder Fill(ItemStack item) {
        EnsureEditingActivity();
        editingActivity.Fill(item);
        return this;
    }

    public FormBuilder Fill(ItemStack item, IActionHandler handler) {
        EnsureEditingActivity();
        editingActivity.Fill(item, handler);
        return this;
    }

    public FormBuilder Remove(int startX, int startY, int endX, int endY) {
        EnsureEditingActivity();
        editingActivity.Remove(new IntPair(startX, startY), new IntPair(endX, endY));
        return this;
    }

    public FormBuilder KeepOriginal(boolean keep) {
        EnsureEditingActivity();
        editingActivity.setKeepOriginal(keep);
        return this;
    }

    public FormBuilder OnPostClick(IPostClickHandler handler) {
        EnsureEditingActivity();
        editingActivity.setPostClickHandler(handler);
        return this;
    }

    public FormBuilder OnOpen(IFormOpenHandler handler) {
        form.setOnOpen(handler);
        return this;
    }
    public FormBuilder OnClose(IFormCloseHandler handler) {
        form.setOnClose(handler);
        return this;
    }
    public FormBuilder OnUpdate(IFormUpdateHandler handler) {
        form.setOnUpdate(handler);
        return this;
    }
    public FormBuilder OnDropItem(IDropItemHandler handler) {
        form.setOnDropItem(handler);
        return this;
    }
    public FormBuilder OnPostDropItem(IPostDropItemHandler handler) {
        form.setOnPostDropItem(handler);
        return this;
    }

    public Form Build() {
        form.EnsureValid();
        editingActivity = null;
        return form;
    }

    private void EnsureEditingActivity() {
        Objects.requireNonNull(editingActivity);
    }
}
