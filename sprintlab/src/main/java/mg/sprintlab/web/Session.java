package mg.sprintlab.web;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private String sessionId;
    private long creationTime;
    private long lastAccessedTime;
    private Map<String, Object> attributes = new HashMap<>();

    public Session(String sessionId, long creationTime, long lastAccessedTime, Map<String, Object> attributes) {
        this.sessionId = sessionId;
        this.creationTime = creationTime;
        this.lastAccessedTime = lastAccessedTime;
        this.attributes = attributes;
    }

    public Session(String sessionId) {
        this.sessionId = sessionId;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = this.creationTime;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }


    public String getSessionId() {
        return sessionId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void updateLastAccessedTime() {
        this.lastAccessedTime = System.currentTimeMillis();
    }
}
