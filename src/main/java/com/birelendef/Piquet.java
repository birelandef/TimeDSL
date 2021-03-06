package com.birelendef;

import java.time.Duration;
import java.time.LocalDateTime;

public class Piquet<T> {
    private Duration piquetDuration;
    private LocalDateTime startPoint;
    private T value;

    public Piquet(Duration piquetDuration, LocalDateTime startPoint, T value) {
        this.piquetDuration = piquetDuration;
        this.startPoint = startPoint;
        this.value = value;
    }

    public Duration getPiquetDuration() {
        return piquetDuration;
    }

    public LocalDateTime getStartPoint() {
        return startPoint;
    }
    public LocalDateTime getEndPoint() {
        return startPoint.plusSeconds(piquetDuration.getSeconds());
    }

    public T getValue() {
        return value;
    }

    public boolean isIncludeDate(LocalDateTime middlePoint){
        return (((middlePoint.isAfter(this.getStartPoint())) && (middlePoint.isBefore(this.getEndPoint()))) ||
                middlePoint.equals(this.getStartPoint()) /*||  middlePoint.equals(this.getEndPoint())*/);
    }

    public boolean isDateBeforePiquet(LocalDateTime comparePoint){
        return startPoint.isAfter(comparePoint);
    }
    public boolean isDateAfterPiquet(LocalDateTime comparePoint){
        return getEndPoint().isBefore(comparePoint);
    }

    @Override
    public String toString() {
        return "Piquet{" +
                "piquetDuration=" + piquetDuration +
                ", startPoint=" + startPoint +
                ", value=" + value +
                '}';
    }
}
