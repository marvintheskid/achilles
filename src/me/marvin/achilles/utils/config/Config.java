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

/*
 * Copyright (c) 2019 marvintheskid (Kovács Márton)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

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
        boolean ignored = plugin.getDataFolder().mkdirs();
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
        loadAnnotatedValues(clazz, false);
    }

    public void loadAnnotatedValues(Class<?> clazz, boolean deep) {
        List<Field> fields = new ArrayList<>();

        if (deep) {
            getFieldsRecursively(clazz, fields);
        } else {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigPath.class)) {
                String config = field.getAnnotation(ConfigPath.class).config();
                String def = field.getAnnotation(ConfigPath.class).def();
                String path = field.getAnnotation(ConfigPath.class).path();
                if (!config.equalsIgnoreCase(name)) continue;
                if (!field.isAccessible()) field.setAccessible(true);

                ConfigResolver<?> resolver = resolverMap.get(field.getType());
                if (resolver == null) {
                    plugin.getLogger().warning("Skipping field \"" + field.getName() + "\" in class " + clazz.getName() + " , because it has @ConfigPath annotation, but doesn't have a registered resolver for it's type.");
                    continue;
                }

                try {
                    Object val = resolver.resolve(fileConfiguration.get(path, def));
                    field.set(clazz, val);
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }

        List<Method> methods = new ArrayList<>();

        if (deep) {
            getMethodsRecursively(clazz, methods);
        } else {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }

        for (Method method : methods) {
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

    private void getFieldsRecursively(Class<?> clazz, List<Field> list) {
        for (Class<?> other : clazz.getDeclaredClasses()) {
            getFieldsRecursively(other, list);
            list.addAll(Arrays.asList(other.getDeclaredFields()));
        }
    }

    private void getMethodsRecursively(Class<?> clazz, List<Method> list) {
        for (Class<?> other : clazz.getDeclaredClasses()) {
            getMethodsRecursively(other, list);
            list.addAll(Arrays.asList(other.getDeclaredMethods()));
        }
    }
}
