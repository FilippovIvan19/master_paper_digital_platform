package org.filippov.impl.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.filippov.api.model.MonitorData.MonitorDataDto;
import org.filippov.api.service.DataSender;
import org.filippov.api.service.FileTopicFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.format.DateTimeParseException;

@Service
@Qualifier("csvTopicFiller")
public class CsvTopicFiller implements FileTopicFiller {
    @Autowired @Qualifier("kafkaSender")
    private DataSender sender;

    public void fillInputTopic(String sourceFilePath) {
        if (sourceFilePath == null) {
            sourceFilePath = "collectedData/collectedData.csv";
        }
        try (Reader in = new FileReader(sourceFilePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setDelimiter(';')
                    .build()
                    .parse(in);
            for (CSVRecord record: records) {
                sender.sendMonitorData(new MonitorDataDto(record));
            }
        } catch (IOException | DateTimeParseException e) {
            e.printStackTrace();
        }
    }
}
