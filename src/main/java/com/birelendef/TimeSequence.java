package com.birelendef;

import com.sun.istack.internal.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * WARN!!! Последовательности - полуоткрытые интервалы
 * [1,2)[2,3)...
 * See {@link Piquet#isIncludeDate(LocalDateTime)} for the date-based equivalent to this class.
 *
 * @param <T> type of function value
 */

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
     * @param <T> type of values
     * @return new TimeSequence without piquets
     */
    public static <T> TimeSequence timeSequence(){
        return new TimeSequence<T>();
    }

    /**
     * Get value for {@link point}
     * @param point date
     * @return value for {@link point}
     * @throws NoSuchValueInPiquets - if piquet included this {@link point} doesn't exist
     */
    public T get(LocalDateTime point) throws NoSuchValueInPiquets {
        Piquet<T> foundPiquet = getPiquet(point);
        if (foundPiquet != null)  return foundPiquet.getValue();
        throw new NoSuchValueInPiquets();

    }

    /**
     * Get value for {@link point}
     * @param point date
     * @param value value for {@link point}
     * @return true, if piquet included this {@link point} exists otherwise false
     */
    public boolean tryGet(LocalDateTime point, T value){
        throw new UnsupportedOperationException();
    }

    /**
     * Find first piquet with value = {@link 1}
     * @param value goal value
     * @return piquet with goal value
     * @throws NoSuchValueInPiquets - if piquet with goal value doesn't exist
     */
    public Piquet<T> find(@NotNull T value) throws NoSuchValueInPiquets{
        return piquets.stream().filter(item -> value.equals(item.getValue()))
                .findFirst().orElseThrow(() -> new NoSuchValueInPiquets());
    }

    /**
     * Get subsequence
     * Получить подпоследовательность, полностью входящую в заданный временной отрезок
     * @param point1
     * @param point2
     * @return subsequence
     */
    public TimeSequence<T> between(LocalDateTime point1, LocalDateTime point2){
        TimeSequence<T> betweenResult = timeSequence();
        if (point1.equals(point2) || this.isEmpty)
            return betweenResult;
        LocalDateTime startInterval;
        LocalDateTime finishInterval;
        if (point1.isBefore(point2)){
            startInterval = point1;
            finishInterval = point2;
        } else {
            startInterval = point1;
            finishInterval = point2;
        }
        if (startInterval.isAfter(getSequenceEndTime()) || startInterval.equals(getSequenceEndTime()) ||
                finishInterval.isBefore(getSequenceStartTime()) || finishInterval.equals(getSequenceStartTime()) )
            return betweenResult;
        Piquet<T> foundPiquetWithStartPoint = null;
        Piquet<T> foundPiquetWithFinishPoint = null;
        foundPiquetWithStartPoint = getPiquet(startInterval);
        foundPiquetWithFinishPoint = getPiquet(finishInterval);
            if (foundPiquetWithStartPoint == null){
                for (int i = 0; i < piquets.size() - 1; i++) {
                    if (piquets.get(i).isDateAfterPiquet(startInterval) && piquets.get(i+1).isDateBeforePiquet(startInterval)) {
                        foundPiquetWithStartPoint = piquets.get(i + 1);
                        break;
                    }
                }
            } else {
                foundPiquetWithStartPoint = new Piquet(Duration.between(startInterval, foundPiquetWithStartPoint.getEndPoint()),
                        startInterval, foundPiquetWithStartPoint.getValue());

            }
            if (foundPiquetWithFinishPoint == null) {
                for (int i = 0; i < piquets.size(); i++) {
                    if (piquets.get(i).isDateAfterPiquet(finishInterval) && ((i == (piquets.size() - 1)) || (piquets.get(i + 1).isDateBeforePiquet(finishInterval)))) {
                        foundPiquetWithFinishPoint = piquets.get(i);
                        break;
                    }
                }
            } else {
                foundPiquetWithFinishPoint = new Piquet(Duration.between(foundPiquetWithFinishPoint.getStartPoint(), finishInterval).abs(),
                        foundPiquetWithFinishPoint.getStartPoint(), foundPiquetWithStartPoint.getValue());
            }
        betweenResult = betweenResult.append(foundPiquetWithStartPoint);
            int i = piquets.indexOf(getPiquet(foundPiquetWithStartPoint.getStartPoint())) + 1 ;
            while (!piquets.get(i).getStartPoint().equals(foundPiquetWithFinishPoint.getStartPoint())){
                Piquet<T> tempPiquet = piquets.get(i);
                betweenResult = betweenResult.append(tempPiquet.getStartPoint(), tempPiquet.getValue(), tempPiquet.getPiquetDuration());
                i++;
            }
        betweenResult = betweenResult.append(foundPiquetWithFinishPoint);
        return betweenResult;
    }

    /**
     * Add new piquet in the end after previous piquet
     * @param value function's value for {@link value} time cut
     * @param until cut's duration
     * @return sequence with new piquet
     */
    public TimeSequence<T> append(T value, Duration until){
        if (isEmpty)
            throw new IllegalStateException(APPLICABLE_EXC);
        return append(this.getSequenceEndTime(),value, until);
    }
    /**
     * Add new piquet on any place
     * @param value function's value for {@link until} time cut
     * @param until cut's duration
     * @return sequence with new piquet
     */
    public TimeSequence<T> append(LocalDateTime startPoint, T value,  Duration until){
        if ((!isEmpty) && (piquets.get(piquetCount-1).getEndPoint().isAfter(startPoint)))
            throw new IllegalStateException(DATE_EXC);
        TimeSequence<T> result = this.copy();
        LocalDateTime currentSequenceEnd = getSequenceEndTime();
        if (!(currentSequenceEnd == null || currentSequenceEnd.equals(startPoint)))
            result.isContinuous = false;
        result.piquets.add(new Piquet<T>(until, startPoint, value));
        result.piquetCount++;
        result.isEmpty = false;
        result.length = length.plus(until);
        return result;
    }

    private TimeSequence<T> append(Piquet<T> piquet){
        return append(piquet.getStartPoint(), piquet.getValue(), piquet.getPiquetDuration());
    }

    private LocalDateTime getSequenceStartTime(){
        return isEmpty ? null : piquets.get(0).getStartPoint();
    }

    private LocalDateTime getSequenceEndTime(){
        return isEmpty ? null : piquets.get(piquetCount-1).getEndPoint();
    }

    /**
     * Get value for {@link point}
     * @param point date
     * @return value for {@link point}
     */
    public Piquet<T> getPiquet(LocalDateTime point) {
        if ((isEmpty) || (piquets.get(0).getStartPoint().isAfter(point)) ||
                (getSequenceEndTime().isBefore(point)) )
            return null;
        Piquet<T> foundPiquet = piquets.stream().filter(item -> item.isIncludeDate(point)).findFirst().orElse(null);
        return foundPiquet;
    }

    /**
     * Clone current timeline to new instance
     * @return new instance
     */
    public TimeSequence<T> copy(){
        TimeSequence newTimeSequence = timeSequence();
        newTimeSequence.isEmpty = this.isEmpty;
        newTimeSequence.length = this.length;
        newTimeSequence.piquetCount = this.piquetCount;
        newTimeSequence.isContinuous = this.isContinuous;
        newTimeSequence.piquets.addAll(this.piquets);
        return newTimeSequence;
    }

    @Override
    public String toString() {
        return "TimeSequence{" +
                "length=" + length +
                ", piquetCount=" + piquetCount +
                ", piquets=" + piquets +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSequence<?> that = (TimeSequence<?>) o;

        if (isEmpty != that.isEmpty) return false;
        if (piquetCount != that.piquetCount) return false;
        if (isContinuous != that.isContinuous) return false;
        if (!length.equals(that.length)) return false;
        return piquets.equals(that.piquets);
    }

    @Override
    public int hashCode() {
        int result = length.hashCode();
        result = 31 * result + (isEmpty ? 1 : 0);
        result = 31 * result + piquetCount;
        result = 31 * result + (isContinuous ? 1 : 0);
        result = 31 * result + piquets.hashCode();
        return result;
    }
}
