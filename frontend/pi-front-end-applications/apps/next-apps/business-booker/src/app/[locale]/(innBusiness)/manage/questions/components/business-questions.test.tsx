import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { BusinessQuestions } from './business-questions';

const mockProps = {
  purchaseOrderManagement: {
    managementHeader: 'Mock Header',
    active: true,
  },
  customerReferenceManagement: {
    managementHeader: 'Mock Header',
    active: false,
  },
  icons: {},
  companyId: 'COMP_adasdad13131',
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('BusinessQuestions Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render BusinessQuestions component', async () => {
    const { getByTestId } = render(<BusinessQuestions {...mockProps} />);

    expect(getByTestId('BusinessQuestions-container')).toBeInTheDocument();
  });
});
