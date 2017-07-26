# BassNES
An NES emulator written in Java. The emulator itself is pretty darn accurate. Boasting cycle accuracy and well timed components. It passes most tests I throw at it. It is also one of the fastest java emulators I know of.

Current Test Rom Results as of 0.4.0

https://pastebin.com/j7JRm3fJ



Currently supported mappers:

0,1,2,3,4,5,7,9,10,11,13,19,24,26,71,75,210

These mappers give a good mix of compatibility covering most games for the NES.



Extra Features:

- NSF Player (.nsf and .nsfe)

- Controller input via Jinput

- Fully Customizable NTSC filter.

- Volume control on a per-channel basis.

- Audio Visualizer with an oscilloscope and piano keyboard

- Save States! (hotkeys are CTRL+(number key) to save and SHIFT+(number key) to load)

- A library version of the BassNES core is provided for inclusion in other projects. It includes
everything and provides an interface for video and audio callback in the style of liberto. Documentation included.



System Requirements:

- Minimum 1.2 GHZ dual core for full speed.




Unimplemented (Planned) Features: 

- More Mappers!

- FDS support

- Significantly tested PAL support (has experimental support)

- Debugging tools such as a memory, name table and pattern table viewers

- Continued optimization to reach goal of 0.86 GHZ as the minimum dual core cpu speed.

# Screenshots

![Alt text](http://i.imgur.com/2TX2RUM.png)
![Alt text](http://i.imgur.com/9sybQD8.png)
![Alt text](http://i.imgur.com/hseyDSk.png)
![Alt text](http://i.imgur.com/jQdaI9A.png)
![Alt text](http://i.imgur.com/YJtTDGV.png)

# License

BassNES is licensed under AGPL 3. The full text can be found here: https://www.gnu.org/licenses/agpl.html

Copyright (C) 2017 Jordan Howe
