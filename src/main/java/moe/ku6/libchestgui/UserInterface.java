package moe.ku6.libchestgui;

import lombok.Getter;
import moe.ku6.libchestgui.gui.ChestGUI;
import moe.ku6.libchestgui.hotbar.HotbarMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Stack;

public class UserInterface implements Listener {
    @Getter
    private final JavaPlugin plugin;
    @Getter
    protected final Player player;
    @Getter
    private final HotbarMenu menu;
    @Getter
    private final ChestGUI gui;
    private boolean destroyed;
    private int tickTimerHandle = 0;

    public UserInterface(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        menu = new HotbarMenu(this);
        gui = new ChestGUI(this);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        tickTimerHandle = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::Tick, 0L, 1L);
    }

    private void Tick() {
        gui.setClosedThisTick(false);
    }

    public void Update() {
        EnsureValid();
        if (gui.GetActiveForm() != null) {
            gui.Rerender();
            return;
        }
        if (menu.GetActiveMenu() != null) {
            menu.PushMenu(menu.GetActiveMenu());
            return;
        }
    }

    @EventHandler
    private void OnPlayerInteract(PlayerInteractEvent e) {
        if (e.getPlayer() != player) {
            return;
        }
        if (menu.GetActiveMenu() != null) {
            menu.OnInteract(e);
            return;
        }
        if (gui.GetActiveForm() != null) {
            gui.OnInteract(e);
            return;
        }
    }

    @EventHandler
    private void OnInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() != player) {
            return;
        }

        if (gui.GetActiveForm() != null) {
            gui.OnInventoryClick(e);
            return;
        }

        if (menu.GetActiveMenu() != null) {
            menu.OnInventoryClick(e);
            return;
        }
    }

    @EventHandler
    private void OnInventoryClose(InventoryCloseEvent e) {
        if (e.getPlayer() != player) {
            return;
        }

        if (gui.GetActiveForm() != null) {
            gui.OnInventoryClose(e);

            if (menu.GetActiveMenu() != null) {
                menu.PushMenu(menu.GetActiveMenu());
            }

            return;
        }

        if (menu.GetActiveMenu() != null) {
            menu.OnInventoryClose(e);
            return;
        }
    }
    @EventHandler
    private void OnDropItem(PlayerDropItemEvent e) {
        if (e.getPlayer() != player) {
            return;
        }

        if (gui.GetActiveForm() != null) {
            gui.OnDropItem(e);
            return;
        }

        if (menu.GetActiveMenu() != null) {
            menu.OnDropItem(e);
        }
    }

    public void Destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTask(tickTimerHandle);
    }

    protected void EnsureValid() {
        if (destroyed) {
            throw new IllegalStateException("This ChestGUI has been destroyed. Please create a new one.");
        }
    }
}
