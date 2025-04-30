package moe.ku6.libchestgui.hotbar;

import moe.ku6.libchestgui.hotbar.handler.IMenuClickHandler;
import org.bukkit.inventory.ItemStack;

public class Menu {
    private final MenuItem[] items = new MenuItem[9];
    private MenuItem currentlyEditing;

    public Menu() {

    }

    public Menu Option(int slot, ItemStack itemStack) {
        if (slot < 0 || slot >= items.length) {
            throw new IndexOutOfBoundsException("Slot must be between 0 and 8.");
        }
        var item = new MenuItem(itemStack);
        items[slot] = item;
        currentlyEditing = item;
        return this;
    }

    public Menu Option(int slot) {
        if (slot < 0 || slot >= items.length) {
            throw new IndexOutOfBoundsException("Slot must be between 0 and 8.");
        }
        currentlyEditing = items[slot];
        if (currentlyEditing == null) {
            throw new IllegalStateException("No item is currently being edited.");
        }
        return this;
    }

    public Menu Item(ItemStack item) {
        EnsureEditing();
        currentlyEditing.setItem(item);
        return this;
    }

    public Menu OnAnyClick(IMenuClickHandler handler) {
        EnsureEditing();
        currentlyEditing.setOnLeftClick(handler);
        currentlyEditing.setOnRightClick(handler);
        currentlyEditing.setOnDrop(handler);
        return this;
    }

    public Menu OnLeftClick(IMenuClickHandler handler) {
        EnsureEditing();
        currentlyEditing.setOnLeftClick(handler);
        return this;
    }

    public Menu OnRightClick(IMenuClickHandler handler) {
        EnsureEditing();
        currentlyEditing.setOnRightClick(handler);
        return this;
    }

    public Menu OnDrop(IMenuClickHandler handler) {
        EnsureEditing();
        currentlyEditing.setOnDrop(handler);
        return this;
    }

    public MenuItem GetItem(int slot) {
        if (slot < 0 || slot >= items.length) {
            throw new IndexOutOfBoundsException("Slot must be between 0 and 8.");
        }
        return items[slot];
    }

    private void EnsureEditing() {
        if (currentlyEditing == null) {
            throw new IllegalStateException("No item is currently being edited.");
        }
    }
}
