package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.mapper.ConfirmItemProcessingRequestBasketMapper;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.mapper.ConfirmItemProcessingRequestBasketMapperImpl;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in.ConfirmItemProcessingRequestDto;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ConfirmItemProcessingRequestBasketMapperTest {

    private final ConfirmItemProcessingRequestBasketMapper confirmItemProcessingRequestBasketMapper =
            new ConfirmItemProcessingRequestBasketMapperImpl();

    @ParameterizedTest
    @CsvSource({"0,COMPLETED", "1,FAILED"})
    void toConfirmItemProcessingDto__Success(Integer status, String description){
        var reqAction = "value";
        var basketAcknowledge = BasketAcknowledge.builder()
                .data(Map.of("reqAction", reqAction))
                .status(status)
                .build();
        var confirmItemProcessingRequestDto = ConfirmItemProcessingRequestDto.builder().build();
        confirmItemProcessingRequestBasketMapper.toConfirmItemProcessingDto(confirmItemProcessingRequestDto, basketAcknowledge);

        assertEquals(reqAction, confirmItemProcessingRequestDto.getReqAction());
        assertNotNull(confirmItemProcessingRequestDto.getReportedAt());
        assertEquals(description, confirmItemProcessingRequestDto.getDescription());
    }
}
