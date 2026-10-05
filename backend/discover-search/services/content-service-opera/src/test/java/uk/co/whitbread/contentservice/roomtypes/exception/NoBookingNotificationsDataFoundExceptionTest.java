package uk.co.whitbread.contentservice.roomtypes.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class NoBookingNotificationsDataFoundExceptionTest {

    @Test
    public void getErrorCode() {
        NoBookingNotificationsDataFoundException error = new NoBookingNotificationsDataFoundException("Some error", "8765");
        assertThat(error.getErrorCode()).isEqualTo("8765");
    }

    @Test
    public void getErrorCodeWithoutCode() {
        NoBookingNotificationsDataFoundException error = new NoBookingNotificationsDataFoundException("Some error");
        assertThat(error.getErrorCode()).isEqualTo("8009");
    }
}
