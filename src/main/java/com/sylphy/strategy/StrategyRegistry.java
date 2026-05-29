package com.sylphy.strategy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 策略注册表，通过扫描策略包发现 ArithmeticProblemStrategy 实现类。
 * @author apple
 */
public final class StrategyRegistry {
    private static final String STRATEGY_PACKAGE = "com.sylphy.strategy";
    private static final String STRATEGY_PACKAGE_PATH = STRATEGY_PACKAGE.replace('.', '/');

    private StrategyRegistry() {
    }

    public static List<ArithmeticProblemStrategy> availableStrategies() {
        List<ArithmeticProblemStrategy> strategies = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(STRATEGY_PACKAGE_PATH);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    loadFromDirectory(classLoader, resource, strategies);
                } else if ("jar".equals(resource.getProtocol())) {
                    loadFromJar(classLoader, resource, strategies);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("failed to scan arithmetic problem strategies", exception);
        }

        strategies.sort(Comparator.comparing(ArithmeticProblemStrategy::key));
        if (strategies.isEmpty()) {
            throw new IllegalStateException("no arithmetic problem strategies found");
        }
        return List.copyOf(strategies);
    }

    private static void loadFromDirectory(ClassLoader classLoader, URL resource,
                                          List<ArithmeticProblemStrategy> strategies) {
        try {
            File directory = new File(resource.toURI());
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".class") && !name.contains("$"));
            if (files == null) {
                return;
            }
            for (File file : files) {
                String className = STRATEGY_PACKAGE + "."
                        + file.getName().substring(0, file.getName().length() - ".class".length());
                addStrategy(classLoader, className, strategies);
            }
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("invalid strategy package path: " + resource, exception);
        }
    }

    private static void loadFromJar(ClassLoader classLoader, URL resource,
                                    List<ArithmeticProblemStrategy> strategies) {
        try {
            JarURLConnection connection = (JarURLConnection) resource.openConnection();
            try (JarFile jarFile = connection.getJarFile()) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (entry.isDirectory()
                            || !name.startsWith(STRATEGY_PACKAGE_PATH + "/")
                            || !name.endsWith(".class")
                            || name.contains("$")) {
                        continue;
                    }
                    String restName = name.substring((STRATEGY_PACKAGE_PATH + "/").length());
                    if (restName.contains("/")) {
                        continue;
                    }
                    String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
                    addStrategy(classLoader, className, strategies);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("failed to scan strategy jar: " + resource, exception);
        }
    }

    private static void addStrategy(ClassLoader classLoader, String className,
                                    List<ArithmeticProblemStrategy> strategies) {
        try {
            Class<?> candidate = Class.forName(className, false, classLoader);
            if (!ArithmeticProblemStrategy.class.isAssignableFrom(candidate)
                    || candidate.isInterface()
                    || Modifier.isAbstract(candidate.getModifiers())) {
                return;
            }
            Object instance = candidate.getDeclaredConstructor().newInstance();
            strategies.add((ArithmeticProblemStrategy) Objects.requireNonNull(instance));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("failed to create strategy: " + className, exception);
        }
    }
}
