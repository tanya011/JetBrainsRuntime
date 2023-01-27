import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import java.awt.*;
import java.awt.event.*;

public class CustomTitleBarAction {

    private static Frame frame;

    public static void main(String... args) {
        WindowDecorations.CustomTitleBar titleBar = JBR.getWindowDecorations().createCustomTitleBar();
        titleBar.setHeight(100);
        frame = new Frame(){
            @Override
            public void paint(Graphics g) {
                Rectangle r = g.getClipBounds();
                g.setColor(Color.BLUE);
                g.fillRect(r.x, r.y, r.width, 100);
                super.paint(g);
            }
        };
        frame.setTitle("Frame");
        frame.setBounds(200, 400, 1000, 200);

        JBR.getWindowDecorations().setCustomTitleBar(frame, titleBar);

        frame.setLayout(null);
        frame.setVisible(true);


        Button toggler = new Button();
        toggler.setBounds(200, 20, 50, 50);
        toggler.addActionListener(e -> titleBar.putProperty("controls.visible", !(boolean)titleBar.getProperties().getOrDefault("controls.visible", true)));
        frame.add(toggler);

        Button button = new Button();
        button.setBounds(300, 20, 50, 50);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("double clicked");
                    super.mouseClicked(e);
                }
            }
        });
        frame.add(button);

        TextField tx = new TextField();
        tx.setBounds(400, 20, 100, 50);
        tx.setText("dsfkljsdflkjdslkfa");
        tx.setEditable(false);
        frame.add(tx);

        TextArea tx2 = new TextArea();
        tx2.setBounds(600, 20, 100, 50);
        tx2.setEditable(false);
        frame.add(tx2);
    }

}