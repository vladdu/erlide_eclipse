/*******************************************************************************
 * Copyright (c) 2010 György Orosz. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: György Orosz - initial API and implementation
 ******************************************************************************/
package org.erlide.wrangler.refactoring.backend.internal;

import org.erlide.wrangler.refactoring.Activator;
import org.erlide.wrangler.refactoring.backend.WranglerSyntaxBackend;

/**
 * Stores Erlide backends for wrangler , and has interface to access them
 *
 * @author Gyorgy Orosz
 * @version %I%, %G%
 */
public class WranglerBackendManager {
    static WranglerRefactoringBackend refactoringBackend;
    static WranglerSyntaxBackend syntaxBackend;

    private WranglerBackendManager() {
    }

    /**
     * Returns an Erlide backend which is used to run Wrangler refactorings
     *
     * @return Erlide backend
     */
    public static WranglerRefactoringBackend getRefactoringBackend() {
        if (WranglerBackendManager.refactoringBackend == null) {
            WranglerBackendManager.refactoringBackend = new WranglerRefactoringBackend(
                    Activator.getDefault().getBackend());
        }
        return WranglerBackendManager.refactoringBackend;
    }

    /**
     * Returns an Erlide backend which is used for sending syntax RPCs
     *
     * @return Erlide backend
     */
    public static WranglerSyntaxBackend getSyntaxBackend() {
        if (WranglerBackendManager.syntaxBackend == null) {
            WranglerBackendManager.syntaxBackend = new WranglerSyntaxBackend(
                    Activator.getDefault().getBackend());
        }
        return WranglerBackendManager.syntaxBackend;
    }
}
