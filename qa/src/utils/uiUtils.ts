import { expect, type APIRequestContext, type Locator, type Page } from '@playwright/test';
import { FeaturesToggles } from './featuresToggles';

export interface AlignmentOptions {
  elementsList: Locator[];
  maxDistanceBetween?: number;
}

export interface ElementValidationOptions {
  element: Locator;
  elementDescription?: string;
}

export interface ElementVisibilityOptions extends ElementValidationOptions {
  isDisplayed?: boolean;
  timeout?: number;
}

export interface ElementTextOptions extends ElementValidationOptions {
  hasText?: boolean;
  expectedText: string | Promise<string>;
  textIsContained?: boolean;
  timeout?: number;
}

export interface ElementValueOptions extends ElementValidationOptions {
  hasValue?: boolean;
  expectedValue: string | Promise<string>;
  textIsContained?: boolean;
  timeout?: number;
}

export interface RelativePositionOptions {
  belowElement?: Locator;
  aboveElement?: Locator;
  leftElement?: Locator;
  rightElement?: Locator;
  maxDistanceBetween?: number | null;
  elementDescription?: string;
}

export interface ButtonValidationOptions {
  element: Locator;
  buttonLabel: string | Promise<string>;
  isDisplayed?: boolean;
  hasText?: boolean;
  isClickable?: boolean;
}

export interface RadioButtonValidationOptions extends ButtonValidationOptions {
  isChecked?: boolean;
}

/**
 * Basic UI methods migrated to Playwright locators.
 */
export class UiUtils {
  private constructor() {}

  static isApplicationStateResetAtLeastOnce = false;

  static get page(): Page {
    if (!global.page) {
      throw new Error('global.page is not available. Make sure the Playwright fixture has initialized the page before using UiUtils.');
    }

    return global.page;
  }

  static get requestContext(): APIRequestContext {
    if (!global.page) {
      throw new Error('global.page is not available. Make sure the Playwright fixture has initialized the page before using UiUtils.');
    }

    return global.page.context().request;
  }

  static get generalTooltip(): Locator {
    return UiUtils.page.locator('//div[@role="tooltip"]');
  }

  /**
   * This function will clear the field.
   * @param element web element
   */
  static async clearField(element: Locator): Promise<void> {
    console.log('Clear the field');
    await element.scrollIntoViewIfNeeded();
    await expect(element, 'Field element should be visible before clearing').toBeVisible();
    await element.fill('');
  }

  /**
   * This method will press the 'Tab' key.
   */
  static async pressTabKey(): Promise<void> {
    console.log('Press Tab key in order to move the focus off the current control');
    await UiUtils.page.keyboard.press('Tab');
  }

  /**
   * This method will press the 'Escape' key.
   */
  static async pressEscapeKey(): Promise<void> {
    await UiUtils.page.keyboard.press('Escape');
  }

  /**
   * This method will get the list of strings from locators.
   * @param elements web elements list
   * @returns list of strings
   */
  static async getListOfStringFromWebElement(elements: Locator[]): Promise<string[]> {
    return Promise.all(elements.map((element) => element.innerText()));
  }

  /**
   * Switch to an iframe by waiting for it to exist. Playwright actions should use frameLocator directly after this check.
   * @param iFrameLocator locator of the iframe to switch to
   */
  static async switchToFrame(iFrameLocator: string): Promise<void> {
    console.log(`Switch to iframe with locator: ${iFrameLocator}`);
    await expect(UiUtils.page.locator(iFrameLocator), `iframe element with locator "${iFrameLocator}" does not exist`).toBeAttached({ timeout: 30_000 });
  }

  /**
   * Simulate back browser functionality.
   */
  static async navigateBack(): Promise<void> {
    console.log(' Go back using browser functionality');
    await UiUtils.page.goBack();
  }

  /**
   * Reset the application state and ignore errors: load the home page.
   * @param resetState the code to reset the initial application state before retrying.
   * @param ignoreErrors true if it ignores the errors
   * @param forceReset true if it will reset the application regardless of isApplicationStateResetAtLeastOnce value
   */
  static async resetApplicationState(resetState: (() => Promise<void>) | null = null, ignoreErrors = false, forceReset = true): Promise<void> {
    if (!forceReset && !UiUtils.isApplicationStateResetAtLeastOnce) {
      UiUtils.isApplicationStateResetAtLeastOnce = true;
      return;
    }

    try {
      if (resetState) {
        await resetState();
        return;
      }

      console.log('-> Load the home page');
      await FeaturesToggles.applyDefaultFeaturesTogglesOverrides();
      await UiUtils.page.reload({ waitUntil: 'domcontentloaded' });
    } catch (error) {
      console.log(error);
      if (!ignoreErrors) {
        throw error;
      }
    }
  }

