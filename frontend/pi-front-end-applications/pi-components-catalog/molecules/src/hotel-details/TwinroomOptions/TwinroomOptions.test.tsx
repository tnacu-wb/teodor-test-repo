import '@testing-library/jest-dom';
import { ROOM_TYPE } from '@whitbread-eos/api';
import React from 'react';

import { fireEvent, render, userEvent } from '../../utils/test-utils';
import TwinroomOptionsComponent from './TwinroomOptions.component';
import TwinroomOptionsContainer from './TwinroomOptions.container';

const mockScreenSize = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useScreenSize: () => mockScreenSize(),
}));

const twinroomContainerProps = {
  data: [
    {
      adults: 2,
      children: 0,
      cotRequested: false,
      roomType: 'TWIN',
      rooms: [
        {
          cotAvailable: false,
          pmsRoomType: 'TWINRM',
          roomClass: 'ST',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            packageCode: 'HSATWN',
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
          specialRequests: ['TW2S'],
        },
        {
          cotAvailable: false,
          pmsRoomType: 'FMTRPL',
          roomClass: 'ST',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            packageCode: null,
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
          specialRequests: ['TWDS'],
        },
        {
          cotAvailable: false,
          pmsRoomType: 'FMQUAD',
          roomClass: 'ST',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            packageCode: null,
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
          specialRequests: ['TWIN'],
        },
      ],
    },
    {
      adults: 2,
      children: 1,
      cotRequested: false,
      roomType: 'FAM',
      rooms: [
        {
          cotAvailable: false,
          pmsRoomType: ROOM_TYPE.FAMILY_TRIPLE,
          roomClass: 'ST',
          silentSubstitution: true,
          roomPriceBreakdown: {
            currencyCode: 'GBP',
            totalNetAmount: 1998,
            packageCode: null,
            dailyPrices: [
              {
                date: '2022-12-09',
                netPrice: 999,
              },
              {
                date: '2022-12-10',
                netPrice: 999,
              },
            ],
          },
          specialRequests: ['TRIP'],
        },
      ],
    },
  ],
  onTwinroomSelection: jest.fn(),
  twinroomSelections: [],
  twinRoomPrices: [
    {
      currencyCode: 'GBP',
      price: 1004,
    },
    {
      currencyCode: 'GBP',
      price: 999,
    },
  ],
};

const twinroomComponentProps = {
  rooms: [
    {
      adults: 2,
      twinroomTypes: [ROOM_TYPE.TWIN_DOUBLE_SOFA],
      children: 0,
      roomType: 'Twin room',
    },
    { adults: 2, twinroomTypes: undefined, children: 1, roomType: 'Family room' },
  ],
  onTwinroomSelection: jest.fn(),
  twinroomSelections: [] as string[],
  twinRoomPrices: [
    {
      currencyCode: 'GBP',
      price: 1004,
    },
    {
      currencyCode: 'GBP',
      price: 999,
    },
  ],
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
}));

const mockCustomLocale = jest.fn();

