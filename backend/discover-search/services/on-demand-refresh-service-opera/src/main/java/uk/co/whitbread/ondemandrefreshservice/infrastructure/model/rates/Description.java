package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Description {

    private String defaultText;
    private List<?> translatedTexts;
}
