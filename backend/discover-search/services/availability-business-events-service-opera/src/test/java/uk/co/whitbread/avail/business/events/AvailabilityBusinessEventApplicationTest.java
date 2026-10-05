package uk.co.whitbread.avail.business.events;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@SpringBootTest(classes = {AvailabilityBusinessEventApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.main.web-application-type=reactive",
        "opera.clientId=testUserName",
        "opera.clientSecret=testPassword",
        "SubscribeBusinessEventsColdStartSvc.runner.enabled=false",
        "spring.datasource.writer.jdbc-url=jdbc:h2:mem:testdb;MODE=PostgreSQL",
        "spring.datasource.writer.driver-class-name=org.h2.Driver",
        "spring.datasource.writer.username=sa",
        "spring.datasource.writer.password=",
        "hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "hibernate.hbm2ddl.auto=create-drop"
    })
@Slf4j
public class AvailabilityBusinessEventApplicationTest {

  @Test
  public void test_JUnit() {
    log.info("TestJunit");
    String str1 = "TestJunit";
    assertEquals("TestJunit", str1);
  }
}