# David2Printer
A program to convert massage between David 3D laser scanner and A reprap 3D printer or other cnc device; In order to use the two to creat a automotic running scanner 

use RXTX for java ：http://fizzed.com/oss/rxtx-for-java

Another version of the program on Arduino platform is :
https://github.com/Dark-Guan/David-3D-laserscanner-automitive-scan-program

#中文
David 版本 v3.10.4
本程序旨在为David 3D laser scanner 和 3D打印机 或者 其他CNC设备之间建立联系，让后者来完成精确的扫描工作；同时通过David2Printer对两者的控制，实现一键全自动360度扫描。
##1.准备
1.需要一台电脑，windows系统，可以运行David以及用串口连接打印机或者其他CNC设备；

2.线激光一个，以及能够将线机关装在打印机或者CNC设备的移动头上的组件（可以自行设计）；

3.一个转台，可以用3D打印机的挤出机改装，也可以另外购置一个步进电机（转台可以自行设计，也可以参照网上的版本）；

4.为了实现串口的转发，需要使用VSPD（Virtual Serial Port Driver）来创建一段互相连通的虚礼串口；

5.额外，本程序在ramps1.4上实验，使用3Dprinter的风扇端口D9来跟跟线机关供电，所以需要将12V转成5V来供电，可以是用继电器转低压回路，也可以是用7805 5V稳压芯片，总之，要确保线激光的安全。

注：david 在扑捉纹理的时候需要关闭线激光，以防干扰，扑捉完成之后再打开。

##2.快速使用

1.用VSPD创建两个虚拟的串口，比方说COM1和COM2，这两个串口会将形成一个管路，COM1收到的信息会发给COM2，COM2收到的信息会发给COM1.

2.David选择Motorized Laser Setup,串口选择步骤1中创建的任意一个，波特率设置成打印机的波特率（在高级设置中设置），NewLine选项填10；校准好相机；把转动平台放到扫描区域内；

3.打开David2Printer 程序（运行 run.bat）,设置波特率为打印机的波特率；David串口选择与步骤二中选择的成对的串口，Printer串口选择打印机的串口，打开两个串口

4.设置扫描步数（100步=1mm），可以点击“到扫描终点”检查扫描步数能不能满足物件大小要求；设置好扫描角度，一次扫描完之后就会转动对应角度，知道完成360度的扫描

5.点击“开始自动扫描”，开始第一次扫描。



