/**
 * piba cards criteria object required in get allPibaCards request
 */
export interface PibaCardsCriteriaData {
  includeCancelledCards?: boolean;
  maxRows?: number;
  pageNumber?: number;
  showMyCards?: boolean;
  userId?: string;
}

export class PibaCardsCriteria {
  [key: string]: unknown;
  includeCancelledCards?: boolean;
  maxRows?: number;
  pageNumber?: number;
  showMyCards?: boolean;
  userId?: string;

  constructor(data: PibaCardsCriteriaData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: PibaCardsCriteriaData): PibaCardsCriteria {
    return new PibaCardsCriteria(data);
  }
}
