package net.ccbluex.tenacc.interfaces;

import net.ccbluex.tenacc.impl.server.ServerTestManager;
import org.jetbrains.annotations.NotNull;

public interface IMixinMinecraftServer {
    @NotNull
    ServerTestManager getTestManager();
}
