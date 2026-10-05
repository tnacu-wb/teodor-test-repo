package uk.co.whitbread.payapp.domain.model.out;

import lombok.Builder;

@Builder
public record GetDdSepaFormStatusResponse(String status) {}
