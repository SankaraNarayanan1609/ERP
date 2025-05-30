import java.net.*;
public class HostResolveTest {
    public static void main(String[] args) throws Exception {
        InetAddress addr = InetAddress.getByName("erplite.storyboarderp.com");
        System.out.println("Resolved: " + addr.getHostAddress());
    }
}
