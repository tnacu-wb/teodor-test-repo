import * as ApiLibrary from './index';

describe('Api', () => {
  it('should have exports', () => {
    expect(ApiLibrary).toEqual(expect.any(Object));
  });

  it('should not have undefined exports', () => {
    for (const k of Object.keys(ApiLibrary)) expect(ApiLibrary).not.toHaveProperty(k, undefined);
  });
});
