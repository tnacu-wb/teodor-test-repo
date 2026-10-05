import type { PackageSelectionInput } from './packageSelectionInput';

/**
 * RoomPackageSelectionByIdInput object required in updateReservationPackagesByReservation request
 */
export interface RoomPackageSelectionByIdInputData {
  packagesSelection?: PackageSelectionInput[];
  reservationId?: string;
}

export class RoomPackageSelectionByIdInput {
  [key: string]: unknown;
  packagesSelection?: PackageSelectionInput[];
  reservationId?: string;

  /**
   * RoomPackageSelectionByIdInput constructor
   * @param data data
   * @param data.packagesSelection packageSelection
   * @param data.reservationId reservationId
   */
  constructor({ packagesSelection, reservationId }: RoomPackageSelectionByIdInputData = {}) {
    this.packagesSelection = packagesSelection;
    this.reservationId = reservationId;
  }

  static fromRequest(data: RoomPackageSelectionByIdInputData): RoomPackageSelectionByIdInput {
    return new RoomPackageSelectionByIdInput(data);
  }
}
