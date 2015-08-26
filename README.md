# David2Printer
A program to convert massage between David 3D laser scanner and A reprap 3D printer or other cnc device; In order to use the two to creat a automotic running scanner 
#中文
本程序旨在David 3D laser scanner 和 3D打印机 或者 其他CNC设备之间建立联系，让后者来完成精确的扫描工作；同时通过David2Printer对两者的控制，实现一键全自动360度扫描。
##1.准备
1.需要一台电脑，windows系统，可以运行David以及用串口连接打印机或者其他CNC设备；

2.线激光一个，以及能够将线机关装在打印机或者CNC设备的移动头上的组件（可以自行设计）；

3.一个转台，可以用3D打印机的挤出机改装，也可以另外购置一个步进电机（转台可以自行设计，也可以参照网上的版本）；

4.额外，本程序在ramps1.4上实验，使用3Dprinter的风扇端口D9来跟跟线机关供电，所以需要将12V转成5V来供电，可以是用继电器转低压回路，也可以是用7805 5V稳压芯片，总之，要确保线激光的安全。

注：david 在扑捉纹理的时候需要关闭线激光，以防干扰，扑捉完成之后再打开。

##1.necessities
