/**
 * CardRequest object to be passed in requests; i.e: initiatePayment request
 */
export interface CardRequestData {
  cardholderName?: string;
  cardType?: string;
  cnpRequired?: boolean;
  expiryMonth?: string;
  expiryYear?: string;
  last4Digits?: string;
  logoUrl?: string;
  token?: string;
  type?: string;
}

export class CardRequest {
  [key: string]: unknown;
  cardType?: string;
  cardholderName?: string;
  cnpRequired?: boolean;
  expiryMonth?: string;
  expiryYear?: string;
  last4Digits?: string;
  logoUrl?: string;
  token?: string;
  type?: string;

  constructor({
    cardholderName,
    cardType,
    cnpRequired,
    expiryMonth,
    expiryYear,
    last4Digits,
    logoUrl,
    token,
    type,
  }: CardRequestData = {}) {
    this.cardholderName = cardholderName;
    this.cardType = cardType;
    this.cnpRequired = cnpRequired;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.last4Digits = last4Digits;
    this.logoUrl = logoUrl;
    this.token = token;
    this.type = type;
  }

  static fromRequest(data: CardRequestData): CardRequest {
    return new CardRequest(data);
  }
}
