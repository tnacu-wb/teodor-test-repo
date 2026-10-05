import '@testing-library/jest-dom';
import { formatDate } from '@whitbread-eos/utils';

import { fireEvent, render } from '../../utils/test-utils';
import AgentMemoCard from './AgentMemoCard.component';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('AgentMemoCard', function () {
  const memo = {
    ids: [
      {
        reservationId: '2941618',
        memoIds: ['148587'],
      },
    ],
    description:
      ' Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',
    createdOn: '2023-08-04',
    modifiedOn: '2023-08-04',
    memoType: 'OPERA',
  };

  const getComponent = () => <AgentMemoCard memo={memo} />;

  it('should render AgentMemoCard', () => {
    const { getByTestId } = render(getComponent());
    expect(getByTestId('AgentMemoCard-Container')).toBeVisible();
  });

  it('should render AgentMemoCard header and modifiedOn text', () => {
    const { getByTestId } = render(getComponent());
    expect(getByTestId('AgentMemoCard-Header')).toBeVisible();
    expect(getByTestId('AgentMemoCard-Date').textContent).toEqual(
      formatDate(memo.modifiedOn, 'dd/MM/yyyy - HH:mm')
    );
  });

  it('should render AgentMemoCard content and description text', () => {
    const { getByTestId } = render(getComponent());
    expect(getByTestId('AgentMemoCard-Content')).toBeVisible();
    expect(getByTestId('AgentMemoCard-Description').textContent).toEqual(
      `ccui.agentMemo.createdBy: ${memo.description}`
    );
  });

  it('should render AgentMemoCard description text with See more button', () => {
    const { getByTestId } = render(getComponent());
    expect(getByTestId('AgentMemoCard-Expand-Collapse')).toBeInTheDocument();
    expect(getByTestId('AgentMemoCard-Expand-Collapse').textContent).toEqual(
      'ccui.agentMemo.seeMore'
    );
  });

  it('should render AgentMemoCard description text with See less button', () => {
    const { getByTestId } = render(getComponent());
    const seeMoreLink = getByTestId('AgentMemoCard-Expand-Collapse');
    fireEvent.click(seeMoreLink);
    expect(getByTestId('AgentMemoCard-Expand-Collapse').textContent).toEqual(
      'ccui.agentMemo.seeLess'
    );
  });
});
