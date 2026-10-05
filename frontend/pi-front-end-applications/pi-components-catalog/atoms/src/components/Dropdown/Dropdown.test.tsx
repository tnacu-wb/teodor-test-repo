import '@testing-library/jest-dom';

import BritishFlagRegular from '../../assets/icons/BritishFlagRegular';
import OneAdult from '../../assets/icons/OneAdult';
import { fireEvent, render } from '../../utils/test-utils';
import Icon from '../Icon';
import Dropdown from './Dropdown.component';

const options = [
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

describe('DropdownComponent', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Dropdown placeholder="Label" options={options} />);
    expect(getByRole('button')).toBeInTheDocument();
  });

  it('should display the icon when this option is available', () => {
    const { getByTestId } = render(
      <Dropdown
        options={options}
        icon={<Icon svg={<OneAdult data-testid="dropdown-with-icon" />} />}
      />
    );
    expect(getByTestId('dropdown-with-icon')).toBeInTheDocument();
  });

  it('should have placeholder when it is passed from props', () => {
    const { getByText } = render(<Dropdown options={options} placeholder="Placeholder" />);
    expect(getByText('Placeholder')).toBeInTheDocument();
  });

  it('should have label if label prop exists', () => {
    const { getByText } = render(<Dropdown options={options} label={'Choose something'} />);
    expect(getByText('Choose something')).toBeInTheDocument();
  });

  it('should have as many options as there are in options prop', () => {
    const { container } = render(<Dropdown options={options} />);
    const displayedOptions = container.querySelectorAll('.chakra-menu__menuitem');
    expect(displayedOptions.length).toEqual(options.length);
  });

  it('should display the selected option', () => {
    const { container } = render(<Dropdown options={options} />);
    const button = container.querySelector('[aria-haspopup="menu"]');
    fireEvent.click(button);
    const displayedOptions = container.querySelectorAll('.chakra-menu__menuitem');
    fireEvent.click(displayedOptions[2]);
    expect(button).toHaveTextContent('Option 3');
  });

  it('should display disabled component when disabled prop exists', () => {
    const { container } = render(<Dropdown options={options} disabled={true} />);
    const button = container.querySelector('[aria-haspopup="menu"]');
    expect(button).toBeDisabled();
  });

  it('should check that label has primary color when dropdown is open', () => {
    const { container } = render(<Dropdown options={options} label="My label" />);
    const button = container.querySelector('[aria-haspopup="menu"]');
    const label = container.querySelector('.chakra-text');
    fireEvent.click(button);
    expect(label).toHaveStyle('color: var(--chakra-colors-primary)');
  });

  it('should check that label has lightGrey2 color when dropdown is disabled', () => {
    const { container } = render(<Dropdown options={options} label="My label" disabled={true} />);
    const button = container.querySelector('[aria-haspopup="menu"]');
    const label = container.querySelector('.chakra-text');
    fireEvent.click(button);
    expect(label).toHaveStyle('color: var(--chakra-colors-lightGrey2)');
  });

  it('should expose combobox semantics when accessibilityRole is combobox', () => {
    const { getByRole } = render(
      <Dropdown options={options} placeholder="Title" accessibilityRole="combobox" />
    );
    const combobox = getByRole('combobox');

    expect(combobox).toHaveAttribute('aria-haspopup', 'listbox');
    expect(combobox).toHaveAttribute('aria-label', 'Title');
  });

  it('should reflect the expanded state on the combobox trigger', () => {
    const { getByRole } = render(
      <Dropdown options={options} placeholder="Title" accessibilityRole="combobox" />
    );
    const combobox = getByRole('combobox');
    expect(combobox).toHaveAttribute('aria-expanded', 'false');

    fireEvent.click(combobox);
    expect(combobox).toHaveAttribute('aria-expanded', 'true');
  });

  it('should stay a menu button when accessibilityRole is not combobox', () => {
    const { queryByRole } = render(<Dropdown options={options} placeholder="Title" />);

    expect(queryByRole('combobox')).not.toBeInTheDocument();
  });

  it('should select an option when the click lands on a nested element inside it', () => {
    const onChange = jest.fn();
    const { getByRole, getByText } = render(
      <Dropdown options={options} placeholder="Title" onChange={onChange} />
    );

    fireEvent.click(getByRole('button'));

    fireEvent.click(getByText('Option 1'));

    expect(onChange).toHaveBeenCalledWith(expect.objectContaining({ id: 1 }));
  });

  it('should expose listbox and option roles when combobox is expanded', () => {
    const { getByRole, container } = render(
      <Dropdown options={options} placeholder="Title" accessibilityRole="combobox" />
    );

    fireEvent.click(getByRole('combobox'));

    const listbox = container.querySelector('[role="listbox"]');
    const renderedOptions = container.querySelectorAll('[role="option"]');

    expect(listbox).toBeInTheDocument();
    expect(renderedOptions).toHaveLength(options.length);
  });
});

describe('Dropdown - focusableRefs & tabbingAccessibility', () => {
  it('adds element to focusableRefs via ref and calls tabbingAccessibility on keydown', () => {
    const tabbingAccessibilityMock = jest.fn();

    const focusableRefs = {
      current: [] as HTMLElement[],
    };

    const { getByTestId } = render(
      <Dropdown
        dataTestId="test"
        options={[{ id: '1', label: 'Option 1' }]}
        focusableRefs={focusableRefs}
        tabbingAccessibility={tabbingAccessibilityMock}
      />
    );

    const button = getByTestId('DropdownComp-test-menuButton');

    expect(focusableRefs.current).toContain(button);

    fireEvent.keyDown(button, { key: 'Tab' });

    expect(tabbingAccessibilityMock).toHaveBeenCalledWith(
      expect.objectContaining({ key: 'Tab' }),
      focusableRefs,
      false
    );
  });

  it('does NOT call tabbingAccessibility if not provided', () => {
    const focusableRefs = {
      current: [] as HTMLElement[],
    };

    const { getByTestId } = render(
      <Dropdown
        dataTestId="test"
        options={[{ id: '1', label: 'Option 1' }]}
        focusableRefs={focusableRefs}
      />
    );

    const button = getByTestId('DropdownComp-test-menuButton');

    fireEvent.keyDown(button, { key: 'Tab' });

    expect(focusableRefs.current).toContain(button);
  });

  it('does NOT push to focusableRefs when ref is null', () => {
    const focusableRefs = {
      current: [] as HTMLElement[],
    };

    const { unmount } = render(
      <Dropdown
        dataTestId="test"
        options={[{ id: '1', label: 'Option 1' }]}
        focusableRefs={focusableRefs}
      />
    );

    const lengthAfterMount = focusableRefs.current.length;

    unmount();

    const lengthAfterUnmount = focusableRefs.current.length;

    expect(lengthAfterUnmount).toBe(lengthAfterMount);
  });
});
