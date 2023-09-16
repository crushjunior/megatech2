//package ru.charushnikov.megatech2.concurency;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;
//import ru.charushnikov.megatech2.gz.GZExtract;
//import ru.megatech.common.ImportStatus;
//import ru.megatech.server.entity.weather.WeatherObservationDataEntity;
//import ru.megatech.server.entity.weather.WeatherObservationPointEntity;
//import ru.megatech.server.service.WeatherObservationDataService;
//import ru.megatech.server.service.WeatherObservationPointService;
//import ru.megatech.ui.vaadin.broadcasting.Broadcaster;
//import ru.megatech.ui.vaadin.broadcasting.ImportProgressMessage;
//import ru.megatech.ui.vaadin.broadcasting.NewPointCreatedMessage;
//import ru.megatech.ui.vaadin.broadcasting.TaskAddedMessage;
//import ru.megatech.ui.vaadin.notification.NotificationUtils;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.concurrent.Callable;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
//import static ru.megatech.Application.IMPORTED_FOLDER;
//import static ru.megatech.common.ImportStatus.*;
//
//
//@Slf4j
//public class ImportCsvJob implements Callable<ImportCsvJob> {
//
//    private static final String WIND_DIRECTION = "DD";
//    private static final String WIND_SPEED = "Ff";
//    private static final String TEMPERATURE = "T";
//    private static final String HUMIDITY = "U";
//    private static final String PRESSURE = "P0";
//    private static final String ALTERNATIVE_PRESSURE = "Po";
//    public static final String WMO_PATTERN = "WMO_ID=(.*?),";
//    public static final String METAR_PATTERN = "METAR=(.*?),";
//    public static final String POINT_NAME_PATTERN = "(.*?),";
//
//    private final File file;
//    private final String name;
//    private long countAll;
//    private long processed;
//    private WeatherObservationDataService dataService;
//    private WeatherObservationPointService pointService;
//    private ImportStatus status;
//
//    public ImportCsvJob(File file,
//                        WeatherObservationDataService dataService,
//                        WeatherObservationPointService pointService) {
//        this.file = file;
//        this.name = file.getName();
//        this.dataService = dataService;
//        this.pointService = pointService;
//        this.status = WAITING;
//    }
//
//    private ImportCsvJob(String name) {
//        this.file = null;
//        this.name = name;
//        this.status = NO_STATUS;
//    }
//
//    @Override
//    public ImportCsvJob call() throws Exception {
//        try {
//            status = IN_PROGRESS;
//            Path tmpDir = Files.createTempDirectory(null);
//            File target = new File(tmpDir.toString(), file.getName().concat(".csv"));
//            GZExtract.decompressGzip(file.toPath(), target.toPath());
//            try (InputStream inputStream = Files.newInputStream(target.toPath());
//                 Stream<String> lines = Files.lines(target.toPath())) {
//                parseAndSave(inputStream,
//                        ';',
//                        '#',
//                        lines.count());
//                Files.move(file.toPath(),
//                        Paths.get(IMPORTED_FOLDER, file.getName()),
//                        REPLACE_EXISTING);
//                status = DONE;
//            }
//        } catch (IOException e) {
//            status = ERROR;
//            log.error(String.format("Job %s error", name), e);
//            NotificationUtils.showError(String.format("Ошибка импорта CSV:%s", name), e);
//        }
//        updateUI();
//        return this;
//    }
//
//    public double getProgress() {
//        return processed <= countAll ? (double) processed / countAll : 1.0d;
//    }
//
//    public String getName() {
//        return this.name;
//    }
//
//    public ImportStatus getStatus() {
//        return this.status;
//    }
//
//    private void parseAndSave(final InputStream csv, char delimeter, char comment, long lines) throws IOException {
//        final CSVFormat csvFormat = CSVFormat.DEFAULT
//                .withDelimiter(delimeter)
//                .withCommentMarker(comment)
//                .withQuote(null);
//        try (final BufferedReader br = new BufferedReader(new InputStreamReader(csv, StandardCharsets.UTF_8))) {
//            final CSVParser csvParser = csvFormat.parse(br);
//            countAll = lines - 7l;
//            processCsv(csvParser);
//            log.info(String.format("Файл %s обработан", this.name));
//            updateUI(String.format("Файл %s обработан", this.name));
//        } catch (
//                Exception e) {
//            log.error(String.format("Error parsing %d of %d in %s", processed, countAll, name));
//            log.error(e.getMessage());
//            NotificationUtils.showError(String.format("Ошибка обработки %d из %d в %s", processed, countAll, name), e);
//        }
//
//    }
//
//    private void processCsv(CSVParser csvParser) {
//        WeatherObservationPointEntity wPoint = null;
//        int wDirectionIndex = -1;
//        int wSpeedIndex = -1;
//        int pressureIndex = -1;
//        int humidityIndex = -1;
//        int temperatureIndex = -1;
//        ArrayList<String> header = new ArrayList<>();
//        for (final CSVRecord record : csvParser) {
//            try {
//                processed = record.getRecordNumber();
//                if (record.getRecordNumber() == 1) {
//                    wPoint = getPoint(record);
//                    updatePointCombo(wPoint.getName());
//                    wSpeedIndex = updateIndex(record, WIND_SPEED);
//                    pressureIndex = updateIndex(record, PRESSURE) >= 0 ? updateIndex(record, PRESSURE) : updateIndex(record, ALTERNATIVE_PRESSURE);
//                    humidityIndex = updateIndex(record, HUMIDITY);
//                    wDirectionIndex = updateIndex(record, WIND_DIRECTION);
//                    temperatureIndex = updateIndex(record, TEMPERATURE);
//                    record.iterator().forEachRemaining(val -> header.add(val.replace("\"", "")));
//                } else {
//                    WeatherObservationDataEntity wEn = new WeatherObservationDataEntity(record,
//                            wPoint,
//                            wSpeedIndex,
//                            pressureIndex,
//                            humidityIndex,
//                            wDirectionIndex,
//                            temperatureIndex);
//                    wEn.setOtherData(header
//                            .stream()
//                            .map(hVal -> String.format("%s:%s"
//                                    , hVal
//                                    , record.get(header.indexOf(hVal)).replace("\"", "")))
//                            .collect(Collectors.joining(";")));
//                    dataService.saveIfNotExists(wEn);
//                }
//                updateUI();
//            } catch (Exception e) {
//                log.error(String.format("Error parsing %d of %d in %s", processed, countAll, name));
//                log.error(e.getMessage());
//                NotificationUtils.showError(String.format("Ошибка обработки %d из %d в %s", processed, countAll, name), e);
//            }
//        }
//    }
//
//    private void updateUI() {
//        if (countAll > 1000 && processed % (countAll / 100) == 0)
//            Broadcaster.broadcast(new ImportProgressMessage(getName(), getProgress(), getStatus()));
//        else
//            Broadcaster.broadcast(new ImportProgressMessage(getName(), getProgress(), getStatus()));
//    }
//
//    private void updateUI(String message) {
//        Broadcaster.broadcast(new TaskAddedMessage(message));
//    }
//
//    private void updatePointCombo(String newPoint) {
//        Broadcaster.broadcast(new NewPointCreatedMessage(newPoint));
//    }
//
//    private int updateIndex(CSVRecord record, String key) {
//        for (int i = 0; i < record.size(); i++) {
//            if (record.get(i).contains(key)) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    private WeatherObservationPointEntity getPoint(CSVRecord record) {
//        WeatherObservationPointEntity res = new WeatherObservationPointEntity();
//        String comment = record.getComment();
//        Pattern wmoPattern = Pattern.compile(WMO_PATTERN);
//        Pattern metarPattern = Pattern.compile(METAR_PATTERN);
//        Matcher wmoMatcher = wmoPattern.matcher(comment);
//        Matcher metarMatcher = metarPattern.matcher(comment);
//        if (wmoMatcher.find()) {
//            res.setInternationalCode(wmoMatcher.group(1));
//        } else if (metarMatcher.find()) {
//            res.setInternationalCode(metarMatcher.group(1));
//        } else {
//            return null;
//        }
//        Pattern namePattern = Pattern.compile(POINT_NAME_PATTERN);
//        Matcher nameMatcher = namePattern.matcher(comment);
//        if (nameMatcher.find()) {
//            res.setName(nameMatcher.group(1));
//        }
//        return pointService.saveIfNotExists(res);
//    }
//
//    public static ImportCsvJob getDefault(String name) {
//        return new ImportCsvJob(name);
//    }
//}
