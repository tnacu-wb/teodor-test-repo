import { HotelRoomTabItem } from './hotelRoomTabItem';

/**
 * One hotel room configuration containing one tab group item and the coresspondent tab panel items
 */
export class HotelRoomConfiguration {
  [key: string]: unknown;
  groupId?: string;
  groupName?: string;
  tabPanelItems: HotelRoomTabItem[] = [];

  /**
   * HotelRoomConfiguration constructor
   * @param data object data
   * @param data.tabGroup tabGroup data
   * @param data.tabPanelItems tabPanelItems data
   */
  constructor(data: { tabGroup?: { groupId?: string; groupName?: string }; tabPanelItems?: Array<Record<string, unknown>> } = {}) {
    const tabGroup = data.tabGroup ?? {};
    this.groupId = tabGroup.groupId;
    this.groupName = tabGroup.groupName;
    const tabPanelItems = data.tabPanelItems ?? [];
    this.tabPanelItems = tabPanelItems.map((tabPanelItem) => new HotelRoomTabItem({ tabItem: tabPanelItem }));
  }

  static fromResponse(data: {
    tabGroup?: { groupId?: string; groupName?: string };
    tabPanelItems?: Array<Record<string, unknown>>;
  }): HotelRoomConfiguration {
    return new HotelRoomConfiguration(data);
  }

}
