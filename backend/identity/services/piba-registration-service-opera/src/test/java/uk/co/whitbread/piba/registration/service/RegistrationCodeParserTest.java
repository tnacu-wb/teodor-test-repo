package uk.co.whitbread.piba.registration.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationCodeParserTest {
    private final RegistrationCodeParser parser = new RegistrationCodeParser();

    @Test
    void shouldParseRegistrationCode_whenRegistrationCodeIsLongerThan2Characters() {
        //when/then
        assertThat(parser.parseRegistrationCode("A4ZN-2AZZ-Z2T6-A4GB")).isEqualTo("GB");
    }

    @Test
    void shouldReturnRegistrationCode_whenRegistrationCodeIsShorterThan2Characters() {
        //when/then
        assertThat(parser.parseRegistrationCode("B")).isEqualTo("B");
    }

}