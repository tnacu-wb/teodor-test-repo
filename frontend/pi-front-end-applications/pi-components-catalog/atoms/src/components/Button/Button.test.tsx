import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import ButtonComponent from './Button.component';

describe('ButtonComponent', () => {
  it('render the Button Component', () => {
    const { getByRole } = render(
      <ButtonComponent size="md" variant="primary">
        Primary
      </ButtonComponent>
    );
    expect(getByRole('button')).toBeInTheDocument();
  });
  it('has the exact label', () => {
    const labels = ['Primary Button', 'Secondary Button', 'Tertiary Button'];
    labels.forEach((label) => {
      const { getByText } = render(
        <ButtonComponent size="md" variant="secondary">
          {label}
        </ButtonComponent>
      );
      expect(getByText(label)).toBeInTheDocument();
    });
  });
});
