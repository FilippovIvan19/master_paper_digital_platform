package org.filippov.api.model.dbentities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_prediction_record")
public class DevicePredictionRecord {
    @Id @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "monitor_id", length = 20)
    private String monitorId;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private Integer month;

    @OneToMany(mappedBy = "predictionRecord", cascade = CascadeType.ALL)
    private List<PredictedDevice> predictedDevices;
}
