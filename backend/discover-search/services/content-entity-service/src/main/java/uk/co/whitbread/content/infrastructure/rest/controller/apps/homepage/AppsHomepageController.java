package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.in.AppsHomepageRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.mapper.ControllerAppsHomepageMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AppsHomepageController implements AppsHomepageApiDocumentation {

  private final ContentInPort contentInPort;
  private final ControllerAppsHomepageMapper mapper;

  @Override
  @GetMapping(value = "/content/homepage", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AppsHomepageResponseDto> getHomepageApps(
      @Valid @ParameterObject AppsHomepageRequestDto appsHomepageRequestDto) {

    var homepageAppsRequest = mapper.toDomainModel(appsHomepageRequestDto);
    var homepageAppsResponse = contentInPort.getAppsHomepage(homepageAppsRequest);
    var homepageAppsResponseDto = mapper.toDto(homepageAppsResponse);
    return ResponseEntity.ok(homepageAppsResponseDto);
  }
}
