import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { CentrallyStoredCardStatus } from './centrally-stored-card-status';

const mockProps = {
  expiryDate: '',
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('CentrallyStoredCardStatus Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CentrallyStoredCardStatus component with date in the past', async () => {
    mockProps.expiryDate = '06/00';
    const { getByText } = render(<CentrallyStoredCardStatus {...mockProps} />);

    expect(getByText('cardMgmt.cardStatus.options.expired')).toBeInTheDocument();
  });

  it('should render CentrallyStoredCardStatus component with date in the future', async () => {
    mockProps.expiryDate = '06/99';
    const { getByText } = render(<CentrallyStoredCardStatus {...mockProps} />);

    expect(getByText('cardMgmt.cardStatus.options.active')).toBeInTheDocument();
  });
});
