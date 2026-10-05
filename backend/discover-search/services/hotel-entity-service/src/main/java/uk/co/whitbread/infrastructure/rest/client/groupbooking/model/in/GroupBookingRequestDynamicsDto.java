package uk.co.whitbread.infrastructure.rest.client.groupbooking.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBookingRequestDynamicsDto {

  private String title;

  @JsonProperty("ebecs_wftitle")
  private String ebecsWftitle;

  @JsonProperty("ebecs_wffname")
  private String ebecsWffname;

  @JsonProperty("ebecs_wflname")
  private String ebecsWflname;

  @JsonProperty("ebecs_wfemail")
  private String ebecsWfemail;

  @JsonProperty("ebecs_wfphone")
  private String ebecsWfphone;

  @JsonProperty("ebecs_wftypeofbooker")
  private String ebecsWftypeofbooker;

  @JsonProperty("ebecs_wfpurposeofstay")
  private String ebecsWfpurposeofstay;

  @JsonProperty("ebecs_wfcompanyname")
  private String ebecsWfcompanyname;

  @JsonProperty("ebecs_wfreasonforvisit")
  private String ebecsWfreasonforvisit;

  @JsonProperty("ebecs_wfreasonforvisitother")
  private String ebecsWfreasonforvisitother;

  @JsonProperty("ebecs_wfhotelname")
  private String ebecsWfhotelname;

  @JsonProperty("ebecs_wfhotelcode")
  private String ebecsWfhotelcode;

  @JsonProperty("ebecs_wfhotelbrand")
  private String ebecsWfhotelbrand;

  @JsonProperty("ebecs_wfpackagetypemealdeal")
  private String ebecsWfpackagetypemealdeal;

  @JsonProperty("ebecs_wfpackagetypebf")
  private String ebecsWfpackagetypebf;

  @JsonProperty("ebecs_wfarrivaldate")
  private String ebecsWfarrivaldate;

  @JsonProperty("ebecs_wfdeparturedate")
  private String ebecsWfdeparturedate;

  @JsonProperty("ebecs_wfschoolyouthgroup")
  private Boolean ebecsWfschoolyouthgroup;

  @JsonProperty("ebecs_wfsingleoccupancy")
  private Integer ebecsWfsingleoccupancy;

  @JsonProperty("ebecs_wfdoubleoccupancy")
  private Integer ebecsWfdoubleoccupancy;

  @JsonProperty("ebecs_wftwinrooms")
  private Integer ebecsWftwinrooms;

  @JsonProperty("ebecs_wffamilyof2")
  private Integer ebecsWffamilyof2;

  @JsonProperty("ebecs_wffamilyof31a")
  private Integer ebecsWffamilyof31a;

  @JsonProperty("ebecs_wffamilyof32a")
  private Integer ebecsWffamilyof32a;

  @JsonProperty("ebecs_wffamilyof4")
  private Integer ebecsWffamilyof4;

  @JsonProperty("ebecs_wfaccessiblesingle")
  private Integer ebecsWfaccessiblesingle;

  @JsonProperty("ebecs_wfaccessibledouble")
  private Integer ebecsWfaccessibledouble;

  @JsonProperty("ebecs_wfaccessibletwin")
  private Integer ebecsWfaccessibletwin;

  @JsonProperty("ebecs_wfcomments")
  private String ebecsWfcomments;

  @JsonProperty("ebecs_wfsource")
  private String ebecsWfsource;

  @JsonProperty("customerid_contact@odata.bind")
  private String customeridContactOdataBind;

}
