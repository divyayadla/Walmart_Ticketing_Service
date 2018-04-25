import com.ticket.service.SeatHold;
import com.ticket.service.TicketService;
import com.ticket.service.TicketServiceImpl;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestCases {


    @Test
    public void test1() {
        assertEquals(1,1);
//        assertEquals(1,0);
    }

    @Test
    public void testAvailability() {
        TicketService obj = new TicketServiceImpl();

        Optional<Integer> a = Optional.of(1);
        assertEquals(obj.numSeatsAvailable(a), 1250);
    }

    String email = "asds";

    @Test
    public void testBookingtickets() {
        TicketService obj = new TicketServiceImpl();
        Optional<Integer> a = Optional.of(1);
        assertEquals(obj.numSeatsAvailable(a), 1250);
        for (int i = 0; i < 27; i++) {
            SeatHold sh = obj.findAndHoldSeats(45, a, a, email);
            String s = obj.reserveSeats(sh.seatHoldId, email);
//            System.out.println(s);
            assertNotEquals(s, "");
        }
    }

    @Test
    public void testHoldInterval() throws InterruptedException {
        TicketService obj = new TicketServiceImpl();
        Optional<Integer> a = Optional.of(1);
        assertEquals(obj.numSeatsAvailable(a), 1250);
        for (int i = 0; i < 24; i++) {
            SeatHold sh = obj.findAndHoldSeats(50, a, a, email);
            String s = obj.reserveSeats(sh.seatHoldId, email);
//            System.out.println(s);
            assertNotEquals(s, "");
        }

        SeatHold sh = obj.findAndHoldSeats(50, a, a, email);


        Thread.sleep(10000);
        SeatHold sh1 = obj.findAndHoldSeats(50, a, a, email);

        String s = obj.reserveSeats(sh1.seatHoldId, email);
//        System.out.println(s);
        assertNotEquals(s, "");

    }


    @Test
    public void testMultipleSectionBooking() {
        TicketService obj = new TicketServiceImpl();
        Optional<Integer> a = Optional.of(1);

        for (int i = 0; i < 24; i++) {
            SeatHold sh = obj.findAndHoldSeats(50, a, a, email);
            String s = obj.reserveSeats(sh.seatHoldId, email);
//            System.out.println(s);
            assertNotEquals(s, "");
        }

        Optional<Integer> b = Optional.of(2);
        for (int i = 0; i < 24; i++) {
            SeatHold sh = obj.findAndHoldSeats(50, b, b, email);
            String s = obj.reserveSeats(sh.seatHoldId, email);
//            System.out.println(s);
            assertNotEquals(s, "");
        }

        SeatHold sh = obj.findAndHoldSeats(100, a, b, email);
        String s = obj.reserveSeats(sh.seatHoldId, email);
//        System.out.println(s);
        assertNotEquals(s, "");
    }

}
