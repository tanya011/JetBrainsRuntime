import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class ScreenInsets {

    private final static int titleBarHeight = 100;
    private final static int topPanelHeight = 50;

    public static void main(String... args) {
        JFrame f = new JFrame();
        f.setBounds(0, 0, 400, 300);

        WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
        titleBar.setHeight(titleBarHeight);
        JBR.getWindowDecorations().setCustomTitleBar(f, titleBar);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.GRAY);
        topPanel.setSize(f.getWidth(), topPanelHeight);
        topPanel.setMinimumSize(new Dimension(f.getWidth(), topPanelHeight));
        JLabel label = new JLabel();
        label.setForeground(Color.BLACK);
        label.setText("Frame name 111");
        topPanel.add(label);
        topPanel.setLayout(new FlowLayout());
        f.add(topPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.RED);
        bottomPanel.setSize(f.getWidth(), titleBarHeight - topPanelHeight);
        bottomPanel.setMinimumSize(new Dimension(f.getWidth(), titleBarHeight - topPanelHeight));
        bottomPanel.setBounds(0, topPanelHeight, f.getWidth(), titleBarHeight - topPanelHeight);
        f.add(bottomPanel);

        JPanel userArea = new JPanel();
        userArea.setBackground(Color.BLUE);
        userArea.setSize(f.getWidth(), f.getHeight() - titleBarHeight);
        userArea.setMinimumSize(new Dimension(f.getWidth(), f.getHeight() - titleBarHeight));
        f.add(userArea);

        System.out.println("h = " + f.getHeight());
        System.out.println("ins = " + f.getInsets().top + " " + f.getInsets().bottom);
        System.out.println(topPanel.getHeight());
        System.out.println(bottomPanel.getHeight());
        System.out.println(userArea.getHeight());

        f.setLayout(null);
        f.setVisible(true);
    }

}
