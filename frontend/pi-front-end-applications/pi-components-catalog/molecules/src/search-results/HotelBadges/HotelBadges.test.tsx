import '@testing-library/jest-dom';

import {
  BIGGER_ROOM_CODE,
  NEW_HOTEL_MESSAGING_FLAG_TEXT,
  PREMIER_EXTRA_CODE,
  PREMIER_PLUS_FACILITY_CODE,
  MLOS,
} from '../../utils/constants';
import { render } from '../../utils/test-utils';
import HotelBadges, { getColor } from './HotelBadges.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: () => ({
    hotelFacilities: mockFacilities,
    messagingFlag: { color: '', description: '', text: 'Open hotel' },
  }),
}));

const mockFacilities = [
  {
    code: PREMIER_PLUS_FACILITY_CODE,
    description: 'Premier Plus room',
    icon: '',
    isVisible: true,
    name: 'Premier Plus room',
    weight: 1,
  },
  {
    code: PREMIER_PLUS_FACILITY_CODE,
    description: 'Premier Plus room',
    icon: '',
    isVisible: true,
    name: 'Premier Plus room',
    weight: 3,
  },
  {
    code: PREMIER_PLUS_FACILITY_CODE,
    description: 'Premier Plus room',
    icon: '',
    isVisible: true,
    name: 'Premier Plus room',
    weight: 2,
  },
];

const baseProps = {
  labels: {
    openingSoon: 'Open soon',
    premierPlus: 'Premier Plus',
    mlos: 'MLOS',
  },
  testId: 'SRP-hotel-badges',
};

const basePropsBadges = {
  ...baseProps,
  hotelOpeningDate: '2022-09-05T00:00:00.000+01:00',
  isHotelOpeningSoon: true,
  hotelFacilities: mockFacilities,
  messagingFlag: { color: '', text: 'Open hotel' },
};

describe('SRP - HotelBadges', () => {
  it('should render Open Soon badge', () => {
    const { getByText } = render(
      <HotelBadges
        {...baseProps}
        hotelOpeningDate={basePropsBadges.hotelOpeningDate}
        isHotelOpeningSoon={basePropsBadges.isHotelOpeningSoon}
      />
    );
    expect(getByText(baseProps.labels.openingSoon)).toBeInTheDocument();
  });
  it('should render Premier Plus badge', () => {
    const { getByText } = render(
      <HotelBadges {...baseProps} hotelFacilities={basePropsBadges.hotelFacilities} />
    );
    expect(getByText(baseProps.labels.premierPlus)).toBeInTheDocument();
  });
  it('should render MLOS badge', () => {
    const { getByText } = render(<HotelBadges {...baseProps} hasMlosRestriction={true} />);
    expect(getByText(baseProps.labels.mlos)).toBeInTheDocument();
  });
  it('should render Messaging flags badge', () => {
    const { getByText } = render(
      <HotelBadges {...baseProps} messagingFlag={basePropsBadges.messagingFlag} />
    );
    expect(getByText(basePropsBadges.messagingFlag.text)).toBeInTheDocument();
  });
  it('should render multiple hotel badges', () => {
    const { getAllByRole } = render(<HotelBadges {...basePropsBadges} />);
    expect(getAllByRole('listitem').length).toBe(3);
  });
  it('should render multiple hotel badges as column display', () => {
    const { queryAllByRole, getByTestId } = render(
      <HotelBadges {...basePropsBadges} isColumnDisplay={true} />
    );
    expect(queryAllByRole('listitem').length).toBe(0);
    expect(getByTestId('SRP-hotel-badges')).toBeInTheDocument();
    expect(getByTestId('SRP-hotel-badges').children).toHaveLength(3);
  });
  it('should not render badges when there are no labels', () => {
    const { getByTestId, getAllByRole, queryAllByText } = render(
      <HotelBadges {...basePropsBadges} labels={undefined} />
    );
    expect(getByTestId('SRP-hotel-badges')).toBeInTheDocument();
    expect(getAllByRole('listitem').length).toBe(1);
    expect(queryAllByText(basePropsBadges.labels.openingSoon)).toHaveLength(0);
    expect(queryAllByText(basePropsBadges.labels.premierPlus)).toHaveLength(0);
    expect(queryAllByText(basePropsBadges.messagingFlag.text)).toHaveLength(1);
  });

  it('should return the correct color based on facility code', () => {
    expect(getColor(BIGGER_ROOM_CODE)).toBe('lightPurple');
    expect(getColor(PREMIER_PLUS_FACILITY_CODE)).toBe('primary');
    expect(getColor(NEW_HOTEL_MESSAGING_FLAG_TEXT)).toBe('maroon');
    expect(getColor(PREMIER_EXTRA_CODE)).toBe('blue');
    expect(getColor(MLOS)).toBe('green');
    expect(getColor('default')).toBe('darkPink');
  });
});

it('should render nothing if the hotel has no badges', () => {
  const { queryByTestId } = render(<HotelBadges {...baseProps} />);
  expect(queryByTestId('SRP-hotel-badges')).not.toBeInTheDocument();
});
