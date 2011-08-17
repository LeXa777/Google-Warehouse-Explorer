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
package org.jdesktop.wonderland.modules.cityblock.common;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * Represents the server-side configuration information for the city cell.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@XmlRootElement(name="city-cell")
@ServerState
public class CityCellServerState extends CellServerState {
    private boolean cellsAdded = false;
    private int streetCount = 10;
    private int avenueCount = 10;

    /** Default constructor */
    public CityCellServerState() {
    }

    @Override
    public String getServerClassName() {
        return "org.jdesktop.wonderland.modules.cityblock.server.CityCellMO";
    }

    @XmlElement
    public boolean getCellsAdded() {
        return cellsAdded;
    }

    public void setCellsAdded(boolean cellsAdded) {
        this.cellsAdded = cellsAdded;
    }

    @XmlElement
    public int getStreetCount() {
        return streetCount;
    }

    public void setStreetCount(int streetCount) {
        this.streetCount = streetCount;
    }

    @XmlElement
    public int getAvenueCount() {
        return avenueCount;
    }

    public void setAvenueCount(int avenueCount) {
        this.avenueCount = avenueCount;
    }

    @Override
    public String toString() {
        return "[CityCellServerState] added: " + cellsAdded;
    }
}
