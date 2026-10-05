# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: qa/tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
Error: page.goto: net::ERR_INVALID_AUTH_CREDENTIALS at https://www.uat.premierinn.digital/gb/en/hotels/england/west-midlands/birmingham.html
Call log:
  - navigating to "https://www.uat.premierinn.digital/gb/en/hotels/england/west-midlands/birmingham.html", waiting until "domcontentloaded"

```

# Test source

```ts
  4   | 
  5   | /**
  6   |  * Destination Landing Page (DLP) - displays hotels in a region with filters, maps,
  7   |  * TripAdvisor integration, and grid/map toggle.
  8   |  *
  9   |  * Handles DLP navigation, hotel listing validation, map/grid views,
  10  |  * TripAdvisor reviews, show more, and hotel distance display.
  11  |  */
  12  | export class DestinationLandingPage extends BasePage {
  13  |   // Page wrapper
  14  |   readonly dlpPageWrapper: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-Wrapper"]');
  15  | 
  16  |   // Hero section
  17  |   readonly heroTitle: Locator = this.page.locator('[data-testid="DLPHeroSection-Title"]');
  18  |   readonly heroDescription: Locator = this.page.locator('[data-testid="DLPHeroSection-Description"]');
  19  |   readonly heroPicture: Locator = this.page.locator('img[data-testid="DLPHeroSection-Picture"]');
  20  | 
  21  |   // Hotel counter
  22  |   readonly hotelCounterLabel: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-Wrapper"] h6');
  23  | 
  24  |   // Hotel cards (grid view)
  25  |   readonly hotelCards: Locator = this.page.locator('[data-testid="DLP-hotel-card"]');
  26  |   readonly gridViewElement: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-HotelsList"]');
  27  | 
  28  |   // Map view
  29  |   readonly mapViewElement: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-mapView"]');
  30  |   readonly mapMarkers: Locator = this.page.locator('gmp-advanced-marker');
  31  |   readonly mapPegmanButton: Locator = this.page.locator('button.gm-svpc');
  32  |   readonly mapZoomInButton: Locator = this.page.locator('button[title="Zoom in"]');
  33  |   readonly mapZoomOutButton: Locator = this.page.locator('button[title="Zoom out"]');
  34  | 
  35  |   // Map/Grid toggle
  36  |   readonly mapGridSwitchToggle: Locator = this.page.locator('[data-testid="DestinationLandingPIPageMapGrid-SwitchToggle"]');
  37  |   readonly mapViewSwitchMapButton: Locator = this.page.locator('[data-testid="DestinationLandingPIPageMapGrid-first"]');
  38  |   readonly mapViewSwitchGridButton: Locator = this.page.locator('[data-testid="DestinationLandingPIPageMapGrid-second"]');
  39  | 
  40  |   // Hotel card elements (map popover and grid)
  41  |   readonly hotelCardTitle: Locator = this.page.locator('[data-testid="DLP-hotel-card"] p').first();
  42  |   readonly hotelCardThumbnail: Locator = this.page.locator('[data-testid="DLP-hotel-thumbnail"] img');
  43  |   readonly hotelCardDistance: Locator = this.page.locator('[data-testid="DLP-hotel-distance"]');
  44  |   readonly hotelCardFacilitiesImage: Locator = this.page.locator('[data-testid="DLP-hotel-facility"] img');
  45  |   readonly viewHotelButton: Locator = this.page.locator('[data-testid="DLP-hotel-button"] button');
  46  |   readonly cardCloseButton: Locator = this.page.locator('button.gm-ui-hover-effect');
  47  | 
  48  |   // TripAdvisor
  49  |   readonly tripAdvisorRatingsImages: Locator = this.page.locator('img[alt="ta-ratings-img"]');
  50  |   readonly tripAdvisorReviewsLinks: Locator = this.page.locator('img[alt="ta-ratings-img"] + * a, img[alt="ta-ratings-img"] ~ a');
  51  |   readonly tripAdvisorBottomReviewSection: Locator = this.page.locator('[data-testid="tripadvisor-bottom-review-section-hdp-Section"]');
  52  |   readonly tripAdvisorImage: Locator = this.page.locator('[data-testid="DLP-hotel-card"] img[alt="ta-ratings-img"]');
  53  | 
  54  |   // Show more
  55  |   readonly showMoreButton: Locator = this.page.locator('[data-testid="DestinationLandingPIPage-showMoreBtn"]');
  56  | 
  57  |   // Notification popup
  58  |   readonly notificationModal: Locator = this.page.locator('[data-testid="pi-notification-permission-popup-modal-content"]');
  59  |   readonly notificationDenyButton: Locator = this.page.locator('[data-testid="pi-notification-permission-popup-deny-btn"]');
  60  |   readonly notificationAllowButton: Locator = this.page.locator('[data-testid="pi-notification-permission-popup-allow-btn"]');
  61  | 
  62  |   // Filters
  63  |   readonly filtersButton: Locator = this.page.locator('[data-testid="DLP-Filters-Open-Button"]');
  64  |   readonly filtersPanel: Locator = this.page.locator('[data-testid="DLP-Filters-Wrapper"]');
  65  | 
  66  |   // Content sections
  67  |   readonly otherDestinationsElements: Locator = this.page.locator('[data-testid="DestinationsList-List"] p');
  68  |   readonly thingsToDoCardsTitleLabels: Locator = this.page.locator('[data-testid="ThingsToDoList-List"] p');
  69  |   readonly faqTabsElements: Locator = this.page.locator('[data-testid="DestinationFaq-TabsComponent"] h3');
  70  |   readonly faqQuestionsElements: Locator = this.page.locator('[data-testid="DestinationFaq-Accordions"] button div');
  71  |   readonly whySectionElements: Locator = this.page.locator('[data-testid="WhyUs-Item"] h4');
  72  | 
  73  |   constructor() {
  74  |     super();
  75  |   }
  76  | 
  77  |   private coordinatesMatch(actual: string | null, expected: string): boolean {
  78  |     if (actual === expected) return true;
  79  | 
  80  |     const parseCoordinates = (position: string | null): [number, number] | null => {
  81  |       const parts = position?.split(',').map(part => Number(part.trim())) ?? [];
  82  |       return parts.length === 2 && parts.every(Number.isFinite) ? [parts[0], parts[1]] : null;
  83  |     };
  84  | 
  85  |     const actualCoordinates = parseCoordinates(actual);
  86  |     const expectedCoordinates = parseCoordinates(expected);
  87  |     if (!actualCoordinates || !expectedCoordinates) return false;
  88  | 
  89  |     return Math.abs(actualCoordinates[0] - expectedCoordinates[0]) < 0.000001
  90  |       && Math.abs(actualCoordinates[1] - expectedCoordinates[1]) < 0.000001;
  91  |   }
  92  | 
  93  |   /**
  94  |    * Navigate to a Destination Landing Page by its relative path.
  95  |    * Sets consent cookies before navigation (same pattern as HomePage).
  96  |    * @param dlpPath - Relative DLP URL, e.g. 'hotels/england/west-midlands/birmingham.html'
  97  |    */
  98  |   async open(dlpPath: string): Promise<void> {
  99  |     console.log(`Navigating to DLP: ${dlpPath}`);
  100 |     await this.cookieConsent.preSetConsentCookies();
  101 | 
  102 |     const locale = getCurrentLocale();
  103 |     const cleanPath = dlpPath.startsWith('/') ? dlpPath.slice(1) : dlpPath;
> 104 |     await this.page.goto(`/${locale.country}/${locale.language}/${cleanPath}`, { waitUntil: 'domcontentloaded', timeout: 30000 });
      |                     ^ Error: page.goto: net::ERR_INVALID_AUTH_CREDENTIALS at https://www.uat.premierinn.digital/gb/en/hotels/england/west-midlands/birmingham.html
  105 |     await this.cookieConsent.dismissIfPresent();
  106 |     await this.notificationPopup.dismissIfPresent();
  107 |   }
  108 | 
  109 |   /**
  110 |    * Validate that the DLP has loaded by checking for the page wrapper element.
  111 |    */
  112 |   async validatePage(): Promise<void> {
  113 |     console.log('Validating DLP page loaded');
  114 |     try {
  115 |       await this.dlpPageWrapper.waitFor({ state: 'visible', timeout: 30000 });
  116 |     } catch {
  117 |       throw new Error(
  118 |         'DestinationLandingPage did not load within 30s. Selector not found: [data-testid="DestinationLandingPIPage-Wrapper"]'
  119 |       );
  120 |     }
  121 |   }
  122 | 
  123 |   /**
  124 |    * Validate core DLP page elements: hotel list, map/grid toggle, and content sections.
  125 |    * Validates the Why section items, FAQ elements, and Other Destinations against expected data.
  126 |    * @param dlpContent - The AEM DLP content response for cross-validation
  127 |    */
  128 |   async validateDlpPageElements(dlpContent: {
  129 |     why?: { whyItems: Array<{ itemTitle: string }> } | null;
  130 |     faqs?: Array<{ title: string; faqItems: Array<{ question: string }> }>;
  131 |     dlps?: { dlpItems: Array<{ title: string }> } | null;
  132 |   }): Promise<void> {
  133 |     console.log('Validating DLP page elements');
  134 |     // Validate hotel cards are visible
  135 |     await expect(this.hotelCards.first(), 'Hotel cards should be displayed on DLP').toBeVisible();
  136 | 
  137 |     // Validate Why section items
  138 |     if (dlpContent.why?.whyItems) {
  139 |       const whyItems = this.whySectionElements;
  140 |       await expect(whyItems, `Why section should have ${dlpContent.why.whyItems.length} items`).toHaveCount(dlpContent.why.whyItems.length);
  141 |       for (const [i, item] of dlpContent.why.whyItems.entries()) {
  142 |         await expect(whyItems.nth(i), `Why item ${i} should display: ${item.itemTitle}`).toHaveText(item.itemTitle.trim());
  143 |       }
  144 |     }
  145 | 
  146 |     // Validate FAQ tabs — the AEM data may represent sections that expand into multiple tabs
  147 |     // on the page. Validate that FAQ tabs are present and that the AEM section titles appear
  148 |     // somewhere in the rendered tabs (containment check, not exact count/position match).
  149 |     if (dlpContent.faqs && dlpContent.faqs.length > 0) {
  150 |       const faqTabs = this.faqTabsElements;
  151 |       const tabCount = await faqTabs.count();
  152 |       // Page must have at least as many tabs as AEM FAQ entries
  153 |       expect(tabCount, 'FAQ tabs must be present on the page').toBeGreaterThanOrEqual(dlpContent.faqs.length);
  154 | 
  155 |       // Validate each AEM FAQ title appears in one of the rendered tabs
  156 |       for (const faq of dlpContent.faqs) {
  157 |         const matchingTab = faqTabs.filter({ hasText: faq.title.trim() });
  158 |         await expect(matchingTab.first(), `FAQ tab "${faq.title}" should be visible on page`).toBeVisible({ timeout: 5000 });
  159 |       }
  160 |     }
  161 | 
  162 |     // Validate Other Destinations — the AEM dlpItems may not match the page's "Explore" section
  163 |     // exactly (different data sources). Validate the section is present and has items, but skip
  164 |     // strict positional text matching since the page renders destination-specific links while
  165 |     // the AEM dictionary may return generic items like "New hotels".
  166 |     if (dlpContent.dlps?.dlpItems) {
  167 |       const destinations = this.otherDestinationsElements;
  168 |       const destCount = await destinations.count();
  169 |       expect(destCount, 'Other Destinations section must have items').toBeGreaterThan(0);
  170 |     }
  171 |   }
  172 | 
  173 |   /**
  174 |    * Validate the Hero section (title, description, picture) against expected DLP content.
  175 |    * @param title - Expected hero title
  176 |    * @param description - Expected hero description (HTML)
  177 |    * @param image - Expected image path (partial match)
  178 |    */
  179 |   async validateHeroSection(title: string, description: string, image: string): Promise<void> {
  180 |     console.log(`Validating hero section with title: ${title}`);
  181 |     await expect(this.heroTitle, 'Hero title should be displayed').toBeVisible();
  182 |     await expect(this.heroTitle, `Hero title should display: ${title}`).toHaveText(title);
  183 | 
  184 |     // Description comparison: normalise non-breaking spaces
  185 |     const descriptionHtml = await this.heroDescription.innerHTML();
  186 |     const normalised = descriptionHtml.trim().replace(/&nbsp;/g, ' ').replace(/ target="_blank"/g, '');
  187 |     const expectedNormalised = description.trim().replace(/\u00A0/g, ' ').replace(/ target="_blank"/g, '');
  188 |     if (!normalised.includes(expectedNormalised) && expectedNormalised !== normalised) {
  189 |       throw new Error(`Hero description mismatch.\nExpected: ${expectedNormalised}\nActual: ${normalised}`);
  190 |     }
  191 | 
  192 |     const pictureSrc = await this.heroPicture.getAttribute('src');
  193 |     if (!pictureSrc || !decodeURIComponent(pictureSrc).includes(image)) {
  194 |       throw new Error(`Hero picture does not contain expected image path: ${image}`);
  195 |     }
  196 |   }
  197 | 
  198 |   /**
  199 |    * Validate TripAdvisor section displays reviews matching the API response.
  200 |    * @param expectedReviews - Array of expected TripAdvisor reviews with rating and numberOfReviews
  201 |    */
  202 |   async validateTripAdvisorSection(expectedReviews: Array<{ rating: number; numberOfReviews: number }>): Promise<void> {
  203 |     console.log(`Validating TripAdvisor section with ${expectedReviews.length} reviews`);
  204 |     await expect(this.tripAdvisorRatingsImages.first(), 'TripAdvisor rating images should be displayed').toBeVisible({ timeout: 10000 });
```