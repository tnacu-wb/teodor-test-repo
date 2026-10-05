package uk.co.whitbread.hotel.account.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EmployeeMapperImpl.class, AddressTypeMapperImpl.class,
        CardNumberMapperImpl.class})
class EmployeeMapperTest {

  @Autowired
  EmployeeMapper employeeMapper;

  @Autowired
  AddressTypeMapper addressTypeMapper;

  @Autowired
  CardNumberMapper cardNumberMapper;

  @Test
  void getEmployeeResponseToCustomer_cdh() {
    final String companyId = "companyAccountId";
    final String employeeId = "employeeAccountId";

    GetEmployeeResponse employeeResponse = mockEmployeeResponse();
    Customer customer = employeeMapper.toCustomer(employeeResponse);

    assertThat(customer.getCompanyId(), is(companyId));
    assertThat(customer.getBusiness().getEmployeeId(), is(employeeId));
  }

  public GetEmployeeResponse mockEmployeeResponse(){
    GetEmployeeResponse employeeResponse = new GetEmployeeResponse();

    employeeResponse.setCompanyAccountId("companyAccountId");
    employeeResponse.setGlobalCompanyId("companyIdFromBart");
    employeeResponse.setEmployeeAccountId("employeeAccountId");
    employeeResponse.setBartEmployeeId("bartEmployeeId");

    return employeeResponse;
  }
}
