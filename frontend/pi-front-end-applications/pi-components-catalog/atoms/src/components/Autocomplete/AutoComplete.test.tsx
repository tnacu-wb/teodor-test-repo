import '@testing-library/jest-dom';

import { ChevronDown, ChevronRight, SearchIcon } from '../../assets/icons';
import { fireEvent, render, waitFor } from '../../utils/test-utils';
import Autocomplete from './AutoComplete.component';

describe('Autocomplete', () => {
  const baseProps = {
    items: [{ value: 'Nigeria' }, { value: 'Japan' }, { value: 'India' }],
    wrapperStyles: {
      w: '50%',
    },
    onChange: jest.fn(),
    inputPlaceholder: 'Location',
    showElements: true,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render without error', () => {
    const { getByRole } = render(<Autocomplete {...baseProps}></Autocomplete>);
    const group = getByRole('combobox');
    expect(group).toBeInTheDocument();
  });

  it('should render the correct placeholder text', () => {
    const { getByPlaceholderText } = render(<Autocomplete {...baseProps}></Autocomplete>);
    const input = getByPlaceholderText('Location');
    expect(input).toBeInTheDocument();
  });

  it('should display an icon if one is passed', () => {
    const testId = 'svg-item';
    const props = {
      ...baseProps,
      icons: { left: () => <SearchIcon data-testid={testId} /> },
    };
    const { getByTestId } = render(<Autocomplete {...props}></Autocomplete>);
    const svgComponent = getByTestId(testId);
    expect(svgComponent).toBeInTheDocument();
  });

  it('should display the primary icon if the input is not clicked and secondary if it is clicked', async () => {
    const primaryTestId = 'primary-svg';
    const secondaryTestId = 'secondary-svg';

    const props = {
      ...baseProps,
      icons: {
        right: ({ isOpen = false }) => {
          return !isOpen ? (
            <ChevronRight data-testid={primaryTestId} />
          ) : (
            <ChevronDown data-testid={secondaryTestId} />
          );
        },
      },
    };
    const { getByTestId, getByRole, queryByTestId } = render(
      <Autocomplete {...props}></Autocomplete>
    );
    const input = getByRole('combobox');
    const primaryTest = getByTestId(primaryTestId);
    expect(primaryTest).toBeInTheDocument();

    fireEvent.change(input, { target: { value: 'a' } });

    expect(await getByTestId(secondaryTestId)).toBeInTheDocument();
    expect(await queryByTestId(primaryTestId)).not.toBeInTheDocument();
  });

  it('should have autoComplete set to off to prevent browser autofill', () => {
    const { getByRole } = render(<Autocomplete {...baseProps} />);
    const input = getByRole('combobox');
    expect(input).toHaveAttribute('autocomplete', 'off');
  });

  it('should group the items accordingly', async () => {
    const props = {
      ...baseProps,
      items: [
        { value: 'Nigeria', group: 'africa' },
        { value: 'Japan', group: 'asia' },
        { value: 'India', group: 'asia' },
      ],
    };
    const { getByRole, queryByText } = render(<Autocomplete {...props}></Autocomplete>);
    const input = getByRole('combobox');
    fireEvent.change(input, { target: { value: 'a' } });

    expect(await queryByText(/africa/i)).toBeInTheDocument();
  });

  describe('Multi-select mode', () => {
    it('should call onSelectOption with correct format in multi-select mode', async () => {
      const onSelectOptionMock = jest.fn();
      const props = {
        ...baseProps,
        multiSelectable: true,
        onSelectOption: onSelectOptionMock,
      };

      const { getByRole, getByText } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'Nigeria' } });

      await waitFor(() => {
        const option = getByText('Nigeria');
        fireEvent.click(option);
      });

      await waitFor(() => {
        expect(onSelectOptionMock).toHaveBeenCalledWith('Nigeria');
      });
    });

    it('should call onInputChange in multi-select mode', async () => {
      const onInputChangeMock = jest.fn();
      const props = {
        ...baseProps,
        multiSelectable: true,
        onInputChange: onInputChangeMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'test' } });

      expect(onInputChangeMock).toHaveBeenCalledWith('test');
    });

    it('should call onChange with comma-separated values in multi-select mode', async () => {
      const onChangeMock = jest.fn();
      const props = {
        ...baseProps,
        multiSelectable: true,
        onChange: onChangeMock,
      };

      const { getByRole, getByText } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'Nigeria' } });

      await waitFor(() => {
        const option = getByText('Nigeria');
        fireEvent.click(option);
      });

      await waitFor(() => {
        expect(onChangeMock).toHaveBeenCalledWith('Nigeria');
      });
    });

    it('should call onFocusInput and setInputHasFocus in multi-select mode', async () => {
      const onFocusInputMock = jest.fn();
      const setInputHasFocusMock = jest.fn();
      const props = {
        ...baseProps,
        multiSelectable: true,
        onFocusInput: onFocusInputMock,
        setInputHasFocus: setInputHasFocusMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.focus(input);

      expect(onFocusInputMock).toHaveBeenCalled();
      expect(setInputHasFocusMock).toHaveBeenCalledWith(true);
    });

    it('should call setInputHasFocus on blur in multi-select mode', async () => {
      const setInputHasFocusMock = jest.fn();
      const props = {
        ...baseProps,
        multiSelectable: true,
        setInputHasFocus: setInputHasFocusMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.blur(input);

      expect(setInputHasFocusMock).toHaveBeenCalledWith(false);
    });
  });

  describe('hasItemObject prop', () => {
    it('should pass item object to onSelectOption when hasItemObject is true', async () => {
      const onSelectOptionMock = jest.fn();
      const props = {
        ...baseProps,
        hasItemObject: true,
        onSelectOption: onSelectOptionMock,
      };

      const { getByRole, getByText } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'Nigeria' } });

      await waitFor(() => {
        const option = getByText('Nigeria');
        fireEvent.click(option);
      });

      await waitFor(() => {
        expect(onSelectOptionMock).toHaveBeenCalledWith({
          label: 'Nigeria',
          originalValue: { value: 'Nigeria' },
          value: 'Nigeria',
        });
      });
    });

    it('should pass item object in multi-select mode when hasItemObject is true', async () => {
      const onSelectOptionMock = jest.fn();
      const props = {
        ...baseProps,
        multiSelectable: true,
        hasItemObject: true,
        onSelectOption: onSelectOptionMock,
      };

      const { getByRole, getByText } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'Nigeria' } });

      await waitFor(() => {
        const option = getByText('Nigeria');
        fireEvent.click(option);
      });

      await waitFor(() => {
        expect(onSelectOptionMock).toHaveBeenCalledWith({ value: 'Nigeria' });
      });
    });
  });

  describe('isPriceFinder prop', () => {
    it('should render items correctly when isPriceFinder is true', () => {
      const props = {
        ...baseProps,
        isPriceFinder: true,
        dataTestId: 'price-finder-test',
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'Nigeria' } });

      expect(input).toBeInTheDocument();
    });

    it('should apply special styles when isPriceFinder is true', () => {
      const props = {
        ...baseProps,
        isPriceFinder: true,
        dataTestId: 'price-finder',
      };

      const { getByTestId } = render(<Autocomplete {...props} />);
      const input = getByTestId('price-finder-locationPlaceholder');

      expect(input).toHaveStyle({ paddingRight: '65px' });
    });
  });

  describe('onBlurInput callback', () => {
    it('should call onBlurInput with first item when blurring with sufficient input', async () => {
      const onBlurInputMock = jest.fn();
      const props = {
        ...baseProps,
        onBlurInput: onBlurInputMock,
        dataTestId: 'blur-test',
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox') as HTMLInputElement;

      fireEvent.change(input, { target: { value: 'Nig' } });
      fireEvent.blur(input, { relatedTarget: null });

      await waitFor(() => {
        expect(onBlurInputMock).toHaveBeenCalledWith('Nigeria');
      });
    });

    it('should not call onBlurInput when input is too short', () => {
      const onBlurInputMock = jest.fn();
      const props = {
        ...baseProps,
        onBlurInput: onBlurInputMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'Ni' } });
      fireEvent.blur(input);

      expect(onBlurInputMock).not.toHaveBeenCalled();
    });

    it('should not call onBlurInput when item was just selected', async () => {
      const onBlurInputMock = jest.fn();
      const props = {
        ...baseProps,
        onBlurInput: onBlurInputMock,
      };

      const { getByRole, getByText } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'Nigeria' } });

      await waitFor(() => {
        const option = getByText('Nigeria');
        fireEvent.click(option);
      });

      fireEvent.blur(input);

      expect(onBlurInputMock).not.toHaveBeenCalled();
    });
  });

  describe('Callback props', () => {
    it('should call onInputChange when input value changes', () => {
      const onInputChangeMock = jest.fn();
      const props = {
        ...baseProps,
        onInputChange: onInputChangeMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'test' } });

      expect(onInputChangeMock).toHaveBeenCalledWith('test');
    });

    it('should call onFocusInput when input is focused', () => {
      const onFocusInputMock = jest.fn();
      const props = {
        ...baseProps,
        onFocusInput: onFocusInputMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.focus(input);

      expect(onFocusInputMock).toHaveBeenCalled();
    });

    it('should call setInputHasFocus with true on focus', () => {
      const setInputHasFocusMock = jest.fn();
      const props = {
        ...baseProps,
        setInputHasFocus: setInputHasFocusMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.focus(input);

      expect(setInputHasFocusMock).toHaveBeenCalledWith(true);
    });

    it('should call setInputHasFocus with false on blur', () => {
      const setInputHasFocusMock = jest.fn();
      const props = {
        ...baseProps,
        setInputHasFocus: setInputHasFocusMock,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.blur(input);

      expect(setInputHasFocusMock).toHaveBeenCalledWith(false);
    });
  });

  describe('showElements prop', () => {
    it('should hide group titles when showElements is false', async () => {
      const props = {
        ...baseProps,
        items: [
          { value: 'Nigeria', group: 'africa' },
          { value: 'Japan', group: 'asia' },
        ],
        showElements: false,
        dataTestId: 'group-test',
      };

      const { getByRole, queryByTestId } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'a' } });

      expect(queryByTestId('group-test-hotelsLabel')).not.toBeInTheDocument();
    });
  });

  describe('disableInternalFilter prop', () => {
    it('should not filter items when disableInternalFilter is true', () => {
      const props = {
        ...baseProps,
        disableInternalFilter: true,
      };

      const { getByRole, getByText } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.change(input, { target: { value: 'xyz' } });

      expect(getByText('Nigeria')).toBeInTheDocument();
      expect(getByText('Japan')).toBeInTheDocument();
      expect(getByText('India')).toBeInTheDocument();
    });
  });

  describe('openListOnFocus prop', () => {
    it('should open menu on focus when openListOnFocus is true', async () => {
      const props = {
        ...baseProps,
        openListOnFocus: true,
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox');

      fireEvent.focus(input);
      fireEvent.change(input, { target: { value: 'a' } });

      await waitFor(() => {
        expect(input).toBeInTheDocument();
      });
    });
  });

  describe('inputSelectedValue prop', () => {
    it('should use inputSelectedValue when provided', () => {
      const props = {
        ...baseProps,
        inputSelectedValue: 'Nigeria',
      };

      const { getByRole } = render(<Autocomplete {...props} />);
      const input = getByRole('combobox') as HTMLInputElement;

      expect(input.value).toBe('Nigeria');
    });
  });

  describe('Icon handling', () => {
    it('should not render left icon when icons.left is null', () => {
      const testId = 'svg-item';
      const props = {
        ...baseProps,
        icons: { left: null },
      };

      const { queryByTestId } = render(<Autocomplete {...props} />);
      const svgComponent = queryByTestId(testId);

      expect(svgComponent).not.toBeInTheDocument();
    });
  });
});
