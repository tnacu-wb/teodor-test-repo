import { initializeUnleash } from '@whitbread-eos/utils/instrumentation';

import { initNode } from './instrumentation-node';

jest.mock('@whitbread-eos/utils/instrumentation', () => ({
  initializeUnleash: jest.fn(),
}));

const mockedInitializeUnleash = jest.mocked(initializeUnleash);

describe('instrumentation-node initNode', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    delete process.env.NEXT_PUBLIC_UNLEASH_APP_NAME;
  });

  afterEach(() => {
    delete process.env.NEXT_PUBLIC_UNLEASH_APP_NAME;
  });

  it('uses NEXT_PUBLIC_UNLEASH_APP_NAME when provided', async () => {
    process.env.NEXT_PUBLIC_UNLEASH_APP_NAME = 'custom-bb-app';

    await initNode();

    expect(mockedInitializeUnleash).toHaveBeenCalledTimes(1);
    expect(mockedInitializeUnleash).toHaveBeenCalledWith('custom-bb-app');
  });

  it('falls back to business-booker when app name is not set', async () => {
    await initNode();

    expect(mockedInitializeUnleash).toHaveBeenCalledTimes(1);
    expect(mockedInitializeUnleash).toHaveBeenCalledWith('business-booker');
  });
});