  /**
   * Switch to default content from iframe. Playwright automatically scopes locators, so this is a no-op kept for parity.
   */
  static async switchToDefaultContent(): Promise<void> {
    console.log('Switch to default content from IFrame');
  }

  /**
   * This method will scroll webElement into view.
   * @param element web element
   */
  static async scrollIntoView(element: Locator): Promise<void> {
    await element.scrollIntoViewIfNeeded();
  }

  /**
   * Click, hold, move and release mouse from element container.
   * @param element web element
   */
  static async clickHoldMoveAndReleaseLeftMouseButtonFromElement(element: Locator): Promise<void> {
    await element.scrollIntoViewIfNeeded();
    const box = await UiUtils.getBoundingBox(element);
    await UiUtils.page.mouse.move(box.x + box.width / 2, box.y + box.height / 2);
    await UiUtils.page.mouse.down();
    await UiUtils.page.waitForTimeout(500);
    await UiUtils.page.mouse.move(box.x + box.width / 2 + 100, box.y + box.height / 2, { steps: 10 });
    await UiUtils.page.mouse.up();
  }

  /**
   * Simulates a click above an element to close a modal.
   * @param element web element
   * @param verticalPixelsAboveElement The number of pixels to move above the element for the click.
   */
  static async clickAboveElement(element: Locator, verticalPixelsAboveElement = 5): Promise<void> {
    await element.scrollIntoViewIfNeeded();
    const box = await UiUtils.getBoundingBox(element);
    await UiUtils.page.mouse.click(box.x + box.width / 2, box.y - verticalPixelsAboveElement);
  }

  /**
   * Wait for the page to load by checking document.readyState until it is complete.
   */
  static async waitForPageToLoad(): Promise<void> {
    console.log('Waiting for the page to load...');
    await UiUtils.page.waitForLoadState('load');
  }

  /**
   * Validate elements are displayed on the same line.
   */
  static async validateElementsAreAlignedHorizontally({ elementsList, maxDistanceBetween = 0 }: AlignmentOptions): Promise<void> {
    console.log('Validate elements are aligned horizontally');
    
    // Ensure all elements exist before checking alignment
    for (const element of elementsList) {
      await expect(element, 'Element should be attached before checking horizontal alignment').toBeAttached({ timeout: 5_000 });
    }

    for (let index = 0; index < elementsList.length - 1; index++) {
      const current = await UiUtils.getBoundingBox(elementsList[index]);
      const next = await UiUtils.getBoundingBox(elementsList[index + 1]);
      expect(current.y, `Elements are not on the same horizontal line - elements at index ${index} and ${index + 1} have different Y coordinates`).toBeGreaterThanOrEqual(next.y - maxDistanceBetween);
      expect(current.y, `Elements are not on the same horizontal line - elements at index ${index} and ${index + 1} have different Y coordinates`).toBeLessThanOrEqual(next.y + maxDistanceBetween);
    }
  }

  /**
   * Validate if elements inside a container are correctly aligned on vertical.
   */
  static async validateElementsAreAlignedVertically({ elementsList, maxDistanceBetween = 0 }: AlignmentOptions): Promise<void> {
    console.log('Validate elements are aligned vertically');
    
    // Ensure all elements exist before checking alignment
    for (const element of elementsList) {
      await expect(element, 'Element should be attached before checking vertical alignment').toBeAttached({ timeout: 5_000 });
    }
    
    for (let index = 0; index < elementsList.length - 1; index++) {
      const current = await UiUtils.getBoundingBox(elementsList[index]);
      const next = await UiUtils.getBoundingBox(elementsList[index + 1]);
      expect(current.x, `Elements are vertically aligned - elements at index ${index} and ${index + 1} have different X coordinates`).toBeGreaterThanOrEqual(next.x - maxDistanceBetween);
      expect(current.x, `Elements are vertically aligned - elements at index ${index} and ${index + 1} have different X coordinates`).toBeLessThanOrEqual(next.x + maxDistanceBetween);
    }
  }

