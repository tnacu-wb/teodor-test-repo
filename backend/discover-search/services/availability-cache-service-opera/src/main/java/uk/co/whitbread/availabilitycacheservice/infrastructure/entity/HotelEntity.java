package uk.co.whitbread.availabilitycacheservice.infrastructure.entity;

import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DBBatchUtil.FETCH_OPERA_HOTELS_FOR_GQT_QUERY;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DBBatchUtil.FETCH_OPERA_HOTELS_QUERY;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DBBatchUtil.FETCH_PRICE_FINDER_MIN_RATE;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "hotel_ac", schema = "avail_cache")
@NamedNativeQuery(name = "HotelEntity.findAvailabilitiesForOpera",
    query = FETCH_OPERA_HOTELS_QUERY,
    resultSetMapping = "Mapping.HotelAvailabilitiesResultSet")
@NamedNativeQuery(name = "HotelEntity.findAvailabilitiesForGqtOpera",
    query = FETCH_OPERA_HOTELS_FOR_GQT_QUERY,
    resultSetMapping = "Mapping.HotelAvailabilitiesResultSet")
@NamedNativeQuery(name = "HotelEntity.findAvailabilitiesForPriceFinder",
    query = FETCH_PRICE_FINDER_MIN_RATE,
    resultSetMapping = "Mapping.PriceFinderResultSet")

@SqlResultSetMapping(name = "Mapping.HotelAvailabilitiesResultSet",
    classes = @ConstructorResult(targetClass = HotelAvailabilitiesResultSet.class,
        columns = {@ColumnResult(name = "hotelId", type = String.class),
            @ColumnResult(name = "hotelCode", type = String.class),
            @ColumnResult(name = "availableDate", type = LocalDate.class),
            @ColumnResult(name = "pmsSource", type = String.class),
            @ColumnResult(name = "rateId", type = String.class),
            @ColumnResult(name = "availability", type = Boolean.class),
            @ColumnResult(name = "rateClassification", type = String.class),
            @ColumnResult(name = "rateCode", type = String.class),
            @ColumnResult(name = "amount", type = BigDecimal.class),
            @ColumnResult(name = "currency", type = String.class),
            @ColumnResult(name = "minNights", type = Integer.class),
            @ColumnResult(name = "maxNights", type = Integer.class),
            @ColumnResult(name = "roomId", type = String.class),
            @ColumnResult(name = "quantity", type = Integer.class),
            @ColumnResult(name = "roomType", type = String.class),
            @ColumnResult(name = "amountWithCityTax", type = BigDecimal.class)}))

@SqlResultSetMapping(name = "Mapping.PriceFinderResultSet",
    classes = @ConstructorResult(targetClass = PriceFinderResultSet.class,
        columns = {@ColumnResult(name = "hotelCode", type = String.class),
            @ColumnResult(name = "availableDate", type = LocalDate.class),
            @ColumnResult(name = "minimumRate", type = BigDecimal.class),
            @ColumnResult(name = "currency", type = String.class),
            @ColumnResult(name = "rateCode", type = String.class),
            @ColumnResult(name = "rateClassification", type = String.class),
            @ColumnResult(name = "roomType", type = String.class),
            @ColumnResult(name = "quantity", type = Integer.class),
            @ColumnResult(name = "minNights", type = Integer.class),
            @ColumnResult(name = "maxNights", type = Integer.class),
            @ColumnResult(name = "minimumRateWithCityTax", type = BigDecimal.class)}))
public class HotelEntity {

  @Id
  @Column(length = 64)
  private String id;

  @Column(name = "hotel_code", length = 6, nullable = false)
  private String hotelCode;

  @Column(name = "avail_date", nullable = false)
  private LocalDate date;

  @Column(name = "pms_source", length = 16, nullable = false)
  private String pmsSource;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
  private List<RatePlanEntity> rates;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
  private List<RoomEntity> rooms;
}
