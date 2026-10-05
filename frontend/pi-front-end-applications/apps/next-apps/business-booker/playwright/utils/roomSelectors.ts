/* eslint-disable prettier/prettier */
import { Room } from '@WB-playwright/types';
import { Page, Locator } from '@playwright/test';

async function getRoomPanelByIndex(page: Page, index: number): Promise<Locator> {
    return page.locator(`//div[contains(@data-testid,"IB-Room-${index + 1}")]`);
}

async function selectAdults(page: Page, roomPanel: Locator, adultsNumber: number) {
  const adultsButton = roomPanel.locator('//div[contains(@data-testid,"IB-RoomOccupancy-Adults-Dropdown-") and contains(@data-testid,"-IB-Select-Trigger")]');
  const adultsList = page.locator(`//button[contains(@data-testid,"IB-RoomOccupancy-Adults-Dropdown") and contains(@data-testid,"-${adultsNumber - 1}-Option")]`);
  await adultsButton.click();
  await adultsList.click();
}

async function selectChildren(page: Page, roomPanel: Locator, childrenNumber: number) {
  const childrenButton = roomPanel.locator('//div[contains(@data-testid,"IB-RoomOccupancy-Children-Dropdown-") and contains(@data-testid,"-IB-Select-Trigger")]');
  const childrenList = page.locator(`//button[contains(@data-testid,"IB-RoomOccupancy-Children-Dropdown") and contains(@data-testid,"-${childrenNumber}-Option")]`);
  await childrenButton.click();
  await childrenList.click();
}

async function switchToggle(roomPanel: Locator, toggleOnState: boolean) {
  const includeCotToggle = roomPanel.locator('//button[contains(@data-testid,"IB-shouldIncludeCot-Switch")]');
  const toggleSwitch = includeCotToggle.locator('//span');
  const isSelected = await toggleSwitch.isChecked();
  if ((toggleOnState && !isSelected) || (!toggleOnState && isSelected)) {
    await includeCotToggle.click();
  }
}

async function selectRoomType(page: Page, roomPanel: Locator, roomType: string) {
  const roomTypeButton = roomPanel.locator('//div[contains(@data-testid,"IB-RoomOccupancy-RoomType-") and contains(@data-testid,"-IB-Select-Trigger")]');
  const roomTypeList = page.locator('//button[contains(@data-testid,"IB-RoomOccupancy-RoomType-") and contains(@data-testid,"Option")]');
  await roomTypeButton.click();
  const roomTypeOptions = [];
  const roomTypeCount = await roomTypeList.count();
  for (let i = 0; i < roomTypeCount; i++) {
    const roomOption = roomTypeList.nth(i);
    const roomOptionLabel = await roomOption.textContent();
    if (roomOptionLabel) {
      roomTypeOptions.push(roomOptionLabel.trim());
    }
  }
  let isRoomTypeSelected = false;
  for (let i = 0; roomTypeOptions.length; i++) {
    if (roomTypeOptions[i] === roomType) {
      await roomTypeList.nth(i).click();
      isRoomTypeSelected = true;
      break;
    }
  }
  if (!isRoomTypeSelected) {
    throw new Error(`Type ${roomType} not found.`);
  }
}

async function selectRoom(page: Page, roomData: Room, index: number) {
  const roomPanel = await getRoomPanelByIndex(page, index)
  await roomPanel.scrollIntoViewIfNeeded();
  await selectAdults(page, roomPanel, roomData.adultsNumber);
  await selectChildren(page, roomPanel, roomData.childrenNumber);
  await switchToggle(roomPanel, roomData.cotRequired);
  if (typeof roomData.roomType !== 'string') {
    await selectRoomType(page, roomPanel, roomData.roomType.name);
  }
}

export async function addRooms(page: Page, roomsList: Room[]) {
  const normalizeRooms = Array.isArray(roomsList) ? roomsList : [roomsList];
  const addRoomButton = page.locator('button[data-testid="IB-Add-Room-Button"]');
  for (let roomNumber = 2; roomNumber <= normalizeRooms.length; roomNumber++) {
    await addRoomButton.click();
  }
  for (const [index, roomData] of normalizeRooms.entries()) {
    await selectRoom(page, roomData, index);
  }
}
