from flask import Flask
from flask import request

from model.PredictedDevice import PredictedDevice

app = Flask(__name__)


@app.post('/identifyDevices')
def identify_devices():
    timestamps = request.json['timestamps']
    amounts = request.json['amounts']

    print(timestamps[0])
    print(type(timestamps[0]))
    print(amounts[0])
    print(type(amounts[0]))

    return [
        PredictedDevice('FRIDGE', 0.99, 'Север', 0.1111),  # refactor devices to enum
        PredictedDevice('TEAPOT', 0.87, '418 I\'m a teapot', 0.2222),
        PredictedDevice('COMPUTER', 0.0001, 'Pizza box PC', 0.3333),
    ]
