import { AutocompleteStyleProps } from '.';
import '@testing-library/jest-dom';
import { act } from '@testing-library/react';
import user from '@testing-library/user-event';

import { FrenchFlagRegular } from '../../assets/icons';
import { render, screen } from '../../utils/test-utils';
import AutocompleteLocation from './AutocompleteLocation.component';

describe('AutoComplete', () => {
  const baseProps = {
    items: [
      {
        value: 'Nigeria',
        group: 'africa',
        component: <FrenchFlagRegular style={{ marginRight: '0.5rem' }} />,
      },
      {
        value: 'Japan',
        group: 'asia',
        component: <FrenchFlagRegular style={{ marginRight: '0.5rem' }} />,
      },
      {
        value: 'India',
        group: 'asia',
        component: <FrenchFlagRegular style={{ marginRight: '0.5rem' }} />,
      },
    ],
    autocompleteStyles: {
      w: '50%',
    } as AutocompleteStyleProps,
    onChange: jest.fn(),
    onSelectOption: jest.fn(),
    inputPlaceholder: 'Enter place, postcode or hotel',
  };

  it('should render without error', () => {
    const { getByPlaceholderText } = render(
      <AutocompleteLocation {...baseProps}></AutocompleteLocation>
    );
    const input = getByPlaceholderText(/Enter place, postcode or hotel/i);
    expect(input).toBeInTheDocument();
  });

  describe('when the user types something into the input', () => {
    beforeEach(() => {
      // Scroll into view is not implemented in JSDOM and required by the code
      Element.prototype.scrollIntoView = () => null;

      const { getByPlaceholderText } = render(
        <AutocompleteLocation {...baseProps}></AutocompleteLocation>
      );
      const input = getByPlaceholderText(/Enter place, postcode or hotel/i);
      user.type(input, 'a');
    });

    it('should render 3 options', () => {
      const popover = screen.getByRole('dialog');
      expect(popover.children).toHaveLength(3);
    });

    it('should clear the input if the x icon is clicked', () => {
      const clearIcon = screen.getByLabelText('clear-icon');
      user.click(clearIcon, { button: 0 });

      const input = screen.getByPlaceholderText(/Enter place, postcode or hotel/i);
      expect(input).toHaveValue('');
    });

    it('should update the input value when an option is selected', async () => {
      const option = screen.getByText(/nigeria/i);
      act(() => {
        user.click(option);
      });
      expect(screen.getByPlaceholderText(/Enter place, postcode or hotel/i)).toHaveValue('Nigeria');
    });

    it('should call onChange with the value when one is selected', async () => {
      const option = screen.getByText(/nigeria/i);
      user.click(option);
      expect(baseProps.onChange).toBeCalledWith('Nigeria', {
        label: 'Nigeria',
        originalValue: 'Nigeria',
        value: 'Nigeria',
      });
    });

    it('should call onSelectOption with the value when an option is selected', async () => {
      const option = screen.getByText(/nigeria/i);
      user.click(option);
      expect(baseProps.onSelectOption).lastCalledWith('Nigeria');
    });
  });

  describe('Accessibility', () => {
    it('should set aria-invalid on input when ariaInvalid is true', () => {
      const { getByPlaceholderText } = render(
        <AutocompleteLocation {...baseProps} ariaInvalid={true} />
      );
      const input = getByPlaceholderText(/Enter place, postcode or hotel/i);
      expect(input).toHaveAttribute('aria-invalid', 'true');
    });

    it('should not set aria-invalid when ariaInvalid is false', () => {
      const { getByPlaceholderText } = render(
        <AutocompleteLocation {...baseProps} ariaInvalid={false} />
      );
      const input = getByPlaceholderText(/Enter place, postcode or hotel/i);
      expect(input).not.toHaveAttribute('aria-invalid');
    });

    it('should set aria-describedby on input when ariaDescribedBy is provided', () => {
      const { getByPlaceholderText } = render(
        <AutocompleteLocation {...baseProps} ariaDescribedBy="error-message-id" />
      );
      const input = getByPlaceholderText(/Enter place, postcode or hotel/i);
      expect(input).toHaveAttribute('aria-describedby', 'error-message-id');
    });

    it('should not have aria-describedby when ariaDescribedBy is not provided', () => {
      const { getByPlaceholderText } = render(<AutocompleteLocation {...baseProps} />);
      const input = getByPlaceholderText(/Enter place, postcode or hotel/i);
      expect(input).not.toHaveAttribute('aria-describedby');
    });

    it('should have both aria-invalid and aria-describedby when error is shown', () => {
      const { getByPlaceholderText } = render(
        <AutocompleteLocation {...baseProps} ariaInvalid={true} ariaDescribedBy="location-error" />
      );
      const input = getByPlaceholderText(/Enter place, postcode or hotel/i);
      expect(input).toHaveAttribute('aria-invalid', 'true');
      expect(input).toHaveAttribute('aria-describedby', 'location-error');
    });
  });
});
