package com.ticket.service;

import java.util.*;

public class TicketServiceImpl implements TicketService {

    /**
     * Local variable used to store venue details
     */
    TreeMap[] venue;

    /**
     * Variable that stores levels
     */
    int levels;

    /**
     * Section array that holds the section object in a seat level
     */
    Section[] arr;

    /**
     * Hashmap is used to store the seats that are on hold by the system.
     * With key being the size of the continuous seats next to each other and
     * Value being the seathold object that contains list of seats that are on hold
     */
    Map<Integer, SeatHold> holdMap = new HashMap<>();

    /**
     * Hold time after which the hold on the tickets is removed and they are made available
     */
    int holdTime;

    /**
     * Lock object that will have lock on the hashmap and booking system.
     */
    Lock lock;

    /**
     * Hold id which gets incremented on each hold request.
     */
    int holdId = 1;

    /**
     * Constructor method to initialize locks and venue and finallyu calls loadData method.
     *
     information
     */
    public TicketServiceImpl() {
        this.lock = Lock.getLockObj();
        this.levels = SectionLevel.values().length;
        this.arr = new Section[levels];
        this.venue = new TreeMap[levels];
        loadData();
    }

    /**
     * Load sample data.
     *
    information
     */
    private void loadData() {
        // initiailize the hold time for 5 secs
        this.holdTime = 5;

        /**
         * initialize the section with values
         */
        arr[0] = new Section(SectionLevel.values()[0], 25,50,100);
        arr[1] = new Section(SectionLevel.values()[1], 20,100,75);
        arr[2] = new Section(SectionLevel.values()[2], 15,100,50);
        arr[3] = new Section(SectionLevel.values()[3], 15,100,40);

        /**
         * iterate all the levels and initialize the treemap which contains the
         * key as size of the continuous seat and values as a alist containing all such seats.
         */
        for (int i = 0; i < levels; i++) {
            Section sec = arr[i];
            venue[i] = new TreeMap<Integer, List<Seat>>();
            TreeMap<Integer, List<Seat>> map = venue[i];
            for (Row row : sec.rows) {
                Seat curr = null;
                for (Seat seat : row.seats) {
                    curr = seat;
                }
                List<Seat> seats = map.getOrDefault(sec.totalSeatsInaRow, new ArrayList<>());
                seats.add(curr);
                map.put(sec.totalSeatsInaRow, seats);
            }
        }
    }

    /**
     * Gets the total no of tickets that are available.
     *
     * @param venueLevel a numeric venue level identifier to limit the search
     * @return num of seats available in the section, if no venuelevel is given
     * then it returns the avaialability of all the sections
     */
    @Override
    public int numSeatsAvailable(Optional<Integer> venueLevel) {
        // checks if venuelevel is given
        if (venueLevel.isPresent()) {
            return arr[venueLevel.get()-1].getAvailableSeats();
        }

        // if venue level is not given then it return the total no of tickets in all the levels
        int sum = 0;
        for (int i = 0; i< levels; i++) {
            sum += arr[i].getAvailableSeats();
        }
        return sum;
    }

