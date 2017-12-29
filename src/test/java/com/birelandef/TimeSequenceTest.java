package com.birelandef;

import com.birelendef.NoSuchValueInPiquets;
import com.birelendef.Piquet;
import com.birelendef.PostExecutionChecker;
import com.birelendef.TimeSequence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import static com.birelendef.TimeSequence.APPLICABLE_EXC;
import static com.birelendef.TimeSequence.DATE_EXC;
import static com.birelendef.TimeSequence.timeSequence;
import static org.hamcrest.CoreMatchers.containsString;

public class TimeSequenceTest {
    public static final LocalDateTime START_POINT = LocalDateTime.of(2015, Month.JANUARY, 1, 0,0,0);
    public static final Duration ONE_DAY = Duration.of(1, ChronoUnit.DAYS);
    public static final Duration ONE_HOUR = Duration.of(1, ChronoUnit.HOURS);
    public static final Duration ONE_MINUTE = Duration.of(1, ChronoUnit.MINUTES);
    public static final Duration ONE_SEC = Duration.of(1, ChronoUnit.SECONDS);
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
            Assert.assertEquals("Values equal", 1, foundPiquet.getValue().intValue());
            Assert.assertEquals("Start points equal", START_POINT.plus(ONE_DAY), foundPiquet.getStartPoint());
            Assert.assertEquals("Durations equal", ONE_HOUR, foundPiquet.getPiquetDuration());
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

    @Test
    public void testGetEmptyExc() throws NoSuchValueInPiquets {
        thrown.expect(NoSuchValueInPiquets.class);
        tsInt.get(START_POINT);
    }