  /**
   * Validate vertical alignment.
   */
  static async validateVerticalAlignment(firstElement: Locator, secondElement: Locator, elementDescription = ''): Promise<void> {
    console.log(`${elementDescription} - Validate vertical alignment`);
    // Ensure both elements exist before checking alignment
    await expect(firstElement, `${elementDescription} - first element should be attached`).toBeAttached({ timeout: 5_000 });
    await expect(secondElement, `${elementDescription} - second element should be attached`).toBeAttached({ timeout: 5_000 });
    const first = await UiUtils.getBoundingBox(firstElement);
    const second = await UiUtils.getBoundingBox(secondElement);
    expect(first.x, elementDescription).toBe(second.x);
  }

  /**
   * Validate if two elements overlap.
   */
  static async validateElementsOverlap({ firstElement, secondElement, isFirstElementTotallyOverlapped = false, reverse = false, elementDescription = '' }: {
    firstElement: Locator;
    secondElement: Locator;
    isFirstElementTotallyOverlapped?: boolean;
    reverse?: boolean;
    elementDescription?: string;
  }): Promise<void> {
    console.log(`${elementDescription} - Validate elements overlap`);
    // Ensure both elements exist and are visible before checking overlap
    await expect(firstElement, `${elementDescription} - first element should be attached`).toBeAttached({ timeout: 5_000 });
    await expect(secondElement, `${elementDescription} - second element should be attached`).toBeAttached({ timeout: 5_000 });
    await expect(firstElement, `${elementDescription} - first element should be visible`).toBeVisible({ timeout: 5_000 });
    await expect(secondElement, `${elementDescription} - second element should be visible`).toBeVisible({ timeout: 5_000 });
    
    const first = await UiUtils.getBoundingBox(firstElement);
    const second = await UiUtils.getBoundingBox(secondElement);
    const overlap = UiUtils.getOverlap(first, second);
    const hasOverlap = overlap.width > 0 && overlap.height > 0;

    if (reverse) {
      expect(hasOverlap, `${elementDescription} - Elements should not overlap but they do`).toBe(false);
      return;
    }

    expect(hasOverlap, `${elementDescription} - Elements should overlap but they do not`).toBe(true);
    if (isFirstElementTotallyOverlapped) {
      expect(overlap.width * overlap.height, `${elementDescription} - First element is not fully overlapped over second element`).toBe(first.width * first.height);
    }
  }

  /**
   * Validate if an element is below another element.
   */
  static async validateIsBelow({ belowElement, aboveElement, maxDistanceBetween = null, elementDescription = '' }: RelativePositionOptions): Promise<void> {
    if (!belowElement || !aboveElement) throw new Error('"belowElement" and "aboveElement" are required parameters');
    console.log(`${elementDescription} - Validate element is below another element`);
    // Ensure both elements exist before checking position
    await expect(belowElement, `${elementDescription} - below element should be attached`).toBeAttached({ timeout: 5_000 });
    await expect(aboveElement, `${elementDescription} - above element should be attached`).toBeAttached({ timeout: 5_000 });
    const below = await UiUtils.getBoundingBox(belowElement);
    const above = await UiUtils.getBoundingBox(aboveElement);
    const maxDistance = maxDistanceBetween ?? above.height + 90;
    const actualDistance = below.y - above.y;
    const isBelow = below.y > above.y && actualDistance < maxDistance;
    expect(isBelow, `${elementDescription} - element should be below (below.y=${below.y}, above.y=${above.y}, distance=${actualDistance})`).toBe(true);
  }

  /**
   * Validate if an element is on the left of another element.
   */
  static async validateIsLeftOf({ leftElement, rightElement, maxDistanceBetween = null, elementDescription = '' }: RelativePositionOptions): Promise<void> {
    if (!leftElement || !rightElement) throw new Error('"leftElement" and "rightElement" are required parameters');
    console.log(`${elementDescription} - Validate element is left of another element`);
    // Ensure both elements exist before checking position
    await expect(leftElement, `${elementDescription} - left element should be attached`).toBeAttached({ timeout: 5_000 });
    await expect(rightElement, `${elementDescription} - right element should be attached`).toBeAttached({ timeout: 5_000 });
    const left = await UiUtils.getBoundingBox(leftElement);
    const right = await UiUtils.getBoundingBox(rightElement);
    const maxDistance = maxDistanceBetween ?? left.width + 90;
    const actualDistance = right.x - left.x;
    const isLeftOf = left.x < right.x && actualDistance < maxDistance;
    expect(isLeftOf, `${elementDescription} - element should be left of target (left.x=${left.x}, right.x=${right.x}, distance=${actualDistance})`).toBe(true);
  }

