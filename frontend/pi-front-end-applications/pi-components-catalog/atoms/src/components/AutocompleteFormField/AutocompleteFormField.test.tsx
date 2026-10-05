import '@testing-library/jest-dom';
import { act } from '@testing-library/react';
import user from '@testing-library/user-event';

import { render, screen } from '../../utils/test-utils';
import { type FormDynamicFieldCompProps, FormFieldsValuesType } from '../Form/formTypes';
import AutoCompleteStaticHotels from './AutocompleteFormField.component';

jest.mock('../Form/FormError', () => {
  return function FormError() {
    return <div data-testid="FormError">FormError</div>;
  };
});
describe('AutoCompleteStaticHotels', () => {
  const baseProps: FormDynamicFieldCompProps = {
    formField: {
      type: 'text' as FormFieldsValuesType,
      name: 'hotels',
      label: 'Hotels',
      props: {
        placeholder: 'Enter hotel name',
        hotels: [
          {
            value: 'Aberdare',
            brand: 'PI',
            component: '',
          },
          {
            value: 'Aberdeen',
            brand: 'PI',
            component: '',
          },
          {
            value: 'Aberdeen (Westhill)',
            brand: 'PI',
            component: '',
          },
        ],
        hasItemObject: true,
        handleSelectOption: jest.fn(),
      },
    },
    field: {
      name: 'hotels',
      value: '',
      onChange: jest.fn(),
      onBlur: jest.fn(),
    },
    handleSetValue: jest.fn(),
    errors: {},
    handleClearErrors: jest.fn(),
    getValues: jest.fn(),
  };

  it('should render without error', () => {
    const { getByPlaceholderText } = render(<AutoCompleteStaticHotels {...baseProps} />);
    const input = getByPlaceholderText(/Enter hotel name/i);
    expect(input).toBeInTheDocument();
  });

  it('should render error when no hotels are selected', () => {
    const modifiedProps = {
      ...baseProps,
      errors: { hotels: { message: 'error', type: 'required' } },
    };
    render(<AutoCompleteStaticHotels {...modifiedProps} />);
    expect(screen.getByText('FormError')).toBeInTheDocument();
  });

  it('should not render when no hotels are passed', () => {
    const modifiedProps = {
      ...baseProps,
      formField: { ...baseProps.formField, props: {} },
    };
    render(<AutoCompleteStaticHotels {...modifiedProps} />);
    expect(screen.queryByText(/Enter hotel name/i)).not.toBeInTheDocument();
  });

  it('should call onChange with the value when one is selected', async () => {
    Element.prototype.scrollIntoView = () => null;
    const { getByPlaceholderText } = render(
      <AutoCompleteStaticHotels {...baseProps}></AutoCompleteStaticHotels>
    );

    const input = getByPlaceholderText(/Enter hotel name/i);
    expect(input).toBeInTheDocument();
    user.type(input, 'a');

    const option = screen.getByText(/aberdare/i);
    expect(option).toBeInTheDocument();
    act(() => {
      user.click(option);
    });
    expect(screen.getByPlaceholderText(/Enter hotel name/i)).toHaveValue('Aberdare');

    expect(baseProps.formField?.props?.handleSelectOption).lastCalledWith({
      label: 'Aberdare',
      originalValue: { brand: 'PI', component: '', value: 'Aberdare' },
      value: 'Aberdare',
    });
  });

  it('should call onChange with the empty value if nothing has been provided', async () => {
    const { getByPlaceholderText } = render(
      <AutoCompleteStaticHotels {...baseProps}></AutoCompleteStaticHotels>
    );

    const input = getByPlaceholderText(/Enter hotel name/i);
    expect(input).toBeInTheDocument();
    user.type(input, '');

    expect(input).toHaveValue('');
  });
});
