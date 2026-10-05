package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RoomType {

    private String code;
    private String description;
    private boolean pseudo;
}
