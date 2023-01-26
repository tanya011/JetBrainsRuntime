import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

abstract class Runner {

    protected Frame frame;
    private final String name;
    protected boolean passed = true;

    Runner(String name) {
        this.name = name;
    }

    final boolean run(){
        System.out.printf("RUN TEST CASE: %s\n%n", name);
        try {
            SwingUtilities.invokeAndWait(this::test);
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            SwingUtilities.invokeLater(() -> frame.dispose());
        }
        if (passed) {
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
        }
        return passed;
    }

    abstract void test();

}
