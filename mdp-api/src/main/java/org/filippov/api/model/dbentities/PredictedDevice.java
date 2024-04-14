package org.filippov.api.model.dbentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "predicted_device")
public class PredictedDevice {
    @Id @GeneratedValue
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "prediction_record_id", nullable = false)
    @JsonIgnore
    private DevicePredictionRecord predictionRecord;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 20)
    private DeviceType device;

    @Column(name = "device_confidence")
    private float deviceConfidence;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "model_confidence")
    private float modelConfidence;

    public enum DeviceType {
        FRIDGE,
        DISH_WASHER,
        HEATER,
        TEAPOT,
        STOVE,
        BOILER,
        TV,
        COMPUTER,
        BLENDER,
    }
}
