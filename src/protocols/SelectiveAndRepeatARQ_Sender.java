
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
        int nextToSend = 0;

        while (winBase < N) { //vital
            try {

                while (nextToSend < Math.min(winBase + winSize, N)) {
                    boolean isLast = (nextToSend == N - 1);
                    System.out.println("Sending packet: " + nextToSend);
                    if (isLast) {
                        sender.sendPacket(packets.get(nextToSend).getPacket(),
                                (char) nextToSend, true);

                    } else {
                        sender.sendPacketWithLost(packets.get(nextToSend),
                                (char) nextToSend, false);
                    }



                    nextToSend++;
                }

                // ALWAYS WAIT 4 RESp
                response = sender.waitForResponse();
                int responseType = (int) response[0];
                int responseNum = (int) response[1];



                if (responseType == ACK) {
                    System.out.println("ACK received, nextExpected=" + responseNum);
                    int newBase = (responseNum == 0 && winBase > 0) ? N : responseNum;
                    winBase = Math.max(winBase, newBase);


                } else if (responseType == NAK) {
                    System.out.println("NAK received for packet: " + responseNum);
                    boolean isLast = (responseNum == N - 1);
                    // NOTE: RESEND
                    sender.sendPacket(packets.get(responseNum).getPacket(),
                            (char) responseNum, isLast);
                }

            } catch (IOException e) {
                System.err.println("Error transmitting packet: " + e.getMessage());
                return;
            }
        }
    }
}
 