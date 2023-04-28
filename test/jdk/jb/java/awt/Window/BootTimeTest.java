import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * @test
 * @summary do nothing
 * @run main/othervm BootTimeTest
 */
public class BootTimeTest {

    public static void main(String... args) throws InterruptedException, IOException {
        Process uptimeProc = Runtime.getRuntime().exec("cmd /c systeminfo | find \"System Boot Time:\"");
        uptimeProc.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
        String line = null;
        line = br.readLine();
        System.out.println("Boot Time : " + line);
        System.out.println("TEST PASSED");
    }

}
