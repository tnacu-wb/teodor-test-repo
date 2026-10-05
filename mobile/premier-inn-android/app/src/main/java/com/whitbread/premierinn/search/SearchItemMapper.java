package com.whitbread.premierinn.search;

import static com.whitbread.premierinn.data.search.ModelMapperKt.toHotelBrand;

import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.api.response.search.SearchItemInput;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;

import io.reactivex.functions.Function;


/**
 * Temporary Mapper until we replace {@link SearchItemInput} with
 * {@link com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem}
 */
public class SearchItemMapper implements Function<SearchSuggetionItem, SearchItemInput> {
    @Override
    public SearchItemInput apply(SearchSuggetionItem item) {

        Coordinates coordinates = Coordinates.create(
                item.getLocation() != null ? (float) item.getLocation().getLatitude() : 0.0f,
                item.getLocation() != null ? (float) item.getLocation().getLongitude() : 0.0f);

        return SearchItemInput.builder()
                .searchText(item.getName())
                .brand(toHotelBrand(item.getType()))
                .hotelId(item.getCode())
                .location(coordinates)
                .build();
    }
}