    @Test
    public void testGetBeforeExc() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_DAY);
        thrown.expect(NoSuchValueInPiquets.class);
        tsInt.get(START_POINT.minus(ONE_HOUR));
    }
    @Test
    public void testGetAfterExc() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        thrown.expect(NoSuchValueInPiquets.class);
        tsInt.get(START_POINT.plus(ONE_DAY));
    }

    @Test
    public void testGetContinuous() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        tsInt.append(3, ONE_DAY);
        tsInt.append(4, ONE_HOUR);
        tsInt.append(5, ONE_DAY);
        LocalDateTime point = START_POINT.plus(ONE_HOUR).plus(ONE_HOUR).plus(ONE_HOUR);
        Assert.assertEquals("Values equal", 3, tsInt.get(point).intValue());
    }

    @Test
    public void testGetNotContinuousStartPoint() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        LocalDateTime newCutDate = START_POINT.plus(ONE_HOUR).plus(ONE_HOUR).plus(ONE_HOUR);
        tsInt.append(newCutDate,3, ONE_DAY);
        tsInt.append(4, ONE_HOUR);
        tsInt.append(5, ONE_DAY);
        LocalDateTime point = newCutDate;
        Assert.assertEquals("Values equal", 3, tsInt.get(point).intValue());
    }

    @Test
    public void testGetNotContinuousMiddlePoint() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        LocalDateTime newCutDate = START_POINT.plus(ONE_HOUR.multipliedBy(3L));
        tsInt.append(newCutDate,3, ONE_DAY);
        tsInt.append(4, ONE_HOUR);
        tsInt.append(5, ONE_DAY);
        thrown.expect(NoSuchValueInPiquets.class);
        LocalDateTime point = newCutDate.minus(ONE_SEC);
        tsInt.get(point);
    }

    @Test
    public void testGetNotContinuousEndPoint() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        LocalDateTime newCutDate = START_POINT.plus(ONE_HOUR.multipliedBy(3L));
        tsInt.append(newCutDate,3, ONE_DAY);
        tsInt.append(4, ONE_HOUR);
        tsInt.append(5, ONE_DAY);
        thrown.expect(NoSuchValueInPiquets.class);
        LocalDateTime point = START_POINT.plus(ONE_HOUR.multipliedBy(2L));
        tsInt.get(point);
    }

    @Test
    public void testbetweenContinuousFullFill() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        tsInt.append(3, ONE_HOUR.multipliedBy(3L));
        LocalDateTime startInterval = START_POINT.plus(ONE_HOUR);
        LocalDateTime finishInterval = START_POINT.plus(ONE_HOUR.multipliedBy(4L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(3L), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 2,resultInterval.getPiquetCount());
    }

    @Test
    public void testbetweenContinuousFullFillMiddlePointTwoPiquets() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR.multipliedBy(2L));
        tsInt.append(3, ONE_HOUR.multipliedBy(3L));
        LocalDateTime startInterval = START_POINT.plus(ONE_HOUR.multipliedBy(2L));
        LocalDateTime finishInterval = START_POINT.plus(ONE_HOUR.multipliedBy(4L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(2L), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 2,resultInterval.getPiquetCount());
    }

    @Test
    public void testbetweenNotContinuousFullFillMiddlePointTwoPiquets() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR.multipliedBy(2L));
        LocalDateTime skipStartDate = START_POINT.plus(ONE_HOUR.multipliedBy(4L));
        tsInt.append(skipStartDate,3, ONE_HOUR.multipliedBy(3L));

        LocalDateTime startInterval = START_POINT.plus(ONE_HOUR.multipliedBy(2L));
        LocalDateTime finishInterval = START_POINT.plus(ONE_HOUR.multipliedBy(5L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(2L), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 2,resultInterval.getPiquetCount());
    }

    @Test
    public void testbetweenContinuousFullFillMiddlePointMorePiquets() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        tsInt.append(3, ONE_HOUR);
        tsInt.append(4, ONE_HOUR);
        tsInt.append(5, ONE_HOUR);
        tsInt.append(6, ONE_HOUR);
        LocalDateTime startInterval = START_POINT.plus(ONE_MINUTE.multipliedBy(30L));
        LocalDateTime finishInterval = startInterval.plus(ONE_HOUR.multipliedBy(5L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(5L), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 6,resultInterval.getPiquetCount());
    }

    @Test
    public void testbetweenNotContinuousFullFillMiddlePointMorePiquets() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        LocalDateTime skipStartDate = START_POINT.plus(ONE_HOUR.multipliedBy(3L));
        tsInt.append(skipStartDate,4, ONE_HOUR);
        tsInt.append(5, ONE_HOUR);
        tsInt.append(6, ONE_HOUR);
        LocalDateTime startInterval = START_POINT.plus(ONE_MINUTE.multipliedBy(30L));
        LocalDateTime finishInterval = startInterval.plus(ONE_HOUR.multipliedBy(5L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(4L), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 5,resultInterval.getPiquetCount());
    }

    /**
     * | 1 | 2 |   | 3 | 4 |   |   |
     * 0   1   2   3   4   5   6   7
     *           ^       ^
     * @throws NoSuchValueInPiquets
     */
    @Test
    public void testbetweenNotContinuousStartPointNotInPiquetEndPointInPiquet() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        LocalDateTime skipStartDate = START_POINT.plus(ONE_HOUR.multipliedBy(3L));
        tsInt.append(skipStartDate,3, ONE_HOUR);
        tsInt.append(4, ONE_HOUR);
        LocalDateTime startInterval = START_POINT.plus(ONE_HOUR.multipliedBy(2L)).plus(ONE_MINUTE.multipliedBy(30L));
        LocalDateTime finishInterval = START_POINT.plus(ONE_HOUR.multipliedBy(4L)).plus(ONE_MINUTE.multipliedBy(30L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.plus(ONE_MINUTE.multipliedBy(30L)), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 2,resultInterval.getPiquetCount());
    }

    /**
     * | 1 | 2 |   | 3 | 4 |   |   |
     * 0   1   2   3   4   5   6   7
     *       ^               ^
     * @throws NoSuchValueInPiquets
     */
    @Test
    public void testbetweenNotContinuousStartPointInPiquetEndPointNotInPiquet() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        LocalDateTime skipStartDate = START_POINT.plus(ONE_HOUR.multipliedBy(3L));
        tsInt.append(skipStartDate,3, ONE_HOUR);
        tsInt.append(4, ONE_HOUR);
        LocalDateTime startInterval = START_POINT.plus(ONE_HOUR).plus(ONE_MINUTE.multipliedBy(30L));
        LocalDateTime finishInterval = START_POINT.plus(ONE_HOUR.multipliedBy(5L)).plus(ONE_MINUTE.multipliedBy(30L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(2L).plus(ONE_MINUTE.multipliedBy(30L)), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 3,resultInterval.getPiquetCount());
    }

    /**
     * | 1 | 2 |   | 3 | 4 |   |   |
     * 0   1   2   3   4   5   6   7
     *           ^           ^
     * @throws NoSuchValueInPiquets
     */
    @Test
    public void testbetweenNotContinuousStartPointNotInPiquet() throws NoSuchValueInPiquets {
        tsInt.append(START_POINT,1, ONE_HOUR);
        tsInt.append(2, ONE_HOUR);
        LocalDateTime skipStartDate = START_POINT.plus(ONE_HOUR.multipliedBy(3L));
        tsInt.append(skipStartDate,3, ONE_HOUR);
        tsInt.append(4, ONE_HOUR);
        LocalDateTime startInterval = START_POINT.plus(ONE_HOUR.multipliedBy(2L)).plus(ONE_MINUTE.multipliedBy(30L));
        LocalDateTime finishInterval = startInterval.plus(ONE_HOUR.multipliedBy(3L));

        TimeSequence<Integer> resultInterval = tsInt.between(startInterval, finishInterval);
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(2L), resultInterval.getLength());
        Assert.assertEquals("Piquets equal", 2,resultInterval.getPiquetCount());
    }
    @Test
    public void testDemonstrateDSL(){
        TimeSequence<String> testDSL = timeSequence().
                append(START_POINT,"A", ONE_HOUR).
                append("B", ONE_HOUR ).
                append("C", ONE_HOUR ).
                append("D", ONE_HOUR ).
                append("F", ONE_HOUR );
        System.out.println(testDSL);
        System.out.println(testDSL.isContinuous());
    }

    @Test
    public void testCopy(){
        TimeSequence<String> original = timeSequence().
                append(START_POINT,"A", ONE_HOUR).
                append("B", ONE_HOUR ).
                append("C", ONE_HOUR ).
                append("D", ONE_HOUR ).
                append("F", ONE_HOUR );
        TimeSequence<String> copy = original.copy();
        Assert.assertEquals("Length equal", ONE_HOUR.multipliedBy(5L), copy.getLength());
        Assert.assertEquals("Piquets equal", 5, copy.getPiquetCount());
        Assert.assertEquals("IsEmpty equal", false, copy.isEmpty());
        Assert.assertEquals("Iscontinious equal", true, copy.isContinuous());
    }

    @Test
    public void testEnv(){
        TimeSequence<String> original = timeSequence().
                append(START_POINT,"A", ONE_HOUR).
                append("B", ONE_HOUR ).
                append("C", ONE_HOUR ).
                append("D", ONE_HOUR );
        try {
            Method testMethod = String.class.getMethod("append", String.class, Duration.class);
            Properties props = new Properties();
            props.put(String.class.getMethod("getLength"), ONE_HOUR.multipliedBy(5L) );
            props.put(String.class.getMethod("getPiquetCount"), 5);
            props.put(String.class.getMethod("isEmpty"), false);
            props.put(String.class.getMethod("isContinuous"), true);
            PostExecutionChecker.execute(props, testMethod, original, "F", ONE_HOUR);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }



}
