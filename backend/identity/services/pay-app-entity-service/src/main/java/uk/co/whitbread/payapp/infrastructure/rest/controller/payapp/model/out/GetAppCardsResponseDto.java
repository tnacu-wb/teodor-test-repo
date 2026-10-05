package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.CardDetailsDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAppCardsResponseDto {

  private List<CardDetailsDto> appCards;

}
