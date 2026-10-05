package uk.co.whitbread.company.employee.client;

public class FeignNonServerErrorException extends RuntimeException {

    public FeignNonServerErrorException(String message) {
        super(message);
    }
}
