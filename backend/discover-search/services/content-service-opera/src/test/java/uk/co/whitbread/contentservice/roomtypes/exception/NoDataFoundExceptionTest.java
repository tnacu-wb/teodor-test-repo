package uk.co.whitbread.contentservice.roomtypes.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class NoDataFoundExceptionTest {

    @Test
    public void getErrorCode() {
        NoDataFoundException error = new NoDataFoundException("Some error", "8765");
        assertThat(error.getErrorCode()).isEqualTo("8765");
    }

    @Test
    public void getErrorCodeWithoutCode() {
        NoDataFoundException error = new NoDataFoundException("Some error");
        assertThat(error.getErrorCode()).isEqualTo("8008");
    }
}