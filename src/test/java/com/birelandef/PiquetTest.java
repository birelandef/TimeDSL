package com.birelandef;

import com.birelendef.Piquet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public class PiquetTest {
    public static final LocalDateTime START_POINT = LocalDateTime.of(2015, Month.JANUARY, 1, 0,0,0);
    public static final Duration ONE_DAY = Duration.of(1, ChronoUnit.DAYS);
    public static final Duration ONE_HOUR = Duration.of(1, ChronoUnit.HOURS);
    private static Piquet<Integer> pq;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        pq = new Piquet<>(ONE_HOUR,START_POINT, 1);
    }
    @Test
    public void testIsDateBeforePiquet(){
        Assert.assertEquals(false, pq.isDateBeforePiquet(START_POINT.plus(ONE_DAY)));
        Assert.assertEquals(true, pq.isDateBeforePiquet(START_POINT.minus(ONE_DAY)));
    }

    @Test
    public void testIsDateAfterPiquet(){
        Assert.assertEquals(true, pq.isDateAfterPiquet(START_POINT.plus(ONE_DAY)));
        Assert.assertEquals(false, pq.isDateAfterPiquet(START_POINT));
    }
}
