package de.audioattack.yacy31c3search.service;

import java.io.InputStream;

/**
 * Created by low012 on 22.12.14.
 */
public interface ISearchResultParser {

    String getSearchUrlParameter();

    void parse(final InputStream input);

}
