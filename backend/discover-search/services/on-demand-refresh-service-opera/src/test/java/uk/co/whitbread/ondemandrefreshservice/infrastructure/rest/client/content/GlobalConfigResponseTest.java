package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.dto.GlobalConfigResponseDto;

import java.util.Collections;
import java.util.List;

class GlobalConfigResponseTest {

    @Test
    void testRecordConstructionAndAccessor() {
        // Arrange
        List<String> hotels = List.of("EDICAR",
                "EDIROS",
                "EDIHAY",
                "LIVCIT",
                "LIVPLI",
                "LIVLIM",
                "LIVJLA",
                "LIVHAN",
                "EDIPRI",
                "EDIOLD",
                "EDICUD",
                "EDINEW",
                "EDILAD",
                "EDIPAR",
                "EDIWAV",
                "EDIAIR",
                "EDIYOR",
                "EDIQUE",
                "MANSPI",
                "MANDAL",
                "MANOXF",
                "MANMTI",
                "MANPMI",
                "MANBAR",
                "EDIPTI");

        // Act
        GlobalConfigResponseDto response = new GlobalConfigResponseDto(hotels);

        // Assert
        assertNotNull(response);
        assertEquals(hotels, response.hotelsWithCityTax());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        GlobalConfigResponseDto response1 = new GlobalConfigResponseDto(List.of("EDICAR",
                "EDIROS",
                "EDIHAY",
                "LIVCIT",
                "LIVPLI",
                "LIVLIM",
                "LIVJLA"));
        GlobalConfigResponseDto response2 = new GlobalConfigResponseDto(List.of("EDICAR",
                "EDIROS",
                "EDIHAY",
                "LIVCIT",
                "LIVPLI",
                "LIVLIM",
                "LIVJLA"));
        GlobalConfigResponseDto response3 = new GlobalConfigResponseDto(List.of("EDILAD", "EDIPAR"));
        GlobalConfigResponseDto response4 = new GlobalConfigResponseDto(null);
        GlobalConfigResponseDto response5 = new GlobalConfigResponseDto(null);

        // Assert for equals
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(null, response1);
        assertNotEquals(new Object(), response1);
        assertEquals(response4, response5);
        assertNotEquals(response1, response4);

        // Assert for hashCode
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
        assertEquals(response4.hashCode(), response5.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        GlobalConfigResponseDto response = new GlobalConfigResponseDto(List.of("EDICAR"));

        // Act & Assert
        assertNotNull(response.toString());
        assertEquals("GlobalConfigResponseDto[hotelsWithCityTax=[EDICAR]]", response.toString());
    }

    @Test
    void testEmptyAndNullListHandling() {
        // Arrange & Act
        GlobalConfigResponseDto responseWithNull = new GlobalConfigResponseDto(null);
        GlobalConfigResponseDto responseWithEmpty = new GlobalConfigResponseDto(Collections.emptyList());

        // Assert
        assertNull(responseWithNull.hotelsWithCityTax());
        assertEquals(Collections.emptyList(), responseWithEmpty.hotelsWithCityTax());
    }
}
