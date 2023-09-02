package ru.charushnikov.megatech2.entity.weather;

import lombok.Data;
import ru.charushnikov.megatech2.entity.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Станция погоды
 */

@Entity
@Data
@Table(name = "point_weather")
public class WeatherObservationPointEntity extends AbstractEntity {

    private String name;

    private String country;

    @OneToMany
    @JoinColumn(name = "data_weather")
    private List<WeatherObservationDataEntity> dataWeather;
}
