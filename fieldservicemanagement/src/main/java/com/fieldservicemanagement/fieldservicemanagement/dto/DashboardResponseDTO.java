package com.fieldservicemanagement.fieldservicemanagement.dto;

public class DashboardResponseDTO {

    private long totalWorkOrders;
    private long newWorkOrders;
    private long assignedWorkOrders;
    private long inProgressWorkOrders;
    private long onHoldWorkOrders;
    private long completedWorkOrders;
    private long closedWorkOrders;
    private long cancelledWorkOrders;

    public DashboardResponseDTO() {
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
    }

    public long getNewWorkOrders() {
        return newWorkOrders;
    }

    public void setNewWorkOrders(long newWorkOrders) {
        this.newWorkOrders = newWorkOrders;
    }

    public long getAssignedWorkOrders() {
        return assignedWorkOrders;
    }

    public void setAssignedWorkOrders(long assignedWorkOrders) {
        this.assignedWorkOrders = assignedWorkOrders;
    }

    public long getInProgressWorkOrders() {
        return inProgressWorkOrders;
    }

    public void setInProgressWorkOrders(long inProgressWorkOrders) {
        this.inProgressWorkOrders = inProgressWorkOrders;
    }

    public long getOnHoldWorkOrders() {
        return onHoldWorkOrders;
    }

    public void setOnHoldWorkOrders(long onHoldWorkOrders) {
        this.onHoldWorkOrders = onHoldWorkOrders;
    }

    public long getCompletedWorkOrders() {
        return completedWorkOrders;
    }

    public void setCompletedWorkOrders(long completedWorkOrders) {
        this.completedWorkOrders = completedWorkOrders;
    }

    public long getClosedWorkOrders() {
        return closedWorkOrders;
    }

    public void setClosedWorkOrders(long closedWorkOrders) {
        this.closedWorkOrders = closedWorkOrders;
    }

    public long getCancelledWorkOrders() {
        return cancelledWorkOrders;
    }

    public void setCancelledWorkOrders(long cancelledWorkOrders) {
        this.cancelledWorkOrders = cancelledWorkOrders;
    }
}