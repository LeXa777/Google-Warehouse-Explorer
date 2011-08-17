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
package org.jdesktop.wonderland.modules.scriptingImager.client;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.scriptingImager.common.ScriptingImagerCellServerState;

/**
 * A factory for the image view to appear in the Cell Palette.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@CellFactory
public class ScriptingImagerCellFactory implements CellFactorySPI {

    public String[] getExtensions() {
        return new String[] { "png", "jpg" };
    }

    public <T extends CellServerState> T getDefaultCellServerState(Properties props)
       {
       ScriptingImagerCellServerState state = new ScriptingImagerCellServerState();

       // HACK!
       Map<String, String> metadata = new HashMap();
       metadata.put("sizing-hint", "0.1");
       state.setMetaData(metadata);
       
       // Look for the content-uri field and set if so
       if (props != null)
           {
           String uri = props.getProperty("content-uri");
           if (uri != null)
               {
               state.setImageURI(uri);
               }
           else
               {
               state.setImageURI("/webdav/content/textures/");
               }
           }
       else
           {
           state.setImageURI("/webdav/content/textures/");
           }
       return (T)state;
       }

    public String getDisplayName() {
        return "ScriptingImager";
    }

    public Image getPreviewImage() {
        return null;
    }
}
