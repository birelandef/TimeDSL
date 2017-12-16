package com.birelendef;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

public class TimeSequence<T> {
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
    private boolean isContinous = false ;
    /**
     * Лист
     */
    private List<Piquet<T>> piquets = new ArrayList<>();
    /**
     *
     * @param <T> - type of values
     * @return new TimeSequence without piquets
     */
    public static <T> TimeSequence timeSequence(){
        return new TimeSequence<T>();
    }
    /**
     * Получить значение на указанный момент времени.
     * @param point
     * @return
     * @throws NoSuchValueInPiquets - если point не попадает ни в один пикет
     */
    public T get(Duration point) throws NoSuchValueInPiquets {
        throw new NoSuchValueInPiquets();
    }

    /**
     * Получить значение на указанный момент времени.
     * @param point
     * @param value
     * @return true, if this value
     */
    boolean tryGet(Duration point, T value){
        throw new UnsupportedOperationException();
    }

    /**
     * Находит первый отрезок, на котором встречается заданное значение
     * @param value
     * @return
     */
    public Piquet<T> find(T value){
        throw new UnsupportedOperationException();
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
     * Добавить в конец последовательности новый отрезок, примыкающий вплотную в предыдущему
     * @param value
     * @param until
     * @return
     */
    public TimeSequence<T> append(T value, Duration until){
        piquets.add(new Piquet<T>(until, this.getSequenceEndTime(), value));
        piquetCount++;
        isEmpty = false;
        length = length.plus(until);
        return this;
    }
    public TimeSequence<T> append(T value, LocalDateTime startPoint,Duration until){
        piquets.add(new Piquet<T>(until, startPoint, value));
        piquetCount++;
        isEmpty = false;
        length = length.plus(until);
        return this;
    }

    private LocalDateTime getSequenceEndTime(){
        if (piquetCount > 0)
            return piquets.get(piquetCount-1).getEndPoint();
        return null;
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