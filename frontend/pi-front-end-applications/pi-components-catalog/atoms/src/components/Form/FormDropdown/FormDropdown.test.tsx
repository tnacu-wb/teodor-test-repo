import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formConstants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormDropdown from './FormDropdown.component';

const Component = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DROPDOWN,
    name: 'country',
    label: 'Country',
    dropdownOptions: [
      {
        id: 1,
        label: 'United Kingdom',
      },
      {
        id: 2,
        label: 'Germany',
      },
      {
        id: 3,
        label: 'France',
      },
    ],
    testid: 'formDropdown',
  };

  const props: FormFieldProps = {
    control,
    formField: formField,
    errors,
    getValues,
  };

  return <FormDropdown {...props} />;
};

describe('Form Dropdown', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<Component />);
    const text = getByTestId('formDropdown');

    expect(text).toBeInTheDocument();
  });

  it('should update the selection', async () => {
    const { getByText, container } = render(<Component />);
    const dropdownToggle = getByText('Country', { selector: 'p' });
    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);

    const dropdownOptionUK = getByText('United Kingdom');
    await dropdownOptionUK.click();

    expect(selector).toHaveTextContent('United Kingdom');
  });

  it('should change the selection to a new one, if it was selected once', () => {
    const { getByText } = render(<Component />);
    const dropdownToggle = getByText('Country', { selector: 'p' });
    userEvent.click(dropdownToggle);
    const dropdownOptionUK = getByText('United Kingdom');
    dropdownOptionUK.click();

    expect(dropdownOptionUK).toHaveTextContent('United Kingdom');

    userEvent.click(dropdownToggle);

    const dropdownOptionGermany = getByText('Germany');
    dropdownOptionGermany.click();

    expect(dropdownOptionGermany).toHaveTextContent('Germany');
  });
});
