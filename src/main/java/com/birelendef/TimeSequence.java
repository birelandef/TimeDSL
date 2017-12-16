package com.birelendef;

import com.sun.istack.internal.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class TimeSequence<T> {
    public static String APPLICABLE_EXC = "Couldn't call method 'append(T value, Duration until)' when timeSequence is empty.";
    public static String DATE_EXC = "Couldn't call method 'append(LocalDateTime startPoint,T value, Duration until)' " +
            "where startPoint is before last piquet's end time.";
    /**
     * Общая длина временного интервала, охватываемого  последовательностью
     */
    private Duration length = Duration.ZERO;
    /**
     * Признак того, является ли последовательность пустой
     */
    private boolean isEmpty = true;
    /**
     * Количество пикетов – интервалов, содержащихся в последовательности.
     */
    private	int piquetCount = 0;
    /**
     * Является ли последовательность непрерывной (или же между пикетами есть разрывы)
     */
    private boolean isContinuous = true ;

    private List<Piquet<T>> piquets = new ArrayList<>();

    public Duration getLength() {
        return length;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getPiquetCount() {
        return piquetCount;
    }

    public boolean isContinuous() {
        return isContinuous;
    }

    /**
     * Static factory method for TimeSequence
     * @param <T> - type of values
     * @return new TimeSequence without piquets
     */
    public static <T> TimeSequence timeSequence(){
        return new TimeSequence<T>();
    }

    /**
     * Get value for @link{point}
     * @param point - date
     * @return value for @link{point}
     * @throws NoSuchValueInPiquets - if piquet included this @link{point} doesn't exist
     */
    public T get(LocalDateTime point) throws NoSuchValueInPiquets {
        if ((isEmpty) || (piquets.get(0).getStartPoint().isAfter(point)) ||
                (getSequenceEndTime().isBefore(point)) )
            throw new NoSuchValueInPiquets();
        Piquet<T> foundPiquet = piquets.stream().filter(item -> item.isIncludeDate(point)).findFirst().orElseThrow(() -> new NoSuchValueInPiquets());
        return foundPiquet.getValue();
    }

    /**
     * Get value for @link{point}
     * @param point - date
     * @param value - value for @link{point}
     * @return true, if piquet included this @link{point} exists otherwise false
     */
    boolean tryGet(LocalDateTime point, T value){
        throw new UnsupportedOperationException();
    }

    /**
     * Find first piquet with value = @link{value}
     * @param value - goal value
     * @return piquet with goal value
     * @throws NoSuchValueInPiquets - if piquet with goal value doesn't exist
     */
    public Piquet<T> find(@NotNull T value) throws NoSuchValueInPiquets{
        return piquets.stream().filter(item -> value.equals(item.getValue()))
                .findFirst().orElseThrow(() -> new NoSuchValueInPiquets());
    }

    /**
     * Получить подпоследовательность, полностью входящую в заданный временной отрезок
     * @param point1
     * @param point2
     * @return
     */
    public TimeSequence<T> between(Duration point1, Duration point2){
        throw new UnsupportedOperationException();
    }

    /**
     * Add new piquet in the end after previous piquet
     * @param value - function's value for @link{until} time cut
     * @param until - cut's duration
     * @return sequence with new piquet
     */
    public TimeSequence<T> append(T value, Duration until){
        if (isEmpty)
            throw new IllegalStateException(APPLICABLE_EXC);
        return append(this.getSequenceEndTime(),value, until);
    }
    /**
     * Add new piquet in the end after previous piquet
     * @param value - function's value for @link{until} time cut
     * @param until - cut's duration
     * @return sequence with new piquet
     */
    public TimeSequence<T> append(LocalDateTime startPoint, T value,  Duration until){
        if ((!isEmpty) && (piquets.get(piquetCount-1).getEndPoint().isAfter(startPoint)))
            throw new IllegalStateException(DATE_EXC);
        piquets.add(new Piquet<T>(until, startPoint, value));
        piquetCount++;
        isEmpty = false;
        length = length.plus(until);
        return this;
    }

    private LocalDateTime getSequenceEndTime(){
        return isEmpty ? null : piquets.get(piquetCount-1).getEndPoint();
    }


    @Override
    public String toString() {
        return "TimeSequence{" +
                "length=" + length +
                ", piquetCount=" + piquetCount +
                ", piquets=" + piquets +
                '}';
    }
}
