/**
 * PackageSelection object present in packages response
 */
export class PackageSelection {
  [key: string]: unknown;
  id?: string;
  noOfSelections?: number;

  /**
   * PackageSelection constructor
   * @param data packageData
   * @param data.packageData packageData
   */
  constructor(data: { packageData?: { id?: string; noOfSelections?: number } } = {}) {
    const packageData = data.packageData ?? {};
    this.id = packageData.id;
    this.noOfSelections = packageData.noOfSelections;
  }

  static fromResponse(data: { packageData?: { id?: string; noOfSelections?: number } }): PackageSelection {
    return new PackageSelection(data);
  }
}
