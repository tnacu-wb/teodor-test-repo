import '@testing-library/jest-dom';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { FormProvider, useForm } from 'react-hook-form';

import { FrequencyRadios } from './FrequencyRadios';

const mockTranslationsFrequencyRadios: { [key: string]: string } = {
  'company.coMngt.alerts.frequency.options.noAlerts': 'No Alerts',
  'company.coMngt.alerts.frequency.options.immediately': 'Immediately',
  'company.coMngt.alerts.frequency.options.daily': 'Daily',
  'company.coMngt.alerts.frequency.options.weekly': 'Weekly',
  'company.coMngt.alerts.frequency.options.monthly': 'Monthly',
};

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => mockTranslationsFrequencyRadios[key] || key,
  }),
  cn: jest.fn((...classes: string[]) => classes.filter(Boolean).join(' ')),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  ...jest.requireActual('@whitbread-eos/atoms/ui'),
  FormRadioGroup: jest.fn(({ items, selectedValue, onChange, name, 'data-testid': dataTestId }) => (
    <div data-testid={dataTestId}>
      {items.map((item: { id: string; value: string; label: string }) => (
        <div key={item.id}>
          <input
            type="radio"
            id={item.id}
            name={name}
            value={item.value}
            checked={selectedValue === item.value}
            onChange={() => onChange(item.value)}
            data-testid={`radio-${item.value}`}
          />
          <label htmlFor={item.id}>{item.label}</label>
        </div>
      ))}
    </div>
  )),
}));

jest.mock('class-variance-authority', () => ({
  cva: () => {
    return () => '';
  },
}));

const renderWithFormProvider = (ui: React.ReactElement, defaultValues?: Record<string, any>) => {
  const Wrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const methods = useForm({
      defaultValues: defaultValues !== undefined ? defaultValues : { frequency: 'immediately' },
    });
    return <FormProvider {...methods}>{children}</FormProvider>;
  };
  return render(ui, { wrapper: Wrapper });
};

describe('FrequencyRadios', () => {
  const baseDataTestId = 'FrequencyRadios';

  it('renders correctly with initial state ("immediately" selected)', () => {
    renderWithFormProvider(<FrequencyRadios />, { frequency: 'immediately' });

    expect(screen.getByTestId(`${baseDataTestId}-group`)).toBeInTheDocument();

    const immediatelyRadio = screen.getByLabelText('Immediately') as HTMLButtonElement;
    expect(immediatelyRadio).toBeChecked();

    expect(screen.getByLabelText('No Alerts') as HTMLButtonElement).not.toBeChecked();
    expect(screen.getByLabelText('Daily') as HTMLButtonElement).not.toBeChecked();
    expect(screen.getByLabelText('Weekly') as HTMLButtonElement).not.toBeChecked();
    expect(screen.getByLabelText('Monthly') as HTMLButtonElement).not.toBeChecked();
  });

  it('changes selected value when a different radio button is clicked', async () => {
    renderWithFormProvider(<FrequencyRadios />, { frequency: 'immediately' });

    const dailyRadio = screen.getByLabelText('Daily') as HTMLButtonElement;
    const immediatelyRadio = screen.getByLabelText('Immediately') as HTMLButtonElement;

    expect(immediatelyRadio).toBeChecked();
    expect(dailyRadio).not.toBeChecked();

    await act(async () => {
      fireEvent.click(dailyRadio);
    });

    expect(dailyRadio).toBeChecked();
    expect(immediatelyRadio).not.toBeChecked();
  });

  it('renders correct labels based on translation', () => {
    renderWithFormProvider(<FrequencyRadios />, { frequency: 'immediately' });

    expect(screen.getByText('No Alerts')).toBeInTheDocument();
    expect(screen.getByText('Immediately')).toBeInTheDocument();
    expect(screen.getByText('Daily')).toBeInTheDocument();
    expect(screen.getByText('Weekly')).toBeInTheDocument();
    expect(screen.getByText('Monthly')).toBeInTheDocument();
  });
});
