from pygame import K_LEFT, K_LSHIFT, K_RIGHT, K_SPACE, K_a, K_d, K_f, K_g, K_h, K_s, K_w
from Controllers.AbstractController import *
import threading

class Keyboard(AbstractController):
    def __init__(self):
        super().__init__()
        self.Const = 0.5

    def start(self):
        self.input_thread = threading.Thread(target=self.__poll, args=())
        self.input_thread.start()

    def __poll(self):
        while True:
            pygame.event.get()
            keys = pygame.key.get_pressed()

            if keys[K_d]:
                self.Delta['X'] = int(self.Ranges['XY'] * self.Const)
            elif keys[K_a]:
                self.Delta['X'] = -int(self.Ranges['XY'] * self.Const)
            else:
                self.Delta['X'] = 0

            if keys[K_w]:
                self.Delta['Y'] = int(self.Ranges['XY'] * self.Const)
            elif keys[K_s]:
                self.Delta['Y'] = -int(self.Ranges['XY'] * self.Const)
            else:
                self.Delta['Y'] = 0
                

            if keys[K_SPACE]:
                self.Delta['Z'] = int(self.Const * self.Ranges['Z'])
            elif keys[K_LSHIFT]:
                self.Delta['Z'] = -int(self.Const * self.Ranges['Z'])
            else:
                self.Delta['Z'] = 0

            
            self.Delta['Flag'] = 0x0
            self.Delta['Flag'] |= 0x1 if keys[K_f] else 0x0
            self.Delta['Flag'] |= 0x40 if keys[K_g] else 0x0
            self.Delta['Flag'] |= 0x80 if keys[K_h] else 0x0

            if keys[K_LEFT]:
                self.Delta['Rotation'] = -0x20
            elif keys[K_RIGHT]:
                self.Delta['Rotation'] = 0x20
            else:
                self.Delta['Rotation'] = 0x0
