import { render, screen } from '@testing-library/react';

import SilentSubstitutionNotificationWrapper from './SilentSubstitutionNotificationWrapper.component';

jest.mock('../SilentSubstitutionNotification/SilentSubstitutionNotification.component', () => ({
  __esModule: true,
  default: (props: any) => (
    <div data-testid="SilentSubstitutionNotification">
      brand:{props.brand}; substitutedRooms:{props.substitutedRooms.join(',')};
      roomTypeInformationResponse:{JSON.stringify(props.roomTypeInformationResponse)}
    </div>
  ),
}));

const hiRoomTypeInfoResponseMock = { foo: 'bar' } as any;

describe('SilentSubstitutionNotificationWrapper', () => {
  it('does not render notification if no substituted rooms', () => {
    render(
      <SilentSubstitutionNotificationWrapper
        brand="Prem"
        roomTypeInformationResponse={hiRoomTypeInfoResponseMock}
        currentClassRoomTypes={[
          { roomLabelCode: 'DLX', silentSubstitution: true } as any,
          { roomLabelCode: 'SUP', silentSubstitution: undefined } as any,
        ]}
      />
    );
    expect(screen.queryByTestId('SilentSubstitutionNotification')).not.toBeInTheDocument();
  });

  it('renders notification for rooms with silentSubstitution === false', () => {
    render(
      <SilentSubstitutionNotificationWrapper
        brand="Hub"
        roomTypeInformationResponse={hiRoomTypeInfoResponseMock}
        currentClassRoomTypes={[
          { roomLabelCode: 'SRM', silentSubstitution: false } as any,
          { roomLabelCode: 'DLL', silentSubstitution: false } as any,
        ]}
      />
    );

    const notification = screen.getByTestId('SilentSubstitutionNotification');
    expect(notification).toHaveTextContent('brand:Hub');
    expect(notification).toHaveTextContent('substitutedRooms:SRM,DLL');
    expect(notification).toHaveTextContent('roomTypeInformationResponse:{"foo":"bar"}');
  });

  it('renders notification only for unique roomLabelCode', () => {
    render(
      <SilentSubstitutionNotificationWrapper
        brand="Hub"
        roomTypeInformationResponse={hiRoomTypeInfoResponseMock}
        currentClassRoomTypes={[
          { roomLabelCode: 'SRM', silentSubstitution: false } as any,
          { roomLabelCode: 'SRM', silentSubstitution: false } as any,
          { roomLabelCode: 'DLL', silentSubstitution: false } as any,
        ]}
      />
    );
    const notification = screen.getByTestId('SilentSubstitutionNotification');
    // Should not list 'SRM' twice
    expect(notification).toHaveTextContent('substitutedRooms:SRM,DLL');
  });

  it('does not throw if currentClassRoomTypes is undefined or empty', () => {
    render(
      <SilentSubstitutionNotificationWrapper
        brand="Test"
        roomTypeInformationResponse={hiRoomTypeInfoResponseMock}
        currentClassRoomTypes={[]}
      />
    );
    expect(screen.queryByTestId('SilentSubstitutionNotification')).not.toBeInTheDocument();
  });

  it('renders notification only for non-silentSubstitution', () => {
    render(
      <SilentSubstitutionNotificationWrapper
        brand="Test"
        roomTypeInformationResponse={hiRoomTypeInfoResponseMock}
        currentClassRoomTypes={[
          { roomLabelCode: 'AAA', silentSubstitution: false } as any,
          { roomLabelCode: 'BBB', silentSubstitution: true } as any,
          { roomLabelCode: 'CCC' } as any,
        ]}
      />
    );
    const notification = screen.getByTestId('SilentSubstitutionNotification');
    expect(notification).toHaveTextContent('substitutedRooms:AAA');
  });
});
