/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.swingexample.client;

import com.jme.math.Vector2f;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import org.jdesktop.wonderland.modules.swingexample.client.cell.SwingExampleCell;
import javax.swing.SwingUtilities;

/**
 *
 * The window for the Swing example.
 *
 * @author deronj
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@ExperimentalAPI
public class SwingExampleWindow extends WindowSwing {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/swingexample/client/Bundle");
    /** The logger used by this class. */
    private static final Logger logger =
            Logger.getLogger(SwingExampleWindow.class.getName());
    /** The cell in which this window is displayed. */
    private SwingExampleCell cell;

    /** The panel displayed within this window. */
    private TestPanel examplePanel;

    /**
     * Create a new instance of SwingExampleWindow.
     *
     * @param cell The cell in which this window is displayed.
     * @param app The app which owns the window.
     * @param width The width of the window (in pixels).
     * @param height The height of the window (in pixels).
     * @param decorated Whether the window is decorated with a frame.
     * @param pixelScale The size of the window pixels.
     */
    public SwingExampleWindow(SwingExampleCell cell, App2D app, int width,
            int height, boolean decorated, Vector2f pixelScale)
            throws InstantiationException {
        super(app, width, height, decorated, pixelScale);
        this.cell = cell;

        setTitle(BUNDLE.getString("Swing_Example"));

        try {
            SwingUtilities.invokeAndWait(new Runnable () {
                public void run () {
                    // This must be invoked on the AWT Event Dispatch Thread
                    examplePanel = new TestPanel();
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // Parent to Wonderland main window for proper focus handling
        JmeClientMain.getFrame().getCanvas3DPanel().add(examplePanel);

        setComponent(examplePanel);
    }
}
