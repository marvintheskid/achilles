package me.marvin.achilles.utils.config;

import lombok.Getter;
import me.marvin.achilles.utils.config.resolver.ConfigResolver;
import me.marvin.achilles.utils.config.resolver.impl.BooleanResolver;
import me.marvin.achilles.utils.config.resolver.impl.IntegerResolver;
import me.marvin.achilles.utils.config.resolver.impl.StringResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

@Getter
public class Config {
    private Map<Class<?>, ConfigResolver<?>> resolverMap;
    private FileConfiguration fileConfiguration;
    private Plugin plugin;
    private String name;
    private File file;

    public Config(String name, Plugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder() + File.separator + name + ".yml");
        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
        this.resolverMap = new HashMap<>();
        this.resolverMap.put(String.class, new StringResolver());
        this.resolverMap.put(Integer.class, new IntegerResolver());
        this.resolverMap.put(int.class, new IntegerResolver());
        this.resolverMap.put(Long.class, new IntegerResolver());
        this.resolverMap.put(long.class, new IntegerResolver());
        this.resolverMap.put(Boolean.class, new BooleanResolver());
        this.resolverMap.put(boolean.class, new BooleanResolver());
    }

    public void saveDefaultConfig() {
        boolean dirResult = plugin.getDataFolder().mkdirs();
        if (!file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }
        loadConfig();
    }

    public void loadConfig() {
        try {
            fileConfiguration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            fileConfiguration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAnnotatedValues(Class<?> clazz) {
        for (Field field : getInheritedPrivateFields(clazz)) {
            if (field.isAnnotationPresent(ConfigPath.class)) {
                String config = field.getAnnotation(ConfigPath.class).config();
                String path = field.getAnnotation(ConfigPath.class).path();
                String fallback = field.getAnnotation(ConfigPath.class).fallback();
                if (!config.equalsIgnoreCase(name)) continue;
                if (!field.isAccessible()) field.setAccessible(true);

                ConfigResolver<?> resolver = resolverMap.get(field.getType());
                if (resolver == null) {
                    plugin.getLogger().warning("Skipping field \"" + field.getName() + "\" in class " + clazz.getName() + " , because it has @ConfigPath annotation, but doesn't have a registered resolver for it's type.");
                    continue;
                }

                try {
                    field.set(clazz, resolver.resolve(fileConfiguration.get(path, fallback)));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }

        for (Method method : getInheritedPrivateMethods(clazz)) {
            if (method.isAnnotationPresent(InitializeAfterConfig.class)) {
                if (!method.getAnnotation(InitializeAfterConfig.class).config().equalsIgnoreCase(name)) continue;
                if (!method.isAccessible()) method.setAccessible(true);
                try {
                    method.invoke(method);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Field> getInheritedPrivateFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        Class<?> i = clazz;
        while (i != null && i != Object.class) {
            result.addAll(Arrays.asList(i.getDeclaredFields()));
            i = i.getSuperclass();
        }
        return result;
    }

    private List<Method> getInheritedPrivateMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        Class<?> i = clazz;
        while (i != null && i != Object.class) {
            result.addAll(Arrays.asList(i.getDeclaredMethods()));
            i = i.getSuperclass();
        }
        return result;
    }
}
