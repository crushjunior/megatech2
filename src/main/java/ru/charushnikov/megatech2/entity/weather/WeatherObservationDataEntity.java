package ru.charushnikov.megatech2.entity.weather;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVRecord;
import ru.charushnikov.megatech2.entity.AbstractEntity;
//import ru.megatech.common.enums.rp5.RP5WindDirection;
//import ru.megatech.common.utils.DateTimeUtils;
//import ru.megatech.common.utils.NumberUtils;
//import ru.megatech.server.entity.point.ObservationPoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//import static ru.megatech.common.utils.DateTimeUtils.DATE_TIME_PATTERN;

/**
 * Данные наблюдения
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"observation_point_id", "observationDateTime"})
)
public class WeatherObservationDataEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "observation_point_id")
    private WeatherObservationPointEntity observationPoint;

    private LocalDateTime observationDateTime;
    private String windDirection;
    private String windSpeed;
    private String pressure;
    private String temperature;
    private String humidity;
    @Lob
    private String otherData;
    @Column
    private Boolean original = true;


    public WeatherObservationDataEntity(CSVRecord record,
                                        WeatherObservationPointEntity wPoint,
                                        int wSpeedIndex,
                                        int pressureIndex,
                                        int humidityIndex,
                                        int wDirectionIndex,
                                        int temperatureIndex) {
        //setObservationDateTime(parseDateTime(record));
        setObservationPoint(wPoint);
        if (wDirectionIndex >= 0)
            setWindDirection(record.get(wDirectionIndex).replace("\"", ""));
        if (wSpeedIndex >= 0)
            setWindSpeed(record.get(wSpeedIndex).replace("\"", ""));
        if (temperatureIndex >= 0)
            setTemperature(record.get(temperatureIndex).replace("\"", ""));
        if (humidityIndex >= 0)
            setHumidity(record.get(humidityIndex).replace("\"", ""));
        if (pressureIndex >= 0)
            setPressure(record.get(pressureIndex).replace("\"", ""));
    }

    @Transient
    public String getFormattedWindSpeed() {
        return String.format("%s м/с", this.windSpeed);
    }

//    private static LocalDateTime parseDateTime(CSVRecord record) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
//        return LocalDateTime.parse(record.get(0).replace("\"", ""), formatter);
//    }
//
//    @Transient
//    public boolean isHolidayDate() {
//        return DateTimeUtils.isNationalHoliday(getObservationDateTime().toLocalDate());
//    }
//
//    @Transient
//    public boolean isWeekendDate() {
//        return DateTimeUtils.isWeekendDate(getObservationDateTime().toLocalDate());
//    }
//
//    @Transient
//    public boolean isWorkTime() {
//        return DateTimeUtils.isWorkTime(getObservationDateTime().toLocalTime());
//    }

    @Transient
    public LocalDate getObservationDate() {
        return this.observationDateTime.toLocalDate();
    }

//    @Transient
//    public WeatherObservationDataEntity getModifiedCopyForPoint(ObservationPoint point) {
//        WeatherObservationDataEntity copy = new WeatherObservationDataEntity();
//        copy.setOriginal(false);
//        if (RP5WindDirection.LULL.getText().equalsIgnoreCase(this.getWindDirection())) {
//            copy.setWindDirection(this.getWindDirection());
//        } else {
//            copy.setWindDirection(point.getPlace().getWind().getText());
//        }
//        copy.setObservationDateTime(this.getObservationDateTime().plusMinutes(NumberUtils.randomLong(5, 55)));
//        copy.setObservationPoint(this.getObservationPoint());
//        copy.setHumidity(this.getHumidity());
//        copy.setOtherData(this.getOtherData());
//        copy.setPressure(this.getPressure());
//        copy.setWindSpeed(this.getWindSpeed());
//        copy.setTemperature(this.getTemperature());
//        return copy;
//    }
}
