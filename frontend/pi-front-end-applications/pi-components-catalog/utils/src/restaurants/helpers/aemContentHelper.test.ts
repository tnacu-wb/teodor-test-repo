import { dropdrownManipulatorFn, replaceInputPlaceholder } from './aemContentHelper';

describe('dropdrownManipulatorFn', () => {
  it('should return text to properties', () => {
    const value = dropdrownManipulatorFn('text, text');
    expect(value).toEqual([
      { label: 'text', id: 1 },
      { label: 'text', id: 2 },
    ]);
  });
});

describe('decodeFromBase64', () => {
  it('should return base64 to text', () => {
    const value = replaceInputPlaceholder('this is a ${restaurant}', 'test');
    expect(value).toEqual('this is a test');
  });
});
