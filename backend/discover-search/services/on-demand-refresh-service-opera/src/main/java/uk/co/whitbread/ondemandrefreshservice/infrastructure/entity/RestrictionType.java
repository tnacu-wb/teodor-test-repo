package uk.co.whitbread.ondemandrefreshservice.infrastructure.entity;

public enum RestrictionType {
  MIN_LOS("MinimumLengthOfStay"),
  MAX_LOS("MaximumLengthOfStay"),

  CL_LOS("Closed"),

  CTA_LOS("ClosedForArrival");

  private String los;

  RestrictionType(final String los){
    this.los = los;
  }

  public String los(){
    return los;
  }
}
