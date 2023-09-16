package ru.charushnikov.megatech2.entity.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.charushnikov.megatech2.entity.AbstractEntity;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class WeatherObservationPointEntity extends AbstractEntity {

    @NotNull
    @Column(unique = true)
    private String internationalCode;

    @NotNull
    private String name;

    //@OneToMany(mappedBy = "observationPoint", fetch = FetchType.LAZY)
    //private List<WeatherObservationDataEntity> weatherData = new LinkedList<>();

    public WeatherObservationPointEntity(@NotNull String internationalCode, @NotNull String name) {
        this.internationalCode = internationalCode;
        this.name = name;
    }

}