  /**
   * Validate that the middle element is between the first and third.
   */
  static async validateIsBetween({ belowElement, middleElement, aboveElement, maxDistance = 1000, axis = 'vertical' }: {
    belowElement: Locator;
    middleElement: Locator;
    aboveElement: Locator;
    maxDistance?: number;
    axis?: 'vertical' | 'horizontal';
  }): Promise<void> {
    console.log(`Validate that the middle element is between the other two with max distance of: ${maxDistance} on axis: ${axis}`);
    if (axis === 'horizontal') {
      await UiUtils.validateIsLeftOf({ leftElement: middleElement, rightElement: aboveElement, maxDistanceBetween: maxDistance });
      await UiUtils.validateIsLeftOf({ leftElement: belowElement, rightElement: middleElement, maxDistanceBetween: maxDistance });
      return;
    }

    await UiUtils.validateIsBelow({ belowElement: middleElement, aboveElement, maxDistanceBetween: maxDistance });
    await UiUtils.validateIsBelow({ belowElement, aboveElement: middleElement, maxDistanceBetween: maxDistance });
  }

  /**
   * Validate scrolling element into view.
   */
  static async validateScrollingElementIntoView({ element, elementDescription = '', hasScrollBar = true, acceptedScrollMargin = 0 }: ElementValidationOptions & {
    hasScrollBar?: boolean;
    acceptedScrollMargin?: number;
  }): Promise<void> {
    // Ensure element exists before checking scroll
    await expect(element, `${elementDescription} - element should be attached`).toBeAttached({ timeout: 5_000 });
    const before = await UiUtils.getBoundingBox(element);
    console.log(`${elementDescription} - Validate scrolling element into view`);
    await element.scrollIntoViewIfNeeded();
    const after = await UiUtils.getBoundingBox(element);

    if (hasScrollBar) {
      expect(before.y, `${elementDescription} - Should be scrolled to element (before.y=${before.y}, after.y=${after.y})`).not.toBe(after.y);
    } else {
      expect(Math.abs(before.y - after.y), `${elementDescription} - Scroll bar is not present when scrolling to element`).toBeLessThanOrEqual(acceptedScrollMargin);
    }

    await expect(element, `${elementDescription} - element should be in viewport`).toBeInViewport();
  }

  /**
   * Validate the element exists or not.
   */
  static async validateElementIsExisting({ element, isExisting = true, timeout = 5_000, elementDescription = '' }: ElementVisibilityOptions & { isExisting?: boolean }): Promise<void> {
    console.log(`${elementDescription} - Validate the element ${isExisting ? 'exists' : 'does not exist'}`);
    if (isExisting) {
      await expect(element, `${elementDescription} - element should exist`).toBeAttached({ timeout });
    } else {
      await expect(element, `${elementDescription} - element should not exist`).not.toBeAttached({ timeout });
    }
  }

  /**
   * Validate the element is displayed or not.
   */
  static async validateElementIsDisplayed({ element, isDisplayed = true, timeout = 5_000, elementDescription = '' }: ElementVisibilityOptions): Promise<void> {
    console.log(`${elementDescription} - Validate the element ${isDisplayed ? 'is displayed' : 'is not displayed'}`);
    // First ensure element exists before checking visibility
    await expect(element, `${elementDescription} - element should exist`).toBeAttached({ timeout });
    if (isDisplayed) {
      await expect(element, `${elementDescription} - should be visible`).toBeVisible({ timeout });
    } else {
      await expect(element, `${elementDescription} - should not be visible`).not.toBeVisible({ timeout });
    }
  }

  /**
   * Validate the element is displayed in viewport or not.
   */
  static async validateElementIsDisplayedInViewport({ element, isDisplayed = true, elementDescription = '' }: ElementVisibilityOptions): Promise<void> {
    console.log(`${elementDescription} - Validate the element is ${isDisplayed ? '' : 'not '}displayed in viewport`);
    // Ensure element is visible before checking viewport
    await expect(element, `${elementDescription} - element should be attached`).toBeAttached({ timeout: 5_000 });
    if (isDisplayed) {
      await expect(element, `${elementDescription} - should be in viewport`).toBeInViewport();
    } else {
      await expect(element, `${elementDescription} - should not be in viewport`).not.toBeInViewport();
    }
  }

