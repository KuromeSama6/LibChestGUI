package moe.ku6.libchestgui.gui;

public interface IActionHandler {
    void Handle(ActionContext ctx);

    static IActionHandler Deny() {
        return ActionContext::Deny;
    }
}
