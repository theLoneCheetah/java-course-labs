package time;

import java.time.LocalDate;

/**
 * Абстракция времени
 * Подмена реального время симуляцией без таймеров
 */
public interface Clock {

    /** Текущая дата по часам */
    LocalDate today();
}