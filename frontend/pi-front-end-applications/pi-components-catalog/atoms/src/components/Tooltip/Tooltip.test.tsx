import '@testing-library/jest-dom';

import { Alert as AlertIcon } from '../../assets/icons';
import { act, render, userEvent, waitFor } from '../../utils/test-utils';
import Tooltip from './Tooltip.component';

describe('Tooltip', () => {
  it('should always display the description', async () => {
    const { getByRole, queryByText } = render(
      <Tooltip description="Notification description" variant="alert" svg={<AlertIcon />}>
        <h1>Tooltip component</h1>
      </Tooltip>
    );

    const children = getByRole('heading', { name: /tooltip component/i });

    await waitFor(() => {
      expect(children).toBeInTheDocument();
    });

    const hidden = queryByText(/description name/i);

    await waitFor(() => {
      expect(hidden).not.toBeInTheDocument();
    });

    await act(async () => {
      userEvent.hover(children);
    });

    await waitFor(() => {
      expect(queryByText(/notification description/i)).toBeInTheDocument();
    });
  });
  it('should display the title if the isTitle prop is enabled', async () => {
    const { getByRole, queryByText } = render(
      <Tooltip
        title="Notification title"
        description="Notification description"
        variant="alert"
        svg={<AlertIcon />}
      >
        <h1>Tooltip component</h1>
      </Tooltip>
    );

    const children = getByRole('heading', { name: /tooltip component/i });

    await waitFor(() => {
      expect(children).toBeInTheDocument();
    });

    const hidden = queryByText(/description name/i);

    await waitFor(() => {
      expect(hidden).not.toBeInTheDocument();
    });

    await act(async () => {
      userEvent.hover(children);
    });

    await waitFor(() => {
      expect(queryByText(/notification title/i)).toBeInTheDocument();
    });
  });
});
