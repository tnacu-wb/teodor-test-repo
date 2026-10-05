package uk.co.whitbread.reservation.infrastructure.rest.client.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.reservation.infrastructure.rest.client.reservation.AemOutPortImpl.RESTAURANT_PREMIERINN;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.domain.model.out.aem.CookieGroup;
import uk.co.whitbread.reservation.domain.model.out.aem.CookiePolicies;
import uk.co.whitbread.reservation.domain.model.out.aem.IntroView;
import uk.co.whitbread.reservation.domain.model.out.aem.LocationsResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.ManageView;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelDataListResponse;
import uk.co.whitbread.reservation.infrastructure.config.AemConfigurationProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.reservation.exception.ResponseParsingException;

@ExtendWith(MockitoExtension.class)
class AemOutPortImplTest {

  private static final String AEM_BASEURL = "http://example.com";
  private static final String HEADER_URL = "sampleRestaurant/headersUri?restaurantName=sampleRestaurant";
  private static final String LABEL_URL = "/headersUri?restaurantName=sampleRestaurant";
  private static final String COOKIE_URL = "/http://example.com";

  private AemOutPortImpl aemOutPortImpl;

  @Mock
  private RestClient restClient;
  @Spy
  private ObjectMapper objectMapper;
  @Mock
  private AemConfigurationProperties aemConfigurationProperties;

  private RestClient.ResponseSpec responseSpec;

