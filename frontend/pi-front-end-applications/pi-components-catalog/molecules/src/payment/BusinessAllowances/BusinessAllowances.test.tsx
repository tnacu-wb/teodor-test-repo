import BusinessAllowances from '.';
import '@testing-library/jest-dom';
import { BusinessAllowance } from '@whitbread-eos/api';
import React, { Dispatch, SetStateAction } from 'react';

import { render, userEvent, fireEvent } from '../../utils/test-utils';

const title = 'Business Allowances';
const dinnerBudget = 'Total dinner budget per person per night';
const invalidDinnerBudget = 'Please enter a whole number between 1 and 999.';
const otherAllowances = 'Other Allowances';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      switch (key) {
        case 'booking.bac.businessAllowances':
          return title;
        case 'booking.bac.dinnerBudget':
          return dinnerBudget;
        case 'booking.bac.dinnerBudget.invalid':
          return invalidDinnerBudget;
        case 'businessAllowances.otherAllowances':
          return otherAllowances;
        default:
          return 'default';
      }
    },
  }),
}));

const hasBusinessAllowanceError = false;
const setHasBusinessAllowanceError = jest.fn();
const hasBusinessAllowanceErrorState: [boolean, Dispatch<SetStateAction<boolean>>] = [
  hasBusinessAllowanceError,
  setHasBusinessAllowanceError,
];

const mockedProps = {
  toggleDinnerAllowance: jest.fn(),
  dinnerAllowance: false,
  paymentHasError: false,
  businessAllowances: {
    totalDinnerBudgetPersonNight: undefined,
    isAlcoholDinner: false,
    carParking: false,
    wifi: false,
    // additionalCharges: false,
  } as BusinessAllowance,
  setBusinessAllowances: jest.fn(),
  hotelCountry: 'United Kingdom',
  setHasBusinessAllowanceError: jest.fn(),
  hasBusinessAllowanceError: false,
  hasBusinessAllowanceErrorState: hasBusinessAllowanceErrorState,
};

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockServerSideCustomLocale,
}));

describe('BusinessAllowances', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });
  it('renders BusinessAllowances empty component', () => {
    const { getAllByRole, getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    expect(getByTestId('BusinessAllowances-Title')).toBeInTheDocument();
    expect(getAllByRole('checkbox').length).toEqual(3);
  });

  it('should display the label input of the Dinner Allowances even the input is disabled', () => {
    mockedProps.dinnerAllowance = true;
    const { getByText } = render(
      <BusinessAllowances
        {...mockedProps}
        businessAllowancesSections={{
          amountDisabled: true,
          allowDinner: true,
          allowAlcohol: true,
          allowCarParking: true,
          allowWiFi: true,
          amount: 0,
        }}
      />
    );
    expect(getByText('Total dinner budget per person per night')).toBeInTheDocument();
  });

  it('renders BusinessAllowances with dinnerAllowance checked', () => {
    mockedProps.dinnerAllowance = true;
    const { getAllByRole } = render(<BusinessAllowances {...mockedProps} />);
    expect(getAllByRole('checkbox').length).toEqual(4);
  });

  it('should call setHasBusinessAllowanceError if invalid character is used', () => {
    mockedProps.dinnerAllowance = true;
    mockedProps.hasBusinessAllowanceError = true;
    const { getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    const input = getByTestId('input-totalDinnerBudgetPersonNight');
    fireEvent.change(input, { target: { value: '!' } });
    expect(setHasBusinessAllowanceError).toHaveBeenCalled();
  });

  it('should set totalDinnerBudgetPersonNight to defaut value if dinnerAllowance is true', async () => {
    mockedProps.dinnerAllowance = true;
    mockedProps.businessAllowances.totalDinnerBudgetPersonNight = 5;
    const { getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    const dinnerCheck = getByTestId('BusinessAllowances-ToggleDinnerAllowance');
    userEvent.click(dinnerCheck);
    expect(mockedProps.businessAllowances.totalDinnerBudgetPersonNight).toEqual(5);
  });

  it('should call setHasBusinessAllowanceError with true if paymentHasError and totalDinnerBudgetPersonNight is less that 1', async () => {
    mockServerSideCustomLocale.language = 'de';
    mockedProps.paymentHasError = true;
    mockedProps.businessAllowances.totalDinnerBudgetPersonNight = 0;
    const { getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    const input = getByTestId('input-totalDinnerBudgetPersonNight');
    fireEvent.change(input, { target: { value: '0' } });
    expect(setHasBusinessAllowanceError).toHaveBeenCalledWith(true);
  });

  it('should enable the alchol dinner allowance', async () => {
    const { getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    const alcoholCheck = getByTestId('BusinessAllowances-ToggleAlcoholDinner');
    userEvent.click(alcoholCheck);
    expect(mockedProps.setBusinessAllowances).toHaveBeenCalledTimes(1);
  });

  it('should enable the car park allowance', () => {
    const { getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    const check = getByTestId('BusinessAllowances-CarParkingCheckbox');
    userEvent.click(check);
    expect(mockedProps.setBusinessAllowances).toHaveBeenCalledTimes(1);
  });

  it('should enable the wifi allowance', () => {
    const { getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    const check = getByTestId('BusinessAllowances-WifiCheckbox');
    userEvent.click(check);
    expect(mockedProps.setBusinessAllowances).toHaveBeenCalledTimes(1);
  });

  it('should set the totalDinnerBudgetPersonNight to 0 if dinnerAllowance if false ', () => {
    mockedProps.dinnerAllowance = false;
    const { getByTestId } = render(<BusinessAllowances {...mockedProps} />);
    const check = getByTestId('BusinessAllowances-ToggleDinnerAllowance');
    userEvent.click(check);
    expect(mockedProps.businessAllowances.totalDinnerBudgetPersonNight).toEqual(0);
  });
});
