import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';

import { LoadMore } from '~components/innBusiness/LoadMoreButton';
import { LoadMoreProps } from '~components/innBusiness/LoadMoreButton/load-more';

const mockProps: LoadMoreProps = {
  getExtraRows: async () => {
    return {
      result: [],
      pageToken: 'asdzxc',
    };
  },
  loadMoreLabel: 'Load more',
  testId: 'DataTablePage',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getPathForLocale: () => {
      return '/';
    },
  };
});

describe('LoadMore Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render LoadMore component ', async () => {
    const { getByTestId } = render(<LoadMore {...mockProps} />);

    await waitFor(async () => {
      expect(getByTestId('DataTablePage-LoadMoreButton')).toBeInTheDocument();
      expect(getByTestId('DataTablePage-LoadMoreButton-label')).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(getByTestId('DataTablePage-LoadMoreButton'));
    });
  });
});
