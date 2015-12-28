/*
 * Copyright 2014 Marc Nause <marc.nause@gmx.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see  http:// www.gnu.org/licenses/.
 */
package de.audioattack.yacy32c3search.parser;

/**
 * Contains data of search item.
 *
 * @author Marc Nause <marc.nause@gmx.de>
 */
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

    private final String title;
    private final String link;
    private final String description;

    /**
     * Constructor.
     *
     * @param link        URL of search item
     * @param title       title of search item
     * @param description description of search item
     */
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

}
