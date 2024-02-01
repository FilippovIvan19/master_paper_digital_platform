package org.filippov.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.filippov.model.MonitorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CsvTopicFiller {
    @Autowired
    private KafkaSender sender;

    public void fillInputTopic(String sourceFilePath) {
        if ("a".equals(sourceFilePath)) {
            sourceFilePath = "collectedData/collectedData.csv";
        }

        try (Reader in = new FileReader(sourceFilePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setDelimiter(';')
                    .build()
                    .parse(in);
//            List<MonitorData> csvData = new ArrayList<>();
            for (CSVRecord record: records) {
//                csvData.add(new MonitorData(record));
                sender.sendMonitorData(new MonitorData(record));
            }


        } catch (IOException | DateTimeParseException e) {
            e.printStackTrace();
        }


    }
}
