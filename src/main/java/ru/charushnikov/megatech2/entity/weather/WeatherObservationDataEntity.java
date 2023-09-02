package ru.charushnikov.megatech2.entity.weather;

import lombok.Data;
import ru.charushnikov.megatech2.entity.AbstractEntity;

import javax.persistence.*;

/**
 * Данные наблюдения
 */

@Entity
@Data
@Table(name = "data_weather")
public class WeatherObservationDataEntity extends AbstractEntity {

    private String windDirection;
    private Integer windSpeed;
    private Integer pressure;
    private Integer humidity;
    private Integer temperature;

    @ManyToOne
    @JoinColumn(name = "point_weather")
    private WeatherObservationPointEntity pointWeather;
}
