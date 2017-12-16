package com.birelandef;

import com.birelendef.NoSuchValueInPiquets;
import com.birelendef.Piquet;
import com.birelendef.TimeSequence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import static com.birelendef.TimeSequence.APPLICABLE_EXC;
import static com.birelendef.TimeSequence.DATE_EXC;
import static com.birelendef.TimeSequence.timeSequence;
import static org.hamcrest.CoreMatchers.containsString;

public class TimeSequenceTest {
    public static final LocalDateTime START_POINT = LocalDateTime.of(2015, Month.JANUARY, 1, 0,0,0);
    public static final Duration ONE_DAY = Duration.of(1, ChronoUnit.DAYS);
    public static final Duration ONE_HOUR = Duration.of(1, ChronoUnit.HOURS);
    private static TimeSequence<Integer> tsInt;
    private static TimeSequence<String> tsString;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        tsInt = timeSequence();
        tsString = timeSequence();
    }
    @Test
    public void testAppendWithoutStartPointExc(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(containsString(APPLICABLE_EXC));
        tsInt.append(1, ONE_DAY);
        tsInt.append(START_POINT,1, ONE_DAY);
    }
    @Test
    public void testAppendWithoutStartPoint(){
        tsInt.append(START_POINT,1, ONE_DAY);
        tsInt.append(1, ONE_DAY);
        Assert.assertEquals("Lengths equal",Duration.ofDays(2),tsInt.getLength());
        Assert.assertEquals("Piquet's counts equal",2, tsInt.getPiquetCount());
    }
    @Test
    public void testAppendWithStartPointExc(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(containsString(DATE_EXC));
        tsInt.append(START_POINT,1, ONE_DAY);
        tsInt.append(START_POINT,1, ONE_DAY);
    }
    @Test
    public void testAppendWithStartPoint(){
        tsInt.append(START_POINT,1, ONE_DAY);
        tsInt.append(START_POINT.plus(ONE_DAY),1, ONE_DAY);
    }
    @Test
    public void testFind(){
        tsInt.append(START_POINT,2, ONE_DAY);
        tsInt.append(1, ONE_HOUR);
        tsInt.append(1, ONE_DAY);
        try {
            Piquet<Integer> foundPiquet = tsInt.find(1);
            Assert.assertEquals("Values equal", foundPiquet.getValue().intValue(), 1);
            Assert.assertEquals("Start points equal", foundPiquet.getStartPoint(), START_POINT.plus(ONE_DAY) );
            Assert.assertEquals("Durations equal", foundPiquet.getPiquetDuration(), ONE_HOUR);
//            System.out.println(foundPiquet);
        } catch (NoSuchValueInPiquets e) {
            Assert.fail();
        }
    }

    @Test
    public void testFindExc() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_DAY);
        tsInt.append(1, ONE_HOUR);
        tsInt.append(1, ONE_DAY);
        thrown.expect(NoSuchValueInPiquets.class);
        tsInt.find(2);
    }

}
