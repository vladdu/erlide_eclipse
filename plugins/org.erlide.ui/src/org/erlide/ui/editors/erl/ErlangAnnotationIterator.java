/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public
 * License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.erlide.ui.editors.erl;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

/**
 * Filters problems based on their types.
 */
public class ErlangAnnotationIterator implements Iterator<Annotation> {

    private Iterator<?> fIterator;

    private Annotation fNext;

    private final boolean fSkipIrrelevants;

    private final boolean fReturnAllAnnotations;

    /**
     * Equivalent to <code>ErlangAnnotationIterator(model, skipIrrelevants, false)</code>.
     */
    public ErlangAnnotationIterator(final IAnnotationModel model,
            final boolean skipIrrelevants) {
        this(model, skipIrrelevants, false);
    }

    /**
     * Returns a new ErlangAnnotationIterator.
     *
     * @param model
     *            the annotation model
     * @param skipIrrelevants
     *            whether to skip irrelevant annotations
     * @param returnAllAnnotations
     *            Whether to return non {@link IErlangAnnotation}s as well
     */
    public ErlangAnnotationIterator(final IAnnotationModel model,
            final boolean skipIrrelevants, final boolean returnAllAnnotations) {
        fReturnAllAnnotations = returnAllAnnotations;
        if (model != null) {
            fIterator = model.getAnnotationIterator();
        } else {
            fIterator = Collections.emptyList().iterator();
        }
        fSkipIrrelevants = skipIrrelevants;
        skip();
    }

    private void skip() {
        while (fIterator.hasNext()) {
            final Annotation next = (Annotation) fIterator.next();
            if (next instanceof IErlangAnnotation) {
                if (fSkipIrrelevants) {
                    if (!next.isMarkedDeleted()) {
                        fNext = next;
                        return;
                    }
                } else {
                    fNext = next;
                    return;
                }
            } else if (fReturnAllAnnotations) {
                fNext = next;
                return;
            }
        }
        fNext = null;
    }

    /*
     * @see Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return fNext != null;
    }

    /*
     * @see Iterator#next()
     */
    @Override
    public Annotation next() {
        try {
            return fNext;
        } finally {
            skip();
        }
    }

    /*
     * @see Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
