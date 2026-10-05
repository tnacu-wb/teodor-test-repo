import { PipelineAction } from '../../../pipeline/PipelineAction';
import { ActionContextKeys } from './ActionContextKeys';
import { toIsoDate } from '../../../utils/base-utils';

export class FilterPackagesPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: any): Promise<void> {
    let hotelInformation = pipelineContext.get(ActionContextKeys.GET_HOTEL_INFORMATION);
    let operaPackages = pipelineContext.get(ActionContextKeys.GET_PACKAGES_OPERA);
    const filteredPackages = this.filterPackages(args, hotelInformation, operaPackages);
    pipelineContext.set(ActionContextKeys.GET_PACKAGES_OPERA, filteredPackages);
  }

  filterPackages(args: any, hotelInformation: any, operaPackages: any): any {
    let filteredPackages = operaPackages;
    hotelInformation?.ancillaryCloseout?.items?.forEach((ancillaryCloseoutItem: any) => {
      if (ancillaryCloseoutItem) {
        const isWithinRange = this.isWithinRange(
          ancillaryCloseoutItem,
          args.startDate,
          args.endDate
        );
        const upsellCodes = new Set(
          ancillaryCloseoutItem.upsellCodes.split(',').map((code: string) => code.trim())
        );

        filteredPackages = this.removeMealsIfWithinRange(
          filteredPackages,
          upsellCodes,
          isWithinRange
        );
      }
    });

    return filteredPackages;
  }

  isWithinRange(ancillaryCloseoutItem: any, arrivalDate: string, departureDate: string): boolean {
    if (ancillaryCloseoutItem.startDate && ancillaryCloseoutItem.endDate) {
      const startDate = Date.parse(toIsoDate(ancillaryCloseoutItem.startDate));
      const endDate = Date.parse(toIsoDate(ancillaryCloseoutItem.endDate));
      const arrivalDateParsed = Date.parse(arrivalDate);
      const departureDateParsed = Date.parse(departureDate);

      return (
        arrivalDateParsed >= startDate &&
        arrivalDateParsed <= endDate &&
        departureDateParsed >= startDate &&
        departureDateParsed <= endDate
      );
    }

    return false;
  }

  removeMealsIfWithinRange(operaPackages: any, upsellCodes: Set<any>, isWithinRange: boolean) {
    if (isWithinRange) {
      operaPackages.packages.meals = operaPackages?.packages?.meals?.filter(
        (meal: any) => !upsellCodes.has(meal.id)
      );
    }

    return operaPackages;
  }
}
