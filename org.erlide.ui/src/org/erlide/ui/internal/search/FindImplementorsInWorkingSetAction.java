/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.erlide.ui.internal.search;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.erlide.engine.services.search.ErlSearchScope;
import org.erlide.ui.editors.erl.AbstractErlangEditor;

/**
 * Finds references of the selected element in working sets. The action is
 * applicable to selections representing a Erlang element.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 */
public class FindImplementorsInWorkingSetAction extends FindImplementorsAction {

    private final IWorkingSet[] fWorkingSets;

    /**
     * Creates a new <code>FindReferencesInWorkingSetAction</code>. The action
     * requires that the selection provided by the site's selection provider is
     * of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>. The
     * user will be prompted to select the working sets.
     *
     * @param site
     *            the site providing context information for this action
     */
    public FindImplementorsInWorkingSetAction(final IWorkbenchSite site) {
        this(site, null);
    }

    /**
     * Creates a new <code>FindReferencesInWorkingSetAction</code>. The action
     * requires that the selection provided by the site's selection provider is
     * of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
     *
     * @param site
     *            the site providing context information for this action
     * @param workingSets
     *            the working sets to be used in the search
     */
    public FindImplementorsInWorkingSetAction(final IWorkbenchSite site,
            final IWorkingSet[] workingSets) {
        super(site);
        fWorkingSets = workingSets;
    }

    /**
     * Note: This constructor is for internal use only. Clients should not call
     * this constructor.
     *
     * @param editor
     *            the Erlang editor
     */
    public FindImplementorsInWorkingSetAction(final AbstractErlangEditor editor) {
        this(editor, null);
    }

    /**
     * Note: This constructor is for internal use only. Clients should not call
     * this constructor.
     *
     * @param editor
     *            the Java editor
     * @param workingSets
     *            the working sets to be used in the search
     */
    public FindImplementorsInWorkingSetAction(final AbstractErlangEditor editor,
            final IWorkingSet[] workingSets) {
        super(editor);
        fWorkingSets = workingSets;
    }

    @Override
    void init() {
        setText("Working set");
        setToolTipText("Find declarations in working set");
        // FIXME setImageDescriptor(JavaPluginImages.DESC_OBJS_SEARCH_REF);
        // FIXME PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
        // IJavaHelpContextIds.FIND_REFERENCES_IN_WORKING_SET_ACTION);
    }

    @Override
    protected ErlSearchScope getScope() {
        // return SearchUtil.getWorkingSetsScope(fWorkingSets);
        return null;
    }

    @Override
    protected String getScopeDescription() {
        return SearchUtil.getWorkingSetsScopeDescription(fWorkingSets);
    }
}
