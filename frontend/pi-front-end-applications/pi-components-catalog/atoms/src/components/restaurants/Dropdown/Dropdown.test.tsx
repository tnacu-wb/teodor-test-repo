import '@testing-library/jest-dom';
import React from 'react';

import { render, fireEvent, userEvent, waitFor } from '../../../utils/test-utils';
import Dropdown from './Dropdown.component';

const onDisplayContentMock = jest.fn();
const onBlurMock = jest.fn();
const onChangeMock = jest.fn();
const props = {
  options: [
    {
      id: '1',
      label: 'Option 1',
    },
    {
      id: '2',
      label: 'Option 2',
    },
    {
      id: '3',
      label: 'Option 3',
    },
    {
      id: '4',
      label: 'Option 4',
    },
    {
      id: '5',
      label: 'Option 5',
    },
    {
      id: '6',
      label: 'Option 6',
    },
    {
      id: '7',
      label: 'Option 7',
    },
  ],
  dropdownStyles: {
    menuListStyles: {
      zIndex: 999,
    },
    menuButtonStyles: {
      h: 'var(--chakra-space-3xl)',
      bg: '#F4F4F4',
    },
  },
  placeholder: 'Placeholder',
  label: 'Choose something',
  onChange: onChangeMock,
  onDisplayContent: onDisplayContentMock,
  onBlur: onBlurMock,
};

const propsWithError = {
  ...props,
  hasError: true,
  isPartOfForm: true,
};

describe('DropdownComponent', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Dropdown {...props} />);
    expect(getByRole('button')).toBeInTheDocument();
  });

  it('should have placeholder when it is passed from props', () => {
    const { getByText } = render(<Dropdown {...props} />);
    expect(getByText('Placeholder')).toBeInTheDocument();
  });

  it('should have label if label prop exists', () => {
    const { getByText } = render(<Dropdown {...props} />);
    expect(getByText('Choose something')).toBeInTheDocument();
  });

  it('should have as many options as there are in options prop', () => {
    const { container } = render(<Dropdown {...props} />);
    const displayedOptions = container.querySelectorAll('.chakra-menu__menuitem');
    expect(displayedOptions.length).toEqual(props.options.length);
  });

  it('should display the selected option', () => {
    const { container } = render(<Dropdown {...props} />);
    const button = container.querySelector('[aria-haspopup="menu"]');
    fireEvent.click(button);
    const displayedOptions = container.querySelectorAll('.chakra-menu__menuitem');
    fireEvent.click(displayedOptions[2]);
    expect(button).toHaveTextContent('Option 3');
  });

  it('should have default selected value in dropdown', () => {
    const { getByTestId } = render(<Dropdown selectedId="1" {...props} />);
    const dafaultDelectedValue = getByTestId('DropdownComp-menuButtonText');
    expect(dafaultDelectedValue).toHaveTextContent('Option 1');
  });

  it('should check that label has #333333 color when dropdown is open', () => {
    const { getByTestId } = render(<Dropdown {...props} />);
    const label = getByTestId('DropdownComp-Label');
    expect(label).toHaveStyle('color: #333333');
  });

  it('should label color #333333', () => {
    const { getByTestId } = render(<Dropdown {...props} />);
    const label = getByTestId('DropdownComp-Label');
    expect(label).toHaveStyle('color: #333333');
  });

  it('should close when outside click happens', async () => {
    render(
      <Dropdown
        {...{
          ...props,
          onDisplayContent: onDisplayContentMock,
          onBlur: onBlurMock,
        }}
      />
    );

    const outsideElement = document.body;
    await userEvent.click(outsideElement);
    expect(onDisplayContentMock).toHaveBeenCalledWith(false);
    expect(onBlurMock).toHaveBeenCalled();
  });

  it('should render with primary border when on focus with error', () => {
    const { container } = render(<Dropdown {...propsWithError} />);
    const button = container.querySelector('[aria-haspopup="menu"]');
    expect(button).toHaveStyle('border: 2px outset buttonface');
  });

  it('should not call onChange and related functions when clicking on already selected option', async () => {
    const { container, getByTestId } = render(<Dropdown selectedId="1" {...props} />);
    const button = container.querySelector('[aria-haspopup="menu"]');
    await userEvent.click(button);
    const option1 = getByTestId('DropdownComp-li-0');
    await userEvent.click(option1);

    await waitFor(() => {
      expect(button).toHaveTextContent('Option 1');
    });
  });
});
