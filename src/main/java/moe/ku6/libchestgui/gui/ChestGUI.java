package moe.ku6.libchestgui.gui;

import moe.ku6.libchestgui.InventoryUserInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Stack;

public class ChestGUI extends InventoryUserInterface {
    private final Stack<Form> forms = new Stack<>();
    private Inventory currentInventory;

    public ChestGUI(JavaPlugin plugin, Player player) {
        super(plugin, player);
    }

    public void Push(Form form) {
        forms.remove(form);
        forms.push(form);
        form.EnsureValid();

        Rerender();
    }

    public void Pop() {
        if (forms.isEmpty()) return;
        forms.pop();
        Rerender();
    }

    public void Clear() {
        for (var form : forms) {
            if (form.getOnClose() != null) {
                form.getOnClose().Handle();
            }
        }
        forms.clear();
        Rerender();
    }

    public void Pop(Form form) {
        if (forms.isEmpty()) return;
        if (form.getOnClose() != null) {
            form.getOnClose().Handle();
        }
        forms.remove(form);
        Rerender();
    }

    public void Rerender() {
        var form = GetActiveForm();
        if (form == null) {
            if (currentInventory != null) {
                player.closeInventory();
                currentInventory = null;
            }
            return;
        }
        form.EnsureValid();

        var type = form.GetInventoryType();
        if (type == InventoryType.CHEST) currentInventory = Bukkit.createInventory(player, form.getRows() * 9, form.getTitle());
        else currentInventory = Bukkit.createInventory(player, form.GetInventoryType(), form.getTitle());

        form.Render(player.getInventory(), currentInventory);
        player.openInventory(currentInventory);
        if (form.getOnOpen() != null) {
            form.getOnOpen().Handle();
        }
    }

    @EventHandler
    private void OnClick(InventoryClickEvent e) {
        if (e.getWhoClicked() != player) return;
        var current = GetActiveForm();
        if (current == null) return;
        var ctx = current.Handle(e);
        if (ctx == null) return;

        if (ctx.isPopNow()) {
            Pop(current);
        }

        if (ctx.isCloseNow()) {
            Clear();
        }
    }

    @EventHandler
    private void OnClose(InventoryCloseEvent event) {
        if (event.getPlayer() != player) return;
        var current = GetActiveForm();
        if (current == null) return;
        Pop(current);
    }

    public Form GetActiveForm() {
        return forms.isEmpty() ? null : forms.peek();
    }
}
