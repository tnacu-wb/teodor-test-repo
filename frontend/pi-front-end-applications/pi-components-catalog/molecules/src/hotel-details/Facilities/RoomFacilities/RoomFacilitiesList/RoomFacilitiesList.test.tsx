import '@testing-library/jest-dom';

import { fireEvent, render } from '../../../../utils/test-utils';
import {
  mockHotelFacilities,
  mockRoomFacilities,
} from '../../FacilitiesModal/FacilitiesModal.test';
import RoomFacilitiesListComponent from './RoomFacilitiesList.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const roomFacilitiesProps = {
  facilities: mockRoomFacilities.hotelInformation.roomConfiguration.tabItems[0].facilities,
  isLessThanSm: false,
  isLessThanMd: false,
};

describe('RoomFacilitiesList', () => {
  describe('RoomFacilitiesList render', () => {
    it('should render five visible room facilities', function () {
      const { getAllByTestId } = render(<RoomFacilitiesListComponent {...roomFacilitiesProps} />);
      expect(getAllByTestId('svg-container').length).toEqual(5);
    });

    it('should render facilities titles', function () {
      const { getByText } = render(<RoomFacilitiesListComponent {...roomFacilitiesProps} />);

      const expectedFacilityTitles = mockHotelFacilities
        .filter((facility) => facility.isVisible)
        .map((facility) => facility.name);

      let renderedFacilityTitles: string[] = [];

      renderedFacilityTitles = roomFacilitiesProps.facilities
        .filter((facility) => facility.isVisible)
        .map((facility) => {
          const facilityTitle = getByText(facility.name);
          return facilityTitle.innerHTML;
        });

      expect(renderedFacilityTitles).toEqual(expectedFacilityTitles);
    });

    it('should not render the facility label for mobile devices', function () {
      const { queryByText } = render(
        <RoomFacilitiesListComponent
          {...roomFacilitiesProps}
          isLessThanMd={true}
          isLessThanSm={true}
        />
      );
      roomFacilitiesProps.facilities.forEach((facility) => {
        expect(queryByText(`${facility.name}`)).toBeFalsy();
      });
    });

    it('should display "See all facilities" link if there are more than 5 facilities', function () {
      const { getByText } = render(<RoomFacilitiesListComponent {...roomFacilitiesProps} />);
      expect(getByText('hoteldetails.seeall.facilities')).toBeInTheDocument();
    });

    it('should not display "See all facilities" link if there are less or equal to 5 facilities', function () {
      const facilities =
        mockRoomFacilities.hotelInformation.roomConfiguration.tabItems[0].facilities.slice(0, 2);
      const { queryByText } = render(
        <RoomFacilitiesListComponent
          facilities={facilities}
          isLessThanSm={false}
          isLessThanMd={false}
        />
      );
      expect(queryByText('hoteldetails.seeall.facilities')).toBeFalsy();
    });

    it('should not display "See all facilities" desktop link on mobile devices', function () {
      const { queryByTestId } = render(
        <RoomFacilitiesListComponent
          {...roomFacilitiesProps}
          isLessThanMd={true}
          isLessThanSm={true}
        />
      );
      expect(queryByTestId('facilities-desktop-link')).toBeFalsy();
    });

    it('should display "See all facilities" mobile link for mobile devices', function () {
      const { getByTestId } = render(
        <RoomFacilitiesListComponent
          {...roomFacilitiesProps}
          isLessThanMd={true}
          isLessThanSm={true}
        />
      );
      expect(getByTestId('facilities-mobile-link')).toBeInTheDocument();
    });
  });

  describe('RoomFacilitiesList modal', () => {
    it('should open RoomFacilitiesModal on See all mouseDown', function () {
      const { getByTestId, getAllByTestId } = render(
        <RoomFacilitiesListComponent {...roomFacilitiesProps} />
      );
      fireEvent.mouseDown(getAllByTestId('facilities-desktop-link')[0]);
      expect(getByTestId('facilities-list')).toBeInTheDocument();
    });

    it('should not open RoomFacilitiesModal on facilities icon mouseDown', function () {
      const { queryByTestId, getAllByTestId } = render(
        <RoomFacilitiesListComponent {...roomFacilitiesProps} />
      );
      fireEvent.mouseDown(getAllByTestId('svg-container')[0]);
      expect(queryByTestId('facilities-modal')).toBeFalsy();
    });

    it('should display RoomFacilitiesModal with facility descriptions', function () {
      const { getAllByTestId } = render(<RoomFacilitiesListComponent {...roomFacilitiesProps} />);
      fireEvent.mouseDown(getAllByTestId('facilities-desktop-link')[0]);
      expect(getAllByTestId('facility-title').length).toBeGreaterThan(0);
      expect(getAllByTestId('facility-description').length).toBeGreaterThan(0);
    });

    it('should display RoomFacilitiesModal without facility descriptions', function () {
      const facilities =
        mockRoomFacilities.hotelInformation.roomConfiguration.tabItems[0].facilities.map(
          (roomFacility) => ({
            code: roomFacility.code,
            icon: roomFacility.icon,
            isVisible: roomFacility.isVisible,
            name: roomFacility.name,
            weight: roomFacility.weight,
          })
        );
      const { queryAllByTestId } = render(
        <RoomFacilitiesListComponent
          facilities={facilities}
          isLessThanSm={false}
          isLessThanMd={false}
        />
      );
      fireEvent.mouseDown(queryAllByTestId('facilities-desktop-link')[0]);
      expect(queryAllByTestId('facility-title').length).toBeGreaterThan(0);
      expect(queryAllByTestId('facility-description').length).toEqual(0);
    });

    it('should close RoomFacilitiesModal on close click', function () {
      const { getByTestId, queryByTestId, getAllByTestId } = render(
        <RoomFacilitiesListComponent {...roomFacilitiesProps} />
      );
      fireEvent.mouseDown(getAllByTestId('facilities-desktop-link')[0]);
      fireEvent.click(getByTestId('facilities-modal-ModalCloseButton'));
      expect(queryByTestId('facilities-modal')).toBeFalsy();
    });
  });
});
