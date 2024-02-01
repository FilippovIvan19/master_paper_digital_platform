package org.filippov.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorData {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]"); // 2023-10-09 10:00:02.002

    private Long monitorId;
    private LocalDateTime timestamp;
    private BigDecimal amount;

    public interface Columns {
        String MONITOR_ID = "monitorId";
        String TIMESTAMP = "timestamp";
        String AMOUNT = "amount";
    }

    public MonitorData(CSVRecord record) throws DateTimeParseException {
        this(
                Long.valueOf(record.get(Columns.MONITOR_ID)),
                LocalDateTime.parse(record.get(Columns.TIMESTAMP), formatter),
                new BigDecimal(record.get(Columns.AMOUNT))
        );
    }
}
