import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import Card from './index';

describe('Card', () => {
  it('render the Card component with a children', () => {
    const { getByText } = render(
      <Card>
        <>
          <p>child 1</p>
          <p>child 2</p>
        </>
      </Card>
    );
    getByText('child 1');
    getByText('child 2');
  });
});
