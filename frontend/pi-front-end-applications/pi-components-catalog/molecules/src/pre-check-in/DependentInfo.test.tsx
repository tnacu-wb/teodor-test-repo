import { useMediaQuery } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useCustomLocale } from '@whitbread-eos/utils';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../utils/test-utils';
import DependentInfo from './DependentInfo';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn().mockReturnValue({ t: (key) => key }),
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockUseCustomLocale = useCustomLocale as jest.Mock;
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: jest.fn().mockImplementation(() => ({ language: 'en' })),
}));

const mockUseMediaQuery = useMediaQuery as jest.Mock;
jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: jest.fn().mockImplementation(() => ['isLargerThanSm']),
}));

jest.mock('./common', () => ({
  ...jest.requireActual('./common'),
  dependencyFields: [
    {
      type: 'input',
      name: 'firstName',
      label: 'firstname',
      testId: 'FirstName',
    },
    {
      type: 'input',
      name: 'lastName',
      label: 'lastname',
      testId: 'Surname',
    },
    {
      type: 'datePicker',
      name: 'dateofbirth',
      label: 'dateofbirth',
      testId: 'dateofbirth',
    },
    {
      type: 'autoComplete',
      name: 'nationality',
      label: 'nationality',
      testId: 'Nationality',
    },
    {
      type: 'input',
      name: 'passport',
      label: 'passport',
      testId: 'Passport',
    },
    {
      type: 'testInputType',
      name: 'testInputName',
      label: 'testInputLabel',
      testId: 'testInputTestID',
    },
  ],
}));

describe('DependentInfo Component', () => {
  const nationalities = [{ value: 'USA' }, { value: 'UK' }];
  const formField = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'fieldName',
    label: 'Field Name',
    testid: 'DependentInfo',
  };

  const errors = {
    dependents: {
      0: {
        passport: {
          message: 'Please enter passport',
          type: 'required',
          ref: { name: 'dependents[0].passport' },
        },
        dateofbirth: {
          message: 'Please enter date of birth',
          type: 'required',
          ref: { name: 'dependents[0].dateofbirth' },
        },
        nationality: {
          message: 'Please enter nationality',
          type: 'required',
          ref: { name: 'dependents[0].nationality' },
        },
      },
    },
  };

  const DependentInfoComponent = () => {
    const { control } = useForm();

    return (
      <DependentInfo
        getValues={() => ({})}
        control={control}
        formField={formField}
        index={0}
        removeDependent={jest.fn()}
        nationalities={nationalities}
        errors={errors}
      />
    );
  };

  it('should render without errors', () => {
    const { getByTestId } = render(<DependentInfoComponent />);
    expect(getByTestId('input-dependents[0].firstName')).toBeInTheDocument();
    expect(getByTestId('input-dependents[0].lastName')).toBeInTheDocument();
  });

  it('should call removeDependent when Delete button is clicked', () => {
    mockUseCustomLocale.mockImplementation(() => ({ language: 'de' }));
    const { getByTestId } = render(<DependentInfoComponent />);
    const deleteButton = getByTestId('DependentInfo-dependents-0-guest')?.querySelector(
      'button'
    ) as HTMLElement;
    userEvent.click(deleteButton);
  });

  it('should render component in desktop view', () => {
    mockUseMediaQuery.mockImplementation(() => ['']);
    const { getByTestId } = render(<DependentInfoComponent />);
    const deleteButton = getByTestId('DependentInfo-dependents-0-guest')?.querySelector(
      'button'
    ) as HTMLElement;
    expect(deleteButton).toBeInTheDocument();
  });
});
