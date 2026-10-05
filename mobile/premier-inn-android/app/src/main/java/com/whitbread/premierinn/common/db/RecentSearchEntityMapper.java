package com.whitbread.premierinn.common.db;


import com.whitbread.premierinn.api.response.search.SearchItemInput;
import com.whitbread.premierinn.domain.search.entity.Location;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;

import io.reactivex.functions.Function;

public class RecentSearchEntityMapper {
    public static final Function<SearchItemInput, SearchSuggetionItem> MAPPER = item -> new SearchSuggetionItem(
            item.searchText(),
            new Location(item.location().latitude(), item.location().longitude()),
            item.hotelId(),
            getNewType(item.searchType())
    );

    public static SearchSuggetionItem.Type getNewType(SearchItemInput.SearchType type) {
        switch (type) {
            case PI_HOTEL:
                return SearchSuggetionItem.Type.PI_HOTEL;
            case HUB_HOTEL:
                return SearchSuggetionItem.Type.HUB_HOTEL;
            case ZIP_HOTEL:
                return SearchSuggetionItem.Type.ZIP_HOTEL;
            case PID_HOTEL:
                return SearchSuggetionItem.Type.PI_GERMAN_HOTEL;
            case DESTINATION:
            default:
                return SearchSuggetionItem.Type.LOCATION;
        }
    }
}
