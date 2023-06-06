package ospx.lumus;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.core.GameState;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.type.Category;
import mindustry.world.Block;
import party.iroiro.luajava.JFunction;
import party.iroiro.luajava.Lua;

import java.util.ArrayList;
import java.util.List;

public class LuaGlobals {
    private static final Class<?>[] MINDUSTRY_CLASSES = {
            Log.class, Items.class, Blocks.class, Block.class, UnitTypes.class, Vars.class, Core.class,
            Call.class, Events.class, GameState.class, Groups.class, EventType.class, Category.class,
            Time.class
    };
    private final Lua context;
    private List<String> packagesToImport = new ArrayList<>();
    private ModConfiguration config;

    public LuaGlobals(Lua context) {
        this.context = context;
    }
    public LuaGlobals(Lua context, ModConfiguration config) {
        this.context = context;
        this.config = config;
    }

    public void pushAll() {
        pushStandardClasses();

        context.push(l -> LumusLuaAPI.pluginConfiguration(l, config));
        context.setGlobal("pluginConfiguration");

        context.createTable(0, 2);
        context.push(l -> LumusLuaAPI.command(l, Command.CommandType.CLIENT));
        context.setField(-2, "clientCommand");

        context.push(l -> LumusLuaAPI.command(l, Command.CommandType.SERVER));
        context.setField(-2, "serverCommand");
        context.setGlobal("commands");

        context.createTable(0, 4);

        context.push(LumusLuaAPI::event);
        context.setField(-2, "eventListener");

        context.push(l -> LumusLuaAPI.setOnInit(l, config));
        context.setField(-2, "onInit");

        context.push(l -> LumusLuaAPI.setOnExit(l, config));
        context.setField(-2, "onExit");

        context.push(l -> LumusLuaAPI.setOnDispose(l, config));
        context.setField(-2, "onDispose");

        context.setGlobal("events");

        pushFunction("importPackage", l -> {
            for (int i = 1; i <= l.getTop(); i++) {
                packagesToImport.add(l.toString(i));
            }
            l.pushNil();
            return 1;
        });

        pushFunction("doImport", l -> {
            long start = Time.millis();
            for (Class<?> clazz : Utils.getClassesInPackages(packagesToImport.toArray(new String[0]))) {
                pushClass(clazz);
            }
            Log.debug("Imported packages in @ms, @", Time.millis() - start, packagesToImport);
            packagesToImport.clear();
            return 1;
        });
    }
    public void pushStandardClasses() {
        for (Class<?> clazz : MINDUSTRY_CLASSES) {
            pushClass(clazz);
        }

        for (Class<?> clazz : EventType.class.getClasses()) {
            pushClass(clazz);
        }
    }

    public void pushClass(Class<?> clazz) {
        context.pushJavaClass(clazz);
        context.setGlobal(clazz.getSimpleName());
    }

    public void pushFunction(String name, JFunction func) {
        context.push(func);
        context.setGlobal(name);
    }
}
