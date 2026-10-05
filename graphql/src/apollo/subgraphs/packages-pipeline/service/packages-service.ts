import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { GetSavedPackagesOperaPipelineAction } from '../actions/GetSavedPackagesOperaPipelineAction';
import { GetBookingInfoAEMPipelineAction } from '../actions/GetBookingInfoAEMPipelineAction';
import { GetMealsAEMPipelineAction } from '../actions/GetMealsAEMPipelineAction';
import { GetLogoAEMPipelineAction } from '../actions/GetLogoAEMPipelineAction';
import { GetPackagesOperaPipelineAction } from '../actions/GetPackagesOperaPipelineAction';
import { logPipelineError } from '../../../utils/base-utils';
import { GetHotelInformationPipelineAction } from '../actions/GetHotelInformationPipelineAction';
import { FilterPackagesPipelineAction } from '../actions/FilterPackagesPipelineAction';

export const packages = async (
  { packagesCriteria }: { packagesCriteria: any },
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  const actions = [
    new GetSavedPackagesOperaPipelineAction(),
    new GetBookingInfoAEMPipelineAction(),
    new GetMealsAEMPipelineAction(),
    new GetLogoAEMPipelineAction(),
    new GetPackagesOperaPipelineAction()
  ];

  if (packagesCriteria.channel === 'DISTR') {
    actions.push(new GetHotelInformationPipelineAction(), new FilterPackagesPipelineAction());
  }

  try {
    let pipelineContext = await pipelineManager.manage(actions, packagesCriteria, context);

    let getSavedPackagesOpera = pipelineContext.get(ActionContextKeys.GET_SAVED_PACKAGES_OPERA);
    let getBookingInfoAem = pipelineContext.get(ActionContextKeys.GET_BOOKING_INFO_AEM);
    let getMealsAem = pipelineContext.get(ActionContextKeys.GET_MEALS_AEM);
    let getLogoAem = pipelineContext.get(ActionContextKeys.GET_LOGO_AEM);
    let getPackagesOpera = pipelineContext.get(ActionContextKeys.GET_PACKAGES_OPERA);

    getPackagesOpera.restaurant = getPackagesOpera.restaurant || {};
    getPackagesOpera.restaurant.logoSrc = getLogoAem?.restaurant?.logoSrc || null;

    const AEM_MEALS = getBookingInfoAem?.upsellItems?.map((item: any) => item.code) || [];
    let KIDS_MEAL_CODES: string[] = [];

    if (getPackagesOpera.restaurant.restaurantNotFound === false) {
      getPackagesOpera.packages.meals = getPackagesOpera.packages?.meals?.filter((meal: any) =>
        AEM_MEALS.includes(meal.id)
      );

      getPackagesOpera.packages?.meals?.forEach((meal: any) => {
        getMealsAem?.upsellItems?.forEach((upsellItem: any) => {
          if (meal.id === upsellItem.code) {
            Object.assign(meal, {
              description: upsellItem.description,
              shortDescription: upsellItem.shortDescription,
              imageSrc: upsellItem.images?.[0],
              name: upsellItem.name,
              freeBreakfastOption: upsellItem.freeBreakfastOption,
              freeBreakfastCode: upsellItem.freeBreakfastCode,
              freeBreakfastMaxPerMeal: upsellItem.freeBreakfastMaxPerMeal,
              upsellType: upsellItem.upsellType
            });
            if (meal.freeBreakfastOption) {
              KIDS_MEAL_CODES.push(upsellItem.freeBreakfastCode);
            }

            meal.freeBreakfastMaxPerMeal = upsellItem.freeBreakfastMaxPerMeal;

            upsellItem.attachments?.forEach((attachment: any) => {
              if (attachment.path?.includes('allergy') || attachment.type === 'allergy') {
                meal.allergyInfoSrc = attachment.path;
                meal.allergyInfoLabel = attachment.label;
              }
              if (attachment.type === 'menu') {
                meal.menu = {
                  name: attachment.label,
                  menuSrc: attachment.path
                };
              }
            });
          }
        });

        getBookingInfoAem?.upsellItems?.forEach((upsellItem: any) => {
          if (meal.id === upsellItem.code) {
            meal.order = upsellItem.order;
            meal.bartId = upsellItem.bartId;
          }
        });
      });

      getPackagesOpera.packages.mealsKids =
        getMealsAem?.upsellItems
          ?.filter((upsellItem: any) => KIDS_MEAL_CODES.includes(upsellItem.code))
          ?.map((upsellItem: any) => ({
            id: upsellItem.code,
            description: upsellItem.description,
            shortDescription: upsellItem.shortDescription,
            imageSrc: upsellItem.images?.[0],
            name: upsellItem.name,
            order: upsellItem.order,
            allergyInfoSrc: upsellItem.attachments?.find((a: any) => a.type === 'allergy')?.path,
            allergyInfoLabel: upsellItem.attachments?.find((a: any) => a.type === 'allergy')?.label,
            menu: upsellItem.attachments
              ?.filter((a: any) => a.type === 'menu')
              ?.map((a: any) => ({ name: a.label, menuSrc: a.path }))[0]
          })) || [];

      getPackagesOpera.restaurant.menus =
        getLogoAem?.restaurant?.menus?.map((menu: any) => ({
          name: menu.name,
          menuSrc: menu.menuSrc
        })) || [];
    }

    if (
      getPackagesOpera.restaurant.restaurantNotFound === true ||
      getPackagesOpera.packages.meals?.length === 0
    ) {
      getPackagesOpera.restaurant.noMealsFound = true;
      getPackagesOpera.restaurant.messageHeader = getBookingInfoAem?.restaurantClosedTitle;
      getPackagesOpera.restaurant.messageDescription = getBookingInfoAem?.restaurantClosedMessage;
    } else {
      getPackagesOpera.restaurant.noMealsFound = false;
    }

    if (
      getPackagesOpera.restaurant.restaurantNotFound === true ||
      getPackagesOpera.restaurant.noMealsFound === true
    ) {
      getPackagesOpera.packages.meals = [];
      getPackagesOpera.packages.mealsKids = [];
    }

    const bfadbfMeal = getPackagesOpera.packages?.meals?.find((item: any) => item.id === 'BFADBF');
    const obfproMeal = getPackagesOpera.packages?.meals?.find((item: any) => item.id === 'OBFPRO');

    if (bfadbfMeal && obfproMeal) {
      const isObfproFree = obfproMeal.isFree === true;
      getPackagesOpera.packages.meals = getPackagesOpera.packages.meals.filter((item: any) => {
        if (isObfproFree) {
          return item.id !== 'BFADBF';
        }

        return item.id !== 'OBFPRO';
      });
    }

    getPackagesOpera.privacyPolicy = getBookingInfoAem?.privacyPolicy;

    var clientName = context.headers['apollographql-client-name'];
    var clientList = ['ios', 'android'];
    var dbrPackageCodes = ['DBR', 'DBDNPR', 'DBBVPR', 'DBCHDF', 'DBPROS'];
    var packagesSectionList = ['meals', 'mealsKids', 'extrasItems'];

    let roomsSelections = [];
    if (
      getSavedPackagesOpera !== 'SKIPPED' &&
      getSavedPackagesOpera?.roomsSelections &&
      getSavedPackagesOpera?.roomsSelections.length > 0
    ) {
      roomsSelections = getSavedPackagesOpera.roomsSelections.map((room: any) => {
        let packagesSelections = [];
        let addMdp = false;
        let addDbr = false;
        let containsBFADBF = false;
        let containsDBCHDF = false;
        let containsDBPROS = false;
        let containsPIBBEV = false;
        let noOfSelectionsMdp = 0;
        let noOfSelectionsDbr = 0;
        let noOfSelectionsDBCHDF = 0;
        let noOfSelectionsDBPROS = 0;
        let noOfSelectionsBbib = 0;

        room.packagesSelection?.forEach((packageSelection: any) => {
          if (packageSelection) {
            if (
              (!packageSelection.id.includes('MD') &&
                !packageSelection.id.includes('DB') &&
                !packageSelection.id.includes('BFADBF') &&
                !packageSelection.id.includes('PIBBEV')) ||
              packageSelection.id.includes('ADDBEV')
            ) {
              packagesSelections.push(packageSelection);
            } else {
              if (packageSelection.id.includes('MD')) {
                addMdp = true;
                noOfSelectionsMdp = packageSelection.noOfSelections;
              } else if (packageSelection.id.includes('BFADBF')) {
                containsBFADBF = true;
                noOfSelectionsBbib = packageSelection.noOfSelections;
              } else if (packageSelection.id.includes('PIBBEV')) {
                containsPIBBEV = true;
                noOfSelectionsBbib = packageSelection.noOfSelections;
              }

              if (!clientList.includes(clientName)) {
                if (
                  packageSelection.id.includes('DBBVPR') ||
                  packageSelection.id.includes('DBDNPR')
                ) {
                  addDbr = true;
                  noOfSelectionsDbr = packageSelection.noOfSelections;
                } else if (packageSelection.id.includes('DBCHDF')) {
                  containsDBCHDF = true;
                  noOfSelectionsDBCHDF = packageSelection.noOfSelections;
                } else if (packageSelection.id.includes('DBPROS')) {
                  containsDBPROS = true;
                  noOfSelectionsDBPROS = packageSelection.noOfSelections;
                }
              }
            }
          }
        });

        if (addMdp) {
          packagesSelections.push({ id: 'MDP', noOfSelections: noOfSelectionsMdp });
        }
        if (addDbr) {
          packagesSelections.push({ id: 'DBR', noOfSelections: noOfSelectionsDbr });
        }
        if (containsDBCHDF) {
          packagesSelections.push({ id: 'DBCHDF', noOfSelections: noOfSelectionsDBCHDF });
        }
        if (containsDBPROS) {
          packagesSelections.push({ id: 'DBPROS', noOfSelections: noOfSelectionsDBPROS });
        }
        if (containsBFADBF) {
          if (containsPIBBEV) {
            packagesSelections.push({ id: 'BBIB', noOfSelections: noOfSelectionsBbib });
          } else {
            packagesSelections.push({ id: 'BFADBF', noOfSelections: noOfSelectionsBbib });
          }
        }

        return { reservationId: room.reservationId, packagesSelection: packagesSelections };
      });
    } else {
      roomsSelections.push({ reservationId: '', packagesSelection: [] });
    }

    const validExtrasIds = new Set(
      getPackagesOpera.packages?.extrasItems?.map((item: any) => item.id) || []
    );

    const ciolCutOffExtras = new Set(['HSCKIN', 'HSCOU2']);

    roomsSelections = roomsSelections.map((room: any) => ({
      ...room,
      packagesSelection:
        room.packagesSelection?.filter(
          (pkg: any) =>
            validExtrasIds.has(pkg.id) ||
            getPackagesOpera.packages?.meals?.some((meal: any) => meal.id === pkg.id) ||
            getPackagesOpera.packages?.mealsKids?.some((meal: any) => meal.id === pkg.id) ||
            ciolCutOffExtras.has(pkg.id)
        ) || []
    }));

    getPackagesOpera.packages.roomSelection = roomsSelections;

    getPackagesOpera.packages.roomSelectionAmendExtras =
      getSavedPackagesOpera?.roomsSelectionsAmendExtras || [];

    if (clientList.includes(clientName)) {
      packagesSectionList.forEach((key) => {
        getPackagesOpera.packages[key] = getPackagesOpera.packages?.[key]?.filter(
          (meal: any) => !dbrPackageCodes.includes(meal.id)
        );
      });
    }

    return getPackagesOpera;
  } catch (error) {
    logPipelineError(error, { packagesCriteria: packagesCriteria }, packages);
    throw error;
  }
};
