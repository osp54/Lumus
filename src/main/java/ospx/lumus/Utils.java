package ospx.lumus;

import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import party.iroiro.luajava.Lua;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static ClassGraph classGraph = new ClassGraph();
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSubclassByName(Class<?> target, String name) {
        return (Class<T>) Structs.find(target.getClasses(), type ->
                Strings.camelToKebab(type.getSimpleName()).equals(Strings.camelToKebab(name)));
    }

    public static List<Class<?>> getClassesInPackages(String... packages) {
        try (ScanResult scanResult = classGraph
                .ignoreClassVisibility()
                .acceptPackages(packages)
                .scan()) {

            return scanResult.getAllClasses().loadClasses().stream().filter(c -> !c.toString().contains("$"))
                    .collect(Collectors.toList());
        }
    }

    public static void handleErrors(Lua lua, Lua.LuaError status, String resourceName) {
        if (status == Lua.LuaError.OK) return;

        if (lua.getTop() != 0 && lua.isString(-1)) {
            Log.err("Error in @: \n@", resourceName, lua.toString(-1));
        }

        lua.setTop(0);
        Throwable e = lua.getJavaError();
        if (e != null) {
            Log.err("Last Java side exception:");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream print = new PrintStream(output);
            e.printStackTrace(print);
            print.flush();
            Log.err(output.toString());
            lua.error((Throwable) null);
        }
    };
}