  @BeforeEach
  void setUp() {
    aemConfigurationProperties = new AemConfigurationProperties();
    aemConfigurationProperties.setBaseUri(AEM_BASEURL);
    aemConfigurationProperties.setHeaderUri(HEADER_URL);
    aemConfigurationProperties.setLabelUri(LABEL_URL);
    aemConfigurationProperties.setCookieConsentUri(COOKIE_URL);

    aemOutPortImpl = new AemOutPortImpl(objectMapper, restClient, aemConfigurationProperties);

    // Set up the common RestClient fluent chain
    RestClient.RequestHeadersUriSpec<?> uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
    responseSpec = mock(RestClient.ResponseSpec.class);

    doReturn(uriSpec).when(restClient).get();
    doReturn(headersSpec).when(uriSpec).uri(anyString());
    when(headersSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void testGetHeaders() {
    String restaurant = "sampleRestaurant";
    String jsonResponse = "{\"logoSrc\":\"dummy-logo-src\",\"logoAlt\":\"dummy-logo-alt\"," +
        "\"homeAltSrc\":\"dummy-home-alt-src\",\"moreLocationName\":\"dummy-more-location-name\"," +
        "\"locationSrc\":[\"dummy-location-src-1\",\"dummy-location-src-2\"]," +
        "\"navbar\":[{\"name\":\"xyz\",\"linkItems\":[{\"name\":\"Name\",\"linkSrc\":\"/sample-link\","
        +
        "\"openInNewTab\":true}]}]}";

    when(responseSpec.body(String.class)).thenReturn(jsonResponse);
    AemHeaderResponse aemHeaderResponse = aemOutPortImpl.getHeaders(restaurant);
    assertNotNull(aemHeaderResponse);
    assertEquals("dummy-logo-src", aemHeaderResponse.getLogoSrc());
    assertEquals("dummy-logo-alt", aemHeaderResponse.getLogoAlt());
    assertEquals("dummy-home-alt-src", aemHeaderResponse.getHomeAltSrc());
  }

  @Test
  void testGetHeadersWhenException_throwException() throws Exception {
    String restaurant = "sampleRestaurant";
    String jsonResponse = "{\"logoSrc\":\"dummy-logo-src\",\"logoAlt\":\"dummy-logo-alt\"," +
        "\"homeAltSrc\":\"dummy-home-alt-src\",\"moreLocationName\":\"dummy-more-location-name\"," +
        "\"locationSrc\":[\"dummy-location-src-1\",\"dummy-location-src-2\"]," +
        "\"navbar\":[{\"name\":\"xyz\",\"linkItems\":[{\"name\":\"Name\",\"linkSrc\":\"/sample-link\","
        +
        "\"openInNewTab\":true}]}]}";

    when(responseSpec.body(String.class)).thenReturn(jsonResponse);
    when(objectMapper.readValue(jsonResponse, AemHeaderResponse.class)).thenThrow(
        ResponseParsingException.class);
    assertThrows(ResponseParsingException.class, () -> aemOutPortImpl.getHeaders(restaurant));
  }

  @Test
  void testGetFooter() {
    String restaurant = "sampleRestaurant";
    String jsonResponse = """
        {
          "copyrightInfo": "Copyright ©2021",
          "legalCopyRightLabel": "Registered office: Whitbread Group PLC.\\r\\nWhitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE.\\r\\nRegistered in England number 29423.\\r\\nVAT registration number 243 2928 64.",
          "socialMediaIcons": [
            {
              "linkSrc": "https://twitter.com/brewersfayre",
              "label": "twitter",
              "visible": true
            },
            {
              "linkSrc": "https://facebook.com/BrewersFayre",
              "label": "facebook",
              "visible": true
            },
            {
              "linkSrc": "https://www.instagram.com/brewersfayre",
              "label": "instagram",
              "visible": true
            }
          ],
          "tabs": [
            {
              "name": "footer",
              "columns": [
                {
                  "name": "footer",
                  "linkItems": [
                    {
                      "name": "About us ",
                      "linkSrc": "/en-gb/about-us",
                      "openInNewTab": true
                    },
                    {
                      "name": "Allergy & Dietary Info",
                      "linkSrc": "/en-gb/allergy-nutrition?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Terms & conditions",
                      "linkSrc": "/en-gb/terms-and-conditions?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Privacy policy",
                      "linkSrc": "/en-gb/privacy-policy?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Cookie notice",
                      "linkSrc": "/en-gb/cookie-policy?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "All locations",
                      "linkSrc": "/en-gb/locations?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Contact us",
                      "linkSrc": "/en-gb/contact-us?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Website Accessibility",
                      "linkSrc": "/en-gb/website-accessibility?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Restaurant Accessibility",
                      "linkSrc": "/en-gb/restaurant-accessibility?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Force for Good",
                      "linkSrc": "/en-gb/force-for-good?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Frequently Asked Questions",
                      "linkSrc": "/en-gb/frequently-asked-questions",
                      "openInNewTab": true
                    },
                    {
                      "name": "School Holidays",
                      "linkSrc": "/en-gb/school-holidays?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Play",
                      "linkSrc": "/en-gb/play?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Drinks",
                      "linkSrc": "/en-gb/drinks?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Sport",
                      "linkSrc": "/en-gb/sport?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Offers",
                      "linkSrc": "/en-gb/offers?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Breakfast",
                      "linkSrc": "/en-gb/breakfast?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Meet the maker",
                      "linkSrc": "/en-gb/meet-our-suppliers?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Function Rooms",
                      "linkSrc": "/en-gb/function-rooms?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Sunday Carvery",
                      "linkSrc": "/en-gb/sunday-roast",
                      "openInNewTab": true
                    },
                    {
                      "name": "Diversity & Inclusion",
                      "linkSrc": "/en-gb/diversity-and-inclusion?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "COVID-19 Guidance",
                      "linkSrc": "/en-gb/guidance-for-brewers-fayre-guests?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Careers",
                      "linkSrc": "https://www.whitbreadcareers.com?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Winter",
                      "linkSrc": "/en-gb/autumn?intcmp=footer",
                      "openInNewTab": true
                    },
                    {
                      "name": "Spring Menu",
                      "linkSrc": "/en-gb/spring?intcmp=footer",
                      "openInNewTab": true
                    }
                  ]
                }
              ]
            }
          ]
        }""";
    when(responseSpec.body(String.class)).thenReturn(jsonResponse);
    AemFooterResponse aemFooterResponse = aemOutPortImpl.getFooters(restaurant);
    assertNotNull(aemFooterResponse);
    assertEquals("Copyright ©2021", aemFooterResponse.getCopyrightInfo());
    assertEquals("twitter", aemFooterResponse.getSocialMediaIcons().getFirst().getLabel());
  }

  @Test
  void testGetFooter_whenException_throwException() {
    String restaurant = "sampleRestaurant";
    String jsonResponse =
        "{\"homeAltSrc\":\"dummy-home-alt-src\",\"moreLocationName\":\"dummy-more-location-name\","
            +
            "\"locationSrc\":[\"dummy-location-src-1\",\"dummy-location-src-2\"]," +
            "\"navbar\":[{\"name\":\"xyz\",\"linkItems\":[{\"name\":\"Name\",\"linkSrc\":\"/sample-link\","
            +
            "\"openInNewTab\":true}]}]}";

    when(responseSpec.body(String.class)).thenReturn(jsonResponse);
    assertThrows(ResponseParsingException.class, () -> aemOutPortImpl.getFooters(restaurant));
  }

  @Test
  void testBookPage() throws Exception {
    String restaurant = "sampleRestaurant";
    String location = "loc";
    String subLocation = "subloc";
    String json = """
        {
            "heroImageSrc": "/dummyHeroImageSrc",
            "subtitleName": "dummySubtitleName",
            "name": "dummyName",
            "heroBackgroundImageSrc": "/dummyHeroBackgroundImageSrc"
        }""";

    BookPage mockBookPageResponse = mockBookPageResponse();
    when(objectMapper.readValue(json, BookPage.class)).thenReturn(mockBookPageResponse);
    when(responseSpec.body(String.class)).thenReturn(json);
    BookPage bookPage = aemOutPortImpl.getBookPageContent(restaurant, location, subLocation);
    assertNotNull(bookPage);
  }

  @Test
  void testBookPageWithNullSubLoc() throws Exception {
    String restaurant = "sampleRestaurant";
    String json = """
        {
            "heroImageSrc": "/dummyHeroImageSrc",
            "subtitleName": "dummySubtitleName",
            "name": "dummyName",
            "heroBackgroundImageSrc": "/dummyHeroBackgroundImageSrc"
        }""";

    BookPage mockBookPageResponse = mockBookPageResponse();
    when(objectMapper.readValue(json, BookPage.class)).thenReturn(mockBookPageResponse);
    when(responseSpec.body(String.class)).thenReturn(json);
    BookPage bookPage = aemOutPortImpl.getBookPageContent(restaurant, null, null);
    assertNotNull(bookPage);
  }

  @Test
  void testBookPage_whenException_throwException() throws Exception {
    String restaurant = "sampleRestaurant";
    String location = "loc";
    String subLocation = "subloc";
    String json = """
        {
            "heroImageSrc": "/dummyHeroImageSrc",
            "subtitleName": "dummySubtitleName",
            "name": "dummyName",
            "heroBackgroundImageSrc": "/dummyHeroBackgroundImageSrc"
        }""";

    when(responseSpec.body(String.class)).thenReturn(json);
    when(objectMapper.readValue(json, BookPage.class)).thenThrow(ResponseParsingException.class);
    assertThrows(ResponseParsingException.class,
        () -> aemOutPortImpl.getBookPageContent(restaurant, location, subLocation));
  }

  @Test
  void testGetCookieContent() {
    AemCookieContentResponse expectedResponse = mockAemCookieContentResponse();
    when(responseSpec.body(AemCookieContentResponse.class)).thenReturn(expectedResponse);
    AemCookieContentResponse actualResponse = aemOutPortImpl.getCookieContent();
    assertNotNull(actualResponse);
  }

  @Test
  void testLocationAem_returnOnlyOneResponse() {
    String restaurant = "barandblock";
    String subLocation = "birmingham";
    String responseJson = """
        [
            {
                "id": "40537270",
                "title": "Birmingham",
                "path": "/en-gb/locations/birmingham",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            }
        ]""";
    when(responseSpec.body(String.class)).thenReturn(responseJson);
    LocationsResponse response = aemOutPortImpl.locations(restaurant, null, subLocation);
    assertNotNull(response);
    assertEquals(1, response.getLocations().size());
    assertEquals("40537270", response.getLocations().getFirst().getId());
  }

  @Test
  void testLocationAem_ifTwoObjectsHaveSameSublocations_returnOnlyOneResponse() {
    String restaurant = "barandblock";
    String subLocation = "the-red-lion";
    String location = "hampshire";
    String responseJson = """
        [
            {
                "id": "40537270",
                "title": "hampshire-the-red-lion",
                "path": "/en-gb/locations/hampshire/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            },
        	{
                "id": "40537271",
                "title": "warwickshire-the-red-lion",
                "path": "/en-gb/locations/warwickshire/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            }
        ]""";
    when(responseSpec.body(String.class)).thenReturn(responseJson);
    LocationsResponse response = aemOutPortImpl.locations(restaurant, location, subLocation);
    assertNotNull(response);
    assertEquals(1, response.getLocations().size());
    assertEquals("40537270", response.getLocations().getFirst().getId());
  }

  @Test
  void testLocationAem_ifThreeObjectsHaveSameSublocations_returnOnlyOneResponse() {
    String restaurant = "barandblock";
    String subLocation = "the-red-lion";
    String location = "warwickshire";
    String responseJson = """
        [
            {
                "id": "40537270",
                "title": "hampshire-the-red-lion",
                "path": "/en-gb/locations/hampshire/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            },
        	{
                "id": "40537271",
                "title": "warwickshire-the-red-lion",
                "path": "/en-gb/locations/warwickshire/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            },
        		{
                "id": "40537271",
                "title": "sample-the-red-lion",
                "path": "/en-gb/locations/sample/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            }
        ]""";
    when(responseSpec.body(String.class)).thenReturn(responseJson);
    LocationsResponse response = aemOutPortImpl.locations(restaurant, location, subLocation);
    assertNotNull(response);
    assertEquals(1, response.getLocations().size());
    assertEquals("40537271", response.getLocations().getFirst().getId());
  }

  @Test
  void testLocationAem_ifLocationOrSublocationNotMatches_returnAllResponses() {
    String restaurant = "barandblock";
    String subLocation = "the-red-lion-nomatch";
    String location = "warwickshire-nomatch";
    String responseJson = """
        [
            {
                "id": "40537270",
                "title": "hampshire-the-red-lion",
                "path": "/en-gb/locations/hampshire/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            },
        	{
                "id": "40537271",
                "title": "warwickshire-the-red-lion",
                "path": "/en-gb/locations/warwickshire/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            },
        		{
                "id": "40537271",
                "title": "sample-the-red-lion",
                "path": "/en-gb/locations/sample/the-red-lion",
                "latitude": "52.480511",
                "longitude": "-1.899135",
                "address1": "3-6 Waterloo St  ",
                "address2": "Birmingham ",
                "address3": "B2 5PG",
                "address4": "",
                "contactInfo": "0121 2275139",
                "googleMapURL": "https://www.google.com/maps/place/Bar+%2B+Block+Steakhouse/@52.4803969,-1.9018069,17z/data=!3m1!4b1!4m5!3m4!1s0x4870bc8c1fc6f51f:0x45af70d9ff4db1ae!8m2!3d52.4803937!4d-1.8996182",
                "externalSystemIdentifier": null,
                "externalSourceSystem": null
            }
        ]""";
    when(responseSpec.body(String.class)).thenReturn(responseJson);
    LocationsResponse response = aemOutPortImpl.locations(restaurant, location, subLocation);
    assertNotNull(response);
    assertEquals(3, response.getLocations().size());
  }

  @Test
  void testZonal_whenException_throwException() {
    String restaurant = "sampleRestaurant";
    String subLocation = "subLocation";
    String responseJson = "[{\"id\":\"1\",\"subLocation\":\"subLocation\"},{\"id\":\"2\",\"subLocation\":\"subLocation\"}]";
    when(responseSpec.body(String.class)).thenReturn(responseJson);
    assertThrows(ResponseParsingException.class,
        () -> aemOutPortImpl.locations(restaurant, null, subLocation));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testGetLabel() {
    // Prepare a mock JSON response as Map<String, String>
    Map<String, String> mockLabelMap = new HashMap<>();
    mockLabelMap.put("key1", "value1");
    mockLabelMap.put("key2", "value2");

    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockLabelMap);

    LabelDataListResponse labelDataResponse = aemOutPortImpl.getLabel();

    assertNotNull(labelDataResponse);
    assertEquals(2, labelDataResponse.getLabels().size());
    assertEquals("key1", labelDataResponse.getLabels().get(0).getKey());
    assertEquals("value2", labelDataResponse.getLabels().get(1).getValue());
  }

  @Test
  void testLocationAem_noMatch_returnsFullList() {
    String restaurant = "barandblock";
    String location = "notfound";
    String subLocation = "alsonotfound";
    String responseJson = "[{\"id\": \"1\", \"path\": \"/en-gb/locations/london/soho\"},{\"id\": \"2\", \"path\": \"/en-gb/locations/manchester/piccadilly\"}]";

    when(responseSpec.body(String.class)).thenReturn(responseJson);
    LocationsResponse response = aemOutPortImpl.locations(restaurant, location, subLocation);

    assertNotNull(response);
    assertEquals(2, response.getLocations().size());
  }

  @Test
  void testLocationAem_nullLocationAndSubLocation_returnsFullList() {
    String restaurant = "barandblock";
    String responseJson =
        "[{\"id\": \"1\", \"path\": \"/en-gb/locations/london/soho\"},{\"id\": \"2\", \"path\": \"/en-gb/locations/manchester/piccadilly\"}]";

    when(responseSpec.body(String.class)).thenReturn(responseJson);
    LocationsResponse response = aemOutPortImpl.locations(restaurant, null, null);
    assertNotNull(response);
    assertEquals(2, response.getLocations().size());
  }

  @Test
  void testLocationAem_unbrandedRestaurant_usesUnbrandedUri() {
    String responseJson = "[]";
    aemConfigurationProperties.setUnbrandedRestaurantsUri("http://example.com/unbranded");

    when(responseSpec.body(String.class)).thenReturn(responseJson);
    LocationsResponse response = aemOutPortImpl.locations(RESTAURANT_PREMIERINN, null, null);
    assertNotNull(response);
    assertEquals(0, response.getLocations().size());
  }

  private AemCookieContentResponse mockAemCookieContentResponse() {
    AemCookieContentResponse aemCookieContentResponse = new AemCookieContentResponse();
    CookiePolicies cookiePolicies = new CookiePolicies();
    cookiePolicies.setBrand("brand");
    cookiePolicies.setVersion("version");
    IntroView introView = new IntroView();
    introView.setDescription("desc");
    introView.setTitle("OG");
    introView.setAcceptAllButtonText("xyx");
    introView.setManageButtonText("text");
    cookiePolicies.setIntroView(introView);
    ManageView manageView = new ManageView();
    manageView.setDescription("manage");
    manageView.setAlwaysActiveText("active");
    manageView.setTitle("OG");
    CookieGroup cookieGroup = new CookieGroup();
    cookieGroup.setCookieName("cook");
    cookieGroup.setDescription("OG-PK");
    cookieGroup.setTitle("OG");
    cookieGroup.setAlwaysActive(true);
    manageView.setCookieGroup(List.of(cookieGroup));
    cookiePolicies.setManageView(manageView);
    aemCookieContentResponse.setCookiePolicies(cookiePolicies);
    return aemCookieContentResponse;
  }


  private BookPage mockBookPageResponse() {
    BookPage bookPage = new BookPage();
    bookPage.setHeroImageSrc("dummyHeroImageSrc");
    bookPage.setSubtitleName("dummySubtitleName");
    bookPage.setName("dummyName");
    bookPage.setHeroBackgroundImageSrc("dummyHeroBackgroundImageSrc");
    return bookPage;
  }
}
