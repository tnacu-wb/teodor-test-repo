package uk.co.whitbread.contentservice.roomtypes.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class NoRatesDataFoundExceptionTest {

    @Test
    public void getErrorCode() {
        NoRatesDataFoundException error = new NoRatesDataFoundException("Some error", "8765");
        assertThat(error.getErrorCode()).isEqualTo("8765");
    }

    @Test
    public void getErrorCodeWithoutCode() {
        NoRatesDataFoundException error = new NoRatesDataFoundException("Some error");
        assertThat(error.getErrorCode()).isEqualTo("8010");
    }
}
