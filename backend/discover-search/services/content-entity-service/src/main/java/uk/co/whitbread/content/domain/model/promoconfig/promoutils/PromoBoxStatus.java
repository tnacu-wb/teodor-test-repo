package uk.co.whitbread.content.domain.model.promoconfig.promoutils;

public enum PromoBoxStatus {
  EMPTY,
  INVALID,
  CODE_ALREADY_APPLIED,
  CODE_EXPIRED,
  UNAVAILABLE,
  SUCCESS,
  MAX_ROOMS_EXCEEDED,
  MIN_ROOMS_NOT_MET
}