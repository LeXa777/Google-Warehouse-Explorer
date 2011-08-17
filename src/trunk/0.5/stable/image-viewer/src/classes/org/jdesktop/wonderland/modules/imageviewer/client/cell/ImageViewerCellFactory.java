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
package org.jdesktop.wonderland.modules.imageviewer.client.cell;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.util.TextureManager;
import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.cell.registry.annotation.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.imageviewer.client.cell.jme.cellrenderer.ImageViewerCellRenderer;
import org.jdesktop.wonderland.modules.imageviewer.common.cell.ImageViewerCellServerState;

/**
 * A factory for the image viewer. This factory is necessary so that these types
 * of Cells can be created, but it does not show up in the Cell Palette.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@CellFactory
public class ImageViewerCellFactory implements CellFactorySPI {

    private static Logger logger = Logger.getLogger(ImageViewerCellFactory.class.getName());

    public String[] getExtensions() {
        return new String[] { "png", "jpg", "jpeg", "gif" };
    }

    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {
       ImageViewerCellServerState state = new ImageViewerCellServerState();
       
       // Look for the content-uri field and set if so
       if (props != null) {
           String uri = props.getProperty("content-uri");
           if (uri != null) {
               state.setImageURI(uri);
           }
       }

       // Set the name of the Cell to the name of the image, absent any path
       // preceeding the name.
       state.setName(getFileName(state.getImageURI()));

       // Figure out a good sizing hint for the image. We first fetch the image
       // (it's ok to do it here, it'll get cached so that later it won't take
       // so long) and figure out its size. From that, we generate the bounding
       // hint.
       BoundingVolume boundingVolume = null;
       try {
           Texture texture = getTexture(state.getImageURI());
           com.jme.image.Image image = texture.getImage();
           float width = image.getWidth() * ImageViewerCell.WIDTH_SCALE_FACTOR;
           float height = image.getHeight() * ImageViewerCell.HEIGHT_SCALE_FACTOR;
           boundingVolume = new BoundingBox(Vector3f.ZERO, width, height,
                   ImageViewerCellRenderer.IMAGE_DEPTH);

           // Make sure we do not cache the texture in memory, this will mess
           // up asset caching with WL (if the URL stays the same, but the
           // underlying asset changes).
           TextureManager.releaseTexture(texture);
       } catch (MalformedURLException excp) {
           logger.log(Level.WARNING, "Unable to form url from " +
                   state.getImageURI(), excp);
       }
       state.setBoundingVolumeHint(new BoundingVolumeHint(true, boundingVolume));

       return (T)state;
    }

    public String getDisplayName() {
        return null;
    }

    public Image getPreviewImage() {
        return null;
    }

    /**
     * Returns the texture associated with the given uri. Uses the current
     * primary session to annotate the URI. This should be ok, since the only
     * time we'd want to create a Cell is on the primary session.
     */
    private Texture getTexture(String uri) throws MalformedURLException {
        // Convert the uri given to a proper url to download
        URL url = AssetUtils.getAssetURL(uri);
        return TextureManager.loadTexture(url);
    }

    /**
     * Returns the file element in the path name given the full path of the
     * image file
     */
    private String getFileName(String uri) {
        // Check to see if there is a final '/'. We always use a forward-slash
        // regardless of platform, because it is typically a wlcontent URI.
        int index = uri.lastIndexOf("/");
        if (index == -1) {
            return uri;
        }
        return uri.substring(index + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        // XXX HACK
        // In order for this NOT to display in the Cell Palettes then the
        // getDisplayName() method must return null. However, this prevents it
        // from appearing in a list of Cells when more than one supports the
        // the PNG/JPG extensions. So we return a good display name here
        // XXX
        return "Image Viewer";
    }
}
