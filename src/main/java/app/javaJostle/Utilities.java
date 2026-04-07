package app.javaJostle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

/**
 * RestrictedRobotClassLoader isolates robot classes from reflection access while
 * allowing normal method calls to work. Robots loaded by this loader cannot:
 * - Use Class.forName() to load app.javaJostle classes
 * - Use reflection to inspect each other or framework classes
 * 
 * But they can:
 * - Call public/protected methods on objects passed to them normally
 * - Access inherited methods from Robot base class
 */
class RestrictedRobotClassLoader extends URLClassLoader {
    private final String allowedRobotClass; // Only this robot class can be loaded from resources

    public RestrictedRobotClassLoader(URL[] urls, ClassLoader parent, String robotClassName) {
        super(urls, parent);
        this.allowedRobotClass = robotClassName;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Allow loading this robot class (and its inner classes) from the isolated resources first.
        if (name.equals(allowedRobotClass) || name.startsWith(allowedRobotClass + "$")) {
            synchronized (getClassLoadingLock(name)) {
                Class<?> alreadyLoaded = findLoadedClass(name);
                if (alreadyLoaded != null) {
                    return alreadyLoaded;
                }
                Class<?> robotClass = findClass(name);
                if (resolve) {
                    resolveClass(robotClass);
                }
                return robotClass;
            }
        }

        // Block reflection and introspection APIs for robot code.
        if (name.startsWith("java.lang.reflect.") ||
            name.startsWith("java.lang.invoke.") ||
            name.startsWith("jdk.internal.reflect.") ||
            name.startsWith("sun.reflect.") ||
            name.equals("sun.misc.Unsafe") ||
            name.equals("jdk.internal.misc.Unsafe")) {
            throw new ClassNotFoundException("Access denied: Reflection is not allowed. Class: " + name);
        }

        // Block thread creation APIs for robot code.
        if (name.equals("java.lang.Thread") ||
            name.startsWith("java.lang.Thread$") ||
            name.equals("java.lang.Runnable") ||
            name.startsWith("java.lang.ThreadGroup") ||
            name.startsWith("java.lang.ThreadLocal") ||
            name.startsWith("java.util.concurrent.") ||
            name.equals("java.util.Timer") ||
            name.equals("java.util.TimerTask")) {
            throw new ClassNotFoundException("Access denied: Threads are not allowed. Class: " + name);
        }

        // Allow everything else through normal parent delegation.
        return super.loadClass(name, resolve);
    }
}

public class Utilities {
    public static BufferedImage WALL_IMAGE;
    public static BufferedImage GRASS_IMAGE;
    public static BufferedImage MUD_IMAGE;
    public static BufferedImage ROBOT_ERROR;
    public static BufferedImage DEFAULT_PROJECTILE_IMAGE;
    public static BufferedImage HEALTH_PACK_IMAGE;
    public static BufferedImage SPEED_PACK_IMAGE;
    public static BufferedImage ATTACK_PACK_IMAGE;

    public static final int WALL = 0;
    public static final int GRASS = 1;
    public static final int MUD = 2;

    public static final double POWER_UP_SPAWN_CHANCE = 0.003;

    public static final int GAME_DELAY = 5;

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    public static final int TILE_SIZE = 32; // Default tile size
    public static final int PROJECTILE_SIZE = 10; // Default projectile size
    public static final int POWER_UP_SIZE = 20; // Default power-up size
    public static final int ROBOT_SIZE = 28; // Default robot size

    public static ArrayList<Integer> keysPressed = new ArrayList<>();
    
    // Each robot class is loaded by its own isolated RestrictedRobotClassLoader
    private static Map<String, Class<?>> loadedRobotClasses = new HashMap<>();
    private static Map<String, RestrictedRobotClassLoader> robotLoaders = new HashMap<>();
    private static final Map<String, BufferedImage> imageCache = new ConcurrentHashMap<>();
    private static final String ROBOT_PACKAGE = "app.javaJostle";

