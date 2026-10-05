import { PackageSelection } from './packageSelection';

/**
 * RoomSelection object present in packages response
 */
export class RoomSelection {
  [key: string]: unknown;
  packagesSelection: PackageSelection[] = [];
  reservationId?: string;

  /**
   * RoomSelection constructor
   * @param data object data
   * @param data.roomSelection roomSelection
   */
  constructor(data: { roomSelection?: { packagesSelection?: Array<Record<string, unknown>>; reservationId?: string } } = {}) {
    const roomSelection = data.roomSelection ?? {};
    const packagesSelection = roomSelection.packagesSelection ?? [];
    this.packagesSelection = packagesSelection.map((packageObj) => new PackageSelection({ packageData: packageObj }));
    this.reservationId = roomSelection.reservationId;
  }

  static fromResponse(data: { roomSelection?: { packagesSelection?: Array<Record<string, unknown>>; reservationId?: string } }): RoomSelection {
    return new RoomSelection(data);
  }

}
