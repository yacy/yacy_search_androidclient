package de.audioattack.yacy31c3search.service;

public class SearchItem {

    // <item>
    // <title>WordPress › Support » Test driving 1.5</title>
    // <link>http://wordpress.org/support/topic/test-driving-15</link>
    // <description>WordPress › Support » &lt;b&gt;Test&lt;/b&gt; driving
    // 5</description>
    // <pubDate>Tue, 25 Jan 2011 16:00:00 -0800</pubDate>
    // <dc:publisher><![CDATA[]]></dc:publisher>
    // <dc:creator><![CDATA[]]></dc:creator>
    // <dc:subject><![CDATA[1 5 driving support test wordpress &raquo;
    // ›]]></dc:subject>
    // <yacy:size>-1</yacy:size>
    // <yacy:sizename>-1 bytes</yacy:sizename>
    // <yacy:host>wordpress.org</yacy:host>
    // <yacy:path>/support/topic/test-driving-15</yacy:path>
    // <yacy:file>/support/topic/test-driving-15</yacy:file>
    // <guid isPermaLink="false">-0SWOkYEbenR</guid>
    //
    // </item>

    private String title;
    private String link;
    private String description;

    public SearchItem(final String link, final String title, final String description) {
        this.link = link;
        this.title = title;
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (title != null) {
            result.append(title);
        }

        if (link != null) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(link.toString());
        }

        return result.toString();
    }

}
