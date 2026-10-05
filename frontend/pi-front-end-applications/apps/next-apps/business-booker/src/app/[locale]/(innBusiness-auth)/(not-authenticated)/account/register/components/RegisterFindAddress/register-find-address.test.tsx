import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { FormProvider, useForm } from 'react-hook-form';

import { RegisterFindAddress } from './register-find-address';

const mockOnSubmit = jest.fn();
const mockIcons = {
  'icon.notification.error': '/path/to/error-icon.svg',
};

const Wrapper = ({ children }: { children: React.ReactNode }) => {
  const methods = useForm();
  return <FormProvider {...methods}>{children}</FormProvider>;
};

describe('RegisterFindAddress', () => {
  it('should render the component correctly', () => {
    render(
      <Wrapper>
        <RegisterFindAddress onSubmit={mockOnSubmit} icons={mockIcons} />
      </Wrapper>
    );
    expect(screen.getByTestId('RegisterForm-findAddress')).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText('userMgmt.employee.add.companyAddress.postcode')
    ).toBeInTheDocument();
    expect(screen.getByTestId('RegisterForm-findAddressButton')).toBeInTheDocument();
  });

  it('should call onSubmit when the button is clicked', () => {
    render(
      <Wrapper>
        <RegisterFindAddress onSubmit={mockOnSubmit} icons={mockIcons} />
      </Wrapper>
    );

    const findAddressButton = screen.getByTestId('RegisterForm-findAddressButton');
    fireEvent.click(findAddressButton);

    expect(mockOnSubmit).toHaveBeenCalled();
  });
});
