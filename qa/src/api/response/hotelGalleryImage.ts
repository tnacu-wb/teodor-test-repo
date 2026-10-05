/**
 * The hotel photo gallery response from API
 * response example"
 {
  "data": {
    "hotelInformation": {
      "galleryImages": [
        {
          "alt": "",
          "caption": "",
          "iconSrc": "",
          "imageSrc": "/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg",
          "thumbnailSrc": "/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg"
        }
      ]
    }
  }
}
 */
export class HotelGalleryImage {
  [key: string]: unknown;
  alt?: string;
  caption?: string;
  iconSrc?: string;
  imageSrc?: string;
  thumbnailSrc?: string;

  /**
   * HotelGalleryImage constructor
   * @param data object
   * @param data.hotelPhotoGalleryImage hotelPhotoGallery response from API
   */
  constructor(data: { hotelPhotoGalleryImage?: Record<string, unknown> } = {}) {
    const hotelPhotoGalleryImage = data.hotelPhotoGalleryImage ?? {};
    this.alt = hotelPhotoGalleryImage.alt as string | undefined;
    this.caption = hotelPhotoGalleryImage.caption as string | undefined;
    this.iconSrc = hotelPhotoGalleryImage.iconSrc as string | undefined;
    this.imageSrc = hotelPhotoGalleryImage.imageSrc as string | undefined;
    this.thumbnailSrc = hotelPhotoGalleryImage.thumbnailSrc as string | undefined;
  }

  static fromResponse(data: { hotelPhotoGalleryImage?: Record<string, unknown> }): HotelGalleryImage {
    return new HotelGalleryImage(data);
  }
}
