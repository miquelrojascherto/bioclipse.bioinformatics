 /*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Jonathan Alvarsson
 *     
 ******************************************************************************/
package net.bioclipse.biojava.business;
import net.bioclipse.core.util.LogUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
/**
 * The activator class controls the plug-in life cycle
 * @author jonalv
 *
 */
public class Activator extends Plugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.biojava.business";
    // The shared instance
    private static Activator plugin;
    private static final Logger logger = Logger.getLogger(Activator.class);
    private ServiceTracker finderTracker;
    /**
     * The constructor
     */
    public Activator() {
    }
    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        finderTracker = new ServiceTracker( context, 
                                            IBiojavaManager.class.getName(), 
                                            null );
        finderTracker.open();
    }
    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    public IBiojavaManager getBioJavaManager() {
        IBiojavaManager manager = null;
        try {
            manager = (IBiojavaManager) finderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            logger.warn("Exception occurred while attempting to get the BiojavaManager" + e);
            LogUtils.debugTrace(logger, e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the biojava manager");
        }
        return manager;
    }
}
