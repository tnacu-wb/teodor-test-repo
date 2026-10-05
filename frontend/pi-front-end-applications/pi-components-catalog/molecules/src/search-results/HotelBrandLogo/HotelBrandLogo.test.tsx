import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelBrandLogo, { checkIfIsNotPiBrand, getBrandLogoURL } from './HotelBrandLogo.component';

const mockedLogos = {
  hubLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg',
  zipLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg',
};

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

describe('SRP - HotelBrandLogo', () => {
  it('should render the HotelBrandLogo HUB', () => {
    const { getByTestId } = render(<HotelBrandLogo brand="HUB" logos={mockedLogos} />);
    expect(getByTestId('srp_hotel-brand-logo')).toBeInTheDocument();
  });
  it('should display hub altText if brand=HUB', () => {
    const { getByAltText } = render(<HotelBrandLogo brand="HUB" logos={mockedLogos} />);
    expect(getByAltText('hub')).toBeInTheDocument();
  });
  it('should display zip altText if brand=ZIP', () => {
    const { getByAltText } = render(<HotelBrandLogo brand="ZIP" logos={mockedLogos} />);
    expect(getByAltText('zip')).toBeInTheDocument();
  });
  it('should render nothing for PI hotel', () => {
    const { container } = render(<HotelBrandLogo brand="PI" logos={mockedLogos} />);
    expect(container.firstChild).toBeNull();
  });
});

describe('checkIfIsNotPiBrand function', () => {
  it('should return true for ZIP and HUB', () => {
    expect(checkIfIsNotPiBrand('ZIP')).toBeTruthy();
    expect(checkIfIsNotPiBrand('HUB')).toBeTruthy();
  });
  it('should return false for PI', () => {
    expect(checkIfIsNotPiBrand('PI')).toBeFalsy();
  });
});

describe('getBrandLogoURL function', () => {
  it('should return true for ZIP and HUB', () => {
    expect(getBrandLogoURL('ZIP', mockedLogos)).toBe(
      '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg'
    );
    expect(getBrandLogoURL('HUB', mockedLogos)).toBe(
      '/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg'
    );
  });
  it('should return null for PI', () => {
    expect(getBrandLogoURL('PI', mockedLogos)).toBeFalsy();
  });
});
