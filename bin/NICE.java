

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.Image;
/**
*  Launch the application
**/

public class NICE {
    //original =2000
    private static final int splashDuration = 2000; // time in MS to show splash screen

    public NICE() {
        new AppSplashScreen( splashDuration ).showSplash();
        SwingUtilities.invokeLater(
        new Runnable() {
            public void run() {
                LC frame = new LC ();
                java.net.URL url = ClassLoader.getSystemResource("logo2_cropped.jpg");
                Toolkit kit = Toolkit.getDefaultToolkit();
                Image img = kit.createImage(url);
                frame.setIconImage(img);
                frame.setVisible(true);
            }
        } );
    }

    public static void main(String[] args) {
        new NICE();
    }
}