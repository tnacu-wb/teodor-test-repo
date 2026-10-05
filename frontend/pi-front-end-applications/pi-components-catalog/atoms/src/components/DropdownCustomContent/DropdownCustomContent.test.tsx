import '@testing-library/jest-dom';
import React from 'react';

import BritishFlagRegular from '../../assets/icons/BritishFlagRegular';
import { render, userEvent } from '../../utils/test-utils';
import Icon from '../Icon';
import DropdownCustomContent, { DropdownOption } from './DropdownCustomContent.component';

const options: DropdownOption[] = [
  {
    id: 1,
    label: 'Option 1',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 2,
    label: 'Option 2',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 3,
    label: 'Option 3',
  },
  {
    id: 4,
    label: 'Option 4',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 5,
    label: 'Option 5',
  },
  {
    id: 6,
    label: 'Option 6',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 7,
    label: 'Option 7',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
];

const mockSetIsSelectedValue = jest.fn();
const mockSetIsOpen = jest.fn();

const mockProps = {
  isOpen: true,
  onChange: jest.fn(),
  setSelectedValue: mockSetIsSelectedValue,
  setIsOpen: mockSetIsOpen,
  options: options,
  baseDataTestId: 'Wrapper',
  dataTestId: 'DropdownCustom',
};

describe('DropdownCustomContent', () => {
  it('should render the component if dropdown is open', () => {
    const { getByTestId } = render(<DropdownCustomContent {...mockProps} />);
    expect(getByTestId('Wrapper-DropdownCustom-entireList')).toBeInTheDocument();
  });

  it('should trigger the setSelectedValue/setIsOpen when clicking an option', () => {
    const { getByTestId } = render(<DropdownCustomContent {...mockProps} />);
    const firstOption = getByTestId('Wrapper-DropdownCustom-0');
    userEvent.click(firstOption);
    expect(mockSetIsSelectedValue).toHaveBeenCalledTimes(1);
    expect(mockSetIsOpen).toHaveBeenCalledTimes(1);
  });

  it('should not render the component if dropdown is not open', () => {
    const props = {
      ...mockProps,
      isOpen: false,
    };
    const { queryByTestId } = render(<DropdownCustomContent {...props} />);
    expect(queryByTestId('Wrapper-DropdownCustom-entireList')).not.toBeInTheDocument();
  });
});
