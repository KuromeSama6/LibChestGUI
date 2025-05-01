package moe.ku6.libchestgui.gui;

public interface IActionHandler {
    void Handle(ActionContext ctx);

    static IActionHandler Deny() {
        return ctx -> {
//            System.out.println("Deny");
            ctx.Deny();
        };
    }

    static IActionHandler Allow() {
        return ctx -> {
//            System.out.println("Allow");
            ctx.Allow();
        };
    }

    static IActionHandler TakeOnly() {
        return ctx -> {
            var action = ctx.getEvent().getAction();
//            System.out.println("TakeOnly action=%s".formatted(action));
            switch (action) {
                case PICKUP_ALL, PICKUP_ONE, PICKUP_HALF, PICKUP_SOME -> {
                    ctx.Allow();
                    return;
                }
                default -> {
                    ctx.Deny();
                }
            }
        };
    }

    static IActionHandler Do(Runnable runnable) {
        return ctx -> {
            runnable.run();
            ctx.Deny();
        };
    }

    static IActionHandler DoAndClose(Runnable runnable) {
        return ctx -> {
            runnable.run();
            ctx.Pop();
        };
    }
}
