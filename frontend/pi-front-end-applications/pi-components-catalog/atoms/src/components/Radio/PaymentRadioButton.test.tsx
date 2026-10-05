import { ChakraProvider, extendTheme } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render, screen } from '../../utils/test-utils';
import PaymentRadioButton from './PaymentRadioButton.component';

const customTheme = extendTheme({
  breakpoints: {
    base: '0px',
    md: '768px',
  },
});

describe('PaymentRadioButton', () => {
  it('should render the component', () => {
    render(<PaymentRadioButton />);
    const wrapper = screen.getByTestId('radio-box-wrapper');
    expect(wrapper).toBeInTheDocument();
  });

  it('should apply vertical border radius on small screens (base)', () => {
    render(
      <ChakraProvider theme={customTheme}>
        <PaymentRadioButton listIndex={0} />
      </ChakraProvider>
    );

    const wrapper = screen.getByTestId('radio-box-wrapper');

    expect(wrapper).toHaveStyle('border-radius: var(--chakra-space-1) var(--chakra-space-1) 0 0');
  });

  it('should apply horizontal border radius on medium screens (md)', () => {
    render(
      <ChakraProvider theme={customTheme}>
        <PaymentRadioButton listIndex={'last'} />
      </ChakraProvider>
    );

    const wrapper = screen.getByTestId('radio-box-wrapper');

    expect(wrapper).toHaveStyle('border-radius: 0 0 var(--chakra-space-1) var(--chakra-space-1)');
  });

  it('should apply 2px border width when checked', () => {
    render(<PaymentRadioButton isChecked={true} />);
    const wrapper = screen.getByTestId('radio-box-wrapper');
    expect(wrapper).toHaveStyle('border-width: 2px');
  });

  it('should apply 1px border width when not checked', () => {
    render(<PaymentRadioButton isChecked={false} />);
    const wrapper = screen.getByTestId('radio-box-wrapper');
    expect(wrapper).toHaveStyle('border-width: 1px');
  });

  it('should apply no border width for the borderless variant', () => {
    render(<PaymentRadioButton variant="borderless" />);
    const wrapper = screen.getByTestId('radio-box-wrapper');
    expect(wrapper).toHaveStyle('border-width: 0');
  });

  it('should render children inside the Radio component', () => {
    render(
      <PaymentRadioButton>
        <span>Child Content</span>
      </PaymentRadioButton>
    );
    const child = screen.getByText('Child Content');
    expect(child).toBeInTheDocument();
  });

  it('should apply the correct background color when checked', () => {
    render(<PaymentRadioButton isChecked={true} />);
    const wrapper = screen.getByTestId('radio-box-wrapper');
    expect(wrapper).toHaveStyle('background: lightGrey5');
  });

  it('should apply the correct background color when not checked', () => {
    render(<PaymentRadioButton isChecked={false} />);
    const wrapper = screen.getByTestId('radio-box-wrapper');
    expect(wrapper).toHaveStyle('background: baseWhite');
  });

  it('should apply responsive border styles based on screen size', () => {
    render(<PaymentRadioButton listIndex={'left'} />);
    const wrapper = screen.getByTestId('radio-box-wrapper');
    expect(wrapper).toHaveStyle('border-left-width: 1px');
    expect(wrapper).toHaveStyle('border-bottom-width: 1px');
  });
});
