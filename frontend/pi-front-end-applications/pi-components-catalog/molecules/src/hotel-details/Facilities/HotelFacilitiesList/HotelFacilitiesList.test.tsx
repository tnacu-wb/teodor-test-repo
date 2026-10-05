import '@testing-library/jest-dom';
import type { FacilityItem } from '@whitbread-eos/api';
import { useStaticHotelInformation } from '@whitbread-eos/utils';

import { fireEvent, render } from '../../../utils/test-utils';
import { mockHotelFacilities } from '../FacilitiesModal/FacilitiesModal.test';
import { HotelFacilitiesList } from './HotelFacilitiesList';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useStaticHotelInformation: jest.fn(),
}));

const mockUseStaticHotelInformation = useStaticHotelInformation as jest.Mock;

const hotelFacilitiesProps = {
  isLessThanSm: false,
};

beforeEach(() => {
  mockUseStaticHotelInformation.mockReturnValue({
    hotelFacilities: mockHotelFacilities,
    isLoading: false,
    isError: false,
    error: null,
  });
});

describe('HotelFacilitiesList', () => {
  describe('HotelFacilitiesList render', () => {
    it('should render HotelFacilitiesList', () => {
      const { getByTestId } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      expect(getByTestId('hdp_hotelFacilitiesText')).toBeInTheDocument();
    });

    it('should render HotelFacilitiesList with soft bundles', () => {
      const { getByTestId } = render(
        <HotelFacilitiesList {...hotelFacilitiesProps} isSoftBundlesVisible={true} />
      );
      expect(getByTestId('hdp_hotelFacilitiesText')).toBeInTheDocument();
    });

    it('should render loading message', () => {
      mockUseStaticHotelInformation.mockReturnValueOnce({
        hotelFacilities: mockHotelFacilities,
        isLoading: true,
        isError: false,
        error: null,
      });

      const { getByText } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
    });

    it('should render error message', () => {
      mockUseStaticHotelInformation.mockReturnValueOnce({
        hotelFacilities: mockHotelFacilities,
        isLoading: false,
        isError: true,
        error: { message: 'Error' },
      });

      const { getByText } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      expect(getByText('Error')).toBeInTheDocument();
    });

    it('should render component heading', () => {
      const { getByText } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      expect(getByText('hoteldetails.hotel.facilities')).toBeInTheDocument();
    });

    it('should render four visible hotel facilities', () => {
      const { getAllByTestId } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      expect(getAllByTestId('svg-container').length).toEqual(4);
    });

    it('should render facilities titles', () => {
      const { getByText } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);

      const renderedFacilityTitles: string[] = [];
      const expectedFacilityTitles = mockHotelFacilities
        .filter((facility) => facility.isVisible)
        .map((facility) => facility.name);

      mockHotelFacilities
        .filter((facility) => facility.isVisible)
        .forEach((facility) => {
          const facilityTitle = getByText(facility.name);
          renderedFacilityTitles.push(facilityTitle.innerHTML);
        });
      expect(renderedFacilityTitles).toEqual(expectedFacilityTitles);
    });

    it('should display "See all" link', () => {
      const { getByText } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      expect(getByText('hoteldetails.seeall')).toBeInTheDocument();
    });

    it('should display "See all hotel facilities" link for mobile devices', () => {
      const { getByText } = render(
        <HotelFacilitiesList {...hotelFacilitiesProps} isLessThanSm={true} />
      );
      expect(getByText('hoteldetails.seeall')).toBeInTheDocument();
    });

    it('should render null if no facilities data is passed', () => {
      mockUseStaticHotelInformation.mockReturnValueOnce({
        hotelFacilities: [] as FacilityItem[],
        isLoading: false,
        isError: false,
        error: null,
      });

      const { queryAllByTestId } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      expect(queryAllByTestId('svg-container').length).toEqual(0);
    });
  });

  describe('HotelFacilitiesList modal', () => {
    it('should open HotelFacilitiesModal on facilities icon click', () => {
      const { getByTestId, getAllByTestId } = render(
        <HotelFacilitiesList {...hotelFacilitiesProps} />
      );
      fireEvent.click(getAllByTestId('svg-container')[0]);
      expect(getByTestId('facilities-list')).toBeInTheDocument();
    });

    it('should display HotelFacilitiesModal with facility descriptions', () => {
      const { getAllByTestId } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      fireEvent.click(getAllByTestId('svg-container')[0]);
      expect(getAllByTestId('facility-title').length).toBeGreaterThan(0);
      expect(getAllByTestId('facility-description').length).toBeGreaterThan(0);
    });

    it('should display HotelFacilitiesModal without facility descriptions', () => {
      const data = mockHotelFacilities.map((hotelFacility) => ({
        code: hotelFacility.code,
        icon: hotelFacility.icon,
        isVisible: hotelFacility.isVisible,
        name: hotelFacility.name,
        weight: hotelFacility.weight,
      }));

      mockUseStaticHotelInformation.mockReturnValue({
        hotelFacilities: data,
        isLoading: false,
        isError: false,
        error: null,
      });

      const { queryAllByTestId } = render(<HotelFacilitiesList {...hotelFacilitiesProps} />);
      fireEvent.click(queryAllByTestId('svg-container')[0]);
      expect(queryAllByTestId('facility-title').length).toBeGreaterThan(0);
      expect(queryAllByTestId('facility-description').length).toEqual(0);
    });

    it('should close HotelFacilitiesModal on close click', () => {
      const { getByTestId, queryByTestId, getAllByTestId } = render(
        <HotelFacilitiesList {...hotelFacilitiesProps} />
      );
      fireEvent.click(getAllByTestId('svg-container')[0]);
      fireEvent.click(getByTestId('facilities-modal-ModalCloseButton'));
      expect(queryByTestId('facilities-modal')).toBeFalsy();
    });
  });
});
