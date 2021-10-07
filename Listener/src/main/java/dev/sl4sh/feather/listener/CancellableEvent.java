package dev.sl4sh.feather.listener;

public class CancellableEvent implements FeatherEvent {

    private boolean cancelled = false;
    private String cancelReason = "";

    public boolean isCancelled() { return this.cancelled; }

    public void setCancelled(boolean cancelled, String reason) {
        this.cancelled = cancelled;
        cancelReason = cancelled ? reason : "";
    }

    public String getCancelReason() {
        return cancelReason;
    }

}
