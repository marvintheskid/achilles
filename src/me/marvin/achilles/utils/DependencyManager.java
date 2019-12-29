package me.marvin.achilles.utils;

import lombok.Getter;
import me.marvin.achilles.Achilles;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.HashMap;

@Getter
public class DependencyManager {
    private static Method ADD_URL_METHOD;
    private static HashMap<String, URL> DEPENDENCIES = new HashMap<>();
    private static final Path DIRECTORY_PATH = Paths.get(Achilles.getInstance().getDataFolder().getAbsolutePath(), "libraries");

    static {
        try {
            DEPENDENCIES.put("jedis-3_1_0.jar", new URL("https://repo1.maven.org/maven2/redis/clients/jedis/3.1.0/jedis-3.1.0.jar"));
            DEPENDENCIES.put("hikaricp-3_4_1.jar", new URL("https://repo1.maven.org/maven2/com/zaxxer/HikariCP/3.4.1/HikariCP-3.4.1.jar"));
        } catch (MalformedURLException ignored) {}

        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadDependencies() {
        DIRECTORY_PATH.toFile().mkdirs();

        DEPENDENCIES.forEach((k, v) -> {
            Path filePath = Paths.get(Achilles.getInstance().getDataFolder().getAbsolutePath(), "libraries", k);
            File file = filePath.toFile();
            if (!file.exists()) {
                try {
                    Achilles.getInstance().getLogger().info("[Dependency] Attempting to download " + k + "...");
                    try (InputStream is = v.openStream()) {
                        Files.copy(is, filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIRECTORY_PATH)) {
                for (Path path : stream) {
                    File file = path.toFile();
                    if (DEPENDENCIES.containsKey(file.getName())) {
                        URL url = file.toURI().toURL();
                        Achilles.getInstance().getLogger().info("[Dependency] Loading dependency " + file.getName() + "...");
                        URLClassLoader classLoader = ((URLClassLoader) Achilles.class.getClassLoader());
                        ADD_URL_METHOD.invoke(classLoader, url);
                    } else {
                        Achilles.getInstance().getLogger().info("[Dependency] Skipping " + file.getName() + "...");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
