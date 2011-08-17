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
package org.jdesktop.wonderland.modules.videoplayer.client;

import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.utils.LibraryLoader;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.video.client.VideoLibraryLoader;

/**
 * Plugin to load video libraries before other code is called
 * @author Jonathan Kaplan <jonathankap@wonderbuilders.com>
 */
@Plugin
public class VideoLibraryPlugin extends BaseClientPlugin {
    static {
        VideoLibraryLoader.setLibraryLoader(new VideoLibraryLoader.LibraryLoaderSPI() {
            public boolean loadLibrary(String string) {
                return LibraryLoader.loadLibrary(string);
            }
        });
    }
}
