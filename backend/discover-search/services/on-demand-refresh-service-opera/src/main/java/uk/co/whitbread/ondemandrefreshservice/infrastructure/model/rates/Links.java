package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Links {

    private String href;
    private String rel;
    private boolean templated;
    private String method;
    private String operationId;
}
