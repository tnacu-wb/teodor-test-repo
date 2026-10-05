import { waitForElement } from './waitForElement';

describe('waitForElement Method', () => {
  beforeAll(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.clearAllMocks();
    jest.clearAllTimers();
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  it('should resolve when the element is found', async () => {
    const mockElement = {};
    document.querySelector = jest.fn().mockReturnValue(mockElement);

    const promise = waitForElement('.selector');
    jest.runAllTimers();

    await expect(promise).resolves.toEqual(mockElement);
  });

  it('should reject when the element is not found within maxTimeOut', async () => {
    document.querySelector = jest.fn().mockReturnValue(null);

    const promise = waitForElement('.selector', 100);
    jest.runAllTimers();

    await expect(promise).rejects.toStrictEqual(
      new Error('Something went wrong when waiting for element')
    );
  });
});
