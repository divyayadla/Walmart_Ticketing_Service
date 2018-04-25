package com.ticket.service;

public class Seat implements Comparable<Seat> {

    /**
     * Variable that stores the section or venue level
     */
    SectionLevel sectionLevel;

    /**
     * Variable that stores the first ticket in the row
     */
    int start;

    /**
     * Variable that has count of all the continuous tickets
     */
    int count;

    /**
     * Variable to store the rownum
     */
    int rowNum;

    /**
     * Variable that stores the price of the seat.
     */
    double price;

    /**
     * Status of the seat whether on hold or available
     */
    boolean isHold;

    /**
     * Constructor to int the variables.
     * @param sectionLevel
     * @param totalSeatsInARow
     * @param rowNum
     * @param price
     */
    Seat(SectionLevel sectionLevel, int totalSeatsInARow, int rowNum, double price) {
        this.sectionLevel = sectionLevel;
        this.start = 1;
        this.count = totalSeatsInARow;
        this.rowNum = rowNum;
        this.price = price;
        this.isHold = false;
    }

    /**
     * Compare method which is based on the start value of the seat.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Seat o) {
        return this.start - o.start;
    }
}
