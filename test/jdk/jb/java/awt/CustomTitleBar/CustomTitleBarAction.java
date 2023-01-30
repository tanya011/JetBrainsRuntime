/*
 * Copyright 2000-2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.jetbrains.JBR;
import com.jetbrains.WindowDecorations;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


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