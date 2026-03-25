In the terminal cd to ARQ-Project.
Run javac -d out .\src\protocols\*.java .\src\*.java
Run java -cp out MainReceiver .\received_data\receivedVideo.mp4
Run java -cp out MainReceiver .\received_data\receivedImage.jpg
Run java -cp out MainReceiver .\received_data\receivedText.txt
You can pick any folder to put the file in after MainReceiver, it is recommended that the file type matches what you send.

On another device
In the terminal cd to ARQ-Project.
Run javac -d out .\src\protocols\*.java .\src\*.java
Run java -cp out MainSender .\data\testVideo.mp4
Run java -cp out MainSender .\data\testImage.jpg
Run java -cp out MainSender .\data\testText.txt

Your receiver device should have the sent file labeled and in the correct folder as you put in your terminal command.
The sender will also list out the number of packets lost, the latency, and damaged packets.

To switch between Stop-And-Wait ARQ and Selective-And-Repeat ARQ open MainReveiver and MainSender and comment out and
uncomment out what it tells you in the comments.
