package uk.co.whitbread.account;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = AccountEntityServiceApplication.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountEntityServiceApplicationTests {

  @Autowired
  public ApplicationContext applicationContext;

  @Test
  void contextLoads() {Assertions.assertNotNull(applicationContext);
  }
}
