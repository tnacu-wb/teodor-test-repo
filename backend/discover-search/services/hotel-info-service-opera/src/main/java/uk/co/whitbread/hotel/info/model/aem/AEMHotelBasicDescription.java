package uk.co.whitbread.hotel.info.model.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AEMHotelBasicDescription {

    
    private String code;
    @EqualsAndHashCode.Include
    private String title;
    private String brand;

}
