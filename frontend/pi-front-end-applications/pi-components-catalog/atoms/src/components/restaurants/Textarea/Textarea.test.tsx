import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import Textarea from './Textarea.component';

describe('TextareaComponent', () => {
  it('render the Textarea Component', () => {
    const { getByRole } = render(
      <Textarea size="md" variant="outline" placeholder="Enter text here ..." />
    );
    expect(getByRole('textbox')).toBeInTheDocument();
  });
});
