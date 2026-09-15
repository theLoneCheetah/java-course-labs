package time;

import java.time.LocalDate;

/**
 * Реализация часов, которая двигает дату только при явном вызове advanceDays()
 */
public class SimulationClock implements Clock {

    private final LocalDate startDate;
    private LocalDate currentDate;

    // Инициализация стартовой датой
    public SimulationClock(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Дата старта обязательна");
        }
        this.startDate = startDate;
        this.currentDate = startDate;
    }

    @Override
    public LocalDate today() {
        return currentDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    /** Сдвинуть «сегодня» на указанное число дней */
    public void advanceDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Число дней должно быть положительным");
        }
        currentDate = currentDate.plusDays(days);
    }
}