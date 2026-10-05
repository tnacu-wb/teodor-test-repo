import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import BreadCrumb from './Breadcrumb.component';

const mockTypographyResolver = (
  legacyTypography: object,
  semanticTypography: { textStyle?: string }
) => (semanticTypography?.textStyle ? semanticTypography : legacyTypography);

jest.mock('@whitbread-eos/utils', () => ({
  useSemanticTypography: () => mockTypographyResolver,
  formatAssetsUrl: (url: string) => url,
}));

const MockedNextLink = () => null;

const breadcrumbItems = [
  { url: '#input1', name: 'Input 1' },
  { url: '#input2', name: 'Input 2' },
  { url: '#', isCurrentPage: true, name: 'Input 3' },
];

describe('Breadcrumb', () => {
  it('render the Breadcrumb Component', () => {
    const { container } = render(<BreadCrumb items={breadcrumbItems} />);
    expect(container.querySelector('ol')).toBeInTheDocument();
  });

  it('render the Breadcrumb Component when using a custom link component', () => {
    const { container } = render(
      <BreadCrumb items={breadcrumbItems} linkComponent={MockedNextLink} />
    );
    expect(container.querySelector('ol')).toBeInTheDocument();
  });

  it('has the exact breadcrumb items', () => {
    const { getByText } = render(<BreadCrumb items={breadcrumbItems} />);
    breadcrumbItems.forEach((item) => {
      expect(getByText(item.name)).toBeInTheDocument();
    });
  });
});
