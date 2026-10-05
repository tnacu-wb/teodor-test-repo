import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { TotalCostSectionComponent } from './totalCostSection.component';

/** Room rate policies modal from the CCUI payment page. */
export class RoomRatePoliciesModalComponent extends CcuiComponent {
	// ######## properties ########

	// ######## UI elements/properties ########

	readonly roomRatePolicyModal: Locator = this.page.locator('section[data-testid="roomRatePolicies-ModalContent"]');
	readonly roomRatePolicyModalTitleLabel: Locator = this.page.locator('div[data-testid="roomRatePolicies-ModalTitle"]');
	readonly roomRatePolicyModalBody: Locator = this.page.locator('div[data-testid="roomRatePolicies-ModalBody"]');
	readonly roomRatePolicyModalCloseButton: Locator = this.page.locator('button[data-testid="roomRatePolicies-ModalCloseButton"]');
	readonly flexTitleLabel: Locator = this.page.locator('b[data-testid="roomRatePolicies_flex-title"]');
	readonly flexDescriptionLabel: Locator = this.page.locator('p[data-testid="roomRatePolicies_flex-description"]');
	readonly semiflexTitleLabel: Locator = this.page.locator('b[data-testid="roomRatePolicies_semiflex-title"]');
	readonly semiflexDescriptionLabel: Locator = this.page.locator('p[data-testid="roomRatePolicies_semiflex-description"]');
	readonly advanceTitleLabel: Locator = this.page.locator('b[data-testid="roomRatePolicies_advance-title"]');
	readonly advanceDescriptionLabel: Locator = this.page.locator('p[data-testid="roomRatePolicies_advance-description"]');
	readonly standardTitleLabel: Locator = this.page.locator('b[data-testid="roomRatePolicies_standard-title"]');
	readonly standardDescriptionLabel: Locator = this.page.locator('p[data-testid="roomRatePolicies_standard-description"]');
	readonly totalCost = new TotalCostSectionComponent();

	/** Return a supplied rate title locator for compatibility with the reference API. */
	getElementTitlesFromList(rateNameElement: Locator): Locator;
	getElementTitlesFromList(rateNameElement: string, rateClassifications: Array<{ rateName?: string }>): Promise<string | undefined>;
	getElementTitlesFromList(rateNameElement: Locator | string, rateClassifications?: Array<{ rateName?: string }>): Locator | Promise<string | undefined> { if (typeof rateNameElement !== 'string') return rateNameElement; return Promise.resolve(rateClassifications?.find(({ rateName }) => rateName === rateNameElement)?.rateName); }
	/** Return a supplied rate description locator for compatibility with the reference API. */
	getElementDescriptionFromList(rateDescriptionElement: Locator): Locator;
	getElementDescriptionFromList(rateDescriptionElement: string, rateClassifications: Array<{ rateDescription?: string }>): Promise<string | undefined>;
	getElementDescriptionFromList(rateDescriptionElement: Locator | string, rateClassifications?: Array<{ rateDescription?: string }>): Locator | Promise<string | undefined> { if (typeof rateDescriptionElement !== 'string') return rateDescriptionElement; return Promise.resolve(rateClassifications?.find(({ rateDescription }) => rateDescription === rateDescriptionElement)?.rateDescription); }

	// ######## UI actions/navigation ########

	// ######## UI validations ########

	/** Validate that the room-rate policy modal title is visible. */
	async validateRoomRatePolicyModalTitle(): Promise<void> { console.log('Validate room rate policy modal title'); await expect(this.roomRatePolicyModalTitleLabel, 'Room rate policy modal title').toContainText(await Strings.ROOM_RATE_POLICY_TITLE.name); }
	/** Validate that the room-rate policy modal close button is visible. */
	async validateRoomRatePolicyModalCloseButton(): Promise<void> { console.log('Validate room rate policy modal close button'); await expect(this.totalCost.closeModalButton, 'Room rate policy close button type').toHaveAttribute('type', 'button'); await expect(this.totalCost.closeModalButton, 'Room rate policy close button').toBeEnabled(); }
	/** Validate that the room-rate policy modal body is visible. */
	async validateRoomRatePolicyModalBody(): Promise<void> { console.log('Validate room rate policy modal body'); await expect(this.roomRatePolicyModalBody, 'Room rate policy modal body').toBeVisible(); }
	/** Validate a rate title and description against API rate classifications. */
	async validateRateClassification(title: Locator, description: Locator, classifications: Array<{ rateName?: string; rateDescription?: string }>): Promise<void> { const actualTitle = await title.innerText(); const actualDescription = await description.innerText(); expect(classifications.some((item) => item.rateName === actualTitle), 'Rate policy title').toBe(true); expect(classifications.some((item) => item.rateDescription === actualDescription), 'Rate policy description').toBe(true); }
	/** Validate the Flex rate title and description against rate classifications. */
	async validateFlexRoomRateNameAndDescription(rateClassifications: Array<{ rateName?: string; rateDescription?: string }>): Promise<void> { console.log('Validate Flex room rate name and description'); await this.validateRateClassification(this.flexTitleLabel, this.flexDescriptionLabel, rateClassifications); }
	/** Validate the Semi-Flex rate title and description against rate classifications. */
	async validateSemiflexRoomRateNameAndDescription(rateClassifications: Array<{ rateName?: string; rateDescription?: string }>): Promise<void> { console.log('Validate Semi-Flex room rate name and description'); await this.validateRateClassification(this.semiflexTitleLabel, this.semiflexDescriptionLabel, rateClassifications); }
	/** Validate the Advance rate title and description against rate classifications. */
	async validateAdvanceRoomRateNameAndDescription(rateClassifications: Array<{ rateName?: string; rateDescription?: string }>): Promise<void> { console.log('Validate Advance room rate name and description'); await this.validateRateClassification(this.advanceTitleLabel, this.advanceDescriptionLabel, rateClassifications); }
	/** Validate the Standard rate title and description against rate classifications. */
	async validateStandardRoomRateNameAndDescription(rateClassifications: Array<{ rateName?: string; rateDescription?: string }>): Promise<void> { console.log('Validate Standard room rate name and description'); await this.validateRateClassification(this.standardTitleLabel, this.standardDescriptionLabel, rateClassifications); }
	/** Validate all displayed room-rate policy labels. */
	async validateRoomRateElementLabels(classifications: Array<{ rateName?: string; rateDescription?: string }>): Promise<void> { console.log('Validate room rate policies title and description labels'); await this.validateFlexRoomRateNameAndDescription(classifications); await this.validateSemiflexRoomRateNameAndDescription(classifications); await this.validateAdvanceRoomRateNameAndDescription(classifications); await this.validateStandardRoomRateNameAndDescription(classifications); }
}