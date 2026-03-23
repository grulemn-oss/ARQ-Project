package protocols;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectiveAndRepeatARQ_Sender {

    private static final byte ACK = 0x06; // ACK
    private static final byte NAK = 0X21; // NAK
    private static final char MAX_SEQ_NUM = 255;
    private static final char TOTAL_SEQ_NUM = (MAX_SEQ_NUM + 1);
    private final NetworkSender sender;
    private int winBase = 0;
    private int winSize = 0;

    public SelectiveAndRepeatARQ_Sender(NetworkSender sender, int winSize) {
        this.sender = sender;
        // Sliding window
        this.winBase = 0;
        this.winSize = winSize;
    }

    public void transmit(List<BISYNCPacket> packets) throws IOException {

        // Handshake
        int N = packets.size();
        sender.sendHandshakeRequest(N, winSize);
        char[] response = sender.waitForResponse();
        if (response[0] != ACK) {
            System.out.println("Handshake failed, exit");
        } else {
            System.out.println("Handshake succeed, proceed!");
        }

        Boolean finished = false;

        // TODO: Task 3.a, Your code below

        while (!finished) {
            try {
            // notice: use sender.sendPacketWithLost() to send out packet
            // but, to resend the lost packet after receiving NAK,
            // use sender.sendPacket(), otherwise, the receiver may not get the resent packet and get stuck
            // also, for the last packet, use sender.sendPacket(), otherwise, it will get stuck

                int initWinSize = winSize;
                int loops = 0;
                // Send Window.
                for (int i = winBase; i < Math.min(winSize, N); i++) {
                    char packetIndex = (char)(i);
                    if (i == packets.size() - 1) {
                        System.out.println("sendPacket number: " + i);
                        sender.sendPacket(packets.get(i).getPacket(), packetIndex, true);
                    } else {
                        System.out.println("sendPacket number: " + i);
                        sender.sendPacketWithLost(packets.get(i), packetIndex, false);
                    }
                }
                // Check ACKs and NAKS and move window
                for (int i = winBase; i < winSize; i++) {
                    response = sender.waitForResponse();
                    if (ACK == (int)(response[0])) {
                        System.out.println("Packet " + i + " successfully transmitted ACK number: " + (int)(response[1]));
                        if (winBase > (int)(response[1])) {
                            loops++;
                        }
                        winBase = (int)(response[1]) + 256*loops + 1;
                        winSize = initWinSize + (int)(response[1]) + 256*loops + 1;
                        System.out.println("winBase: " + winBase + " winSize: " + winSize);
                        if (i == packets.size() - 1) {
                            finished = true;
                        }
                    } else if (NAK == (int)(response[0])) {
                        System.out.println("sendPacket number: " + i);
                        sender.sendPacket(packets.get(i).getPacket(), (char)(winBase), false);
                    }
                }
            }catch (IOException e){
                System.err.println("Error transmitting packet: " + e.getMessage());
                return;
            }
        }
    }

}
