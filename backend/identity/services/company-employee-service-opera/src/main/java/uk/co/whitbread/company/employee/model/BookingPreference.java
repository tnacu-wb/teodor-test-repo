package uk.co.whitbread.company.employee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingPreference {
    @NotNull
    private Integer adults;
    @NotNull
    private Integer children;
    @NotNull
    private Boolean cotRequired;
    private RoomType roomType;
    private Boolean premierBreakfast;
    private Boolean continentalBreakfast;
    private Boolean mealDeal;
    private Boolean preselectWifi;
    private Boolean electronicInvoice;
}
