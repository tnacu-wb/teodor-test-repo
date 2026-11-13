import { HotelFacility } from './hotelFacility';
import { HotelGalleryImage } from './hotelGalleryImage';

/**
 * One room tab item from API response
 * response example: 
 *  {
        "roomDescription": "A super-comfy Hypnos bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.",
        "roomName": "Standard double",
        "roomType": "double",
        "images": [
            {
                "alt": "Non hotel specific accessible bathroom",
                "caption": "Accessible Lowered Bathroom",
                "iconSrc": "/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LWB.svg",
                "imageSrc": "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-lowered-bath-accessible.jpg",
                "thumbnailSrc": "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-lowered-bath-accessible.jpg"
            }
        ],
        "facilities": [
            {
                "weight": 2,
                "name": "Improved refreshments",
                "isVisible": true,
                "icon": "/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/111.svg",
                "description": "Including a variety of Pure Leaf tea bags and a sweet treat",
                "code": "111"
            }
        ]
    }
 */
export class HotelRoomTabItem {
  [key: string]: unknown;
  facilities: HotelFacility[] = [];
  images: HotelGalleryImage[] = [];
  roomDescription?: string;
  roomName?: string;
  roomType?: string;

  /**
   * HotelRoomTabItem constructor
   * @param data object data
   * @param data.tabItem tabItem object
   */
  constructor(data: { tabItem?: Record<string, unknown> } = {}) {
    const tabItem = data.tabItem ?? {};
    this.roomDescription = tabItem.roomDescription as string | undefined;
    this.roomName = tabItem.roomName as string | undefined;
    this.roomType = tabItem.roomType as string | undefined;

    const images = Array.isArray(tabItem.images) ? (tabItem.images as Array<Record<string, unknown>>) : [];
    this.images = images.map((imageItem) => new HotelGalleryImage({ hotelPhotoGalleryImage: imageItem }));

    const facilities = Array.isArray(tabItem.facilities) ? (tabItem.facilities as Array<Record<string, unknown>>) : [];
    this.facilities = facilities.map((facilityItem) => new HotelFacility({ hotelFacility: facilityItem }));
  }

  static fromResponse(data: { tabItem?: Record<string, unknown> }): HotelRoomTabItem {
    return new HotelRoomTabItem(data);
  }

}
