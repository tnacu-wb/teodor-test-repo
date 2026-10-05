import '@testing-library/jest-dom/extend-expect';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { FormProvider, useForm } from 'react-hook-form';

import { AlertRecipients } from './AlertRecipients';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  formatIBAssetsUrl: (url: string) => url,
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  FormPeoplePicker: jest.fn(({ handleEmployeeChange, disabled, placeholder, id }) => (
    <div>
      <input
        data-testid={id}
        placeholder={placeholder}
        disabled={disabled}
        onChange={(e) => {
          if (e.target.value) {
            handleEmployeeChange({
              value: e.target.value,
              label: `${e.target.value}_name (${e.target.value}_email@example.com)`,
              employeeData: { emailAddress: `${e.target.value}_email@example.com` },
            });
          }
        }}
      />
    </div>
  )),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ src, alt }: { src: string; alt: string }) => <img src={src} alt={alt} />,
}));

const mockIcons = {
  'icon.notification.error': 'error-icon.svg',
  'icon.notification.dismiss': 'dismiss-icon.svg',
};

const mockCompanyId = 'test-company-id';

const Wrapper = ({
  children,
  defaultValues = {},
}: {
  children: React.ReactNode;
  defaultValues?: any;
}) => {
  const methods = useForm({ defaultValues });
  return <FormProvider {...methods}>{children}</FormProvider>;
};

describe('AlertRecipients', () => {
  it('renders correctly and allows adding a recipient', async () => {
    render(
      <Wrapper defaultValues={{ recipients: [] }}>
        <AlertRecipients icons={mockIcons} companyId={mockCompanyId} />
      </Wrapper>
    );

    const peoplePickerInput = screen.getByTestId('AlertRecipients-people-picker');
    expect(peoplePickerInput).toBeInTheDocument();

    fireEvent.change(peoplePickerInput, { target: { value: 'employee1' } });

    await waitFor(() => {
      expect(screen.getByTestId('AlertRecipients-badge-employee1')).toBeInTheDocument();
      expect(screen.getByText('employee1_email@example.com')).toBeInTheDocument();
    });
  });

  it('allows removing an existing recipient', async () => {
    const initialRecipients = [{ id: 'emp1', name: 'Emp One', email: 'emp1@example.com' }];
    render(
      <Wrapper defaultValues={{ recipients: initialRecipients }}>
        <AlertRecipients icons={mockIcons} companyId={mockCompanyId} />
      </Wrapper>
    );

    expect(screen.getByTestId('AlertRecipients-badge-emp1')).toBeInTheDocument();
    expect(screen.getByText('emp1@example.com')).toBeInTheDocument();

    const removeButton = screen.getByTestId('AlertRecipients-remove-emp1');
    fireEvent.click(removeButton);

    await waitFor(() => {
      expect(screen.queryByTestId('AlertRecipients-badge-emp1')).not.toBeInTheDocument();
      expect(screen.queryByText('emp1@example.com')).not.toBeInTheDocument();
    });
  });

  it('disables adding more recipients when the maximum (10) is reached and prevents adding more', async () => {
    const initialRecipients = Array.from({ length: 9 }, (_, i) => ({
      id: `emp${i}`,
      name: `Employee ${i}`,
      email: `emp${i}@example.com`,
    }));

    render(
      <Wrapper defaultValues={{ recipients: initialRecipients }}>
        <AlertRecipients icons={mockIcons} companyId={mockCompanyId} />
      </Wrapper>
    );

    const peoplePickerInput = screen.getByTestId(
      'AlertRecipients-people-picker'
    ) as HTMLInputElement;
    expect(peoplePickerInput.disabled).toBe(false);

    fireEvent.change(peoplePickerInput, { target: { value: 'emp9' } });
    await waitFor(() => {
      expect(screen.getByTestId('AlertRecipients-badge-emp9')).toBeInTheDocument();
    });
    expect(screen.getAllByTestId(/AlertRecipients-badge-/)).toHaveLength(10);

    await waitFor(() => {
      expect(peoplePickerInput.disabled).toBe(true);
    });

    expect(screen.getAllByTestId(/AlertRecipients-badge-/)).toHaveLength(10);
    expect(screen.queryByTestId('AlertRecipients-badge-emp10')).not.toBeInTheDocument();
  });
});
