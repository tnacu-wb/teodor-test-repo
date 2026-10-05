import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import Section from './Section';

describe('Section', () => {
  it('render the Section component with children', () => {
    const { getByText } = render(
      <Section>
        <>
          <p>child 1</p>
          <p>child 2</p>
        </>
      </Section>
    );
    getByText('child 1');
    getByText('child 2');
  });
});
