package org.opentripplanner.standalone;

import java.io.Serializable;

public class TurnRestriction implements Serializable {
    private static final long serialVersionUID = 6072427988268244536L;
    public TurnRestrictionType type;
    public Edge from;
    public Edge to;
    public RepeatingTimePeriod time;
    public TraverseModeSet modes;
    
    @Override
    public String toString() {
        return this.type.name() + " from " + this.from + " to " + this.to + "(" + this.modes + ")";
    }

    public TurnRestriction() {
        this.time = null;
    }

    /**
     * Convenience constructor.
     *
     * @param from
     * @param to
     * @param type
     */
    public TurnRestriction(Edge from, Edge to, TurnRestrictionType type, TraverseModeSet modes) {
        this();
        this.from = from;
        this.to = to;
        this.type = type;
        this.modes = modes;
    }

    /**
     * Return true if the turn restriction is in force at the time described by the long.
     * 
     * @param time
     * @return
     */
    public boolean active(long time) {
        if (this.time != null) { return this.time.active(time); }
        return true;
    }
}
