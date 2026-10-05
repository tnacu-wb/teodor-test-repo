import '@testing-library/jest-dom';
import { isIVMEnabled } from '@whitbread-eos/utils';
import React from 'react';

import { render, waitFor } from '../../utils/test-utils';
import HotelThumbnail from './HotelThumbnail.component';

const mockedImageData = {
  imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/D/DUBSOU/dublin-cc-external.jpg',
  imageAlt: 'Dublin City Centre (Temple Bar) exterior',
};

const mockedBrandLogos = {
  hubLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg',
  zipLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg',
};

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  isIVMEnabled: jest.fn(),
}));

jest.mock(
  'next/image',
  () =>
    function Image({ src, alt }) {
      return <img src={src} alt={alt} />;
    }
);

describe('SRP - HotelThumbnail', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should have src property which contains correct value', async () => {
    const { getByAltText } = render(
      <HotelThumbnail
        imageData={mockedImageData}
        testId="SRP-hotel-hotel-thumbnail"
        brand="PI"
        brandLogos={mockedBrandLogos}
      />
    );

    const displayedImage = getByAltText(mockedImageData.imageAlt);

    await waitFor(() => {
      expect(displayedImage).toBeInTheDocument();
      expect(displayedImage.getAttribute('src')).toContain(
        'https://secure2.premierinn.com/example/image.png'
      );
    });
  });

  it('should have alt property which contains correct value', async () => {
    (isIVMEnabled as jest.Mock).mockImplementation(() => true);
    const { getByAltText } = render(
      <HotelThumbnail
        imageData={mockedImageData}
        testId="SRP-hotel-hotel-thumbnail"
        brand="PI"
        brandLogos={mockedBrandLogos}
      />
    );

    const displayedImage = getByAltText(mockedImageData.imageAlt);

    await waitFor(() => {
      expect(displayedImage).toBeInTheDocument();
      expect(displayedImage.getAttribute('alt')).toContain(
        'Dublin City Centre (Temple Bar) exterior'
      );
    });
  });
});
