import { expect, type Locator } from '@playwright/test';
import { UiUtils } from '../../../utils/uiUtils';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Room details container from the CCUI confirmation page.
 * Mirrors qa/reference/test/pages/components/ccui/confirmBooking/roomDetailsContainer.js
 * and its common room-details base component.
 */
export class RoomDetailsContainerComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  /**
   * Create a room-details container scoped to the supplied room card.
   * @param container Room-card locator used as the component root.
   */
  constructor(readonly container: Locator = global.page.locator('[data-testid^="RoomCardHeader-room"]').first(), private readonly roomIndex = 0) { super(); }

  /** Return the room label in the room-card header. */
  get roomLabel(): Locator { return this.container.locator('p[data-testid$="-Label"]'); }
  /** Return the room header date interval. */
  get roomDatesHeaderLabel(): Locator { return this.container.locator('p[data-testid$="-Dates"]'); }
  /** Return the room total header label. */
  get roomTotalHeaderLabel(): Locator { return this.container.locator('p[data-testid$="-Total-Label"]'); }
  /** Return the room total header amount. */
  get roomPriceHeaderLabel(): Locator { return this.container.locator('p[data-testid$="-Total-Amount"]'); }
  /** Return the city-tax message for this room. */
  get cityTaxMessageLabel(): Locator { return this.page.locator(`[data-testid="RoomCardInfo-room${this.roomIndex + 1}-CityTaxMessage"]`); }
  /** Return the expand/collapse control for the room card. */
  get expandCollapseRoomInfoButton(): Locator { return this.container.locator('[data-testid="svg-container"]'); }
  /** Return the room information container. */
  get roomCardInfoContainer(): Locator { return this.page.locator(`[data-testid="RoomCardInfo-room${this.roomIndex + 1}"]`); }
  /** Return the lead-guest label. */
  get leadGuestLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-LeadGuest-Label"]'); }
  /** Return the lead-guest name. */
  get leadGuestNameLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-LeadGuest-Name"]'); }
  /** Return the room-name label. */
  get yourRoomLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RoomLabel"]'); }
  /** Return the room details text. */
  get yourRoomDetailsLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RoomDetails"]'); }
  /** Return the rate label. */
  get yourRateLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RoomRate-Label"]'); }
  /** Return the rate details text. */
  get yourRateDetailsLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RoomRate-Details"]'); }
  /** Return the room-group label. */
  get yourRoomGroupLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RoomGroup-Label"]'); }
  /** Return the room-group details text. */
  get yourRoomGroupDetailsLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RoomGroup-Details"]'); }
  /** Return the arriving-date section. */
  get datesArrivingSection(): Locator { return this.roomCardInfoContainer.locator('[data-testid$="-RightColumn-Arrival-Label"]').locator('..'); }
  /** Return the leaving-date section. */
  get datesLeavingSection(): Locator { return this.roomCardInfoContainer.locator('[data-testid$="-RightColumn-Leaving-Label"]').locator('..'); }
  /** Return the room total label. */
  get roomTotalLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RightColumn-RoomTotalPrice-Label"]'); }
  /** Return the room total amount. */
  get roomPriceLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RightColumn-RoomTotalPrice-Amount"]'); }
  /** Return the meals section. */
  get mealsSection(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RightColumn-Meals-Label"]').locator('..'); }
  /** Return meal labels in the room summary. */
  get mealLabelList(): Locator { return this.mealsSection.locator('div div p'); }
  /** Return the meals label. */
  get mealsLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RightColumn-Meals-Label"]'); }
  /** Return the meals price. */
  get mealsPriceLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RightColumn-Meals-Cost"]'); }
  /** Return the extras label. */
  get extrasLabel(): Locator { return this.roomCardInfoContainer.locator('p[data-testid$="-RightColumn-Extras-Label"]'); }
  /**
   * Return the arrival day label at a given index.
   * @param dayIndex Zero-based nightly-price index.
   */
  getArrivalDayLabelByIndex(dayIndex: number): Locator { return this.roomCardInfoContainer.locator(`p[data-testid$="-RightColumn-Arrival-Day-${dayIndex}"]`); }
  /**
   * Return the arrival date label at a given index.
   * @param dayIndex Zero-based nightly-price index.
   */
  getArrivalRoomDateLabelByIndex(dayIndex: number): Locator { return this.roomCardInfoContainer.locator(`[data-testid$="-RightColumn-Room-PricePerNight-${dayIndex}"] p`).first(); }
  /**
   * Return the arrival nightly price at a given index.
   * @param dayIndex Zero-based nightly-price index.
   */
  getArrivalRoomPriceLabelByIndex(dayIndex: number): Locator { return this.roomCardInfoContainer.locator(`[data-testid$="-RightColumn-Room-PricePerNight-${dayIndex}"] p`).nth(1); }

  // ######## UI actions/navigation ########

  /** Return whether the room information panel is currently displayed. */
  async isExpanded(): Promise<boolean> {
    return this.roomCardInfoContainer.isVisible();
  }

  /** Expand or collapse the current room summary. */
  async clickOnExpandCollapseSummaryButton(): Promise<void> {
    console.log('Click on Expand/Collapse summary button');
    await this.expandCollapseRoomInfoButton.scrollIntoViewIfNeeded();
    await this.expandCollapseRoomInfoButton.click();
  }

  // ######## UI validations ########

  /** Validate that the room summary arrow is displayed. */
  async validateSummaryArrow(): Promise<void> {
    console.log('Validate Room summary arrow is displayed');
    await expect(this.expandCollapseRoomInfoButton, 'Room summary arrow').toBeVisible();
  }

  /** Validate the room summary arrow is positioned to the right of the room price. */
  async validateExpandCollapseSummaryButtonPosition(): Promise<void> {
    console.log('Validate the room summary arrow position');
    await UiUtils.validateIsLeftOf({
      leftElement: this.roomPriceHeaderLabel,
      rightElement: this.expandCollapseRoomInfoButton,
      elementDescription: 'Room total price and summary arrow',
    });
  }

  /**
   * Validate the room summary elements are displayed.
   * @param hasMeals Whether the room is expected to contain a meals section.
   */
  async validateRoomElementsAreDisplayed(hasMeals = false): Promise<void> {
    console.log('Validate room details elements are displayed');
    const elements: Array<[Locator, string]> = [
      [this.roomLabel, 'Room label'],
      [this.roomDatesHeaderLabel, 'Room dates label'],
      [this.roomTotalHeaderLabel, 'Room total label'],
      [this.roomPriceHeaderLabel, 'Room price label'],
      [this.expandCollapseRoomInfoButton, 'Room expand/collapse arrow'],
      [this.leadGuestLabel, 'Lead guest label'],
      [this.leadGuestNameLabel, 'Lead guest name'],
      [this.yourRoomLabel, 'Your room label'],
      [this.yourRoomDetailsLabel, 'Your room details'],
      [this.yourRateLabel, 'Rate label'],
      [this.yourRateDetailsLabel, 'Rate details'],
      [this.yourRoomGroupLabel, 'Room group label'],
      [this.yourRoomGroupDetailsLabel, 'Room group details'],
      [this.datesArrivingSection, 'Arriving section'],
      [this.datesLeavingSection, 'Leaving section'],
      [this.roomTotalLabel, 'Room total section label'],
      [this.roomPriceLabel, 'Room total price'],
    ];
    for (const [locator, description] of elements) await expect(locator, description).toBeVisible();
    if (hasMeals) await expect(this.mealsSection, 'Meals section').toBeVisible();
  }
  /** Validate the room city-tax message display state. */
  async validateCityTaxMessage(isDisplayed = true): Promise<void> { console.log(`Validate city tax message for room ${this.roomIndex + 1}`); if (isDisplayed) { await expect(this.cityTaxMessageLabel, 'Room city tax message').toBeVisible(); await expect(this.cityTaxMessageLabel, 'Room city tax message text').toContainText(await Strings.CITY_TAX_MESSAGE.name); } else await expect(this.cityTaxMessageLabel, 'Room city tax message').toBeHidden(); }
}