import { InputGroup } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import NumberOfNights from './NumberOfNights.component';

const defaultProps = {
  noOfNightsStyles: {},
  inputValue: 10,
  inputPlaceholder: 'Nights',
  maxNights: 364,
  isLessThanSm: true,
  errorNights: false,
  noOfNightsError: 'Error',
  handleOnChange: jest.fn(),
  handleOnBlur: jest.fn(),
};

describe('NumberOfNights', () => {
  it('should render the component', () => {
    const { getByRole } = render(
      <InputGroup>
        <NumberOfNights {...defaultProps} />
      </InputGroup>
    );
    expect(getByRole('spinbutton')).toBeEnabled();
  });

  it('should display the correct value', () => {
    const { getByDisplayValue } = render(
      <InputGroup>
        <NumberOfNights {...defaultProps} />
      </InputGroup>
    );
    expect(getByDisplayValue('10')).toBeInTheDocument();
  });

  it('should display an alert if the inputValue exceeds 364 days', () => {
    const { getByRole } = render(
      <InputGroup>
        <NumberOfNights {...defaultProps} inputValue={500} isLessThanSm={false} />
      </InputGroup>
    );
    expect(getByRole('alert')).toBeInTheDocument();
  });

  it('should not display an alert if input value is 0 and hideError prop is true ', () => {
    const { queryByRole } = render(
      <InputGroup>
        <NumberOfNights
          {...defaultProps}
          isLessThanSm={false}
          inputValue={0}
          hideErrorForMinNights={true}
        />
      </InputGroup>
    );
    expect(queryByRole('alert')).not.toBeInTheDocument();
  });

  it('should disable the input if isDisabled prop is true', () => {
    const { getByRole } = render(
      <InputGroup>
        <NumberOfNights
          {...defaultProps}
          isLessThanSm={false}
          inputValue={0}
          hideErrorForMinNights={true}
          isDisabled={true}
        />
      </InputGroup>
    );
    expect(getByRole('spinbutton')).toBeDisabled();
  });
});
