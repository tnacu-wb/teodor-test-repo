import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import DescriptionBox from './DescriptionBox.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  renderSanitizedHtml: jest.fn().mockImplementation(() => {
    return 'Test description';
  }),
}));

describe('DescriptionBox', () => {
  it('should render the DescriptionBox component', () => {
    const html = '<p>Test description</p>';
    const { getByText } = render(<DescriptionBox html={html} />);
    expect(getByText('Test description')).toBeInTheDocument();
  });
});
