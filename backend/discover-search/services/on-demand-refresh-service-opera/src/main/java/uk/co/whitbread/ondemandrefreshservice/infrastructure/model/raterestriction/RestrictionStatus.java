package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RestrictionStatus {

    private String code;
    private int unit;

}
