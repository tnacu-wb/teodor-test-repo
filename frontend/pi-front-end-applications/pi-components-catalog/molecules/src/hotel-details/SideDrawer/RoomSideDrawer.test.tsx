import '@testing-library/jest-dom';
import { TabItem } from '@whitbread-eos/api';
import type { ReactElement, ReactNode, RefObject } from 'react';

import { fireEvent, render } from '../../utils/test-utils';
import { RoomSideDrawer } from './RoomSideDrawer.component';

type MockHotelRoomContentProps = {
  tabItems: TabItem[];
  isLessThanLg: boolean;
  isLessThanMd: boolean;
  isLessThanSm: boolean;
  containerRef: RefObject<HTMLDivElement>;
};

const mockUseScreenSize = jest.fn();
const mockRenderSanitizedHtml = jest.fn((value: string) => value);
const mockTranslation = jest.fn((key: string) => key);
const mockHotelRoomContent = jest.fn<ReactElement, [MockHotelRoomContentProps]>(() => (
  <div data-testid="Hotel-Room-Content" />
));

jest.mock('@whitbread-eos/atoms', () => ({
  Dismiss: () => <svg data-testid="Dismiss-Icon" />,
  Icon: ({ svg }: { svg: ReactNode }) => <span data-testid="Room-Drawer-Icon">{svg}</span>,
  Info: () => <svg data-testid="Info-Icon" />,
  Notification: ({
    description,
    prefixDataTestId,
  }: {
    description: ReactNode;
    prefixDataTestId?: string;
  }) => (
    <div data-testid={prefixDataTestId ? `${prefixDataTestId}-Notification` : 'Notification'}>
      {description}
    </div>
  ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useScreenSize: () => mockUseScreenSize(),
  renderSanitizedHtml: (value: string) => mockRenderSanitizedHtml(value),
}));

jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: mockTranslation,
  }),
}));

jest.mock('../HotelRoomContent', () => ({
  HotelRoomContent: (props: MockHotelRoomContentProps) => mockHotelRoomContent(props),
}));

const mockRoomStaticDetails: TabItem = {
  roomName: 'Premier Plus Double',
  roomDescription: 'A room description',
  roomType: 'Premier Plus',
};

const mockProps = {
  visible: true,
  onClose: jest.fn(),
  title: 'Room details',
  brand: 'pi',
};

describe('RoomSideDrawer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseScreenSize.mockReturnValue({
      isLessThanSm: false,
      isLessThanMd: false,
      isLessThanLg: false,
    });
  });

  it('should not render RoomSideDrawer when not visible', () => {
    const { queryByTestId } = render(<RoomSideDrawer {...mockProps} visible={false} />);

    expect(queryByTestId('Room-Drawer-Container')).not.toBeInTheDocument();
    expect(queryByTestId('Side-Drawer-Backdrop')).not.toBeInTheDocument();
    expect(queryByTestId('Hotel-Room-Content')).not.toBeInTheDocument();
  });

  it('should render the title, optional Premier Plus badge and expected child props', () => {
    mockUseScreenSize.mockReturnValue({
      isLessThanSm: true,
      isLessThanMd: false,
      isLessThanLg: true,
    });

    const { getByTestId, getByText } = render(
      <RoomSideDrawer {...mockProps} isPremierPlus roomStaticDetails={mockRoomStaticDetails} />
    );

    expect(getByTestId('Room-Drawer-Title')).toBeInTheDocument();
    expect(getByText('Room details')).toBeInTheDocument();
    expect(getByText('Premier Plus')).toBeInTheDocument();
    expect(getByTestId('Hotel-Room-Content')).toBeInTheDocument();

    const hotelRoomContentProps = mockHotelRoomContent.mock.calls[0][0];

    expect(hotelRoomContentProps.tabItems).toEqual([mockRoomStaticDetails]);
    expect(hotelRoomContentProps.isLessThanSm).toBe(true);
    expect(hotelRoomContentProps.isLessThanMd).toBe(false);
    expect(hotelRoomContentProps.isLessThanLg).toBe(true);
    expect(hotelRoomContentProps.containerRef.current).toBeInstanceOf(HTMLDivElement);
  });

  it('should call onClose from the backdrop and dismiss button and pass empty tabItems without roomStaticDetails', () => {
    const { getByTestId } = render(<RoomSideDrawer {...mockProps} />);

    fireEvent.click(getByTestId('Room-Drawer-Backdrop'));
    fireEvent.click(getByTestId('Dismiss-Room-Drawer'));

    expect(mockProps.onClose).toHaveBeenCalledTimes(2);
    expect(mockHotelRoomContent.mock.calls[0][0].tabItems).toEqual([]);
  });
});
