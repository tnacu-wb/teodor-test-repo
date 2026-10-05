package com.whitbread.premierinn.searchresults;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class SearchResultsInputTest {

    SearchResultsInput input;

    @Before
    public void setUp() throws Exception {
        input = SearchResultsInput.builder()
                .placeName("name")
                .numRooms(2)
                .latitude(0f)
                .longitude(0f)
                .arrivalDate(LocalDate.of(2017, 1, 10))
                .departureDate(LocalDate.of(2017, 2, 12))
                .adults(Arrays.asList(1, 1))
                .children(new ArrayList<>(Collections.singletonList(2)))
                .infants(Collections.emptyList())
                .cots(new ArrayList<>(Collections.singletonList(false)))
                .roomTypeCodes(new ArrayList<>(Collections.singletonList("DISS")))
                .build();
    }

    @Test
    public void totalNumOfGuests() {
        assertThat(input.totalNumOfGuests(), is(4));
    }
}
