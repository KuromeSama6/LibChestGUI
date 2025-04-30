package moe.ku6.libchestgui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class InventoryUserInterface implements Listener {
    @Getter
    private final JavaPlugin plugin;
    @Getter
    protected final Player player;
    private boolean destroyed;

    public InventoryUserInterface(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void Destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        HandlerList.unregisterAll(this);
    }

    protected void EnsureValid() {
        if (destroyed) {
            throw new IllegalStateException("This ChestGUI has been destroyed. Please create a new one.");
        }
    }
}
