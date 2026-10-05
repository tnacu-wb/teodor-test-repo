import '@testing-library/jest-dom';
import { isIVMEnabled } from '@whitbread-eos/utils';
import React from 'react';

import { render, fireEvent } from '../../utils/test-utils';
import Multiselect from './MultiSelect.component';

const onChangeMock = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  formatAssetsUrl: jest.fn((url) => `https://premierinn.com${url}`),
  isIVMEnabled: jest.fn(),
}));

const nationalities = [
  {
    value: 'GB',
    label: 'United Kingdom (the)',
    image: '/content/dam/global/flags/United-Kingdom.png',
  },
  {
    value: 'DE',
    label: 'Germany',
    image: '/content/dam/global/flags/Germany.png',
  },
];

const defaultProps = {
  options: nationalities,
  dataTestId: 'select',
  value: null,
  onChange: onChangeMock,
  placeholder: 'Select an option',
  styles: {},
  name: 'select-nationality',
  isMulti: true,
  closeMenuOnSelect: false,
};

describe('DatepickerComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Should render with proper placeholder', () => {
    const { getByTestId } = render(<Multiselect {...defaultProps} />);
    expect(getByTestId('select-label')).toBeInTheDocument();
  });

  it('Should select an option', () => {
    const { getByRole } = render(<Multiselect {...defaultProps} />);
    const expectedMock = {
      value: 'GB',
      label: 'United Kingdom (the)',
      image: '/content/dam/global/flags/United-Kingdom.png',
    };

    fireEvent.change(getByRole('combobox'), { target: { value: 'GB' } });
    fireEvent.keyDown(getByRole('combobox'), { key: 'Enter', code: 'Enter' });
    expect(onChangeMock).toHaveBeenCalledWith([expectedMock], {
      action: 'select-option',
      name: expect.any(String),
      option: expect.any(Object),
    });
  });

  it('Should render with proper image', () => {
    (isIVMEnabled as jest.Mock).mockImplementation(() => true);

    const { getByRole, getByAltText } = render(<Multiselect {...defaultProps} />);

    fireEvent.change(getByRole('combobox'), { target: { value: 'GB' } });
    fireEvent.keyDown(getByRole('combobox'), { key: 'Enter', code: 'Enter' });

    const images = getByAltText('multi-select-img-GB');
    expect(images).toBeInTheDocument();
  });
});
