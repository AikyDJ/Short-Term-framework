package mg.sprintlab.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Finds every class carrying a given annotation inside a package (and its
 * sub-packages).
 * Works for exploded directories (WEB-INF/classes) and for jars (WEB-INF/lib).
 */
public final class ClassScanner {

    private static final String DOT_CLASS = ".class";
    private ClassScanner() {
    }

    public static Set<Class<?>> findAnnotated(String basePackage,
            Class<? extends Annotation> annotation,
            ClassLoader loader) throws IOException {
        String path = basePackage.replace('.', '/');
        Enumeration<URL> urls = loader.getResources(path);
        if (!urls.hasMoreElements()) {
            throw new IllegalStateException("Package not found on the classpath: " + basePackage);
        }

        Set<Class<?>> result = new LinkedHashSet<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                try {
                    scanDirectory(new File(url.toURI()), basePackage, annotation, loader, result);
                } catch (URISyntaxException e) {
                    throw new IOException(e);
                }
            } else if ("jar".equals(protocol)) {
                scanJar(url, path, annotation, loader, result);
            }
        }
        return result;
    }

    private static void scanDirectory(File dir, String pkg,
            Class<? extends Annotation> annotation,
            ClassLoader loader, Set<Class<?>> result) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName();
            if (file.isDirectory()) {
                scanDirectory(file, pkg + "." + name, annotation, loader, result);
            } else if (name.endsWith(DOT_CLASS)) {
                String simpleName = name.substring(0, name.length() - DOT_CLASS.length());
                addIfAnnotated(pkg + "." + simpleName, annotation, loader, result);
            }
        }
    }

    private static void scanJar(URL url, String path,
            Class<? extends Annotation> annotation,
            ClassLoader loader, Set<Class<?>> result) throws IOException {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        connection.setUseCaches(false);
        try (JarFile jar = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path + "/") && name.endsWith(DOT_CLASS)) {
                    String className = name.substring(0, name.length() - DOT_CLASS.length())
                            .replace('/', '.');
                    addIfAnnotated(className, annotation, loader, result);
                }
            }
        }
    }

    private static void addIfAnnotated(String className,
            Class<? extends Annotation> annotation,
            ClassLoader loader, Set<Class<?>> result) {
        try {
            // initialize=false: we only inspect the class, we do not run its static blocks
            Class<?> clazz = Class.forName(className, false, loader);
            if (clazz.isAnnotationPresent(annotation)) {
                result.add(clazz);
            }
        } catch (ClassNotFoundException | LinkageError e) {
            // class that cannot be loaded: ignore it
        }
    }
}
