package ospx.lumus;

import arc.struct.Seq;

@SuppressWarnings("unused")
public class ModConfiguration {
    public ModMetadata meta = new ModMetadata();

    public Runnable onExit = () -> {};
    public Runnable onDispose = () -> {};
    public Runnable onInit = () -> {};

    public ModConfiguration() {}
}