package uk.co.whitbread.hotel.account.utils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import uk.co.whitbread.hotel.account.exceptions.AllStaysRetrieveException;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysResponse;

public class Paginator {

    private Paginator() {
    }

    public static StaysResponse retrievePageFromStays(StaysResponse staysResponse, int pageIndex, int pageSize) {
        validatePageIndex(pageIndex);
        validatePageSize(pageSize);

        List<Stay> stays = Optional.ofNullable(staysResponse.getStays()).orElse(Collections.emptyList());

        int staysSize = stays.size();
        int fromIndex = Math.multiplyExact(pageSize, pageIndex - 1);
        int toIndex = Math.min(staysSize, Math.addExact(fromIndex, pageSize));

        if (fromIndex > 0 && fromIndex >= staysSize) {
            throw new AllStaysRetrieveException("Page does not exist. There are only: " + staysSize + " stays");
        }

        List<Stay> staysPage = stays.subList(fromIndex, toIndex);

        setPageInfo(staysResponse, pageIndex, staysSize, staysPage);

        return staysResponse;
    }

    private static void setPageInfo(StaysResponse staysResponse, int pageIndex, int staysSize, List<Stay> staysPage) {
        staysResponse.setTotalSize(staysSize);
        staysResponse.setPageIndex(pageIndex);
        staysResponse.setPageSize(staysPage.size());
        staysResponse.setStays(staysPage);
    }

    private static void validatePageIndex(int pageIndex) {
        if (pageIndex < 0) {
            throw new AllStaysRetrieveException("Page index must be strictly positive");
        }
    }

    private static void validatePageSize(int pageSize) {
        if (pageSize < 0) {
            throw new AllStaysRetrieveException("Page size must be strictly positive");
        }
    }
}
