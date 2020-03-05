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

/*
 * Copyright (c) 2019-Present marvintheskid (Kovács Márton)
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
