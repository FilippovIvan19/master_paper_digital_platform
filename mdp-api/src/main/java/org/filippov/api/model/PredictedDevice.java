package org.filippov.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictedDevice {
    private DeviceType device;
    private float deviceConfidence;
    private String model;
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
