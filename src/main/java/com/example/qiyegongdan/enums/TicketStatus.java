package com.example.qiyegongdan.enums;

public enum TicketStatus {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    PENDING_APPROVAL,
    RESOLVED,
    CLOSED,
    REOPENED;

    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case PENDING -> target == ASSIGNED || target == CLOSED;
            case ASSIGNED -> target == IN_PROGRESS || target == CLOSED;
            case IN_PROGRESS -> target == PENDING_APPROVAL || target == RESOLVED || target == CLOSED;
            case PENDING_APPROVAL -> target == CLOSED || target == IN_PROGRESS;
            case RESOLVED -> target == CLOSED || target == REOPENED;
            case CLOSED -> target == REOPENED;
            case REOPENED -> target == ASSIGNED || target == IN_PROGRESS || target == CLOSED;
        };
    }
}