/**
 * PackageSelectionInput object required in updateReservationPackagesByReservation request
 */
export interface PackageSelectionInputData {
  id?: string;
  noOfSelections?: number;
}

export class PackageSelectionInput {
  [key: string]: unknown;
  id?: string;
  noOfSelections?: number;

  constructor({ id, noOfSelections }: PackageSelectionInputData = {}) {
    this.id = id;
    this.noOfSelections = noOfSelections;
  }

  static fromRequest(data: PackageSelectionInputData): PackageSelectionInput {
    return new PackageSelectionInput(data);
  }
}
