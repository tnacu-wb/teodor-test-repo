import { HashType } from '@whitbread-eos/api';

import { hashString } from './hashing';

describe('Hash String Conversion', () => {
  beforeEach(() => {
    global.crypto = {
      subtle: {
        digest: jest.fn(async (): Promise<ArrayBufferLike> => {
          return new Uint8Array([1, 2, 3, 4]).buffer;
        }),
      },
    } as any;
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should hash a string using SHA256', async () => {
    const result = await hashString('test', HashType.SHA256);
    expect(result).toBe('9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08');
  });

  it('should hash a string using SHA384', async () => {
    const result = await hashString('test', HashType.SHA384);
    expect(result).toBe(
      '768412320f7b0aa5812fce428dc4706b3cae50e02a64caa16a782249bfe8efc4b7ef1ccb126255d196047dfedf17a0a9'
    );
  });

  it('should hash a string using SHA512', async () => {
    const result = await hashString('test', HashType.SHA512);
    expect(result).toBe(
      'ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff'
    );
  });

  it('should log an error for an invalid hash type', async () => {
    const consoleErrorSpy = jest.spyOn(console, 'error');
    await hashString('test', 'INVALID_HASH_TYPE' as HashType);
    expect(consoleErrorSpy).toHaveBeenCalledWith(
      `Failed to hash the provided string: Unsupported hash type: INVALID_HASH_TYPE`
    );
  });
});
