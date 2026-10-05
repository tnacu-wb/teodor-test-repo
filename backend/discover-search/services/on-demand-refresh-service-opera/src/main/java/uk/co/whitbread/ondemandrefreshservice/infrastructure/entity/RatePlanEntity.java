package uk.co.whitbread.ondemandrefreshservice.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "rate", schema = "avail_cache")
public class RatePlanEntity implements Persistable<String>, Serializable {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "avail")
    private boolean availability;

    @Column(name = "rate_category", length = 1)
    @EqualsAndHashCode.Include
    private String rateClassification;

    @Column
    private BigDecimal amount;

    @Column(name = "premium_amt")
    private BigDecimal premiumAmount;

    @Column(name = "amount_with_city_tax")
    private BigDecimal amountWithCityTax;

    @Column(length = 1)
    private String currency;

    @Column(name = "min_nights")
    private int minNights;

    @Column(name = "max_nights")
    private int maxNights;

    @Column(name = "rate_code", length = 32)
    @EqualsAndHashCode.Include
    private String rateCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_rate_hotel"))
    private HotelEntity hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_rate_room"))
    private RoomEntity room;

    @Builder.Default
    private @Transient boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
