package com.ticket.service;

import java.util.TreeSet;

public class Section {

    /**
     * Variable for storing the row info
     */
    Row[] rows;

    /**
     * Local variable for available seats
     */
    int availableSeats;

    /**
     * Variable to store total seatsina row info
     */
    int totalSeatsInaRow;
    Section(SectionLevel sectionLevel, int rowsCount, int seatsInaRow, double price){
        this.rows = new Row[rowsCount];
        this.totalSeatsInaRow = seatsInaRow;
        for (int i = 0; i < rowsCount; i++) {
            Seat seat = new Seat(sectionLevel, seatsInaRow, i, price);
            rows[i] = new Row(seat);
            availableSeats += seatsInaRow;
        }
    }

    /**
     * Increase seats in the section
     * @param c
     */
    public void increaseSeats(int c) {
        availableSeats += c;
    }

    /**
     * Decrease searts in the section
     * @param c
     */
    public void decreaseSeats(int c) {
        if (c > availableSeats) {
            return;
        }
        availableSeats -= c;
    }

    /**
     * Gets total seats that are avaialble
     * @return
     */
    public int getAvailableSeats() {
        return availableSeats;
    }

}

class Row {
    /**
     * Variable to store all the seats in a row
     */
    TreeSet<Seat> seats;
    Row(Seat seat) {
        seats = new TreeSet<>();
        seats.add(seat);
    }
}