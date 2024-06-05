from dataclasses import dataclass


@dataclass
class PredictedDevice:
    device: str
    deviceConfidence: float
    model: str
    modelConfidence: float
