/*
 * Code borrowed from PyDev
 */
/*
 * @author Fabio Zadrozny Created: June 2004 License: Common Public License v1.0
 */

package org.erlide.ui.internal.information;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.Iterator;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.erlide.ui.util.UIStringUtils;
import org.erlide.util.StringUtils;

/**
 * Based on HTMLTextPresenter
 *
 * @author Fabio
 */
@SuppressWarnings("restriction")
public class ErlInformationPresenter
        implements DefaultInformationControl.IInformationPresenter,
        DefaultInformationControl.IInformationPresenterExtension {

    public static final String LINE_DELIM = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

    private int fCounter;
    private final boolean fEnforceUpperLineLimit;

    public ErlInformationPresenter(final boolean enforceUpperLineLimit) {
        fEnforceUpperLineLimit = enforceUpperLineLimit;
    }

    public ErlInformationPresenter() {
        this(true);
    }

    /**
     * Creates the reader and properly puts the presentation into place.
     */
    protected Reader createReader(final String hoverInfo,
            final TextPresentation presentation) {
        String str = UIStringUtils.removeWhitespaceColumnsToLeft(hoverInfo);

        str = correctLineDelimiters(str);
        str = makeEdocBold(presentation, str);

        return new StringReader(str);
    }

    /**
     * The line delimiters must match the platform for the bolds to be correct, so, in
     * this function we remove the ones existing and add the ones dependent on the
     * platform
     */
    private String correctLineDelimiters(final String str) {
        final StringBuilder buf = new StringBuilder();
        for (String s : StringUtils.splitLines(str)) {

            boolean found = false;
            while (s.endsWith("\r") || s.endsWith("\n")) {
                found = true;
                s = s.substring(0, s.length() - 1);
            }
            buf.append(s);
            if (found) {
                buf.append(ErlInformationPresenter.LINE_DELIM);
            }
        }
        return buf.toString();
    }

    /**
     * Changes the @xxx bbb: things for bold
     */
    private String makeEdocBold(final TextPresentation presentation, final String str) {
        int lastIndex = 0;

        // 1st, let's mark in bold the things generated in edoc.
        while (true) {
            final int start = str.indexOf('@', lastIndex);
            if (start == -1) {
                break;
            }
            int end = start + 1;
            while (end < str.length()) {
                if (str.charAt(end) != ':') {
                    end++;
                } else {
                    break;
                }
            }
            if (end == start) {
                break;
            }
            lastIndex = end;
            presentation.addStyleRange(
                    new StyleRange(start, end - start, null, null, SWT.BOLD));
        }

        // return the input (this one doesn't change the string)
        return str;
    }

    protected void adaptTextPresentation(final TextPresentation presentation,
            final int offset, final int insertLength) {

        final int yoursStart = offset;
        int yoursEnd = offset + insertLength - 1;
        yoursEnd = Math.max(yoursStart, yoursEnd);

        @SuppressWarnings("unchecked")
        final Iterator<StyleRange> e = presentation.getAllStyleRangeIterator();
        while (e.hasNext()) {

            final StyleRange range = e.next();

            final int myStart = range.start;
            int myEnd = range.start + range.length - 1;
            myEnd = Math.max(myStart, myEnd);

            if (myEnd < yoursStart) {
                continue;
            }

            if (myStart < yoursStart) {
                range.length += insertLength;
            } else {
                range.start += insertLength;
            }
        }
    }

    private void append(final StringBuffer buffer, final String string,
            final TextPresentation presentation) {

        final int length = string.length();
        buffer.append(string);

        if (presentation != null) {
            adaptTextPresentation(presentation, fCounter, length);
        }

        fCounter += length;
    }

    private String getIndent(final String line) {
        final int length = line.length();

        int i = 0;
        while (i < length && Character.isWhitespace(line.charAt(i))) {
            ++i;
        }

        return (i == length ? line : line.substring(0, i)) + " "; //$NON-NLS-1$
    }

    @Deprecated
    @Override
    public String updatePresentation(final Display display, final String hoverInfo,
            final TextPresentation presentation, final int maxWidth,
            final int maxHeight) {
        return updatePresentation((Drawable) display, hoverInfo, presentation, maxWidth,
                maxHeight);
    }

    @Override
    public String updatePresentation(final Drawable drawable, final String hoverInfo,
            final TextPresentation presentation, final int maxWidth,
            final int maxHeight) {

        if (hoverInfo == null) {
            return null;
        }

        final GC gc = new GC(drawable);
        try {

            final StringBuffer buffer = new StringBuffer();
            int maxNumberOfLines = Math
                    .round((float) maxHeight / (float) gc.getFontMetrics().getHeight());

            fCounter = 0;
            final LineBreakingReader reader = new LineBreakingReader(
                    createReader(hoverInfo, presentation), gc, maxWidth);

            boolean lastLineFormatted = false;
            String lastLineIndent = null;

            String line = reader.readLine();
            boolean lineFormatted = reader.isFormattedLine();
            boolean firstLineProcessed = false;

            while (line != null) {

                if (fEnforceUpperLineLimit && maxNumberOfLines <= 0) {
                    break;
                }

                if (firstLineProcessed) {
                    if (!lastLineFormatted) {
                        append(buffer, ErlInformationPresenter.LINE_DELIM, null);
                    } else {
                        append(buffer, ErlInformationPresenter.LINE_DELIM, presentation);
                        if (lastLineIndent != null) {
                            append(buffer, lastLineIndent, presentation);
                        }
                    }
                }

                append(buffer, line, null);
                firstLineProcessed = true;

                lastLineFormatted = lineFormatted;
                if (!lineFormatted) {
                    lastLineIndent = null;
                } else if (lastLineIndent == null) {
                    lastLineIndent = getIndent(line);
                }

                line = reader.readLine();
                lineFormatted = reader.isFormattedLine();

                maxNumberOfLines--;
            }

            if (line != null) {
                append(buffer, ErlInformationPresenter.LINE_DELIM,
                        lineFormatted ? presentation : null);
            }
            return trim(buffer, presentation);

        } catch (final IOException e) {
            // ignore TODO do something else?
            return null;
        } finally {
            gc.dispose();
        }
    }

    private String trim(final StringBuffer buffer, final TextPresentation presentation) {
        final int length = buffer.length();

        int end = length - 1;
        while (end >= 0 && Character.isWhitespace(buffer.charAt(end))) {
            --end;
        }

        if (end == -1) {
            return ""; //$NON-NLS-1$
        }

        if (end < length - 1) {
            buffer.delete(end + 1, length);
        } else {
            end = length;
        }

        int start = 0;
        while (start < end && Character.isWhitespace(buffer.charAt(start))) {
            ++start;
        }

        buffer.delete(0, start);
        presentation.setResultWindow(new Region(start, buffer.length()));
        return buffer.toString();
    }

    /**
     * Minimal replacement for the removed internal JFace
     * {@code org.eclipse.jface.internal.text.link.contentassist.LineBreakingReader}.
     * Reads input one logical line at a time and, if the line is wider than the
     * configured pixel width when measured with the given {@link GC}, breaks it
     * on word boundaries. A "formatted" line is a continuation produced by such
     * a break (i.e. the second or later chunk of a wrapped source line).
     */
    private static final class LineBreakingReader {
        private final BufferedReader fReader;
        private final GC fGC;
        private final int fMaxWidth;
        private String fLine;
        private int fOffset;
        private BreakIterator fLineBreakIterator;
        private boolean fBreakWords;
        private boolean fIsFormattedLine;

        LineBreakingReader(final Reader reader, final GC gc, final int maxLineWidth) {
            fReader = new BufferedReader(reader);
            fGC = gc;
            fMaxWidth = maxLineWidth;
            fOffset = 0;
            fLine = null;
            fLineBreakIterator = BreakIterator.getLineInstance();
            fBreakWords = true;
        }

        boolean isFormattedLine() {
            return fIsFormattedLine;
        }

        String readLine() throws IOException {
            if (fLine == null) {
                final String line = fReader.readLine();
                if (line == null) {
                    return null;
                }
                if (fGC.textExtent(line).x < fMaxWidth) {
                    fIsFormattedLine = false;
                    return line;
                }
                fLine = line;
                fLineBreakIterator.setText(line);
                fOffset = 0;
            }
            final int breakOffset = findNextBreakOffset(fOffset);
            String res;
            if (breakOffset != BreakIterator.DONE) {
                res = fLine.substring(fOffset, breakOffset);
                fOffset = breakOffset;
                fIsFormattedLine = true;
            } else {
                res = fLine.substring(fOffset);
                fLine = null;
                fIsFormattedLine = false;
            }
            return res;
        }

        private int findNextBreakOffset(final int currOffset) {
            int currWidth = 0;
            int nextOffset = fLineBreakIterator.following(currOffset);
            while (nextOffset != BreakIterator.DONE) {
                final String word = fLine.substring(currOffset, nextOffset);
                final int wordWidth = fGC.textExtent(word).x;
                final int nextWidth = wordWidth + currWidth;
                if (nextWidth > fMaxWidth) {
                    if (currWidth > 0) {
                        return findWordBegin(currOffset, nextOffset);
                    }
                    if (fBreakWords) {
                        return findCharBreak(currOffset, wordWidth);
                    }
                    return nextOffset;
                }
                currWidth = nextWidth;
                nextOffset = fLineBreakIterator.next();
            }
            return BreakIterator.DONE;
        }

        private int findWordBegin(final int start, final int end) {
            int i = end;
            while (i > start && Character.isWhitespace(fLine.charAt(i - 1))) {
                i--;
            }
            return i == start ? end : i;
        }

        private int findCharBreak(final int start, final int wordWidth) {
            // Fallback: break at a character boundary inside an oversized word.
            int width = 0;
            for (int i = start; i < fLine.length(); i++) {
                width += fGC.textExtent(String.valueOf(fLine.charAt(i))).x;
                if (width > fMaxWidth) {
                    return i == start ? start + 1 : i;
                }
            }
            return fLine.length();
        }
    }
}
