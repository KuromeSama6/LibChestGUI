package moe.ku6.libchestgui.gui;

import lombok.Getter;
import lombok.Setter;
import moe.ku6.libchestgui.InventoryUserInterface;
import moe.ku6.libchestgui.UserInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Stack;

public class ChestGUI extends InventoryUserInterface {
    @Getter
    private final Stack<Form> forms = new Stack<>();
    private Inventory currentInventory;
    @Setter @Getter
    private boolean closedThisTick;

    public ChestGUI(UserInterface userInterface) {
        super(userInterface);
    }

    public void Push(Form form) {
        var current = forms.stream()
                        .filter(c -> c.getTitle().equals(form.getTitle()))
                        .findFirst()
                        .orElse(null);
        forms.remove(current);
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
                userInterface.getPlayer().closeInventory();
                closedThisTick = true;
                currentInventory = null;
            }
            return;
        }
        form.EnsureValid();

        var type = form.GetInventoryType();
        if (type == InventoryType.CHEST) currentInventory = Bukkit.createInventory(userInterface.getPlayer(), form.getRows() * 9, form.getTitle());
        else currentInventory = Bukkit.createInventory(userInterface.getPlayer(), form.GetInventoryType(), form.getTitle());

        form.Render(userInterface.getPlayer().getInventory(), currentInventory);
        if (currentInventory != null) closedThisTick = true;
        userInterface.getPlayer().openInventory(currentInventory);
        if (form.getOnOpen() != null) {
            form.getOnOpen().Handle();
        }
    }

    public void Refresh() {
        var form = GetActiveForm();
        if (form == null) return;
        form.Render(userInterface.getPlayer().getInventory(), currentInventory);
        if (form.getOnUpdate() != null) {
            form.getOnUpdate().Handle();
        }
    }

    public void Refresh(Activity activity) {
        var form = GetActiveForm();
        if (form == null) return;
        form.Render(userInterface.getPlayer().getInventory(), currentInventory, activity);
        if (form.getOnUpdate() != null) {
            form.getOnUpdate().Handle();
        }
    }

    @Override
    public void OnDropItem(PlayerDropItemEvent e) {
        if (GetActiveForm() != null) {
            var form = GetActiveForm();
            if (form.getOnPostDropItem() != null) {
                form.getOnPostDropItem().Handle(e);
            }
        }
    }

    @Override
    public void OnInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() != userInterface.getPlayer()) return;
        var current = GetActiveForm();
        if (current == null) return;

        var ctx = current.Handle(e);
        if (ctx == null) return;

        if (ctx.isAllowed() && ctx.getActivity().getPostClickHandler() != null) {
            ctx.getActivity().getPostClickHandler().Handle(ctx);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(userInterface.getPlugin(), () -> {
            if (ctx.isPopNow()) {
                Pop(current);
                return;
            }

            if (ctx.isCloseNow()) {
                Clear();
                return;
            }

            if (ctx.isRerenderNow()) {
                Rerender();
                return;
            }

            if (ctx.isRefreshNow()) {
                current.Render(userInterface.getPlayer().getInventory(), currentInventory);
                if (current.getOnUpdate() != null) {
                    current.getOnUpdate().Handle();
                }
                e.setCancelled(true);
                return;
            }
        }, 1);
    }

    @Override
    public void OnInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() != userInterface.getPlayer()
        ) return;
        var current = GetActiveForm();
        if (current == null) return;

        if (closedThisTick) {
            closedThisTick = false;
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(userInterface.getPlugin(), () -> Pop(current), 1);
    }

    @Override
    public void OnInteract(PlayerInteractEvent e) {

    }

    public Form GetActiveForm() {
        return forms.isEmpty() ? null : forms.peek();
    }
}
