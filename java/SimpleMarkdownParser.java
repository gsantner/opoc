/*
 * ----------------------------------------------------------------------------
 * "THE COKE-WARE LIBRARY LICENSE" (Revision 255):
 * Gregor Santner <gsantner.github.io> wrote this file. You can do whatever
 * you want with this stuff. If we meet some day, and you think this stuff is
 * worth it, you can buy me a coke in return. Provided as is without any kind
 * of warranty. No attribution required.                  - Gregor Santner
 * ----------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Simple Markdown Parser
 * <p>
 * Parses most common markdown tags. Only inline tags are supported, multiline/block syntax
 * is not supported (citation, multiline code, ..). This is intended to stay as easy as possible.
 * <p>
 * You can e.g. apply a accent color by replacing #000001 with your accentColor string.
 * <p>
 * simpleLineFilterAndroidTextView output is intended to be used at simple Android TextViews,
 * were a limited set of html tags is supported. This allow to still display e.g. a simple
 * CHANGELOG.md file without inlcuding a WebView for showing HTML, or other additional UI-libraries.
 * <p>
 * simpleLineFilterHtmlPart is intended to be used at engines understanding most common HTML tags.
 * <p>
 * You can use this anywhere you want, no backlink/attribution required, but I would appreciate it.
 */
@SuppressWarnings({"WeakerAccess", "CaughtExceptionImmediatelyRethrown"})
public class SimpleMarkdownParser {
    public interface SimpleLineFilter {
        String filterLine(String line);
    }

    public static final SimpleLineFilter simpleLineFilterAndroidTextView = new SimpleLineFilter() {
        @Override
        public String filterLine(String line) {
            // TextView supports a limited set of html tags, most notably
            // a href, b, big, font size&color, i, li, small, u
            line = line
                    .replace("~°", "&nbsp;&nbsp;") // double space/half tab
                    .replaceAll("^### ([^<]*)", "<br/><big><b><font color='#000000'>$1</font></b></big>") // h3
                    .replaceAll("^## ([^<]*)", "<br/><big><big><b><font color='#000000'>$1</font></b></big></big><br/><br/>") /// h2 (DEP: h3)
                    .replaceAll("^# ([^<]*)", "<br/><big><big><big><b><font color='#000000'>$1</font></b></big></big></big><br/><br/>") // h1 (DEP: h2,h3)
                    .replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<a href=\\'$2\\'>$1</a>") // img
                    .replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\\'$2\\'>$1</a>") // a href (DEP: img)
                    .replaceAll("^(-|\\*) ([^<]*)", "<font color='#000001'>&#8226;</font> $2  ") // unordered list + end line
                    .replaceAll("^  (-|\\*) ([^<]*)", "&nbsp;&nbsp;<font color='#000001'>&#8226;</font> $2  ") // unordered list2 + end line
                    .replaceAll("`([^<]*)`", "<i>$1</i>") // code
                    .replace("\\*", "●") // temporary replace escaped star symbol
                    .replaceAll("\\*\\*([^<]*)\\*\\*", "<b>$1</b>") // bold (DEP: temp star)
                    .replaceAll("\\*([^<]*)\\*", "<i>$1</i>") // italic (DEP: temp star code)
                    .replace("●", "*") // restore escaped star symbol (DEP: b,i)
                    .replaceAll("  $", "<br/>") // new line (DEP: ul)
            ;
            return !line.endsWith("<br/>") && !line.endsWith("<nobr/>")
                    && !line.trim().isEmpty() ? line + "<br/>" : line;
        }
    };

    public static final SimpleLineFilter simpleLineFilterHtmlPart = new SimpleLineFilter() {
        @Override
        public String filterLine(String line) {
            line = line
                    .replaceAll("~°", "&nbsp;&nbsp;") // double space/half tab
                    .replaceAll("^### ([^<]*)", "<h3>$1</h3><nobr/>") // h3
                    .replaceAll("^## ([^<]*)", "<h2>$1</h2><nobr/>") /// h2 (DEP: h3)
                    .replaceAll("^# ([^<]*)", "<h1>$1</h1><nobr/>") // h1 (DEP: h2,h3)
                    .replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<img src=\\'$2\\' alt='$1' />") // img
                    .replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\\'$2\\'>$1</a>") // a href (DEP: img)
                    .replaceAll("^(-|\\*) ([^<]*)", "<font color='#000001'>&#8226;</font> $2  ") // unordered list + end line
                    .replaceAll("^  (-|\\*) ([^<]*)", "&nbsp;&nbsp;<font color='#000001'>&#8226;</font> $2  ") // unordered list2 + end line
                    .replaceAll("`([^<]*)`", "<code>$1</code>") // code
                    .replace("\\*", "●") // temporary replace escaped star symbol
                    .replaceAll("\\*\\*([^<]*)\\*\\*", "<b>$1</b>") // bold (DEP: temp star)
                    .replaceAll("\\*([^<]*)\\*", "<b>$1</b>") // italic (DEP: temp star)
                    .replace("●", "*") // restore escaped star symbol (DEP: b,i)
                    .replaceAll("  $", "<br/>") // new line (DEP: ul)
            ;
            return !line.endsWith("<br/>") && !line.endsWith("<nobr/>")
                    && !line.trim().isEmpty() ? line + "<br/>" : line;
        }
    };

    public static String simpleMarkdownParse(String filepath, SimpleLineFilter simpleLineFilter) throws IOException {
        return simpleMarkdownParse(new FileInputStream(filepath), simpleLineFilter, "");
    }

    public static String simpleMarkdownParse(InputStream inputStream, SimpleLineFilter simpleLineFilter, String lineMdPrefix) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(simpleLineFilter.filterLine(lineMdPrefix + line));
                sb.append("\n");
            }
        } catch (IOException rethrow) {
            throw rethrow;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }

    public static String replaceUlCharacter(String html, String replacment) {
        return html.replace("&#8226;", replacment);
    }

    public static String colorToHexString(int intColor) {
        return String.format("#%06X", 0xFFFFFF & intColor);
    }
}

/*
    // Apply to Android TextView:
    String html = SimpleMarkdownParser.simpleMarkdownParse(
            context.getResources().openRawResource(R.raw.changelog),
            SimpleMarkdownParser.simpleLineFilterAndroidTextView, ""
    );
    textContributors.setText(new SpannableString(Html.fromHtml(html)));

    // As wrapper method, includes applying accent color
    public void loadRawMarkdownToTextView(@RawRes int rawMdFile, TextView textView, String prepend) {
        Context context = textView.getContext();
        try {
            String html = SimpleMarkdownParser.simpleMarkdownParse(
                    context.getResources().openRawResource(rawMdFile),
                    SimpleMarkdownParser.simpleLineFilterAndroidTextView, prepend
            );
            html = html
                    .replace("&#8226;", "*")
                    .replace("#000001", SimpleMarkdownParser.colorToHexString(ContextCompat.getColor(context, R.color.accent)));
            textView.setText(new SpannableString(Html.fromHtml(html)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 */
