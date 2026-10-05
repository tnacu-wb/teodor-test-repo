import { validateBasketReference } from '../../../apollo/utils/basket-utils';

jest.mock('../../../apollo/log/logger', () => () => ({
  info: jest.fn(),
  error: jest.fn()
}));

describe('validateBasketReference', () => {
  const validBasketId = Buffer.from('basket-123').toString('base64');

  it('should pass when basketReference matches decoded basketIds', () => {
    const context = { headers: { 'wb-basket-id': validBasketId } };
    expect(() => validateBasketReference(context, 'basket-123')).not.toThrow();
  });

  it('throws error when wb-basket-id header is missing', () => {
    const context = { headers: {} };
    expect(() => validateBasketReference(context, 'basket-123')).toThrow(
      'Unauthorized: Basket reference not found in header(empty basketIds))'
    );
  });

  it('throws error when Wb-basket-id header is empty', () => {
    const context = { headers: { 'wb-basket-id': '' } };
    expect(() => validateBasketReference(context, 'basket-123')).toThrow(
      'Unauthorized: Basket reference not found in header(empty basketIds))'
    );
  });

  it('throws error when basketReference does not match any decoded basketIds', () => {
    const context = { headers: { 'wb-basket-id': validBasketId } };
    expect(() => validateBasketReference(context, 'basket-456')).toThrow(
      'Unauthorized: Basket reference not matches with basketId header'
    );
  });

  it('throws error when Wb-basket-id contains only empty values', () => {
    const context = { headers: { 'wb-basket-id': ',' } };
    expect(() => validateBasketReference(context, 'basket-123')).toThrow(
      'Unauthorized: Basket reference not matches with basketId header'
    );
  });

  it('throws error when Wb-basket-id contains malformed base64', () => {
    const context = { headers: { 'wb-basket-id': 'not-base64' } };
    expect(() => validateBasketReference(context, 'basket-123')).toThrow(
      'Unauthorized: Basket reference not matches with basketId header'
    );
  });
});
