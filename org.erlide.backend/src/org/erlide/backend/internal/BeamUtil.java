package org.erlide.backend.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.internal.runtime.Activator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IPath;
import org.erlide.backend.BackendUtils;
import org.erlide.util.ErlLogger;
import org.osgi.framework.Bundle;

import com.ericsson.otp.erlang.OtpErlangBinary;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class BeamUtil {

    public static OtpErlangBinary getBeamBinary(final String moduleName,
            final URL beamPath) {
        FileInputStream s;
        try {
            s = (FileInputStream) beamPath.openStream();
            try {
                return getBeamBinary(moduleName, s);
            } finally {
                s.close();
            }

        } catch (final IOException e) {
            ErlLogger.warn(e);
            return null;
        }
    }

    public static OtpErlangBinary getBeamBinary(final String moduleName,
            final IPath beamPath) {
        FileInputStream s;
        try {
            s = new FileInputStream(beamPath.toPortableString());
            try {
                return getBeamBinary(moduleName, s);
            } finally {
                s.close();
            }

        } catch (final IOException e) {
            ErlLogger.warn(e);
            return null;
        }
    }

    private static OtpErlangBinary getBeamBinary(final String moduleName,
            final FileInputStream stream) {
        try {
            final int sz = (int) stream.getChannel().size();
            final byte[] buf = new byte[sz];
            stream.read(buf);
            return new OtpErlangBinary(buf);
        } catch (final IOException e) {
            ErlLogger.warn(e);
            return null;
        }
    }

    public static Collection<String> getPathsFromBundleAndFragments(final String name,
            final Bundle b) {
        final List<String> result = Lists.newArrayList();
        final String entryName = name.replace(" ", "%20");
        URL entry = b.getEntry(entryName);
        if (entry != null) {
            final String aPath = getPathFromUrl(entry);
            if (aPath != null) {
                result.add(aPath);
            }
        }

        final Activator activator = Activator.getDefault();
        if (activator != null) {
            final Bundle[] fragments = activator.getFragments(b);
            if (fragments != null) {
                for (int i = 0; i < fragments.length; i++) {
                    entry = fragments[i].getEntry(entryName);
                    if (entry != null) {
                        final String aPath = getPathFromUrl(entry);
                        result.add(aPath);
                    }
                }
            }
        }
        return result;
    }

    public static String getPathFromUrl(final URL entry) {
        try {
            final URL fileURL = FileLocator.toFileURL(entry);
            final URI uri = new URI(fileURL.toString().replace(" ", "%20"));
            final String path = new File(uri).getAbsolutePath();
            return path;
        } catch (final IOException e) {
            ErlLogger.error(e);
        } catch (final URISyntaxException e) {
            ErlLogger.error(e);
        }
        return null;
    }

    private BeamUtil() {
    }

    /**
     * @noreference This method is not intended to be referenced by clients.
     */
    public static void unpackBeamFiles(final Bundle b, final String location) {
        if (location == null) {
            ErlLogger.warn("Could not find 'ebin' in bundle %s.", b.getSymbolicName());
            return;
        }
        final File ebinDir = new File(location + "/ebin");
        ebinDir.mkdirs();
        for (final String fn : ebinDir.list()) {
            if (fn.charAt(0) == '.') {
                continue;
            }
            final File f = new File(fn);
            f.delete();
        }

        // TODO Do we have to also check any fragments?
        // see FindSupport.findInFragments

        final IConfigurationElement[] els = BackendUtils
                .getCodepathConfigurationElements();
        for (final IConfigurationElement el : els) {
            final IContributor c = el.getContributor();
            if (c.getName().equals(b.getSymbolicName())) {
                final String dirPath = el.getAttribute("path");
                final Enumeration<?> e = b.getEntryPaths(dirPath);
                if (e == null) {
                    ErlLogger.debug("* !!! error loading plugin " + b.getSymbolicName());
                    return;
                }
                while (e.hasMoreElements()) {
                    final String s = (String) e.nextElement();
                    final String beamModuleName = BackendUtils.getBeamModuleName(s);
                    if (beamModuleName != null) {
                        ErlLogger.debug(" unpack: " + beamModuleName);
                        final File beam = new File(ebinDir, beamModuleName + ".beam");
                        try {
                            beam.createNewFile();
                            final FileOutputStream fs = new FileOutputStream(beam);
                            try {
                                final URL url = b.getEntry(s);
                                if (url != null) {
                                    final OtpErlangBinary bin = getBeamBinary(
                                            beamModuleName, url);
                                    if (bin != null) {
                                        fs.write(bin.binaryValue());
                                    }
                                }
                            } finally {
                                fs.close();
                            }
                        } catch (final IOException e1) {
                            ErlLogger.warn(e1);
                        }
                    }
                }
            }
        }

    }

}
