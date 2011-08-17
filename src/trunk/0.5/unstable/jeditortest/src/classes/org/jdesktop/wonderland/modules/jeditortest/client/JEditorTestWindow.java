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
package org.jdesktop.wonderland.modules.jeditortest.client;

import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import com.jme.math.Vector2f;
import javax.swing.JPanel;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import javax.swing.SwingUtilities;

/**
 *
 * The window for the JEditor test.
 *
 * @author deronj
 */

@ExperimentalAPI
public class JEditorTestWindow extends WindowSwing  {

    /** The logger used by this class. */
    private static final Logger logger = Logger.getLogger(JEditorTestWindow.class.getName());

    private JPanel testPanel;

    /**
     * Create a new instance of JEditorTestWindow.
     *
     * @param app The app which owns the window.
     * @param width The width of the window (in pixels).
     * @param height The height of the window (in pixels).
     * @param decorated Whether the window is decorated with a frame.
     * @param pixelScale The size of the window pixels.
     */
    public JEditorTestWindow (App2D app, int width, int height, boolean decorated, Vector2f pixelScale)
        throws InstantiationException
    {
	super(app, width, height, decorated, pixelScale);

	setTitle("JEditor Test");
	
        try {
            SwingUtilities.invokeAndWait(new Runnable () {
                public void run () {
                    // This must be invoked on the AWT Event Dispatch Thread
                    testPanel = new TestJEditorPane();
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

	// Parent to Wonderland main window for proper focus handling 
       	JmeClientMain.getFrame().getCanvas3DPanel().add(testPanel);

	setComponent(testPanel);

        // Note: This particular test panel only is laid out properly if forced size mode
        setSize(width, height);
    }
}
