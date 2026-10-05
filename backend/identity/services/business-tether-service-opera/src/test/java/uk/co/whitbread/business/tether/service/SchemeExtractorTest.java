package uk.co.whitbread.business.tether.service;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemeExtractorTest {

    private final SchemeExtractor schemeExtractor = new SchemeExtractor();

    @Test
    public void shouldExtractGBScheme_whenCorrectLinkCodeIsPresent() {
        //when/then
        assertThat(schemeExtractor.extractScheme(createTetherLinkRequest("gtd-fdy-fgb"))).isEqualTo(Scheme.GB);
    }

    @Test
    public void shouldExtractDEScheme_whenLinkCodeWithDESuffixIsPresent() {
        //when/then
        assertThat(schemeExtractor.extractScheme(createTetherLinkRequest("gtd-fdy-fde"))).isEqualTo(Scheme.DE);
    }

    @Test
    public void shouldExtractDefaultGBScheme_whenLinkCodeWithoutGBSuffixIsPresent() {
        //when/then
        assertThat(schemeExtractor.extractScheme(createTetherLinkRequest("gtd-fdy-fxx"))).isEqualTo(Scheme.GB);
    }

    private TetherLinkRequest createTetherLinkRequest(String linkCode) {
        TetherLinkRequest tetherLinkRequest = new TetherLinkRequest();
        tetherLinkRequest.setLinkCode(linkCode);
        return tetherLinkRequest;
    }
}