package uk.co.whitbread.ohip.domain.model.reservation.in;

public record ProfileUpdateIndicators(boolean updateCompanyProfile, boolean updateContactProfile,
                                      boolean updateGuestProfile) {}
