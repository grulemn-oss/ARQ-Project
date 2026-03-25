In the terminal cd to ARQ-Project.
Run javac -d out .\src\protocols\*.java .\src\*.java
Run java -cp out MainReceiver .\received_data\recievedVideo.mp4
You can pick any folder to put the file in after MainReceiver, it is recommended that the file type matches what you send.

On another device
In the terminal cd to ARQ-Project.
Run javac -d out .\src\protocols\*.java .\src\*.java
Run java -cp out MainSender .\data\testVideo.mp4

Your receiver device should have the sent file labeled and in the correct folder as you put in your terminal command.
The sender will also list out the number of packets lost, the latency, and damaged packets.

To switch between Stop-And-Wait ARQ and Selective-And-Repeat ARQ open MainReveiver and MainSender and comment out and
uncomment out what it tells you in the comments.
