from Controllers.AbstractController import AbstractController
import pygame
import time
import threading

class Joystick(AbstractController):
    def __init__(self):
        super().__init__()
        self.joystick = pygame.joystick.Joystick(0)

    def start(self):
        if self.joystick != None:
            self.joystick.init()
            self.input_thread = threading.Thread(target=self.__poll, args=())
            self.input_thread.start()

    def __poll(self):
        while True:
            pygame.event.get()

            self.Delta['X'] = int(self.joystick.get_axis(0) * self.Ranges['XY'])
            self.Delta['Y'] = -int(self.joystick.get_axis(1) * self.Ranges['XY'])

            if self.joystick.get_axis(5) >= 0.0:
                self.Delta['Z'] = int(self.joystick.get_axis(5) * self.Ranges['Z'])
            elif self.joystick.get_axis(2) >= 0.0:
                self.Delta['Z'] = -int(self.joystick.get_axis(2) * self.Ranges['Z'])
            else:
                self.Delta['Z'] = 0

            self.Delta['Flag'] = 0x0
            self.Delta['Flag'] |= 0x1 if self.joystick.get_button(9) else 0x0
            self.Delta['Flag'] |= 0x40 if self.joystick.get_button(1) else 0x0
            self.Delta['Flag'] |= 0x80 if self.joystick.get_button(2) else 0x0

            if self.joystick.get_button(4):
                self.Delta['Rotation'] = -0x20
            elif self.joystick.get_button(5):
                self.Delta['Rotation'] = 0x20
            else:
                self.Delta['Rotation'] = 0x0

            