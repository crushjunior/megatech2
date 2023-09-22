package ru.charushnikov.megatech2.entity.weather;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import ru.charushnikov.megatech2.entity.AbstractEntity;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Станция погоды
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
public class WeatherObservationPointEntity extends AbstractEntity {

    public WeatherObservationPointEntity(@NotNull String internationalCode, @NotNull String name) {
        this.internationalCode = internationalCode;
        this.name = name;
    }

    @NotNull
    @Column(unique = true)
    private String internationalCode;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "observationPoint", fetch = FetchType.LAZY)
    private List<WeatherObservationDataEntity> weatherData = new LinkedList<>();

}
