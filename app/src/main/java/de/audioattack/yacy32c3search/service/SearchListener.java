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
package de.audioattack.yacy32c3search.service;

import de.audioattack.yacy32c3search.parser.SearchItem;

/**
 * Will be informed about progress in parsing.
 *
 * @author Marc Nause <marc.nause@gmx.de>
 */
public interface SearchListener {

    /**
     * Will be called when loading data starts.
     */
    void onLoadingData();

    /**
     * Will be informed when loading and parsing of data has finished
     */
    void onFinishedData();

    /**
     * Will be called if retrieving search results failed due to an error.
     *
     * @param ex reason of fail
     */
    void onError(Exception ex);

    /**
     * Will be called if search results can't be loaded since network is unavailable.
     */
    void onNetworkUnavailable();

    /**
     * Will be called when list of results is cleared.
     *
     * @param numberOfResults number of results which have been cleared from results list.
     */
    void onOldResultCleared(int numberOfResults);

    /**
     * Will be called when an item is added to result list.
     *
     * @param item item which has been added
     */
    void onItemAdded(SearchItem item);
}
