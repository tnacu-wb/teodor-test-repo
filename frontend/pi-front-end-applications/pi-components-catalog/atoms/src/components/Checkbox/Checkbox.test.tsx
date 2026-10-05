import { Icon, Text } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import Checkbox from './Checkbox.component';

describe('Checkbox', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Checkbox />);

    const checkbox = getByRole('checkbox');

    expect(checkbox).toBeInTheDocument();
  });
  it('should select the checkbox if it is not checked and to check it ,', () => {
    const { getByRole } = render(<Checkbox />);
    const checkbox = getByRole('checkbox');
    expect(checkbox).not.toBeChecked();

    fireEvent.click(checkbox);
    expect(checkbox).toBeChecked();
  });

  it('render the Checkbox component with a children text', () => {
    const checkboxText =
      'I have read, understand and accept the Terms and Conditions. Cancellations must be made within 24 hours after booking.';
    const { getByText } = render(
      <Checkbox>
        <>
          <Text>{checkboxText}</Text>
        </>
      </Checkbox>
    );
    getByText(checkboxText);
  });
  it('render the Checkbox component with label and icon', () => {
    const { getByTestId } = render(
      <Checkbox>
        <>
          <Text>
            Label <Icon data-testid="checkbox-icon" />
          </Text>
        </>
      </Checkbox>
    );
    const icon = getByTestId('checkbox-icon');
    expect(icon).toBeInTheDocument();
  });
  it('should display the border if the isBorder prop has been enabled', () => {
    const checkboxText =
      'I have read, understand and accept the Terms and Conditions. Cancellations must be made within 24 hours after booking.';
    const { getByTestId } = render(
      <Checkbox variant="border">
        <>
          <Text>{checkboxText}</Text>
        </>
      </Checkbox>
    );
    const wrapper = getByTestId('box-wrapper');
    expect(wrapper).toBeInTheDocument();
  });
});
