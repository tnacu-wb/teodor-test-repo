import '@testing-library/jest-dom';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { FormProvider, useForm } from 'react-hook-form';

import { AlertToggles } from './AlertToggles';

const mockTranslationsAlertToggles: { [key: string]: string } = {
  'company.coMngt.alerts.booking.alert.sameDay.label': 'Same Day Booking Alert',
  'company.coMngt.alerts.booking.alert.weekendNight.label': 'Weekend Arrival Alert',
  'company.coMngt.alerts.booking.alert.partWeekend.label': 'Part Weekend Booking Alert',
};

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => mockTranslationsAlertToggles[key] || key,
  }),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Switch: jest.fn(({ id, checked, onCheckedChange, 'data-testid': dataTestId, className }) => (
    <input
      type="checkbox"
      id={id}
      checked={checked}
      onChange={onCheckedChange}
      data-testid={dataTestId}
      className={className}
    />
  )),
  Label: jest.fn(({ children, className, ...props }) => (
    <label className={className} {...props}>
      {children}
    </label>
  )),
}));

const defaultInitialAlertToggles = {
  sameDayBooking: true,
  weekendArrival: true,
  partWeekendBooking: true,
};

const renderWithFormProvider = (
  ui: React.ReactElement,
  defaultValues: { alertToggles?: Record<string, boolean> } = {
    alertToggles: defaultInitialAlertToggles,
  }
) => {
  const Wrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const methods = useForm({ defaultValues });
    return <FormProvider {...methods}>{children}</FormProvider>;
  };
  return render(ui, { wrapper: Wrapper });
};

describe('AlertToggles', () => {
  const baseDataTestId = 'AlertToggles';

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders correctly with initial state (all toggles checked)', () => {
    renderWithFormProvider(<AlertToggles />);

    const sameDaySwitch = screen.getByTestId(`${baseDataTestId}-switch-sameDayBooking`);
    const weekendArrivalSwitch = screen.getByTestId(`${baseDataTestId}-switch-weekendArrival`);
    const partWeekendSwitch = screen.getByTestId(`${baseDataTestId}-switch-partWeekendBooking`);

    expect(sameDaySwitch).toBeInTheDocument();
    expect(sameDaySwitch).toBeChecked();
    expect(weekendArrivalSwitch).toBeInTheDocument();
    expect(weekendArrivalSwitch).toBeChecked();
    expect(partWeekendSwitch).toBeInTheDocument();
    expect(partWeekendSwitch).toBeChecked();

    expect(screen.getByText('Same Day Booking Alert')).toBeInTheDocument();
    expect(screen.getByText('Weekend Arrival Alert')).toBeInTheDocument();
    expect(screen.getByText('Part Weekend Booking Alert')).toBeInTheDocument();
  });

  it('toggles state when a switch (checkbox mock) is clicked', async () => {
    renderWithFormProvider(<AlertToggles />);

    const sameDaySwitch = screen.getByTestId(
      `${baseDataTestId}-switch-sameDayBooking`
    ) as HTMLInputElement;

    expect(sameDaySwitch).toBeChecked();

    await act(async () => {
      fireEvent.click(sameDaySwitch);
    });
    expect(sameDaySwitch).not.toBeChecked();

    await act(async () => {
      fireEvent.click(sameDaySwitch);
    });
    expect(sameDaySwitch).toBeChecked();
  });

  it('renders correct labels based on translation', () => {
    renderWithFormProvider(<AlertToggles />);

    expect(screen.getByText('Same Day Booking Alert')).toBeInTheDocument();
    expect(screen.getByText('Weekend Arrival Alert')).toBeInTheDocument();
    expect(screen.getByText('Part Weekend Booking Alert')).toBeInTheDocument();
  });

  it('renders with specific initial toggles from form context', () => {
    const specificToggles = {
      sameDayBooking: false,
      weekendArrival: true,
      partWeekendBooking: false,
    };
    renderWithFormProvider(<AlertToggles />, { alertToggles: specificToggles });

    expect(screen.getByTestId(`${baseDataTestId}-switch-sameDayBooking`)).not.toBeChecked();
    expect(screen.getByTestId(`${baseDataTestId}-switch-weekendArrival`)).toBeChecked();
    expect(screen.getByTestId(`${baseDataTestId}-switch-partWeekendBooking`)).not.toBeChecked();
  });
});
