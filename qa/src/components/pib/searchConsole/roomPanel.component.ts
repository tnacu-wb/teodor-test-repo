import { type Locator, type Page } from "@playwright/test";
import type { Room } from "../../../test-data/room";
import { Strings } from "../../../test-data/strings";

/**
 * A single room panel item from IB search bar
 */
export class RoomPanelComponent {
  private readonly page: Page = global.page;
  private readonly isGerman = global.browser?.options?.locale?.toLowerCase() === "de-de";

  /** A single room panel item from IB search bar. */
  constructor(container: Locator, roomIndex = 0) {
    this.container = container;
    const adultsLabel = this.isGerman ? Strings.ADULTS.data.de : Strings.ADULTS.data.default;
    const childrenLabel = this.isGerman ? Strings.CHILDREN.data.de : Strings.CHILDREN.data.default;
    const roomTypeLabel = this.isGerman ? Strings.ROOM_TYPE.data.de : Strings.ROOM_TYPE.data.default;
    this.adultsMenuButton = container
      .locator('[data-testid*="IB-RoomOccupancy-Adults-Dropdown-"]')
      .nth(roomIndex)
      .or(this.page.getByRole("button", { name: new RegExp(adultsLabel ?? "Adults", "i") }).nth(roomIndex));
    this.childrenMenuButton = container
      .locator('[data-testid*="IB-RoomOccupancy-Children-Dropdown-"]')
      .nth(roomIndex)
      .or(this.page.getByRole("button", { name: new RegExp(childrenLabel ?? "Children", "i") }).nth(roomIndex));
    this.roomTypeMenuButton = container
      .locator('[data-testid*="IB-RoomOccupancy-RoomType-"]')
      .nth(roomIndex)
      .or(this.page.getByRole("button", { name: new RegExp(roomTypeLabel ?? "Room type", "i") }).nth(roomIndex));
    this.includeCotToggle = container
      .locator('[data-testid*="IB-shouldIncludeCot-Switch"]')
      .nth(roomIndex)
      .or(this.page.getByRole("switch").nth(roomIndex));
  }
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly container: Locator;
  readonly adultsMenuButton: Locator;
  readonly childrenMenuButton: Locator;
  readonly roomTypeMenuButton: Locator;
  readonly includeCotToggle: Locator;

  // ######## UI actions/navigation ########
  /** Select the room occupancy, room type, and cot preference. */
  async selectRoom({ room }: { room: Room }): Promise<void> {
    console.log(`Select IB room adults=${room.adultsNumber} children=${room.childrenNumber}`);
    await this.selectAdults(room.adultsNumber);
    await this.selectChildren(room.childrenNumber);
    await this.selectRoomType(await room.roomType.name.name);
    await this.setIncludeCot(room.cotRequired);
  }

  /** Select the number of adults for this room. */
  async selectAdults(adults: number): Promise<void> {
    console.log(`Select IB room adults=${adults}`);
    await this.adultsMenuButton.click();
    const adultOptions = this.page.locator('button[data-testid*="IB-RoomOccupancy-Adults-Dropdown"]');
    if (await adultOptions.count()) {
      await adultOptions.nth(adults - 1).click();
      return;
    }
    const adultLabel = adults === 1
      ? (this.isGerman ? Strings.ADULT_ANCILLARIES_BOOKING_INFO.data.de : Strings.ADULT_ANCILLARIES_BOOKING_INFO.data.default)
      : (this.isGerman ? Strings.ADULTS.data.de : Strings.ADULTS.data.default);
    const adultOptionName = new RegExp(`^${adults}\\s+${adultLabel ?? "Adults"}$`, "i");
    const adultRoleOption = this.page.getByRole("option", { name: adultOptionName });
    if (await adultRoleOption.count()) {
      await adultRoleOption.click();
      return;
    }
    const adultTextOption = this.page.getByText(adultOptionName).last();
    if (await adultTextOption.count()) {
      await adultTextOption.click();
      return;
    }
    for (let index = 1; index < adults; index += 1) await this.page.keyboard.press("ArrowDown");
    await this.page.keyboard.press("Enter");
  }

  /** Select the number of children for this room. */
  async selectChildren(children: number): Promise<void> {
    console.log(`Select IB room children=${children}`);
    await this.childrenMenuButton.click();
    const childrenOptions = this.page.locator('button[data-testid*="IB-RoomOccupancy-Children-Dropdown"]');
    if (await childrenOptions.count()) {
      await childrenOptions.nth(children).click();
      return;
    }
    const childrenLabel = this.isGerman ? Strings.CHILDREN.data.de : Strings.CHILDREN.data.default;
    const childrenOptionName = new RegExp(`^${children}\\s+${childrenLabel ?? "Children"}$`, "i");
    const childrenRoleOption = this.page.getByRole("option", { name: childrenOptionName });
    if (await childrenRoleOption.count()) {
      await childrenRoleOption.click();
      return;
    }
    const childrenTextOption = this.page.getByText(childrenOptionName).last();
    if (await childrenTextOption.count()) {
      await childrenTextOption.click();
      return;
    }
    for (let index = 0; index < children; index += 1) await this.page.keyboard.press("ArrowDown");
    await this.page.keyboard.press("Enter");
  }

  /** Select the room type by its displayed label. */
  async selectRoomType(roomType: string): Promise<void> {
    console.log(`Select IB room type=${roomType}`);
    await this.roomTypeMenuButton.click();
    const localizedRoomType = this.isGerman && roomType.toLowerCase() === String(Strings.DOUBLE.data.default).toLowerCase()
      ? Strings.DOUBLE.data.de
      : roomType;
    const roomTypeOption = this.page
      .locator('[data-testid*="IB-RoomOccupancy-RoomType-"][data-testid*="Option"]')
      .filter({ hasText: localizedRoomType ?? roomType });
    if (await roomTypeOption.count()) {
      await roomTypeOption.click();
      return;
    }
    const roomTypeOptions = this.page.locator('[data-testid*="IB-RoomOccupancy-RoomType-"][data-testid*="Option"]');
    if (await roomTypeOptions.count()) {
      const roomTypeIndex = roomType.toLowerCase() === "single" ? 0 : roomType.toLowerCase() === "double" ? 1 : 2;
      await roomTypeOptions.nth(roomTypeIndex).click();
      return;
    }
    const visibleRoomTypeOption = this.page.getByText(new RegExp(`^${localizedRoomType ?? roomType}$`, "i")).last();
    if (await visibleRoomTypeOption.count()) {
      await visibleRoomTypeOption.click();
      return;
    }
    const roomTypeIndex = roomType.toLowerCase() === "single" ? 0 : roomType.toLowerCase() === "double" ? 1 : 2;
    for (let index = 0; index < roomTypeIndex; index += 1) await this.page.keyboard.press("ArrowDown");
    await this.page.keyboard.press("Enter");
  }

  /** Set the include-cot toggle to the requested state. */
  async setIncludeCot(enabled: boolean): Promise<void> {
    console.log(`Set IB room cot enabled=${enabled}`);
    const currentState = await this.includeCotToggle.getAttribute("data-state");
    if ((currentState === "checked") !== enabled) await this.includeCotToggle.click();
  }

  // ######## UI validations ########
}
