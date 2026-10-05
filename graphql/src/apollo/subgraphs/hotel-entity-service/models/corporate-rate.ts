export class CorporateRate {
  corporateId?: string;
  ratePlanSets?: string[];

  constructor(data: any) {
    this.corporateId = data.corporateId;
    this.ratePlanSets = data.ratePlanSets;
  }
}
