import pygame

class AbstractController:
    def __init__(self):
        self.Ranges = {
            'XY': 0x30,
            'Z': 0x50
        }
        self.Delta = {
            'Flag': 0x0,
            'X': 0x80,
            'Y': 0x80,
            'Z': 0x80,
            'Rotation': 0x80
        }
        self.input_thread = None

    def GetDelta(self):
        return self.Delta
