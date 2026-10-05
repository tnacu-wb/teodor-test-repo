import { isObjectNotEmpty } from './objectNotEmpty';

describe('Object not empty', () => {
  it('should return true if at least one property is not empty', () => {
    const obj = {
      title: 'Mrs',
      firstName: '',
      lastName: '',
      emailAddress: '',
      id: '',
    };

    const result = isObjectNotEmpty(obj);

    expect(result).toBe(true);
  });

  it('should return false if all properties are empty', () => {
    const obj = {
      title: '',
      firstName: '',
      lastName: '',
      emailAddress: '',
      id: '',
    };

    const result = isObjectNotEmpty(obj);

    expect(result).toBe(false);
  });
});
