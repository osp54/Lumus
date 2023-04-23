package ospx.lumus;

import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Stats;
import party.iroiro.luajava.Lua;

public class LuaGlobals {
    private static final Class<?>[] pushClasses = {
            Log.class, Items.class, Blocks.class, Block.class, Liquids.class, UnitTypes.class, Vars.class, Core.class,
            Call.class, Events.class, EventType.class, Stats.class, ConsumeLiquidFilter.class, Wall.class, Category.class,
            ItemStack.class, BuildVisibility.class, ItemTurret.class, BasicBulletType.class, Time.class
    };
    public static void init(Lua lua) {
        for (Class<?> clazz : pushClasses) {
            pushClass(lua, clazz);
        }

        for (Class<?> clazz : EventType.class.getClasses()) {
            pushClass(lua, clazz);
        }
    }

    public static void pushClass(Lua lua, Class<?> clazz) {
        lua.pushJavaClass(clazz);
        lua.setGlobal(clazz.getSimpleName());
    }
}
