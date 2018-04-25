package com.ticket.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ThreadImpl extends Thread{

    /**
     * Variable that store the seconds to wait before making the seats available to users.
     */
    int delay;

    /**
     * Variable storing the holdId of the seats that are held.
     */
    int holdId;

    /**
     * TicketService Impl obj
     */
    TicketServiceImpl obj;

    /**
     * COnstructor for creating the threadImpl obj
     * @param delay
     * @param holdId
     * @param obj
     */
    ThreadImpl(int delay, int holdId, TicketServiceImpl obj) {
        this.obj = obj;
        this.delay = delay;
        this.holdId = holdId;
    }

    /**
     * Overide the run method for the thread
     */
    @Override
    public void run() {
        try {

            // threads wait until the hold is over.
            Thread.sleep(delay*1000);

            // fetches lock
            Lock.getLockObj().lock();

            Map<Integer, SeatHold> map = obj.holdMap;
            Section[] arr = obj.arr;

            // validate the holdId
            if (map.containsKey(holdId)) {
                for (Seat seat : map.get(holdId).seats) {

                    // change the hold status of the seat
                    seat.isHold = false;
                    int rownum = seat.rowNum;
                    int secid = seat.sectionLevel.ordinal();
                    Section sec = arr[secid];

                    // update the availability of the section
                    sec.increaseSeats(seat.count);
                    Row row = sec.rows[rownum];
                    TreeMap<Integer, List<Seat>> tm = obj.venue[secid];
                    Seat prev = row.seats.floor(seat);
                    Seat next = row.seats.ceiling(seat);

                    //merge the seats in the left with the unoccupied previous seats if present
                    if (prev != null && prev.start + prev.count == seat.start) {
                        int pc = prev.count;
                        List<Seat> l = tm.get(pc);
                        int p = -1;
                        for (int i = 0; i < l.size(); i++) {
                            if (l.get(i) == prev) {
                                p = i;
                                break;
                            }
                        }
                        l.remove(p);
                        row.seats.remove(prev);
                        seat.count += (seat.start-prev.start);
                        seat.start = prev.start;
                    }

                    //merge the seats in the right with the unoccupied previous seats if present
                    if (next != null && seat.start+ seat.count == next.start) {
                        int nc = next.count;
                        List<Seat> l = tm.get(nc);
                        int p = -1;
                        for (int i = 0; i < l.size(); i++) {
                            if (l.get(i) == prev) {
                                p = i;
                                break;
                            }
                        }
                        l.remove(p);
                        row.seats.remove(next);
                        seat.count += next.count;
                    }

                    // finally update the section's treemap holding all the available seats
                    List<Seat> ll = tm.getOrDefault(seat.count, new ArrayList<>());
                    ll.add(seat);
                    tm.put(seat.count, ll);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // releases lock
            Lock.getLockObj().unlock();
        }
    }
}
