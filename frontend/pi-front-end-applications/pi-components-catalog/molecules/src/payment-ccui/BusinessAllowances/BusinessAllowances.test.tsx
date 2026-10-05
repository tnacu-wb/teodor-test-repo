import BusinessAllowancesCCUI from '.';
import '@testing-library/jest-dom';
import { BusinessAllowanceCCUItype } from '@whitbread-eos/api';

import { fireEvent, render } from '../../utils/test-utils';

const mockSetHasError = jest.fn();
const mockSetValue = jest.fn();

const mockedProps = {
  toggleDinnerAllowance: jest.fn(),
  dinnerAllowance: false,
  businessAllowances: {
    totalDinnerBudgetPersonNight: undefined,
    isAlcoholDinner: false,
    carParking: false,
    ultimateWifi: false,
    mealDeal: false,
    premierInnBreakfast: false,
    continentalBreakfast: false,
  } as BusinessAllowanceCCUItype,
  setBusinessAllowances: mockSetValue,
  currency: '',
  language: '',
  setHasError: mockSetHasError,
};
describe('BusinessAllowances', () => {
  it('renders BusinessAllowances empty component', () => {
    const { getByText, getAllByRole } = render(<BusinessAllowancesCCUI {...mockedProps} />);
    expect(getByText('ccui.businessAllowances.title')).toBeInTheDocument();
    expect(getAllByRole('checkbox').length).toEqual(7);
  });

  it('renders BusinessAllowances with dinnerAllowance checked', () => {
    mockedProps.dinnerAllowance = true;
    const { getAllByRole } = render(<BusinessAllowancesCCUI {...mockedProps} />);
    expect(getAllByRole('checkbox').length).toEqual(7);
  });

  it('renders BusinessAllowances with devider', () => {
    mockedProps.businessAllowances.totalDinnerBudgetPersonNight = 'total';
    const { getAllByRole } = render(<BusinessAllowancesCCUI {...mockedProps} />);
    expect(getAllByRole('checkbox').length).toEqual(7);
  });

  it('should update value on total dinner budget input change', () => {
    const { getByTestId } = render(<BusinessAllowancesCCUI {...mockedProps} />);
    const totalDinnerBudget: HTMLInputElement = getByTestId(
      'input-totalDinnerBudgetPersonNight'
    ) as HTMLInputElement;

    fireEvent.focus(totalDinnerBudget, { target: { value: '120' } });

    expect(totalDinnerBudget.value).toEqual('120');

    fireEvent.change(totalDinnerBudget, { target: { value: '120' } });

    expect(mockSetValue).toBeCalled();
    expect(mockSetHasError).toBeCalledWith(false);
  });

  it('should set an error state when invalid total budget is typed', () => {
    const { getByTestId } = render(<BusinessAllowancesCCUI {...mockedProps} />);
    const totalDinnerBudget: HTMLInputElement = getByTestId(
      'input-totalDinnerBudgetPersonNight'
    ) as HTMLInputElement;

    fireEvent.change(totalDinnerBudget, {
      target: {
        value: 'Test',
      },
    });

    expect(mockSetValue).toHaveBeenCalled();
    expect(mockSetHasError).toBeCalledWith(true);
  });
});
