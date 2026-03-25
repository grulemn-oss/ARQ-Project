package protocols;

import java.io.IOException;
import java.util.List;

public class StopAndWaitARQ_Sender {
    private static final byte ACK = 0x06; // ACK
    private static final byte NAK = 0X21; // NAK
    private final NetworkSender sender;
    private char currSeqNumber = 0; // 0 - 255

    public StopAndWaitARQ_Sender(NetworkSender sender) {
        this.sender = sender;
        this.currSeqNumber = 0;
    }

    public void transmit(List<BISYNCPacket> packets) {
        for (int i = 0; i < packets.size(); i++) {
            BISYNCPacket packet = packets.get(i);
            boolean packetReceived = false;
            boolean isLastPacket = (i == packets.size() - 1);

            // DONE: Task 2.a, Your code below
            // notice: use sender.sendPacketWithError() to send out packet
            try {
                while (!packetReceived) {
                    System.out.println("frame: " + (int)(currSeqNumber)); // print out frame number
                    sender.sendPacketWithError(packet, currSeqNumber, isLastPacket); // send packet
                    char[] response = sender.waitForResponse(); // wait for ACK or NAK
                    packetReceived = ACK == (int)(response[0]); // if ACK end while loop else loop again
                    // print out received ACK or NAK and total packet number
                    if (ACK == (int)(response[0])) {
                        System.out.println("ACK: " + (int)(response[1]) + "\nPacket " + i + " successfully transmitted.");
                    } else if (NAK == (int)(response[0])) {
                        System.out.println("NAK: " + (int)(response[1]) + "\nPacket " + i + " unsuccessfully transmitted.");
                    }
                }
                // After the ACK is received increment the frame number.
                currSeqNumber = (char) (((int) (currSeqNumber) + 1) % 256);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }


}
