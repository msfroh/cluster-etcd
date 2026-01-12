package org.opensearch.cluster.controller.enums;

/**
 * Enum representing the health state of a search unit or node.
 * 
 * <ul>
 *   <li><strong>GREEN</strong> - Node is healthy and functioning normally with active shards</li>
 *   <li><strong>YELLOW</strong> - Node is healthy but inactive (no active shards or degraded performance)</li>
 *   <li><strong>RED</strong> - Node is unhealthy due to resource constraints or failures</li>
 * </ul>
 */
public enum HealthState {
    /**
     * Node is healthy and functioning normally with active shards.
     */
    GREEN,
    
    /**
     * Node is healthy but inactive (no active shards or degraded performance).
     */
    YELLOW,
    
    /**
     * Node is unhealthy due to resource constraints or failures.
     */
    RED;
    
    public String getValue() {
        return name().toLowerCase();
    }
    
    public static HealthState fromString(String value) {
        if (value == null) {
            return null;
        }
        
        String normalizedValue = value.toUpperCase().trim();
        try {
            return HealthState.valueOf(normalizedValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown health state: " + value);
        }
    }
    
    /**
     * Check if this health state indicates the node is healthy enough for allocation.
     */
    public boolean isHealthy() {
        return this == GREEN || this == YELLOW;
    }
}
