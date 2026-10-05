package uk.co.whitbread.hotel.account.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.hotel.account.model.AddressType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AddressTypeMapperTest {

  private AddressTypeMapper addressTypeMapper;

  @BeforeEach
  void setUp() {
    addressTypeMapper = Mappers.getMapper(AddressTypeMapper.class);
  }

  @Test
  void testToAddressType_inputSameAsEnumName() {
    assertThat(addressTypeMapper.toAddressType("BUSINESS"), is(AddressType.BUSINESS));
  }

  @Test
  void testToAddressType_Trim() {
    assertThat(addressTypeMapper.toAddressType("BUSINESS "), is(AddressType.BUSINESS));
  }

  @Test
  void testToAddressType_ignoreCase() {
    assertThat(addressTypeMapper.toAddressType("bUSiNESs "), is(AddressType.BUSINESS));
  }
}
