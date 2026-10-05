package uk.co.whitbread.contentservice.roomtypes.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class NoCookiePoliciesDataFoundExceptionTest {

    @Test
    public void getErrorCode() {
        NoCookiePoliciesDataFoundException error = new NoCookiePoliciesDataFoundException("Some error", "8765");
        assertThat(error.getErrorCode()).isEqualTo("8765");
    }

    @Test
    public void getErrorCodeWithoutCode() {
        NoCookiePoliciesDataFoundException error = new NoCookiePoliciesDataFoundException("Some error");
        assertThat(error.getErrorCode()).isEqualTo("8011");
    }
}
