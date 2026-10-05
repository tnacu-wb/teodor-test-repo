import '@testing-library/jest-dom';

import { fireEvent, render, screen } from '../../../utils/test-utils';
import Tabs from './Tabs.component';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

const onClickMock = jest.fn();

const SessionComponent = () => {
  const props = {
    baseDataTestId: 'session',
    name: 'time',
    tabLabel: 'Which session',
    tapLabel: 'What Time',
    setValue: jest.fn(),
    getValues: jest.fn(),
    tabData: [
      { availablility: true, name: 'Breakfast' },
      { availablility: true, name: 'Lunch' },
      { availablility: true, name: 'Dinner' },
    ],
    tabPanelData: {
      dinner: [
        {
          time: '19:00',
          available: true,
          totalCapacity: 30,
          remainingCapacity: 30,
          canEnquire: true,
          closed: false,
        },
        {
          time: '20:00',
          available: true,
          totalCapacity: 30,
          remainingCapacity: 30,
          canEnquire: true,
          closed: false,
        },
      ],
      lunch: [
        {
          time: '12:00',
          available: false,
          totalCapacity: 20,
          remainingCapacity: 20,
          canEnquire: true,
          closed: false,
        },
        {
          time: '13:00',
          available: false,
          totalCapacity: 20,
          remainingCapacity: 20,
          canEnquire: true,
          closed: false,
        },
        {
          time: '13:15',
          available: true,
          totalCapacity: 20,
          remainingCapacity: 20,
          canEnquire: true,
          closed: false,
        },
        {
          time: '13:00',
          available: false,
          totalCapacity: 20,
          remainingCapacity: 20,
          canEnquire: true,
          closed: false,
        },
      ],
      breakFast: [
        {
          time: '06:30',
          available: true,
          totalCapacity: 28,
          remainingCapacity: 28,
          canEnquire: true,
          closed: false,
        },
      ],
    },
    onClick: onClickMock,
  };
  return <Tabs {...props} />;
};

const SessionComponentWithError = () => {
  const props = {
    error: 'error',
    baseDataTestId: 'session',
    name: 'time',
    tabLabel: 'Which session',
    tapLabel: 'What Time',
    setValue: jest.fn(),
    getValues: jest.fn(),
    tabData: [
      { availablility: true, name: 'Breakfast' },
      { availablility: true, name: 'Lunch' },
      { availablility: true, name: 'Dinner' },
    ],
    tabPanelData: {
      dinner: [
        {
          time: '19:00',
          available: true,
          totalCapacity: 30,
          remainingCapacity: 30,
          canEnquire: true,
          closed: false,
        },
      ],
      lunch: [
        {
          time: '12:00',
          available: true,
          totalCapacity: 20,
          remainingCapacity: 20,
          canEnquire: true,
          closed: false,
        },
      ],
      breakFast: [
        {
          time: '06:30',
          available: true,
          totalCapacity: 28,
          remainingCapacity: 28,
          canEnquire: true,
          closed: false,
        },
      ],
    },
    onClick: onClickMock,
  };
  return <Tabs {...props} />;
};

describe('Session Form Component', () => {
  it('should render the component', () => {
    render(<SessionComponent />);
    expect(screen.getByText('Lunch')).toBeInTheDocument();
  });
  it('should call handleTabsChange function when a tab is clicked', () => {
    render(<SessionComponent />);
    fireEvent.click(screen.getByText('Dinner'));
    fireEvent.click(screen.getByText('19:00'));
    const selectedTimeButton = screen.getByText('19:00');
    const computedStyles = window.getComputedStyle(selectedTimeButton);
    expect(computedStyles.color).toBe('white');
  });
  it('should call handleSession function when any time selected', () => {
    render(<SessionComponent />);
    fireEvent.click(screen.getByText('Lunch'));
    fireEvent.click(screen.getByText('12:00'));
    const selectedTimeButton = screen.getByText('12:00');
    const computedStyles = window.getComputedStyle(selectedTimeButton);
    expect(computedStyles.background).toBe('transparent');
  });
  it('should session label color null when non of the session selected', () => {
    render(<SessionComponentWithError />);
    fireEvent.click(screen.getByText('Lunch'));
    fireEvent.click(screen.getByText('12:00'));
    const sessionLabel = screen.getByTestId('session-time-session-label');
    const computedStyles = window.getComputedStyle(sessionLabel);
    expect(computedStyles.color).toBe('');
  });
});
