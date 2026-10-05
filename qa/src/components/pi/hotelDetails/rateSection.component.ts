import { type Locator } from '@playwright/test';
import { RatePresentationSectionComponent } from './ratePresentationSection.component';
import { RateOptionsSectionComponent } from './rateOptionsSection.component';

/**
 * One rate card within the 'Choose your rate' component containing the UI elements, custom
 * actions and validations. Mirrors qa/reference `components/opera/hotelDetails/rateSection.js`
 * (PI/CCUI variant only - the BB `ratePresentationSection` variant is out of scope for this
 * PI-only target).
 */
export class RateSectionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // UI components

  get ratePresentationSection(): RatePresentationSectionComponent {
    return new RatePresentationSectionComponent(this.container);
  }

  get rateOptionsSection(): RateOptionsSectionComponent {
    return new RateOptionsSectionComponent(this.container);
  }

  // ######## UI validations ########

  /** Validate both the rate presentation (room type) and rate options sub-sections. */
  async validateData(): Promise<void> {
    console.log('Validate rate section');
    await this.ratePresentationSection.validateData();
    for (const rateOption of await this.rateOptionsSection.rateOptionsList()) {
      await rateOption.validateData();
    }
  }
}
