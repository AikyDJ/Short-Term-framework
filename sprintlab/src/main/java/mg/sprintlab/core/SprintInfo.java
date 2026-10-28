package mg.sprintlab.core;

/**
 * Sprint 0 smoke test: lets the client project prove it can see the framework jar.
 * Will be removed or kept as a small utility once the real classes exist.
 */
public final class SprintInfo {

    public static final String NAME = "sprintlab";
    public static final String VERSION = "1.0-SNAPSHOT";

    private SprintInfo() {
    }

    public static String banner() {
        return NAME + " " + VERSION + " (Sprint 0)";
    }
}
