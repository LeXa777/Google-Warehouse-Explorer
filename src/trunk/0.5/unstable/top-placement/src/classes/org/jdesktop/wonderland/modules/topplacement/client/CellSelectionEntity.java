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
package org.jdesktop.wonderland.modules.topplacement.client;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.jme.CellRefComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A visual Entity that displays the bounds of the scene graph root as either
 * a sphere or box.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellSelectionEntity extends Entity {

    // The Cell to display the bounds
    private Cell cell = null;

    // The root node of the bounds viewer scene graph
    private Node rootNode = null;

    // Listener for changes in the transform of the cell
    private TransformChangeListener updateListener = null;

    // True if the bounds viewer Entity is visible, false if not
    private boolean isVisible = false;

    // The color of the selection
    private ColorRGBA color = ColorRGBA.red;

    // The translation of the last shown bounds
    private Vector3f boundsCenter;

    /**
     * Constructor that takes the root Node of the Cell's scene graph
     */
    public CellSelectionEntity(Cell cell) {
        super("Cell Selection");

        // Create a new Node that serves as the root for the bounds viewer
        // scene graph
        this.cell = cell;

        // Add a reference so clicking on this entity selects the proper
        // cell
        addComponent(CellRefComponent.class, new CellRefComponent(cell));
    }

    public void showBounds(final BoundingVolume bounds) {
	if (rootNode != null) {
	    dispose();
	}

        // record the bounds translation
        boundsCenter = bounds.getCenter();

        rootNode = new Node("Cell Selection");
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        RenderComponent rc = rm.createRenderComponent(rootNode);
        this.addComponent(RenderComponent.class, rc);

        // Set the Z-buffer state on the root node
        ZBufferState zbuf = (ZBufferState)rm.createRendererState(StateType.ZBuffer);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        rootNode.setRenderState(zbuf);

        // Display as a wireframe
        WireframeState wf = (WireframeState)rm.createRendererState(StateType.Wireframe);
        wf.setEnabled(true);
        rootNode.setRenderState(wf);

        // Set the material for the wireframe
        MaterialState ms = (MaterialState)rm.createRendererState(StateType.Material);
        ms.setAmbient(color);
        ms.setEnabled(true);
        rootNode.setRenderState(ms);

        // Draw some geometry that mimics the bounds, either a sphere or a
        // box. Add to the scene graph of this Entity.
        if (bounds instanceof BoundingSphere) {
            float radius = ((BoundingSphere) bounds).radius;
            Sphere sphere = new Sphere("Sphere", 30, 30, radius);
            rootNode.attachChild(sphere);
        } else if (bounds instanceof BoundingBox) {
            float xExtent = ((BoundingBox)bounds).xExtent;
            float yExtent = ((BoundingBox)bounds).yExtent;
            float zExtent = ((BoundingBox)bounds).zExtent;
            Box box = new Box("Box", Vector3f.ZERO, xExtent, yExtent, zExtent);
            rootNode.attachChild(box);
        }

        // Fetch the world translation for the root node of the cell and set
        // the translation for this entity root node
        Vector3f translation = cell.getWorldTransform().getTranslation(null);
	translation = translation.add(boundsCenter);
        rootNode.setLocalTranslation(translation);
        rootNode.setLocalRotation(cell.getWorldTransform().getRotation(null));

        // Listen for changes to the cell's translation and apply the same
        // update to the root node of the bounds viewer. We also re-set the size
        // of the bounds: this handles the case where the bounds of the
        // scene graph has changed and we need to update the bounds viewer
        // accordingly.
        updateListener = new TransformChangeListener() {
            public void transformChanged(final Cell cell, ChangeSource source) {
                // We need to perform this work inside a proper updater, to
                // make sure we are MT thread safe
                final WorldManager wm = ClientContextJME.getWorldManager();
                RenderUpdater u = new RenderUpdater() {
                    public void update(Object obj) {
                        CellTransform transform = cell.getWorldTransform();
                        Vector3f translation = transform.getTranslation(null);
		        translation = translation.add(boundsCenter);
                        rootNode.setLocalTranslation(translation);
                        rootNode.setLocalRotation(transform.getRotation(null));
                        wm.addToUpdateList(rootNode);
                    }
                };
                wm.addRenderUpdater(u, this);
            }
        };
        cell.addTransformChangeListener(updateListener);
	setVisible(true);
    }

    public void setColor(final ColorRGBA color) {
        this.color = color;

        final WorldManager wm = ClientContextJME.getWorldManager();
        RenderUpdater u = new RenderUpdater() {
            public void update(Object obj) {
                if (rootNode == null) {
                    // nothing to update
                    return;
                }

                MaterialState ms = (MaterialState) rootNode.getRenderState(StateType.Material);
                ms.setAmbient(color);
                wm.addToUpdateList(rootNode);
            }
        };
        wm.addRenderUpdater(u, this);
    }

    public void showDrag(final Vector3f delta) {
        final WorldManager wm = ClientContextJME.getWorldManager();
        RenderUpdater u = new RenderUpdater() {
            public void update(Object obj) {
                CellTransform transform = cell.getWorldTransform();
                Vector3f translation = transform.getTranslation(null);
                translation = translation.add(boundsCenter);
                translation = translation.add(delta);
		rootNode.setLocalTranslation(translation);
                wm.addToUpdateList(rootNode);
            }
        };
        wm.addRenderUpdater(u, this);
    }

    public void showRotaton(final Quaternion rotation) {
        final WorldManager wm = ClientContextJME.getWorldManager();
        RenderUpdater u = new RenderUpdater() {
            public void update(Object obj) {
                CellTransform transform = cell.getWorldTransform();
                Quaternion rot = transform.getRotation(null);
                rot = rot.mult(rotation);
                rootNode.setLocalRotation(rot);
                wm.addToUpdateList(rootNode);
            }
        };
        wm.addRenderUpdater(u, this);
    }

    public void reset() {
        final WorldManager wm = ClientContextJME.getWorldManager();
        RenderUpdater u = new RenderUpdater() {
            public void update(Object obj) {
                CellTransform transform = cell.getWorldTransform();
                Vector3f translation = transform.getTranslation(null);
                translation = translation.add(boundsCenter);
                rootNode.setLocalRotation(transform.getRotation(null));
                rootNode.setLocalTranslation(translation);
                wm.addToUpdateList(rootNode);
            }
        };
        wm.addRenderUpdater(u, this);
    }

    /**
     * Returns the root Node for the bounds viewer Entity.
     *
     * @param The Entity root Node
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * Sets whether the bounds viewer is visible (true) or invisible (false).
     *
     * @param visible True to make the affordance visible, false to not
     */
    public synchronized void setVisible(boolean visible) {
        WorldManager wm = ClientContextJME.getWorldManager();

        // If we want to make the affordance visible and it already is not
        // visible, then make it visible. We do not need to put add/remove
        // Entities on the MT Game render thread, they are already thread safe.
        if (visible == true && isVisible == false) {
            wm.addEntity(this);
            isVisible = true;
            return;
        }

        // If we want to make the affordance invisible and it already is
        // visible, then make it invisible
        if (visible == false && isVisible == true) {
            wm.removeEntity(this);
            isVisible = false;
            return;
        }
    }

    public void dispose() {
        // First, to make sure the affordance is no longer visible
        setVisible(false);

        // Clean up all of the listeners so this class gets properly garbage
        // collected.
        cell.removeTransformChangeListener(updateListener);
        updateListener = null;
	rootNode = null;
    }

}
