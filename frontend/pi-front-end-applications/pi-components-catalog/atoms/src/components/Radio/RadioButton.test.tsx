import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import RadioButton from './RadioButton.component';

describe('RadioButton', () => {
  it('should render the component', () => {
    const { getByRole } = render(<RadioButton />);

    const radio = getByRole('radio');

    expect(radio).toBeInTheDocument();
  });
  it('should select the radio button if it is pressed and after pressing it again, it should remain selected', () => {
    const { getByRole } = render(<RadioButton />);
    const radio = getByRole('radio');
    expect(radio).not.toBeChecked();

    fireEvent.click(radio);
    expect(radio).toBeChecked();

    fireEvent.click(radio);
    expect(radio).toBeChecked();
  });
  it('should change the border colour if it isChecked is enabled', () => {
    const { getByTestId } = render(
      <RadioButton
        isChecked={true}
        padding={{
          mobile: 'var(--chakra-space-xs) 0',
          sm: 'var(--chakra-space-xmd) var(--chakra-space-md) var(--chakra-space-xmd) var(--chakra-space-xmd)',
        }}
      />
    );
    const wrapper = getByTestId(/wrapper/i);

    expect(wrapper).toHaveStyle('border: 2px solid var(--chakra-colors-primary)');
  });
  it('should clear the border if the borderless variant is enabled', () => {
    const { getByTestId } = render(<RadioButton isChecked={true} variant="borderless" />);
    const wrapper = getByTestId(/wrapper/i);

    expect(wrapper).toHaveStyle('borderWidth: 0px');
  });

  it('should make border look like top group border if listindex is 0', () => {
    const { getByTestId } = render(
      <RadioButton isChecked={true} variant="borderless" listIndex={0} />
    );
    const wrapper = getByTestId(/wrapper/i);

    expect(wrapper).toHaveStyle('borderWidth: 0px');
  });

  it('should make border look like bottom group border if listindex is last', () => {
    const { getByTestId } = render(
      <RadioButton isChecked={true} variant="borderless" listIndex={'last'} />
    );
    const wrapper = getByTestId(/wrapper/i);

    expect(wrapper).toHaveStyle('border-radius: 0 0 var(--chakra-space-1) var(--chakra-space-1)');
  });

  it('should make border have 1px width if listindex is left and not checked with borders', () => {
    const { getByTestId } = render(<RadioButton isChecked={false} listIndex={'left'} />);
    const wrapper = getByTestId(/wrapper/i);

    expect(wrapper).toHaveStyle('borderWidth: 1px');
  });

  it('should make border have border radius only on left corners if listindex is left', () => {
    const { getByTestId } = render(
      <RadioButton isChecked={true} variant="borderless" listIndex={'left'} />
    );
    const wrapper = getByTestId(/wrapper/i);

    expect(wrapper).toHaveStyle('border-radius: var(--chakra-space-1) 0 0 var(--chakra-space-1)');
  });

  it('should make border look like middle group border if listindex is diff than 0 or last', () => {
    const { getByTestId } = render(
      <RadioButton isChecked={true} variant="borderless" listIndex={1} />
    );
    const wrapper = getByTestId(/wrapper/i);

    expect(wrapper).toHaveStyle('border-radius: 0px');
  });

  it('renders correctly with withOutline set to true and isChecked', () => {
    const { getByTestId } = render(
      <RadioButton isChecked={true} withOutline={true} type="test" data-testid="radio-button" />
    );

    const radioBoxWrapper = getByTestId('radio-box-wrapper_test');
    expect(radioBoxWrapper).toHaveStyle({
      outline: '4px solid var(--chakra-colors-primary)',
      outlineOffset: '-4px',
      borderWidth: '1px',
      borderColor: 'lightGrey1',
    });
  });

  it('renders correctly with withOutline set to true and isChecked as false', () => {
    const { getByTestId } = render(
      <RadioButton isChecked={false} withOutline={true} type="test" data-testid="radio-button" />
    );

    const radioBoxWrapper = getByTestId('radio-box-wrapper_test');
    expect(radioBoxWrapper).toHaveStyle({
      outline: '',
      outlineOffset: '-4px',
      borderWidth: '1px',
      borderColor: 'lightGrey1',
    });
  });
});
