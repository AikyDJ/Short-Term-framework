package mg.sprintlab.web;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * The session as the DEVELOPER sees it. The developer only declares a parameter
 *
 *     public ModelView login(Session session, @RequestParam("name") String name) {
 *         session.setAttribute("user", name);
 *     }
 *
 * and never touches HttpSession. The framework injects it (see ClassParmScanner).
 *
 * This class is a thin wrapper around the real HttpSession: no copy, no synchronisation,
 * a single source of truth. Reading never creates a session (no useless JSESSIONID cookie);
 * writing creates it when needed.
 */
public class Session {

    private final HttpServletRequest request;
    private String role = "USER"; // default role for the session, can be modified as needed

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public Session(HttpServletRequest request) {
        this.request = Objects.requireNonNull(request, "request");
    }

    /** The existing HttpSession, or null if the client has none yet. */
    private HttpSession existing() {
        return request.getSession(false);
    }

    // ---------------------------------------------------------------- read (never creates a session)

    public Object getAttribute(String key) {
        HttpSession session = existing();
        return session == null ? null : session.getAttribute(key);
    }

    /** Typed read: User user = session.getAttribute("user", User.class); */
    public <T> T getAttribute(String key, Class<T> type) {
        Object value = getAttribute(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalStateException("Session attribute '" + key + "' is a "
                    + value.getClass().getName() + ", not a " + type.getName());
        }
        return type.cast(value);
    }

    public boolean hasAttribute(String key) {
        return getAttribute(key) != null;
    }

    /** Read-only snapshot of all the attributes. */
    public Map<String, Object> getAttributes() {
        HttpSession session = existing();
        if (session == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        for (String name : Collections.list(session.getAttributeNames())) {
            copy.put(name, session.getAttribute(name));
        }
        return Collections.unmodifiableMap(copy);
    }

    /** null if there is no session yet. */
    public String getSessionId() {
        HttpSession session = existing();
        return session == null ? null : session.getId();
    }

    /** -1 if there is no session yet. */
    public long getCreationTime() {
        HttpSession session = existing();
        return session == null ? -1 : session.getCreationTime();
    }

    /** -1 if there is no session yet. */
    public long getLastAccessedTime() {
        HttpSession session = existing();
        return session == null ? -1 : session.getLastAccessedTime();
    }

    // ---------------------------------------------------------------- write (creates the session if needed)

    /** A null value removes the attribute (same as HttpSession). */
    public void setAttribute(String key, Object value) {
        request.getSession(true).setAttribute(key, value);
    }

    public void removeAttribute(String key) {
        HttpSession session = existing();
        if (session != null) {
            session.removeAttribute(key);
        }
    }

    /** Logout: destroys the session and everything in it. */
    public void invalidate() {
        HttpSession session = existing();
        if (session != null) {
            session.invalidate();
        }
    }
}