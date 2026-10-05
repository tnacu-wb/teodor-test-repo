import createLogger from '../log/logger';
import { basename } from 'path';

const log = createLogger(basename(__filename));

export function validateBasketReference(context: any, basketReference: string) {
  log.info(`Validating basket reference against the basketId header for: ${basketReference}`);
  try {
    const basketIds = context.headers?.['wb-basket-id'];
    if (!basketIds) {
      throw new Error('Unauthorized: Basket reference not found in header(empty basketIds))');
    }
    const basketIdArray = basketIds
      .split(',')
      .map((id: string) => id.trim())
      .filter(Boolean);

    const decodedBasketIds = basketIdArray.map((id: string) =>
      Buffer.from(id, 'base64').toString('utf-8')
    );

    if (!decodedBasketIds.includes(basketReference)) {
      throw new Error('Unauthorized: Basket reference not matches with basketId header');
    }
  } catch (error: Error | any) {
    log.error(
      `Validating basket reference against the basketId header failed for: ${basketReference}`
    );
    throw error;
  }
}
