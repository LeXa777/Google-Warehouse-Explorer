/**
 * Project Wonderland
 *
 * $URL$
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Rev$
 * $Date$
 * $Author$
 */

package com.sun.labs.miw.client.cell;

import com.sun.labs.miw.common.MIWAlbum;
import java.util.Collection;

public interface AlbumCollection {
    public Collection<MIWAlbum> getAlbums();
    public MIWAlbum getAlbum(String name);
}
