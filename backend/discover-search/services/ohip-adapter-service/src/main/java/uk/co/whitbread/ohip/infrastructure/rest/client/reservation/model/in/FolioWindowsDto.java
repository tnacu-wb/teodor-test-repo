package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CompAccountingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CurrencyAmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FiscalFolioInstruction;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FiscalResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FolioType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FolioWindowExchangeAmounts;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PayeeInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TransactionServiceTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FolioWindowsDto {

  @JsonProperty("revenue")
  private CurrencyAmountType revenue;

  @JsonProperty("payment")
  private CurrencyAmountType payment;

  @JsonProperty("balance")
  private CurrencyAmountType balance;

  @JsonProperty("debitRevenue")
  private CurrencyAmountType debitRevenue;

  @JsonProperty("debitPayment")
  private CurrencyAmountType debitPayment;

  @JsonProperty("debitBalance")
  private CurrencyAmountType debitBalance;

  @JsonProperty("exchange")
  private FolioWindowExchangeAmounts exchange;

  @JsonProperty("paymentMethod")
  private ReservationPaymentMethodType paymentMethod;

  @JsonProperty("payeeInfo")
  private PayeeInfoType payeeInfo;

  @JsonProperty("compAccountingInfo")
  private CompAccountingType compAccountingInfo;

  @JsonProperty("storedFolioId")
  private UniqueIDType storedFolioId;

  @JsonProperty("storedFolioName")
  private String storedFolioName;

  @JsonProperty("fiscalResponseType")
  private FiscalResponseType fiscalResponseType;

  @JsonProperty("fiscalFolioInstruction")
  private FiscalFolioInstruction fiscalFolioInstruction;

  @JsonProperty("folios")
  @Valid
  private List<FolioType> folios = null;

  @JsonProperty("vATOffsetAmount")
  private CurrencyAmountType vATOffsetAmount;

  @JsonProperty("serviceTypeInfo")
  @Valid
  private List<TransactionServiceTypeType> serviceTypeInfo = null;

  @JsonProperty("folioWindowNo")
  private Integer folioWindowNo;

  @JsonProperty("internalFolioWindowID")
  private String internalFolioWindowID;

  @JsonProperty("emptyFolio")
  private Boolean emptyFolio;

  @JsonProperty("emptyWindow")
  private Boolean emptyWindow;

  @JsonProperty("simpleFolio")
  private Boolean simpleFolio;

  @JsonProperty("totalPages")
  private Integer totalPages;

  @JsonProperty("offset")
  private Integer offset;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("hasMore")
  private Boolean hasMore;

  @JsonProperty("totalResults")
  private Integer totalResults;

  @JsonProperty("count")
  private Integer count;
}