import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { userEvent, render, act } from '../../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formContants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormDropdown from './FormDropdown.component';

const ComponentWithAdults = () => {
  const {
    control,
    formState: { errors },
    getValues,
    setValue,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DROPDOWN,
    name: 'adults',
    label: 'How many adults',
    dropdownOptions: [
      {
        id: '1',
        label: '1 Adult',
      },
      {
        id: '2',
        label: '2 Adults',
      },
      {
        id: '17',
        label: 'More then 16 adults',
      },
    ],
    testid: 'formDropdown',
  };

  const props: FormFieldProps = {
    control,
    formField: formField,
    errors,
    setIsEnquiry: jest.fn(),
    getValues,
    setValue,
  };

  return <FormDropdown {...props} />;
};

const ComponentWithChildren = () => {
  const {
    control,
    formState: { errors },
    getValues,
    setValue,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DROPDOWN,
    name: 'children',
    label: 'How many children',
    dropdownOptions: [
      {
        id: '1',
        label: '1 Children',
      },
      {
        id: '2',
        label: '2 Children',
      },
      {
        id: '3',
        label: '3 Children',
      },
      {
        id: '15',
        label: '15 Children',
      },
      {
        id: '17',
        label: '17 Children',
      },
    ],
    testid: 'formDropdown',
  };

  const props: FormFieldProps = {
    control,
    formField: formField,
    errors,
    setIsEnquiry: jest.fn(),
    getValues,
    setValue,
  };

  return <FormDropdown {...props} />;
};

describe('Form Dropdown', () => {
  it('should render the adults dropdown component', () => {
    const { getByTestId } = render(<ComponentWithAdults />);
    const text = getByTestId('formDropdown');
    expect(text).toBeInTheDocument();
  });

  it('should render the children dropdown component', () => {
    const { getByTestId } = render(<ComponentWithChildren />);
    const text = getByTestId('formDropdown');
    expect(text).toBeInTheDocument();
  });

  it('should update the selection in Adults dropdown', async () => {
    const { getByText, container } = render(<ComponentWithAdults />);
    const dropdownToggleForAdults = getByText('How many adults', { selector: 'p' });
    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggleForAdults);
    const dropdownAdults = getByText('2 Adults');
    act(() => {
      dropdownAdults.click();
    });

    expect(selector).toHaveTextContent('2 Adults');
  });

  it('should update the selection in children dropdown', async () => {
    const { getByText, container } = render(<ComponentWithChildren />);
    const dropdownToggle = getByText('How many children', { selector: 'p' });
    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);
    const dropdownToggleForChildren = getByText('2 Children');
    act(() => {
      dropdownToggleForChildren.click();
    });

    expect(selector).toHaveTextContent('2 Children');
  });

  it('should change the selection to a new one, if it was selected once', async () => {
    const { getByText, getByTestId } = render(<ComponentWithAdults />);
    const adultDropdownToggle = getByText('How many adults', { selector: 'p' });
    const adultSelector = getByTestId('DropdownComp-menuButton-adults');
    await userEvent.click(adultDropdownToggle);
    act(() => {
      const dropdownToggleForAdults = getByText('2 Adults');
      dropdownToggleForAdults.click();
    });
    expect(adultSelector).toHaveTextContent('2 Adults');
  });

  it('should change the selection to a new one, if it was selected once', async () => {
    const { getByText, getByTestId } = render(<ComponentWithAdults />);
    const adultDropdownToggle = getByText('How many adults', { selector: 'p' });
    const adultSelector = getByTestId('DropdownComp-menuButton-adults');
    await userEvent.click(adultDropdownToggle);
    act(() => {
      const dropdownToggleForAdults = getByText('2 Adults');
      dropdownToggleForAdults.click();
    });
    expect(adultSelector).toHaveTextContent('2 Adults');

    render(<ComponentWithChildren />);
    const childrenDropdownToggle = getByText('How many children', { selector: 'p' });
    const childrenSelector = getByTestId('DropdownComp-menuButton-children');
    await userEvent.click(childrenDropdownToggle);
    const dropdownToggleForChildren = getByText('15 Children');
    act(() => {
      dropdownToggleForChildren.click();
    });
    expect(childrenSelector).toHaveTextContent('15 Children');
  });

  it('should render the children dropdown component', () => {
    const { getByText, getByTestId } = render(<ComponentWithChildren />);
    const childrenSelector = getByTestId('DropdownComp-menuButton-children');

    getByText('17 Children').click();
    expect(childrenSelector).toBeInTheDocument();
  });
});
