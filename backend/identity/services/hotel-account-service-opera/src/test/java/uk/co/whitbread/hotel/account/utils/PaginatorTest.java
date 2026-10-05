package uk.co.whitbread.hotel.account.utils;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.account.exceptions.AllStaysRetrieveException;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysResponse;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.whitbread.hotel.account.utils.Paginator.retrievePageFromStays;

class PaginatorTest {

    @Test
    void retrievePageFromStays_NegativeIndex_ThrowsAllStaysRetrieveException() {

        assertThrows(AllStaysRetrieveException.class,
                () -> retrievePageFromStays(new StaysResponse(), -1, 0),
                "Page index must be strictly positive");
    }

    @Test
    void retrievePageFromStays_NegativeSize_ThrowsAllStaysRetrieveException() {

        assertThrows(AllStaysRetrieveException.class,
                () -> retrievePageFromStays(new StaysResponse(), 1, -5),
                "Page size must be strictly positive");
    }

    @Test
    void retrievePageFromStays_OutOfBoundaryIndex_ThrowsAllStaysRetrieveException() {

        assertThrows(AllStaysRetrieveException.class,
                () -> retrievePageFromStays(new StaysResponse(), 100, 100),
                "Page does not exist. There are only: 0 stays");
    }

    @Test
    void retrievePageFromStays_ValidFullPage() {
        List<Stay> stays = Collections.nCopies(15, new Stay());

        StaysResponse staysResponse = new StaysResponse();
        staysResponse.setStays(stays);

        StaysResponse expectedStaysResponse = new StaysResponse();
        List<Stay> firstPage = stays.subList(6, 9);
        expectedStaysResponse.setStays(firstPage);
        expectedStaysResponse.setPageIndex(3);
        expectedStaysResponse.setPageSize(3);
        expectedStaysResponse.setTotalSize(15);

        assertThat(retrievePageFromStays(staysResponse, 3, 3), is(equalTo(expectedStaysResponse)));
    }

    @Test
    void retrievePageFromStays_ValidPartialPage() {
        List<Stay> stays = Collections.nCopies(15, new Stay());

        StaysResponse staysResponse = new StaysResponse();
        staysResponse.setStays(stays);

        StaysResponse expectedStaysResponse = new StaysResponse();
        List<Stay> firstPage = stays.subList(10, 15);
        expectedStaysResponse.setStays(firstPage);
        expectedStaysResponse.setPageIndex(2);
        expectedStaysResponse.setPageSize(5);
        expectedStaysResponse.setTotalSize(15);

        assertThat(retrievePageFromStays(staysResponse, 2, 10), is(equalTo(expectedStaysResponse)));
    }
}
