package uk.co.whitbread.payapp.domain.model.out;

public record AppPreCheckResponse(Boolean isTetheredUser, String applicationGuid) {
}
