package de.audioattack.yacy31c3search.service;

import java.io.InputStream;

interface ISearchResultParser {

    String getSearchUrlParameter();

    void parse(final InputStream input);

}
