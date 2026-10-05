package uk.co.whitbread.digitalkey.domain.ports.secondary;

import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.UpdateUdfc20Request;

public interface CharacterUdfOutPort {

  void updateUdfc20(UpdateUdfc20Request updateUdfc20Request);

}