/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public
 * License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.erlide.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * A registry that maps <code>ImageDescriptors</code> to <code>Image</code>.
 */
public class ImageDescriptorRegistry {

    private final Map<ImageDescriptor, Image> fRegistry = new HashMap<>(10);

    private final Display fDisplay;

    /**
     * Creates a new image descriptor registry for the current or default display,
     * respectively.
     */
    public ImageDescriptorRegistry() {
        this(SWTUtil.getStandardDisplay());
    }

    /**
     * Creates a new image descriptor registry for the given display. All images managed
     * by this registry will be disposed when the display gets disposed.
     *
     * @param display
     *            the display the images managed by this registry are allocated for
     */
    public ImageDescriptorRegistry(final Display display) {
        fDisplay = display;
        hookDisplay();
    }

    /**
     * Returns the image assiciated with the given image descriptor.
     *
     * @param descriptor
     *            the image descriptor for which the registry manages an image
     * @return the image associated with the image descriptor or <code>null</code> if the
     *         image descriptor can't create the requested image.
     */
    public Image get(final ImageDescriptor descriptor0) {
        final ImageDescriptor descriptor = descriptor0 != null ? descriptor0
                : ImageDescriptor.getMissingImageDescriptor();

        Image result = fRegistry.get(descriptor);
        if (result != null) {
            return result;
        }

        result = descriptor.createImage();
        if (result != null) {
            fRegistry.put(descriptor, result);
        }
        return result;
    }

    /**
     * Disposes all images managed by this registry.
     */
    public void dispose() {
        for (final Object element : fRegistry.values()) {
            final Image image = (Image) element;
            image.dispose();
        }
        fRegistry.clear();
    }

    private void hookDisplay() {
        fDisplay.disposeExec(this::dispose);
    }
}
