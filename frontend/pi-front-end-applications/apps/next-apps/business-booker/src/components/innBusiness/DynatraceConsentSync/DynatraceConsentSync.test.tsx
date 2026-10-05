import { render } from '@testing-library/react';

import DynatraceConsentSync from './DynatraceConsentSync';

const mockSyncDynatraceConsent = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ONETRUST_GROUPS_UPDATED_EVENT: 'OneTrustGroupsUpdated',
  syncDynatraceConsentFromOneTrust: (options: unknown) => mockSyncDynatraceConsent(options),
}));

describe('DynatraceConsentSync', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('syncs existing consent and listens for consent updates', () => {
    const { unmount } = render(<DynatraceConsentSync isDynatraceRumCookieConsentEnabled={true} />);

    expect(mockSyncDynatraceConsent).toHaveBeenCalledWith({ isEnabled: true });

    window.dispatchEvent(new Event('OneTrustGroupsUpdated'));
    expect(mockSyncDynatraceConsent).toHaveBeenCalledTimes(2);

    unmount();
    window.dispatchEvent(new Event('OneTrustGroupsUpdated'));
    expect(mockSyncDynatraceConsent).toHaveBeenCalledTimes(2);
  });

  it('forwards disabled consent when the feature flag is disabled', () => {
    render(<DynatraceConsentSync isDynatraceRumCookieConsentEnabled={false} />);

    expect(mockSyncDynatraceConsent).toHaveBeenCalledWith({ isEnabled: false });
  });
});
