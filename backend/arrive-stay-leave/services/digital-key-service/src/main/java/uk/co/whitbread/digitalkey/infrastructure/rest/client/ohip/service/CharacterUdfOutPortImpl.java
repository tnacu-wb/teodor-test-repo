package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CharacterUdfOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.UpdateUdfc20Request;

@RequiredArgsConstructor
@Slf4j
public class CharacterUdfOutPortImpl implements CharacterUdfOutPort {

  private final OhipAdapterClient ohipAdapterClient;

  @Override
  public void updateUdfc20(UpdateUdfc20Request updateUdfc20Request) {
    ohipAdapterClient.updateUdfc20(updateUdfc20Request);
  }

}