package moe.ku6.libchestgui.hotbar;

import moe.ku6.libchestgui.InventoryUserInterface;
import moe.ku6.libchestgui.UserInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Stack;

public class HotbarMenu extends InventoryUserInterface {
    private final Stack<Menu> menus = new Stack<>();

    public HotbarMenu(UserInterface userInterface) {
        super(userInterface);
    }

    public void PushMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null.");
        }
        menus.push(menu);
        ApplyMenu();
    }

    public void PushMenuClean(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null.");
        }
        menus.clear();
        menus.push(menu);
        ApplyMenu();
    }

    public void PopMenu() {
        if (menus.empty()) {
            throw new IllegalStateException("No menus to pop.");
        }
        menus.pop();
        ApplyMenu();
    }

    public void PopAllMenus() {
        menus.clear();
        ApplyMenu();
    }

    private void ApplyMenu() {
        var menu = GetActiveMenu();
        if (menu == null) {
            for (int i = 0; i < 9; i++) {
                userInterface.getPlayer().getInventory().clear(i);
            }
            return;
        }

        var inv = userInterface.getPlayer().getInventory();
        for (int i = 0; i < 9; i++) {
            var item = menu.GetItem(i);
            if (item != null) {
                inv.setItem(i, item.getItem());
            } else {
                inv.clear(i);
            }
        }
    }

    @Override
    public void OnInteract(PlayerInteractEvent e) {
        if (e.getPlayer() != userInterface.getPlayer()) return;
        var action = e.getAction();
        var menu = GetActiveMenu();
        if (menu == null) return;

        var slot = e.getPlayer().getInventory().getHeldItemSlot();
        var item = menu.GetItem(slot);
        if (item == null) return;

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (item.getOnLeftClick() != null) {
                item.getOnLeftClick().Handle();
            }
        } else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (item.getOnRightClick() != null) {
                item.getOnRightClick().Handle();
            }
        }
    }

    @Override
    public void OnDropItem(PlayerDropItemEvent e) {
        if (e.getPlayer() != userInterface.getPlayer()) return;
        if (GetActiveMenu() != null) {
            e.setCancelled(true);
            return;
        }
    }

    @Override
    public void OnInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() != userInterface.getPlayer()) return;
        var slot = e.getSlot();
        if (slot < 0 || slot >= 9 || GetActiveMenu() == null) return;

        e.setCancelled(true);
        var menu = GetActiveMenu();

        switch (e.getClick()) {
            case DROP, CONTROL_DROP -> {
                var item = menu.GetItem(slot);
                if (item != null && item.getOnDrop() != null) {
                    item.getOnDrop().Handle();
                }
            }
            case LEFT, SHIFT_LEFT -> {
                var item = menu.GetItem(slot);
                if (item != null && item.getOnLeftClick() != null) {
                    item.getOnLeftClick().Handle();
                }
            }
            case RIGHT, SHIFT_RIGHT -> {
                var item = menu.GetItem(slot);
                if (item != null && item.getOnRightClick() != null) {
                    item.getOnRightClick().Handle();
                }
            }
        }
    }

    @Override
    public void OnInventoryClose(InventoryCloseEvent e) {

    }

    @EventHandler
    private void OnPlayerCloseInventory(InventoryCloseEvent e) {
        if (e.getPlayer() != userInterface.getPlayer()) return;
        if (GetActiveMenu() != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(userInterface.getPlugin(), () -> {
                if (userInterface.getPlayer().getOpenInventory() == null) ApplyMenu();
            }, 1);
        }
    }

    public Menu GetActiveMenu() {
        return menus.empty() ? null : menus.peek();
    }
}
