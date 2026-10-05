package uk.co.whitbread.shared.cdh.model.spending.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    @JsonProperty("BookingReference")
    private String bookingReference;

    @JsonProperty("CustomerAccountId")
    private String customerAccountId;

    @JsonProperty("CompanyAccountId")
    private String companyAccountId;

    @JsonProperty("EmployeeAccountId")
    private String employeeAccountId;

    @JsonProperty("BookingType")
    private String bookingType;

    @JsonProperty("CardType")
    private String cardType;

    @JsonProperty("CardToken")
    private String cardToken;

    @JsonProperty("CardMaskedPAN")
    private String cardMaskedPAN;

    @JsonProperty("BookerName")
    private String bookerName;

    @JsonProperty("BookingValue")
    private BigDecimal bookingValue;

    @JsonProperty("BookingDate")
    private LocalDateTime bookingDate;

    @JsonProperty("ArrivalDate")
    private LocalDateTime arrivalDate;

    @JsonProperty("DepartureDate")
    private LocalDateTime departureDate;

    @JsonProperty("PIBAAccountId")
    private String pibaAccountId;

    @JsonProperty("PIBAAccountNo")
    private String pibaAccountNo;

    @JsonProperty("PIBACostCentre")
    private String pibaCostCentre;

    @JsonProperty("PIBACardNo")
    private String pibaCardNo;

    @JsonProperty("PIBAUser")
    private String pibaUser;

    @JsonProperty("BookingStatus")
    private String bookingStatus;

    @JsonProperty("HotelCode")
    private String hotelCode;

    @JsonProperty("HotelName")
    private String hotelName;

}
