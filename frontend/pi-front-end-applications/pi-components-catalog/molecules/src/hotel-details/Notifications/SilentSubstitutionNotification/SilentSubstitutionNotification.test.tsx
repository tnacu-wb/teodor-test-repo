import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import SilentSubstitutionNotification from './SilentSubstitutionNotification.component';

const mockedRoomTypeInformationResponse = {
  isLoadingRoomTypeInformation: false,
  isErrorRoomTypeInformation: false,
  dataRoomTypeInformation: {
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: ['DBLWIN'],
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription: 'Compact rooms, designed around you.',
          roomImage:
            '/content/dam/pi/websites/desktop/new-hotel-details-content/Hub/Hub-Standard-Room.jpg',
          groupId: 'double',
        },
        {
          roomTypeCode: ['BIGWIN'],
          roomCategory: 'Bigger Room',
          roomLabel: 'Bigger Room',
          roomDescription:
            'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
          roomImage: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
          groupId: 'double',
        },
        {
          roomTypeCode: ['ACCWIN'],
          roomCategory: 'Accessible',
          roomLabel: 'Accessible room',
          roomDescription:
            'A larger room with a 480mm-high Hypnos bed, level access en-suite shower room with folding seat & wide-entry doors',
          roomImage:
            '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/hub-accessible-bedroom.jpg',
          groupId: 'accessible',
        },
        {
          roomTypeCode: ['GB'],
          roomCategory: 'Bigger Room',
          roomLabel: 'Bigger room',
          roomDescription:
            'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize bed.',
          roomImage: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
          groupId: '',
        },
        {
          roomTypeCode: ['DB'],
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription: 'Compact rooms, designed around you.',
          roomImage:
            '/content/dam/pi/websites/desktop/new-hotel-details-content/Hub/Hub-Standard-Room.jpg',
          groupId: '',
        },
      ],
    },
  },
  errorRoomTypeInformation: null,
};
const mockedProps = {
  brand: 'pi',
  substitutedRooms: ['BIGWIN', 'DBLWIN'],
  roomTypeInformationResponse: mockedRoomTypeInformationResponse,
};

const substitutedRoomLabels = 'Bigger Room, Standard Room';

describe('SilentSubstitutionNotification', () => {
  it('should render notification text when substitutedRooms is present', async () => {
    const { getByText, getByTestId } = render(<SilentSubstitutionNotification {...mockedProps} />);
    expect(getByTestId('substitution-notification')).toBeInTheDocument();
    expect(
      getByText(
        `searchresults.list.restrictions.available searchresults.list.restrictions.offer ${substitutedRoomLabels}`
      )
    ).toBeInTheDocument();
  });

  it('should render notification text for HUB brand', async () => {
    const { getByText, getByTestId } = render(
      <SilentSubstitutionNotification {...mockedProps} brand={'hub'} />
    );
    expect(getByTestId('substitution-notification')).toBeInTheDocument();
    expect(
      getByText(
        `searchresults.list.restrictions.hub.available searchresults.list.restrictions.offer ${substitutedRoomLabels}`
      )
    ).toBeInTheDocument();
  });

  it('should not render notification text substitutedRooms is empty', async () => {
    const { queryByTestId } = render(
      <SilentSubstitutionNotification {...mockedProps} substitutedRooms={[]} />
    );
    expect(queryByTestId('substitution-notification')).not.toBeInTheDocument();
  });
});
