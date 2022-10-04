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
            events = pygame.event.get()
            if not (self.joystick.get_button(4) or self.joystick.get_button(5)):
                self.Delta['Rotation'] = 0x0
            if not (self.joystick.get_button(9) or self.joystick.get_button(1) or self.joystick.get_button(2)):
                self.Delta['Flag'] = 0x0
            if not self.joystick.get_button(7) and not self.joystick.get_button(6):
                self.Delta['Z'] = 0x00
            if events is None:
                continue
            for event in events: 
                match event.type:
                    case pygame.JOYAXISMOTION:
                        self.Delta['X'] = int(self.joystick.get_axis(0) * self.Ranges['XY'])
                        self.Delta['Y'] = -int(self.joystick.get_axis(1) * self.Ranges['XY'])
                        if self.joystick.get_axis(5) > 0.0:
                            self.Delta['Z'] = int(self.joystick.get_axis(5) * self.Ranges['Z'])
                        if self.joystick.get_axis(2) > 0.0:
                            self.Delta['Z'] = -int(self.joystick.get_axis(2) * self.Ranges['Z'])
                    case pygame.JOYBUTTONDOWN:
                        match event.button:
                            case 1:
                                self.Delta['Flag'] = 0x40
                            case 2:
                                self.Delta['Flag'] = 0x80
                            case 9: # Options button on PS5 controller.
                                self.Delta['Flag'] = 0x1
                            case 4:
                                self.Delta['Rotation'] = -0x20
                            case 5:
                                self.Delta['Rotation'] = 0x20
                            case 6: # Share button on PS5 controller.
                                self.Delta['Flag'] = 0x0
                                self.Delta['X'] = 0
                                self.Delta['Y'] = 0
                                self.Delta['Z'] = -0x7F
                                self.Delta['Rotation'] = 0x0

            