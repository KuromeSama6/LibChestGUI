package moe.ku6.libchestgui.gui;

import org.bukkit.event.player.PlayerDropItemEvent;

public interface IPostDropItemHandler {
    void Handle(PlayerDropItemEvent e);
}