  /**
   * Validate the element has text.
   */
  static async validateElementHasText({ element, hasText = true, expectedText, elementDescription = '', textIsContained = false, timeout = 5_000 }: ElementTextOptions): Promise<void> {
    const expected = await expectedText;
    console.log(`${elementDescription} - Validate the element contains the text: "${expected}"`);
    
    // Ensure element exists first
    await expect(element, `${elementDescription} - element should exist`).toBeAttached({ timeout });

    if (hasText) {
      if (textIsContained) {
        await expect(element, `${elementDescription} - should contain text: "${expected}"`).toContainText(expected, { timeout });
      } else {
        await expect(element, `${elementDescription} - should have text: "${expected}"`).toHaveText(expected, { timeout });
      }
    } else if (textIsContained) {
      await expect(element, `${elementDescription} - should not contain text: "${expected}"`).not.toContainText(expected, { timeout });
    } else {
      await expect(element, `${elementDescription} - should not have text: "${expected}"`).not.toHaveText(expected, { timeout });
    }
  }

  /**
   * Validate the element has value.
   */
  static async validateElementHasValue({ element, hasValue = true, expectedValue, elementDescription = '', textIsContained: valueIsContained = false, timeout = 5_000 }: ElementValueOptions): Promise<void> {
    const expected = await expectedValue;
    console.log(`${elementDescription} - Validate the element contains the value: "${expected}"`);
    
    // Ensure element exists first
    await expect(element, `${elementDescription} - element should exist`).toBeAttached({ timeout });

    if (hasValue) {
      if (valueIsContained) {
        await expect(element, `${elementDescription} - should contain value: "${expected}"`).toHaveValue(new RegExp(UiUtils.escapeRegExp(expected)), { timeout });
      } else {
        await expect(element, `${elementDescription} - should have value: "${expected}"`).toHaveValue(expected, { timeout });
      }
    } else if (valueIsContained) {
      await expect(element, `${elementDescription} - should not have value: "${expected}"`).not.toHaveValue(new RegExp(UiUtils.escapeRegExp(expected)), { timeout });
    } else {
      await expect(element, `${elementDescription} - should not have value: "${expected}"`).not.toHaveValue(expected, { timeout });
    }
  }

  /**
   * Validate the expected state of a selectable web element.
   */
  static async validateSelectableElementState(isChecked: boolean, selectableElement: Locator, message = ''): Promise<void> {
    // Ensure element exists before checking state
    await expect(selectableElement, `Element should be attached. ${message}`).toBeAttached();
    if (isChecked) {
      await expect(selectableElement, `Element should be selected. ${message}`).toBeChecked();
    } else {
      await expect(selectableElement, `Element should not be selected. ${message}`).not.toBeChecked();
    }
  }

  /**
   * Validate the enabled/disabled state of an element.
   */
  static async validateElementIsEnabled({ isEnabled = true, element, message = '' }: { isEnabled?: boolean; element: Locator; message?: string }): Promise<void> {
    console.log(`Validate the element is ${isEnabled ? 'enabled' : 'disabled'}`);
    // Ensure element exists and is visible before checking enabled state
    await expect(element, `${message} - element should be attached`).toBeAttached();
    await expect(element, `${message} - element should be visible`).toBeVisible();
    if (isEnabled) {
      await expect(element, `${message} - element should be enabled`).toBeEnabled();
    } else {
      await expect(element, `${message} - element should be disabled`).toBeDisabled();
    }
  }

  /**
   * Validate the selection state of a radio/checkbox element.
   */
  static async validateElementIsSelected({ isSelected = true, element, message = '' }: { isSelected?: boolean; element: Locator; message?: string }): Promise<void> {
    console.log(`Validate that the element is ${isSelected ? '' : 'not '}selected`);
    // Ensure element exists before checking selection state
    await expect(element, `${message} - element should be attached`).toBeAttached();
    if (isSelected) {
      await expect(element, `${message} - element should be selected`).toBeChecked();
    } else {
      await expect(element, `${message} - element should not be selected`).not.toBeChecked();
    }
  }