    public static BufferedImage loadImage(String imgName) {
        if (imgName == null) {
            return null;
        }

        BufferedImage cached = imageCache.get(imgName);
        if (cached != null) {
            return cached;
        }

        try {
            // Ensure the full path is constructed correctly
            BufferedImage img = ImageIO.read(new File("src/main/resources/images/" + imgName));
            BufferedImage cropped = cropToContent(img);
            imageCache.put(imgName, cropped);
            return cropped;
        } catch (IOException e) {
            System.err.println("Failed to load image: " + imgName + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void loadImages() {
        WALL_IMAGE = loadImage("wall.png");
        GRASS_IMAGE = loadImage("grass.png");
        MUD_IMAGE = loadImage("mud.png");
        ROBOT_ERROR = loadImage("robotError.png");
        DEFAULT_PROJECTILE_IMAGE = loadImage("defaultProjectile.png");

        if (WALL_IMAGE == null) {
            System.err.println("WALL_IMAGE could not be loaded.");
        }
        if (GRASS_IMAGE == null) {
            System.err.println("GRASS_IMAGE could not be loaded.");
        }
        if (MUD_IMAGE == null) {
            System.err.println("MUD_IMAGE could not be loaded.");
        }
    }

    public static BufferedImage cropToContent(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();

        int minX = width;
        int minY = height;
        int maxX = 0;
        int maxY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = src.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;

                if (alpha > 0) {
                    if (x < minX)
                        minX = x;
                    if (y < minY)
                        minY = y;
                    if (x > maxX)
                        maxX = x;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }

        return src.getSubimage(minX, minY, (maxX - minX + 1), (maxY - minY + 1));
    }

    public static void handleKeyPressed(int keyCode) {
        if (!keysPressed.contains(keyCode)) {
            keysPressed.add(keyCode);
        }
    }

    public static void handleKeyReleased(int keyCode) {
        for (int i = 0; i < keysPressed.size(); i++) {
            if (keysPressed.get(i) == keyCode) {
                keysPressed.remove(i);
                break;
            }
        }
    }

    // For testing purposes only
    private static boolean[] testKeyStates = new boolean[256];

    // Test helper methods
    public static void resetKeyStates() {
        Arrays.fill(testKeyStates, false);
    }

    public static void setKeyPressed(int keyCode, boolean isPressed) {
        if (keyCode < testKeyStates.length) {
            testKeyStates[keyCode] = isPressed;
        }
    }

    private static boolean inTestMode = false;

    public static void setTestMode(boolean enabled) {
        inTestMode = enabled;
    }

    public static boolean isKeyPressed(int keyCode) {
        if (inTestMode) {
            return keyCode < testKeyStates.length && testKeyStates[keyCode];
        } else {
            // Original implementation
            return keysPressed.contains(keyCode);
        }
    }

    /**
     * Preload all robot classes from the robots resource directory.
     * Each robot is isolated in its own RestrictedRobotClassLoader.
     */
    public static void preloadRobotClasses() {
        File robotsResourceDir = new File("src/main/resources/robots");
        loadedRobotClasses.clear();
        robotLoaders.clear();
        
        if (!robotsResourceDir.exists() || !robotsResourceDir.isDirectory()) {
            System.err.println("Robots resource directory not found: " + robotsResourceDir.getAbsolutePath());
            return;
        }
        
        try {
            // Recursively find and load all .class files as package-qualified names.
            loadClassesFromDirectory(robotsResourceDir, robotsResourceDir);
            
            System.out.println("Preloaded " + loadedRobotClasses.size() + " robot classes in isolated loaders");
        } catch (Exception e) {
            System.err.println("Failed to preload robot classes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Recursively load classes from a directory, each in its own isolated RestrictedRobotClassLoader.
     */
    private static void loadClassesFromDirectory(File dir, File rootDir) {
        if (!dir.isDirectory()) {
            return;
        }
        
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                loadClassesFromDirectory(file, rootDir);
            } else if (file.isFile() && file.getName().endsWith(".class")) {
                String relativeClassPath = rootDir.toPath().relativize(file.toPath()).toString();
                String fullClassName = relativeClassPath
                        .replace(File.separatorChar, '.')
                    .replaceAll("\\.class$", "");

                // Skip inner classes and non-robot classes
                if (!fullClassName.startsWith(ROBOT_PACKAGE + ".") || fullClassName.contains("$")) {
                    continue;
                }

                String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
                
                try {
                    // Create an isolated RestrictedRobotClassLoader for this specific robot
                    URL robotsUrl = rootDir.toURI().toURL();
                    RestrictedRobotClassLoader isolatedLoader = new RestrictedRobotClassLoader(
                            new URL[] { robotsUrl }, 
                            Utilities.class.getClassLoader(),
                            fullClassName
                    );
                    
                    // Store the loader to keep it alive and prevent garbage collection
                    robotLoaders.put(fullClassName, isolatedLoader);
                    
                    // Load the robot class using its isolated loader
                    Class<?> loadedClass = isolatedLoader.loadClass(fullClassName);

                    if (!Robot.class.isAssignableFrom(loadedClass)) {
                        continue;
                    }

                    loadedClass.getConstructor(int.class, int.class);
                    loadedRobotClasses.put(className, loadedClass);
                    System.out.println("Preloaded robot in isolated loader: " + fullClassName + " (cached as " + className + ")");
                } catch (Throwable t) {
                    System.err.println("Failed to preload class " + fullClassName + ": " + t.getMessage());
                }
            }
        }
    }

    public static ArrayList<String> getLoadedRobotNames() {
        ArrayList<String> names = new ArrayList<>(loadedRobotClasses.keySet());
        Collections.sort(names);
        return names;
    }
    
    /**
     * Get a preloaded robot class by simple name.
     */
    public static Class<?> getRobotClass(String className) {
        return loadedRobotClasses.get(className);
    }

    public static Robot createRobot(int x, int y, String className) {
        // Get the preloaded class from cache
        Class<?> loadedClass = getRobotClass(className);
        
        if (loadedClass == null) {
            System.err.println("Robot class not preloaded: " + className);
            return null;
        }
        
        try {
            // loadedRobotClasses only contains validated Robot classes.
            Constructor<?> constructor = loadedClass.getConstructor(int.class, int.class);
            Robot robot = (Robot) constructor.newInstance(x, y);

            System.out.println("Created robot instance: " + className);
            return robot;
        } catch (NoSuchMethodException e) {
            System.err.println("Constructor (int, int) not found for " + className + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error instantiating robot class " + className + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}