import { render } from '@testing-library/react';

import HotelFacilities from './HotelFacilities.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const mockedFacilities = [
  {
    code: 'HRS',
    description: '',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/HRS.svg',
    isVisible: true,
    name: 'Restaurant',
    weight: 1,
  },
  {
    code: 'CPF',
    description: 'Parking',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/CPF.svg',
    isVisible: true,
    name: 'Parking',
    weight: 1,
  },
  {
    code: 'FAM',
    description: 'Family rooms',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/FAM.svg',
    isVisible: true,
    name: 'Family rooms',
    weight: 1,
  },
  {
    code: 'LUG',
    description: 'Gepäckaufbewahrung',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LUG.svg',
    isVisible: false,
    name: 'Gepäckaufbewahrung',
    weight: 20,
  },
  {
    code: 'HAR',
    description: 'Accessible',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
    isVisible: true,
    name: 'Accessible',
    weight: 1,
  },
  {
    code: 'ACO',
    description: 'Air conditioning',
    icon: null,
    isVisible: true,
    name: null,
    weight: 5,
  },
];

const mockedRoomTypes = ['SB', 'DB'];
const defaultProps = {
  facilities: mockedFacilities,
  roomTypes: mockedRoomTypes,
};

describe('HotelCardFacilities', () => {
  it('should render the correct facilities', () => {
    const { getAllByTestId } = render(<HotelFacilities {...defaultProps} />);
    expect(getAllByTestId('svg-container').length).toEqual(3);
  });

  it('should display the Accessible icon if the roomTypes prop contains at least one Accessible type', () => {
    const { getAllByTestId } = render(<HotelFacilities {...defaultProps} roomTypes={['DIS']} />);
    expect(getAllByTestId('svg-container').length).toEqual(4);
  });

  it('should display the dlpFacilities order if isDLPPage prop is true', () => {
    const { getAllByTestId } = render(<HotelFacilities {...defaultProps} isDLPPage />);
    expect(getAllByTestId('svg-container').length).toEqual(4);
  });
});