describe('twinroomOptions', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render twinroomOptions', () => {
    const { getByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getByTestId('twin-title')).toBeInTheDocument();
  });

  it('should render nothing if no data is passed', () => {
    const { queryByTestId } = render(
      <TwinroomOptionsContainer {...twinroomContainerProps} data={undefined} />
    );
    expect(queryByTestId('twin-title')).toBeNull();
  });

  it('should render choose twin title', () => {
    const { getByText } = render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(getByText('seo.chooseTwinRoom.title')).toBeInTheDocument();
  });

  it('should render choose twin title', async () => {
    mockScreenSize.mockReturnValue({
      isLessThanXs: true,
    });

    const { getByText, getAllByText, getAllByTestId } = render(
      <TwinroomOptionsComponent {...twinroomComponentProps} />
    );
    expect(getByText('twinroom.improvedTwin.description')).toBeInTheDocument();
    expect(getByText('twinroom.standardTwin.description')).toBeInTheDocument();

    const showDescription1 = getAllByTestId('improvedTwin-description-toggle')[0];
    const showDescription2 = getAllByTestId('standardTwin-description-toggle')[0];

    await userEvent.click(showDescription1);
    await userEvent.click(showDescription2);

    expect(getAllByText('hoteldetails.hide')[0]).toBeInTheDocument();
  });

  it('should render choose twin title', () => {
    const { getByText } = render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(getByText('seo.chooseTwinRoom.title')).toBeInTheDocument();
  });

  it('should render 2 room titles s', () => {
    const { getAllByRole } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByRole('heading', { name: /booking.hotel.summary.room/i }).length).toEqual(2);
  });

  it('should call onTwinroomSelection with "Twin - double bed and sofa" on component mount if twinroomType is - Twin - double bed and sofa', () => {
    render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(twinroomComponentProps.onTwinroomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.TWIN_DOUBLE_SOFA
    );
  });

  it('should call onTwinroomSelection with "Twin - two single beds" on component mount if twinroomType is - Twin - two single beds', () => {
    twinroomComponentProps.rooms[0].twinroomTypes = [ROOM_TYPE.TWIN_TWO_BEDS];
    render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(twinroomComponentProps.onTwinroomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.TWIN_TWO_BEDS
    );
  });

  it('should render 2 Adult for the first room', () => {
    const { getByText } = render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(getByText('2 dashboard.bookings.adults')).toBeInTheDocument();
  });

  it('should render 2 Adults, 1 Child for the second room', () => {
    const { getByText } = render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(
      getByText('2 dashboard.bookings.adults, 1 dashboard.bookings.child')
    ).toBeInTheDocument();
  });

  it('should render a Twin room heading for Twin room', () => {
    const { getByRole } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getByRole('heading', { name: /pihotelinfo.chooseTwinRoom.title/i })).toBeInTheDocument();
  });

  it('should render a Famiy room heading for Family room', () => {
    const { getByRole } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getByRole('heading', { name: /pihotelinfo.familyTitle/i })).toBeInTheDocument();
  });

  it('should render Double room heading for Double room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.DOUBLE;
    twinroomContainerProps.data[1].roomType = 'DB';

    twinroomComponentProps.rooms[1].roomType = 'Double room';
    twinroomComponentProps.rooms[1].children = 0;
    const { getByRole } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getByRole('heading', { name: /dashboard.bookings.doubleRoom/i })).toBeInTheDocument();
  });

  it('should render Single room heading for Single room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.SINGLE;
    twinroomContainerProps.data[1].roomType = 'SB';

    twinroomComponentProps.rooms[1].roomType = 'Single room';
    twinroomComponentProps.rooms[1].children = 0;
    const { getByRole } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);

    expect(getByRole('heading', { name: /dashboard.bookings.singleRoom/i })).toBeInTheDocument();
  });

  it('should render no twin selection required message for Double type room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.DOUBLE;

    const { getAllByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByTestId('twin-no-selection-required').length).toEqual(1);
  });

  it('should render no twin selection required message for Family type room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.FAMILY_TRIPLE;
    const { getAllByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByTestId('twin-no-selection-required').length).toEqual(1);
  });

  it('should render no twin selection required message for SINGLE room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.SINGLE;
    const { getAllByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByTestId('twin-no-selection-required').length).toEqual(1);
  });

  it('should render no twin selection required message for PPLDBL room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.PREMIER_PLUS;
    const { getAllByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByTestId('twin-no-selection-required').length).toEqual(1);
  });

  it('should render no twin selection required message for EXTDBL room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.STANDARD_EXTRA;
    const { getAllByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByTestId('twin-no-selection-required').length).toEqual(1);
  });

  it('should render no twin selection required message for BIGWIN room', () => {
    twinroomContainerProps.data[1].rooms[0].pmsRoomType = ROOM_TYPE.STANDARD_BIGGER;
    const { getAllByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByTestId('twin-no-selection-required').length).toEqual(1);
  });

  it('should render no twin selection required message for other room type room', () => {
    twinroomContainerProps.data[1].roomType = 'xx';
    const { getAllByTestId } = render(<TwinroomOptionsContainer {...twinroomContainerProps} />);
    expect(getAllByTestId('twin-no-selection-required').length).toEqual(1);
  });

  it('should render 2 radio buttons for the two twin room options', () => {
    const { getAllByRole } = render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(getAllByRole('radio').length).toEqual(2);
  });

  it('should render a checked twin - two single beds and an unchecked twin - double and sofa bed radio buttons', () => {
    twinroomComponentProps.twinroomSelections = [ROOM_TYPE.TWIN_TWO_BEDS];
    const { getAllByRole } = render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[1]).not.toBeChecked();
  });

  it('should render twin - two single beds radio button texts', () => {
    const { getByTestId, getByText } = render(
      <TwinroomOptionsComponent {...twinroomComponentProps} />
    );
    expect(getByTestId('improved-twin')).toBeInTheDocument();
    expect(getByText('twinroom.improvedTwin.description')).toBeInTheDocument();
  });

  it('should render twin - double and sofa bed radio button texts', () => {
    const { getByTestId, getByText } = render(
      <TwinroomOptionsComponent {...twinroomComponentProps} />
    );
    expect(getByTestId('double-sofa-twin')).toBeInTheDocument();
    expect(getByText('twinroom.standardTwin.description')).toBeInTheDocument();
  });

  it('should render call onTwinroomSelection on twin double bed and sofa radio button click', () => {
    const { getAllByRole } = render(<TwinroomOptionsComponent {...twinroomComponentProps} />);
    const doubleSofaRoomRadioButton = getAllByRole('radio')[1];
    fireEvent.click(doubleSofaRoomRadioButton);
    expect(twinroomComponentProps.onTwinroomSelection).toHaveBeenCalledWith(
      0,
      ROOM_TYPE.TWIN_DOUBLE_SOFA
    );
  });
});