    /**
     * Find and hold the best available seats for a customer based on
     * the min and max venuelevel given
     *
     * @param numSeats the number of seats to find and hold
     * @param minLevel the minimum venue level
     * @param maxLevel the maximum venue level
     * @param customerEmail unique identifier for the customer
     * @return it returns SeatHold object which will have the information of the seats
     * that are on hold and if no seats are held then return empty string for the emailId.
     */
    @Override
    public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail) {

        // validate the input parameters.
        if (numSeats < 1) return new SeatHold(new ArrayList<>(), customerEmail);
        int min = 0, max = levels-1;
        if (minLevel.isPresent()) min = minLevel.get()-1;
        if (maxLevel.isPresent()) max = maxLevel.get()-1;

        // fetches the section value that has the availability of all the tickets.
        int levelId = findSectionWithTickets(min, max, numSeats);

        if (levelId < min) {
            // if there is no availability on the section
            return new SeatHold(new ArrayList<>(), customerEmail);
        } else if (levelId > max) {
            // if there is a possibility in multiple sections
            return getSeatsInaSection(min, max, numSeats, customerEmail);
        } else {
            // if there is a possibility in a single section
            return getSeatsInaSection(levelId, levelId, numSeats, customerEmail);
        }
    }

    /**
     * Find section id which can accomodate all the tickets.
     *
     * @param min min venue level
     * @param max max venue level
     * @param n total no of tickets to book
     * @return return section level that can accomodate all the tickets
     */
    private int findSectionWithTickets(int min, int max, int n) {
        int sum = 0;

        // while iterating find if multiple sections can accomodate the seats.
        for (int i = min; i <= max; i++) {

            // if availavility of a section is enough then return that particular section id
            if (arr[i].getAvailableSeats() >= n) return i;
            sum += arr[i].getAvailableSeats();
        }
        // return max+1 if multiple sections can accomodate the seats
        if (sum >= n) {
            return max+1;
        }
        // if it cannot then return value less than min.
        return min-1;
    }

    /**
     * Gets the seats in a section range.
     * This method is called by findAndHoldSeats after check the availability for the request.
     *
     * @param min min venue level
     * @param max max venue level
     * @param n total no of seats to hold
     * @param emailId for which the tickets needs to be hold.
     * @return SeatHold object which will have information about the seatHold status
     * and the seats that are on hold
     */
    private SeatHold getSeatsInaSection(int min, int max, int n, String emailId) {
        List<Seat> list = new ArrayList<>();
        for (int i = min; i <= max; i++) {

            // Get the treemap containing seats info from venue
            TreeMap<Integer, List<Seat>> tm = venue[i];

            // iterate until the total no of tickets are covered
            while (n > 0) {
                Integer key = tm.ceilingKey(n);

                // check if there contains a group of seats greater than the available tickets or not
                if (key == null) {
                    key = tm.floorKey(n);
                    if (key == null) break;
                    List<Seat> seatList = tm.get(key);

                    // remove the seats from the treemap if there are goen to hold status.
                    if(tm.get(key).size() == 1) {
                        tm.remove(key);
                    }
                    Seat seat1 = seatList.remove(seatList.size()-1);
                    list.add(seat1);

                    // mark the status og the seat.
                    seat1.isHold = true;
                    n -= seat1.count;

                    // finally decrease the seats count.
                    arr[i].decreaseSeats(seat1.count);
                    Row row = arr[i].rows[seat1.rowNum];

                    // also remove the seats from the row.
                    row.seats.remove(seat1);
                } else {

                    // if the system can have accommodate the remaining tickets
                    List<Seat> seats = tm.get(key);
                    if (tm.get(key).size() == 1) {
                        tm.remove(key);
                    }

                    // finally remove current seat and mark the status to hold
                    Seat seat1 = seats.remove(seats.size()-1);
                    seat1.isHold = true;
                    seat1.count = n;

                    // decrement the seats count in the section
                    arr[i].decreaseSeats(seat1.count);
                    int rem = key - n;
                    n = 0;

                    // also remove the seats from the row.
                    Row row = arr[i].rows[seat1.rowNum];
                    row.seats.remove(seat1);
                    Seat newSeat = new Seat(SectionLevel.values()[i], rem, seat1.rowNum, seat1.price);
                    List<Seat> l = tm.getOrDefault(newSeat.count, new ArrayList<>());
                    row.seats.add(newSeat);
                    l.add(newSeat);
                    tm.put(newSeat.count, l);

                    // finally add the seat to seathold object
                    list.add(seat1);
                }
            }
        }

        // set the seathold status to true.
        SeatHold seatHold = new SeatHold(list, emailId);
        seatHold.holdStatus = true;
        try {

            // fetch the lock for the resource
            lock.lock();
            holdId++;
            holdMap.put(holdId, seatHold);
            seatHold.seatHoldId = holdId;

            // assign the hold removal after a time to another thread.
            ThreadImpl thread = new ThreadImpl(holdTime, holdId, this);
            thread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // unlock the resource
            lock.unlock();
        }
        return seatHold;
    }

    /**
     * Reserves the seats for a user
     *
     * @param seatHoldId the seat hold identifier
     * @param customerEmail the email address of the customer to which the seat hold
    is assigned
     * @return
     */
    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        String res = "";
        try {

            // fetch the lock over the resource
            lock.lock();

            // check for the seatHoldId status.
            if (holdMap.containsKey(seatHoldId)) {
                SeatHold seatHold = holdMap.get(seatHoldId);
                holdMap.remove(seatHoldId);

                // removed the seatholdiD and validate the user.
                if (seatHold.emailId.equals(customerEmail)) {
                    StringBuilder sb = new StringBuilder();

                    // Generate the ticket text.
                    for (Seat seat : seatHold.seats) {
                        int start = seat.start;
                        int count = seat.count;
                        for (int j = 0; j < count; j++) {
                            String ticket = seat.rowNum + "-" + (j + start);
                            if (sb.length() > 0) sb.append(",");
                            sb.append(ticket);
                        }
                    }
                    res = sb.toString();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // release the lock on the resource
            lock.unlock();
        }
        return res;
    }
}
