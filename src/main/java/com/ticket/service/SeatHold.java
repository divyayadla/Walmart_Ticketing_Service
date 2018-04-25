package com.ticket.service;

import java.util.List;

public class SeatHold {

    /**
     * List of seats that are held
     */
    List<Seat> seats;

    /**
     * Status of all the seats
     */
    boolean isSeatHold;

    /**
     * Variable to store the user info
     */
    String emailId;

    /**
     * Variable to store seatHoldId
     */
    public int seatHoldId;

    /**
     * Stores the holdStatus
     */
    boolean holdStatus = false;
    SeatHold(List<Seat> seats, String emailId) {
        this.seats = seats;
        this.isSeatHold = true;
        this.emailId = emailId;
    }
}
