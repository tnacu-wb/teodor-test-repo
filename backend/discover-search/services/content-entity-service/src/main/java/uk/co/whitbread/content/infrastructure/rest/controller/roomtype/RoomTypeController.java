package uk.co.whitbread.content.infrastructure.rest.controller.roomtype;

import static uk.co.whitbread.content.domain.utils.SanitizingUtils.sanitize;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.RoomTypeInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.mapper.RoomTypeDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.mapper.RoomTypeRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.in.RoomTypeRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.out.RoomTypeDto;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RoomTypeController implements RoomTypeApiDocumentation {

  private final RoomTypeInPort roomTypeInPort;
  private final RoomTypeRequestDtoMapper roomTypeRequestDtoMapper;
  private final RoomTypeDtoMapper roomTypeDtoMapper;


  @GetMapping(value = ROOM_TYPE_PATH + "/room-type", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RoomTypeDto> getRoomTypeInformation(@Valid @ParameterObject
                                                            RoomTypeRequestDto roomTypeRequestDto) {

    log.debug("Request to get room types for {} {} brand {} started.",
        sanitize(roomTypeRequestDto.getCountry()), sanitize(roomTypeRequestDto.getLanguage()),
        sanitize(roomTypeRequestDto.getBrand()));
    var request = roomTypeRequestDtoMapper.toDomainModel(roomTypeRequestDto);
    var roomTypeDto = roomTypeDtoMapper.toDto(roomTypeInPort.getRoomType(request));
    return ResponseEntity.status(HttpStatus.OK).body(roomTypeDto);
  }
}
