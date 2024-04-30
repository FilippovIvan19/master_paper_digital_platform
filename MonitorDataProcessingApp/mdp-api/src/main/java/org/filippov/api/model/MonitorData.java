package org.filippov.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorData {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]"); // 2023-10-09 10:00:02.002

    private String monitorId;
    private LocalDateTime timestamp;
    private BigDecimal amount;
    private SerializableRegularEnumSet<Flag> flags;


    public MonitorData(MonitorDataDto dto) {
        this(
                dto.getMonitorId(),
                LocalDateTime.parse(dto.getTimestamp(), formatter),
                dto.getAmount(),
                new SerializableRegularEnumSet<>(dto.getFlags(), Flag.class)
        );
    }

    public MonitorDataDto toDto() {
        return new MonitorDataDto(
                monitorId,
                timestamp.format(formatter),
                amount,
                flags.toString()
        );
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonitorDataDto {
        private String monitorId;
        private String timestamp;
        private BigDecimal amount;
        private String flags;

        public MonitorDataDto(CSVRecord record) throws DateTimeParseException {
            this(
                    record.get(Columns.MONITOR_ID),
                    record.get(Columns.TIMESTAMP),
                    new BigDecimal(record.get(Columns.AMOUNT)),
                    record.get(Columns.FLAGS)
            );
        }
    }


    public interface Columns {
        String MONITOR_ID = "monitorId";
        String TIMESTAMP = "timestamp";
        String AMOUNT = "amount";
        String FLAGS = "flags";
    }

    public enum Flag {
        INTERVENTION_WARNING,
        CRASH_WARNING,
        LOW_BATTERY,
    }
}
