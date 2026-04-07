package app.javaJostle;

/**
 * RestrictedRobotClassLoader is defined as a nested class within Utilities.java
 * It sandboxes robot classes by:
 * - Blocking: java.lang.reflect.*, java.lang.Thread, java.util.concurrent.*
 * - Allowing: Common java.util/java.io classes needed for robot logic
 * - Restricting: Access to internal app.javaJostle classes (only Robot, Map, etc.)
 */
