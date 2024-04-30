package org.filippov.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeries {
    private List<String> timestamps;
    private List<BigDecimal> amounts;
}
