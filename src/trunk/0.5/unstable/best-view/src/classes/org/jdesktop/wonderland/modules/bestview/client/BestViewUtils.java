/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

package org.jdesktop.wonderland.modules.bestview.client;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import java.awt.Canvas;
import java.util.Collection;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.utils.CellPlacementUtils;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * Static utilities for calculating best view
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class BestViewUtils {
    private static final Logger LOGGER =
            Logger.getLogger(BestViewUtils.class.getName());

    /**
     * Find the best view of a cell
     * @param cell the cell to get a best view of
     * @return the position of a cell's best view
     */
    public static CellTransform getBestView(Cell cell) {
        BoundingVolume bounds = getModelBounds(cell);
        float distance = getBestDistance(bounds);

        // add a small fudge factor so we can see borders
        distance += 0.2f;

        // calculate the look vector to this cell -- we only care about the y axis
        // rotation
        Quaternion rotation = cell.getWorldTransform().getRotation(null);
        Vector3f lookVec = CellPlacementUtils.getLookDirection(rotation, null);

        LOGGER.fine("Look vector: " + lookVec);

        // translate into a quaternion using lookAt
        Quaternion look = new Quaternion();
        look.lookAt(lookVec.negate(), Vector3f.UNIT_Y);

        // find the origin by translating the look vector
        Vector3f origin = lookVec.mult(distance);

        LOGGER.fine("Look vector x distance = " + origin);

        origin.addLocal(cell.getWorldTransform().getTranslation(null));

        LOGGER.fine("Origin + " + cell.getWorldTransform().getTranslation(null) +
                    " = " + origin);

        return new CellTransform(look, origin);
    }

    /**
     * Get the ideal distance away from the given bounds
     * @param bounds the bounds to get a distance from
     * @return the ideal distance from the given bounds
     */
    public static float getBestDistance(BoundingVolume bounds) {
        ViewManager vm = ViewManager.getViewManager();

        // first get the field of view and view size
        float fov = vm.getViewProperties().getFieldOfView();

        Canvas c = JmeClientMain.getFrame().getCanvas();
        int width = c.getWidth();
        int height = c.getHeight();

        // fov is calculated in the y dimension, so calculate the x and
        // y fovs based on the y value
        float fovX = fov;
        float fovY = fov;

        float ratio = (float) width / (float) height;
        fovX *= ratio;

        // calculate bounds, x and y distances
        float xRadius = 1.0f;
        float yRadius = 1.0f;
        float z = 1.0f;
        if (bounds instanceof BoundingSphere) {
            xRadius = yRadius = z = ((BoundingSphere)bounds).radius;
        } else if (bounds instanceof BoundingBox) {
            BoundingBox bb = (BoundingBox) bounds;
            xRadius = bb.xExtent;
            yRadius = bb.yExtent;
            z = bb.zExtent;
        }

        LOGGER.fine("Calculated distance to " + bounds + " is: " +
                    " x: " + getDistance(xRadius, z, fovX) + " for fov " + fovX +
                    " y: " + getDistance(yRadius, z, fovY) + " for fov " + fovY);

        // find the X and Y distances
        return Math.max(getDistance(xRadius, z, fovX),
                        getDistance(yRadius, z, fovY));
    }

    /**
     * Find the bounds of the model associated with this component's cell. If
     * minimal bounds cannot be found, use the cell's world bounds (which
     * are not likely to be accurate).
     *
     * @param cell the cell to get model bounds for
     * @return the best possible bounds we can find for this object
     */
    public static BoundingVolume getModelBounds(Cell cell) {
        // find a JME renderer for the cell
        CellRendererJME renderer = (CellRendererJME)
                cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        if (renderer == null) {
            return cell.getWorldBounds();
        }

        // next get the root of the scene graph
        Entity e = renderer.getEntity();
        RenderComponent rc = e.getComponent(RenderComponent.class);
        if (rc == null || rc.getSceneRoot() == null) {
            return cell.getWorldBounds();
        }

        // calculate the bounds from the root of the scene graph
        return getModelBounds(rc.getSceneRoot());
    }

    /**
     * Find the bounds of a node. This is different than node.getWorldBounds()
     * because we try to calculate the minimal bounds for the object without
     * taking rotation into account. For flat objects (like 2D windows), this
     * makes a significant difference in the calculated size.
     *
     * @param node the node to calculate the bounds of
     * @return the calculated bounds
     */
    private static BoundingVolume getModelBounds(Node node) {
        BoundingVolume out = null;

        Collection<Spatial> children = node.getChildren();
        if (children == null) {
            return out;
        }

        // caclulate the bounds by merging the bounds of all children
        for (Spatial s : children) {
            BoundingVolume childBounds = null;
            if (s instanceof Geometry) {
                childBounds = getModelBounds((Geometry) s);
            } else if (s instanceof Node) {
                childBounds = getModelBounds((Node) s);
            }

            if (out == null) {
                out = childBounds;
            } else {
                out.mergeLocal(childBounds);
            }
        }

        return out;
    }

    /**
     * Get the model bounds of a piece of geometry. This returns the properly
     * scaled bounds, but with no rotation or offset.
     * @param g the object to caculate the bounds of
     * @return the scaled bounds
     */
    private static BoundingVolume getModelBounds(Geometry g) {
        BoundingVolume clone = g.getModelBound().clone(null);
        clone = clone.transform(new Quaternion(), Vector3f.ZERO,
                                g.getWorldScale(), clone);
        return clone;
    }

    /**
     * Find the optimal distance for the given radius and field-of-view
     *
     * @param radius the radius to fit in the field of view
     * @param z the z offset to add to the result
     * @param fov the field of view to calculate for
     */
    private static float getDistance(float radius, float z, float fov) {
        double eyeDist = radius / Math.tan(Math.toRadians(fov / 2.0));

        // To make sure the front face of the Cell is in-view we must add the
        // bounds of the Cell along the z-axis
        eyeDist += z;

        return (float) eyeDist;
    }

}