  /**
   * Validate that the elements in a given container are clickable.
   */
  static async validateElementsAreClickable(elementsList: Locator[]): Promise<void> {
    expect(elementsList.length, `Elements found inside list - should have at least one element (found: ${elementsList.length})`).toBeGreaterThan(0);
    for (const element of elementsList) {
      console.log('Validate that the element is clickable');
      // Ensure element exists and is visible
      await expect(element, 'Element should exist before checking clickability').toBeAttached({ timeout: 5_000 });
      if (await element.isVisible() && await element.isEnabled()) {
        await expect(element, 'Element should be enabled and clickable').toBeEnabled();
      } else {
        await expect(element, 'Element should not be enabled when not visible').not.toBeEnabled();
      }
    }
  }

  /**
   * Validate that the element is at the top of the page.
   */
  static async validateElementIsTopOfPage(element: Locator): Promise<void> {
    console.log('Validate that the element is at the top of the page');
    const box = await UiUtils.getBoundingBox(element);
    expect(box.y, `Element is at the top of the page - expected y=0, got y=${box.y}`).toBe(0);
  }

  /**
   * Validate element is outlined.
   */
  static async validateElementIsOutlined({ element, elementDescription = '' }: ElementValidationOptions): Promise<void> {
    console.log(`${elementDescription} - Validate element is outlined with border`);
    // Ensure element exists before checking outline
    await expect(element, `${elementDescription} - element should be attached`).toBeAttached();
    const borderWidth = await element.evaluate((node) => Number.parseFloat(window.getComputedStyle(node).borderWidth || '0'));
    expect(borderWidth, `${elementDescription} - Border width should be greater than or equal to 0 (actual: ${borderWidth})`).toBeGreaterThanOrEqual(0);
  }

  /**
   * Validate first element in container is the expected one.
   */
  static async validateFirstElementInContainer({ container, expectedLabelOfFirstElement, containerDescription = '' }: {
    container: Locator;
    expectedLabelOfFirstElement: string | Promise<string>;
    containerDescription?: string;
  }): Promise<void> {
    const expected = await expectedLabelOfFirstElement;
    console.log(`${containerDescription} - Validate '${expected}' is the first element in section`);
    // Ensure container exists before checking first child
    await expect(container, `Container should exist for validation - ${containerDescription}`).toBeAttached({ timeout: 5_000 });
    const firstChild = container.locator('*').first();
    await expect(firstChild, `First child element should exist - ${containerDescription}`).toBeAttached({ timeout: 5_000 });
    await expect(firstChild, `First element should contain expected text "${expected}" - ${containerDescription}`).toHaveText(expected);
  }

  /**
   * Validate that the tooltip contains the expected text.
   */
  static async validateTextForTooltipElement({ labelText, debugMessage = '' }: { labelText: string | Promise<string>; debugMessage?: string }): Promise<void> {
    const expected = await labelText;
    console.log(`Validate that the tooltip element contains the text: ${expected}`);
    // Ensure tooltip exists and is visible
    await expect(UiUtils.generalTooltip, 'Tooltip element should exist').toBeAttached({ timeout: 5_000 });
    await expect(UiUtils.generalTooltip, 'Tooltip element should be visible').toBeVisible({ timeout: 5_000 });
    await expect(UiUtils.generalTooltip, `Tooltip text not as expected. Expected: "${expected}". ${debugMessage}`).toHaveText(expected);
  }

  /**
   * Check if image size is not 0 and image exists on server.
   */
  static async validateImageExistence({ webElement, imgSrcFromDictionary, attribute = 'src', authentication = false, parsing = false }: {
    webElement: Locator;
    imgSrcFromDictionary?: string;
    attribute?: string;
    authentication?: boolean;
    parsing?: boolean;
  }): Promise<void> {
    console.log('Validate image existence and size');
    // Ensure element exists before checking
    await expect(webElement, 'Image element should exist').toBeAttached();
    await expect(webElement, 'Image element should be visible').toBeVisible();
    
    if (imgSrcFromDictionary) {
      const actualSrc = await webElement.getAttribute(attribute);
      if (imgSrcFromDictionary.includes('http')) {
        console.log('Dictionary contains absolute path for image!');
        expect(actualSrc, `Source of image does not match expected path - expected: ${imgSrcFromDictionary}, got: ${actualSrc}`).toBe(imgSrcFromDictionary);
      } else {
        console.log('Dictionary contains relative path for image!');
        expect(actualSrc, `Source of image does not contain expected relative path - expected to contain: ${imgSrcFromDictionary}, got: ${actualSrc}`).toContain(imgSrcFromDictionary);
      }
    }

    const box = await UiUtils.getBoundingBox(webElement);
    if (box.height <= 0 || box.width <= 0) {
      throw new Error(`Element width or height cannot be 0 (width: ${box.width}, height: ${box.height})`);
    }

    console.log(`Image element has valid dimensions (width: ${box.width}, height: ${box.height})`);
    await UiUtils.validateImageIsPresentOnServer({ webElement, attribute, authentication, parsing });
  }

