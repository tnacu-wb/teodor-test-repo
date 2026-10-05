//
//  MealOption+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

extension MealOption {
	var mealOptionRowModel: MealOptionRowModel {
		MealOptionRowModel(option: self, title: title, subTitle: description, selected: false)
	}

	var title: String {
		switch self {
		case .premierInnBreakfast:
			return PILocalizedString("userPreferencePremierInnBreakfast", comment: "Premier Inn Breakfast")
		case .continentalBreakfast:
			return PILocalizedString("userPreferenceContinentalBreakfast", comment: "Continental Breakfast")
		case .mealDeal:
			return PILocalizedString("userPreferenceMealDeal", comment: "Meal Deal")
		default:
			return PILocalizedString("userPreferenceNoMeal", comment: "None")
		}
	}

	var description: String {
		switch self {
		case .premierInnBreakfast:
			return PILocalizedString("userPreferencePremierInnBreakfastDescription", comment: "Premier Inn Breakfast")
		case .continentalBreakfast:
			return PILocalizedString("userPreferenceContinentalBreakfastDescription", comment: "Continental Breakfast")
		case .mealDeal:
			return PILocalizedString("userPreferenceMealDealDescription", comment: "Meal Deal")
		default:
			return PILocalizedString("userPreferenceNoMealDescription", comment: "None")
		}
	}
}
