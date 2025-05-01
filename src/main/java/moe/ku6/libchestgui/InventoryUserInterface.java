package moe.ku6.libchestgui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class InventoryUserInterface {
    protected final UserInterface userInterface;

    public InventoryUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public abstract void OnDropItem(PlayerDropItemEvent event);
    public abstract void OnInventoryClick(InventoryClickEvent e);
    public abstract void OnInventoryClose(InventoryCloseEvent e);
    public abstract void OnInteract(PlayerInteractEvent e);
}