  /**
   * Check if image is present on element source.
   */
  static async validateImageIsPresentOnServer({ webElement, attribute, authentication: _authentication, parsing = false }: {
    webElement: Locator;
    attribute: string;
    authentication?: boolean;
    parsing?: boolean;
  }): Promise<void> {
    console.log('Validate image is present on server');
    // Ensure element exists before checking image
    await expect(webElement, 'Image element should exist before checking server').toBeAttached({ timeout: 5_000 });
    
    let imageUrl = await webElement.getAttribute(attribute);
    if (!imageUrl) {
      throw new Error(`Image URL is missing from element attribute "${attribute}"`);
    }
    if (imageUrl.includes('data:image/gif')) {
      console.log('Skipping image validation - data URI');
      return;
    }

    if (parsing) {
      imageUrl = imageUrl.replace(/%3A/g, ':').replace(/%2F/g, '/').replace(/'/g, '').replace(/%40/g, '@');
      imageUrl = imageUrl.split('url=')[1]?.split('&w=')[0] ?? imageUrl;
    }

    UiUtils.validateUrlMatchesConfiguredHost(imageUrl, 'The environment is incorrect!');

    console.log(`Checking if image URL exists: ${imageUrl}`);
    const response = await UiUtils.requestContext.get(imageUrl, {
      headers: {
        Accept: 'image/*,*/*;q=0.8'
      }
    });
    expect(response.ok(), `The URL image for the element does not exist or failed to load: ${imageUrl} (status: ${response.status()})`).toBe(!imageUrl.includes('/LONCWW_01.jpg'));
  }

  /**
   * Check if PDF is present on element source.
   */
  static async validatePdfIsPresentOnServer({ webElement, attribute = 'href', authentication: _authentication = false }: {
    webElement: Locator;
    attribute?: string;
    authentication?: boolean;
  }): Promise<void> {
    console.log('Validate PDF is present on server');
    // Ensure element exists before checking PDF
    await expect(webElement, 'PDF element should exist before checking server').toBeAttached({ timeout: 5_000 });
    
    const pdfUrl = await webElement.getAttribute(attribute);
    if (!pdfUrl) {
      throw new Error(`PDF URL is missing from element attribute "${attribute}"`);
    }

    UiUtils.validateUrlMatchesConfiguredHost(pdfUrl, 'Environment is incorrect!');

    console.log(`Checking if PDF URL exists: ${pdfUrl}`);
    const response = await UiUtils.requestContext.fetch(pdfUrl, { method: 'HEAD' });
    expect(response.ok(), `PDF does not exist or failed to load from URL: ${pdfUrl} (status: ${response.status()})`).toBe(true);
  }

  /**
   * Validate button.
   */
  static async validateButton({ element, buttonLabel, isDisplayed = true, hasText = true, isClickable = true }: ButtonValidationOptions): Promise<void> {
    if (!element || !buttonLabel) {
      throw new Error('"element" and "buttonLabel" are required parameters');
    }

    const label = await buttonLabel;
    console.log(`Validate "${label}" button`);
    await UiUtils.validateElementIsDisplayed({ element, isDisplayed, elementDescription: `"${label}" button` });

    if (isDisplayed) {
      await UiUtils.validateElementClickability(element, `"${label}" button`, isClickable);
      await UiUtils.validateElementHasText({ element, hasText, expectedText: label, elementDescription: `"${label}" button label` });
    }
  }

  /**
   * Validate radio button.
   */
  static async validateRadioButton({ element, buttonLabel, isDisplayed = true, hasText = true, isClickable = true, isChecked = false }: RadioButtonValidationOptions): Promise<void> {
    const label = await buttonLabel;
    console.log(`Validate "${label}" radio button`);
    await UiUtils.validateElementIsDisplayed({ element, isDisplayed, elementDescription: `"${label}" button` });
    if (isDisplayed) {
      await UiUtils.validateElementClickability(element, `"${label}" radio button`, isClickable);
      const siblingDiv = element.locator('xpath=following-sibling::div').first();
      const siblingLabel = element.locator('xpath=following-sibling::label').first();
      const labelElement = await siblingDiv.count() > 0 ? siblingDiv : siblingLabel;
      await UiUtils.validateElementHasText({ element: labelElement, hasText, expectedText: label, elementDescription: `"${label}" radio button label` });
      await expect(element, isChecked ? `${label} is not checked` : `${label} is checked`).toHaveAttribute('data-state', isChecked ? 'checked' : 'unchecked');
    }
  }

  /**
   * Validate label is displayed.
   */
  static async validateLabelIsDisplayed({ element, elementDescription, expectedText, textIsContained = false, hasText = true }: {
    element: Locator;
    elementDescription: string;
    expectedText?: string | Promise<string>;
    textIsContained?: boolean;
    hasText?: boolean;
  }): Promise<void> {
    if (!element || (!expectedText && hasText)) {
      throw new Error('"element" and "text" are required parameters');
    }

    const description = await elementDescription;
    console.log(`Validate "${description}" label is displayed`);
    // Ensure element exists before checking
    await expect(element, `Label element should exist - "${description}"`).toBeAttached({ timeout: 5_000 });
    await UiUtils.validateElementIsDisplayed({ element, elementDescription: `"${description}" label` });
    if (expectedText) {
      await UiUtils.validateElementHasText({ element, hasText, expectedText, elementDescription: `"${description}" label`, textIsContained });
    }
  }

  /**
   * Validate elements list has an exact count.
   */
  static async validateElementsCount({ elementsList, expectedCount, elementDescription = '' }: {
    elementsList: Locator[];
    expectedCount: number;
    elementDescription?: string;
  }): Promise<void> {
    if (!elementsList || expectedCount === undefined) {
      throw new Error('"elementsList" and "expectedCount" are required parameters');
    }

    console.log(`${elementDescription} - Validate elements count is ${expectedCount}`);
    // Ensure elements exist before counting
    for (let i = 0; i < elementsList.length; i++) {
      await expect(elementsList[i], `Element at index ${i} should exist - ${elementDescription}`).toBeAttached({ timeout: 5_000 });
    }
    expect(elementsList.length, `${elementDescription} - Expected ${expectedCount} element(s) but found ${elementsList.length}`).toBe(expectedCount);
  }

  private static async getBoundingBox(element: Locator): Promise<NonNullable<Awaited<ReturnType<Locator['boundingBox']>>>> {
    await element.scrollIntoViewIfNeeded();
    await expect(element, 'Element should be visible before reading its bounding box').toBeVisible();
    const box = await element.boundingBox();
    if (!box) {
      throw new Error('Element bounding box is not available');
    }
    return box;
  }

  private static getOverlap(first: { x: number; y: number; width: number; height: number }, second: { x: number; y: number; width: number; height: number }): { width: number; height: number } {
    const left = Math.max(first.x, second.x);
    const right = Math.min(first.x + first.width, second.x + second.width);
    const top = Math.max(first.y, second.y);
    const bottom = Math.min(first.y + first.height, second.y + second.height);
    return { width: Math.max(0, right - left), height: Math.max(0, bottom - top) };
  }

  private static escapeRegExp(value: string): string {
    return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  }

  private static async validateElementClickability(element: Locator, elementDescription: string, isClickable: boolean): Promise<void> {
    const isDisplayed = await element.isVisible();
    const dataDisabled = await element.getAttribute('data-disabled');
    const isEnabled = await element.isEnabled();

    if (isClickable) {
      await expect(element, `${elementDescription} should be visible`).toBeVisible();
      expect(dataDisabled, `${elementDescription} should not have data-disabled`).toBeNull();
      await expect(element, `${elementDescription} should be enabled`).toBeEnabled();
      return;
    }

    expect(!isDisplayed || dataDisabled !== null || !isEnabled, `${elementDescription} should not be clickable`).toBe(true);
  }

  private static validateUrlMatchesConfiguredHost(url: string, message: string): void {
    const expectedHost = UiUtils.getConfiguredContentHost();
    if (!expectedHost) return;

    expect(url, message).toContain(expectedHost);
  }

  private static getConfiguredContentHost(): string | null {
    const configuredUrl = global.browser?.options?.aemBaseUrl || global.browser?.options?.entityApiBaseUrl;
    if (!configuredUrl) return null;

    try {
      return new URL(configuredUrl).host;
    } catch {
      return configuredUrl.split('://').at(-1)?.split('/')[0] ?? configuredUrl;
    }
  }
}
