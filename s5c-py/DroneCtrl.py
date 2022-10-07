from asyncio import Handle

import numpy
from Controllers.Joystick import Joystick
from Controllers.Keyboard import Keyboard
from TCPStream import *
from UDPStream import *
import pygame
from pygame.locals import *
import threading
import h264decoder
import cv2

Host = '172.17.10.1'
Ports = {
    'TCP': 8888,
    'UDP': 9125
}
PreviousDelta = {}
pygame.init()
pygame.joystick.init()

Handler = None
while Handler == None:
    selection = input('Enter J for Joystick, K for Keyboard: ')
    if len(selection) > 1 or len(selection) == 0:
        continue

    match selection.lower():
        case 'j':
            Handler = Joystick()
        case 'k':
            Handler = Keyboard()


TCP = TCPStream()
print('[+] Connecting to TCP...')
TCP.Connect(Host, Ports['TCP'])
print('[+] Connected to TCP.')
TCP.Link()

UDP = UDPStream()
print('[+] Connecting to UDP...')
UDP.Connect(Host, Ports['UDP'])
print('[+] Connected to UDP.')

display = pygame.display.set_mode((300, 300))

def VideoStreamHandler():
    decoder = h264decoder.H264Decoder()
    while True:
        buffer = TCP.Receive(0x5B4)
        framedata = decoder.decode(buffer)
        for frame in framedata:
            (d, w, h, l) = frame
            d = numpy.frombuffer(d, dtype=numpy.ubyte, count=len(d))
            d = d.reshape((h, l//3, 3))
            d = d[:, :w, :]
            cv2.imshow('Camera View', d)
            cv2.waitKey(1)

try:
    # VideoThread = threading.Thread(target=VideoStreamHandler, args=())
    Handler.start()
    # VideoThread.start()

    while True:
        Delta = Handler.GetDelta()
        if list(Delta.values()) != list(PreviousDelta.values()):
            UDP.SendCommand(Delta['Flag'], 0x80 + Delta['X'], 0x80 + Delta['Y'], 0x80 + Delta['Z'], 0x80 + Delta['Rotation'])
        PreviousDelta = Delta
except KeyboardInterrupt:
    TCP.Disconnect()
    UDP.Disconnect()
