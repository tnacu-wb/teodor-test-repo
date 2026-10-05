package uk.co.whitbread.payapp.domain.model.out;

import lombok.Builder;

@Builder
public record UpdateResumeUrlResponse(
    Integer status,
    String message) {}
