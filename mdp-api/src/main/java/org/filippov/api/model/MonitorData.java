package org.filippov.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

    private Long monitorId;
    private LocalDateTime timestamp;
    private BigDecimal amount;
    @JsonDeserialize(using = FlagsDeserializer.class)
    private SerializableRegularEnumSet<Flag> flags;

    public interface Columns {
        String MONITOR_ID = "monitorId";
        String TIMESTAMP = "timestamp";
        String AMOUNT = "amount";
        String FLAGS = "flags";
    }

    public MonitorData(CSVRecord record) throws DateTimeParseException {
        this(
                Long.valueOf(record.get(Columns.MONITOR_ID)),
                LocalDateTime.parse(record.get(Columns.TIMESTAMP), formatter),
                new BigDecimal(record.get(Columns.AMOUNT)),
                new SerializableRegularEnumSet<>(record.get(Columns.FLAGS), Flag.class)
        );
    }

    public enum Flag {
        INTERVENTION_WARNING,
        CRASH_WARNING,
        LOW_BATTERY,
    }

    public static class FlagsDeserializer extends SerializableRegularEnumSet.Deserializer<Flag> {
        @Override
        public Class<Flag> getElementType() {
            return Flag.class;
        }
    }

}
