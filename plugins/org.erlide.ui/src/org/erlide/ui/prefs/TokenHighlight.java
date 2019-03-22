package org.erlide.ui.prefs;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.erlide.ui.prefs.plugin.ColoringPreferencePage;
import org.osgi.service.prefs.Preferences;

public enum TokenHighlight {
    //@formatter:off
    DEFAULT(new RGB(66,113,174), 0),
    KEYWORD(new RGB(137,89,168), 1),
    ATOM(new RGB(77,77,76), 0),
    MACRO(new RGB(234,183,0), 0),
    ARROW(new RGB(66,113,174), 1),
    CHAR(new RGB(113,140,0), 0),
    VARIABLE(new RGB(200,40,41), 0),
    INTEGER(new RGB( 245,135,31), 0),
    FLOAT(new RGB( 245,135,31), 0),

    COMMENT(new RGB(142,144,140), 0),
    EDOC_TAG(new RGB(142,144,140), 1, "EDoc tag (in comments)"),
    HTML_TAG(new RGB(142,144,140), 2, "HTML tag (in comments)"),

    STRING(new RGB(113,140,0), 0),
    ESCAPE_TAG(new RGB(113,140,0), 1, "Escaped chars (in strings)"),
    TILDE_TAG(new RGB(113,140,0), 1, "Format specifiers (in strings)");
    //@formatter:on

    private final RGB defaultColor;
    int defaultStyle;
    private final String displayName;

    private TokenHighlight(RGB defaultColor, int defaultStyle) {
        this(defaultColor, defaultStyle, null);
    }

    private TokenHighlight(RGB defaultColor, int defaultStyle, final String displayName) {
        this.defaultColor = defaultColor;
        this.defaultStyle = defaultStyle;
        this.displayName = displayName;
    }

    public String getName() {
        return toString().toLowerCase();
    }

    public String getDisplayName() {
        if (displayName == null) {
            return getName().replaceAll("_", " ");
        }
        return displayName;
    }

    public String getStylesKey() {
        return ColoringPreferencePage.COLORS_QUALIFIER + getName() + "_"
                + ColoringPreferencePage.STYLE_KEY;
    }

    public String getColorKey() {
        return ColoringPreferencePage.COLORS_QUALIFIER + getName() + "_"
                + ColoringPreferencePage.COLOR_KEY;
    }

    public HighlightStyle getStyle(final IPreferenceStore store) {
        final IEclipsePreferences node = InstanceScope.INSTANCE
                .getNode(ColoringPreferencePage.OLD_COLORS_QUALIFIER + "/" + getName());
        if (node != null) {
            final String colorString = node.get(ColoringPreferencePage.COLOR_KEY, null);
            if (colorString != null) {
                store.setValue(getColorKey(), colorString);
            }
            final int styles = node.getInt(ColoringPreferencePage.STYLE_KEY, -1);
            if (styles != -1) {
                store.setValue(getStylesKey(), styles);
            }
            try {
                final Preferences parent = node.parent();
                node.removeNode();
                parent.sync();
            } catch (final Exception e) {
                // ignore
            }
        }

        final String colorString = store.getString(getColorKey());
        RGB color;
        int styles;
        try {
            color = StringConverter.asRGB(colorString);
            styles = store.getInt(getStylesKey());
        } catch (final Exception e) {
            color = defaultColor;
            styles = defaultStyle;
        }
        return new HighlightStyle(color, styles);
    }

    public static boolean isColorKey(final String key) {
        if (!key.startsWith(ColoringPreferencePage.COLORS_QUALIFIER)) {
            return false;
        }
        return key.endsWith(ColoringPreferencePage.COLOR_KEY);
    }

    public static boolean isStylesKey(final String key) {
        if (!key.startsWith(ColoringPreferencePage.COLORS_QUALIFIER)) {
            return false;
        }
        return key.endsWith(ColoringPreferencePage.STYLE_KEY);
    }

    public static String getKeyName(final String key) {
        if (!key.startsWith(ColoringPreferencePage.COLORS_QUALIFIER)) {
            return null;
        }
        if (key.endsWith(ColoringPreferencePage.COLOR_KEY)) {
            return key.substring(ColoringPreferencePage.COLORS_QUALIFIER.length(),
                    key.length() - ColoringPreferencePage.COLOR_KEY.length() - 1);
        }
        return key.substring(ColoringPreferencePage.COLORS_QUALIFIER.length(),
                key.length() - ColoringPreferencePage.STYLE_KEY.length() - 1);
    }

}
