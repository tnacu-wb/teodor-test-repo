import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { CompanyContainer } from './CompanyContainer';

const mockProps = {
  title: 'Main contact',
  onEditChange: jest.fn(),
  showEditButton: true,
  onShowEditButtonChange: jest.fn(),
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('CompanyContainer Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyContainer component', async () => {
    const { getByTestId } = render(<CompanyContainer {...mockProps} />);

    expect(getByTestId('CompanyContainer')).toBeInTheDocument();
  });
});
