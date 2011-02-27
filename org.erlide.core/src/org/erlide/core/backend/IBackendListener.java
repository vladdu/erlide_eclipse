/*******************************************************************************
 * Copyright (c) 2004 Vlad Dumitrescu and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Vlad Dumitrescu
 *******************************************************************************/
package org.erlide.core.backend;

import org.eclipse.core.resources.IProject;
import org.erlide.core.backend.rpc.RpcCallSite;

public interface IBackendListener {

    public void runtimeAdded(Backend backend);

    public void runtimeRemoved(Backend backend);

    public void moduleLoaded(RpcCallSite backend, IProject project,
            String moduleName);

}