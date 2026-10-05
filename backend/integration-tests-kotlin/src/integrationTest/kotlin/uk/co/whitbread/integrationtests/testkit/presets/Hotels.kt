package uk.co.whitbread.integrationtests.testkit.presets

import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.PackageCatalogue
import uk.co.whitbread.integrationtests.testkit.model.PackageComponent
import uk.co.whitbread.integrationtests.testkit.model.PackageComposition
import uk.co.whitbread.integrationtests.testkit.model.PackageDefinition

private fun roomType(
    roomClass: String,
    roomType: String,
    numberOfRooms: Int,
    accessible: Boolean = false,
): HotelRoomType =
    HotelRoomType(
        roomClass = roomClass,
        accessible = accessible,
        roomType = roomType,
        numberOfRooms = numberOfRooms,
    )

private val framtiRoomTypes =
    listOf(
        roomType("SE", "EXTDBL", 0),
        roomType("SE", "EXDZPL", 0),
        roomType("SE", "EXDLOW", 0),
        roomType("SE", "EXDWET", 0),
        roomType("ST", "DOUBLE", 101),
        roomType("ST", "ZPLDBL", 51),
        roomType("ST", "TWINRM", 1),
        roomType("ST", "FMTRPL", 2),
        roomType("ST", "FMTHRE", 15),
        roomType("ST", "FMQUAD", 0),
        roomType("ST", "FMFOUR", 7),
        roomType("ST", "DBLDBL", 0),
        roomType("ST", "FMBUNK", 0),
        roomType("ST", "FMTRSC", 0),
        roomType("ST", "SINGLE", 0),
        roomType("ST", "BRFDBL", 0),
        roomType("ST", "BRFZPL", 0),
        roomType("ST", "BRFTWN", 0),
        roomType("ST", "LOWDBL", 0),
        roomType("ST", "WETDBL", 3),
        roomType("ST", "LOWTWN", 0),
        roomType("ST", "WETTWN", 0),
        roomType("ST", "ACCSGL", 0),
        roomType("PP", "PPLDBL", 31),
        roomType("PP", "PDBZPL", 0),
        roomType("PP", "PFAMIL", 0),
        roomType("PP", "PPDLOW", 1),
        roomType("PP", "PPDWET", 1),
        roomType("ST", "DBLWIN", 0),
        roomType("ST", "DBLNWD", 0),
        roomType("BG", "BIGWIN", 0),
        roomType("BG", "BIGNWD", 0),
        roomType("BG", "BIGZPL", 0),
        roomType("ST", "ACCWIN", 0),
        roomType("ST", "ACCNWD", 0),
        roomType("ST", "WINCMB", 0),
        roomType("ST", "NWDCMB", 0),
        roomType("ST", "WINSPL", 0),
        roomType("ST", "NWDSPL", 0),
        roomType("SV", "VDOUBL", 0),
        roomType("PV", "VPPDBL", 0),
        roomType("SV", "VZPDBL", 0),
        roomType("SV", "VTWNRM", 0),
        roomType("SV", "VFMTRP", 0),
        roomType("SV", "VFMTHR", 0),
        roomType("SV", "VFMQUD", 0),
        roomType("SV", "VFMFOR", 0),
        roomType("SV", "VDBDBL", 0),
        roomType("SV", "VSINGL", 0),
        roomType("SV", "VBRFDB", 0, accessible = true),
    )

private val heaptiRoomTypes =
    listOf(
        roomType("ST", "DOUBLE", 19),
        roomType("ST", "ZPLDBL", 70),
        roomType("ST", "FMTRPL", 17),
        roomType("ST", "FMQUAD", 42),
        roomType("ST", "FMFOUR", 9),
        roomType("ST", "LOWDBL", 14),
        roomType("PP", "PPLDBL", 7),
        roomType("PV", "VPPDBL", 10),
        roomType("ST", "TWINRM", 1),
        roomType("ST", "FMTHRE", 0),
        roomType("ST", "DBLDBL", 0),
        roomType("ST", "FMBUNK", 0),
        roomType("ST", "FMTRSC", 0),
        roomType("ST", "SINGLE", 0),
        roomType("ST", "BRFDBL", 0),
        roomType("ST", "BRFZPL", 0),
        roomType("ST", "BRFTWN", 0),
        roomType("ST", "WETDBL", 0),
        roomType("ST", "LOWTWN", 0),
        roomType("ST", "WETTWN", 0),
        roomType("ST", "ACCSGL", 0),
        roomType("PP", "PDBZPL", 0),
        roomType("PP", "PFAMIL", 0),
        roomType("PP", "PPDLOW", 0),
        roomType("PP", "PPDWET", 0),
        roomType("ST", "DBLWIN", 0),
        roomType("SE", "EXTDBL", 0),
        roomType("ST", "DBLNWD", 0),
        roomType("SE", "EXDZPL", 0),
        roomType("BG", "BIGWIN", 0),
        roomType("SE", "EXDWET", 0),
        roomType("BG", "BIGNWD", 0),
        roomType("SE", "EXDLOW", 0),
        roomType("BG", "BIGZPL", 0),
        roomType("ST", "ACCWIN", 0),
        roomType("ST", "ACCNWD", 0),
        roomType("ST", "WINCMB", 0),
        roomType("ST", "NWDCMB", 0),
        roomType("ST", "WINSPL", 0),
        roomType("ST", "NWDSPL", 0),
        roomType("ST", "TSTDBL", 0),
        roomType("SV", "VDOUBL", 0),
        roomType("ST", "TSTSGL", 0),
        roomType("SV", "VZPDBL", 0),
        roomType("SV", "VTWNRM", 0),
        roomType("SV", "VFMTRP", 0),
        roomType("SV", "VFMTHR", 0),
        roomType("SV", "VFMQUD", 0),
        roomType("SV", "VFMFOR", 0),
        roomType("SV", "VDBDBL", 0),
    )

private val heaptiPackageCatalogue =
    PackageCatalogue(
        packages =
            listOf(
                PackageDefinition(
                    code = "BFADBF",
                    description = "Premier Inn Breakfast Food VEN",
                    price = 110.99,
                    currency = "GBP",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "EVERY_NIGHT",
                ),
                PackageDefinition(
                    code = "BFCHDF",
                    description = "Free Childrens Breakfast",
                    price = 0.0,
                    currency = "GBP",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "EVERY_NIGHT",
                ),
                PackageDefinition(
                    code = "OBFBRK",
                    description = "Premier Inn Breakfast Food",
                    price = 11.99,
                    currency = "GBP",
                    calculationRule = "PER_ADULT",
                    postingRhythm = "EVERY_NIGHT",
                ),
                PackageDefinition(
                    code = "CARPRK",
                    description = "Car Parking",
                    price = 7.0,
                    currency = "GBP",
                    calculationRule = "FLAT_RATE",
                    postingRhythm = "EVERY_NIGHT",
                ),
                PackageDefinition(
                    code = "HSCKIN",
                    description = "Early Check In",
                    price = 10.0,
                    currency = "GBP",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "ARRIVAL_NIGHT",
                    inventoryArticleNumber = "ECI",
                ),
                PackageDefinition(
                    code = "HSCOU2",
                    description = "Late Check Out 2pm",
                    price = 10.0,
                    currency = "GBP",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "LAST_NIGHT",
                    inventoryArticleNumber = "LCO2",
                ),
                PackageDefinition(
                    code = "MDP",
                    description = "Meal Deal",
                    price = 26.99,
                    currency = "GBP",
                    composition = mealDealComposition(),
                ),
                PackageDefinition(
                    code = "DBPROS",
                    description = "Bottle of Prosecco",
                    shortDescription = "Bottle of Prosecco",
                    price = 20.0,
                    currency = "GBP",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "ARRIVAL_NIGHT",
                    inventoryArticleNumber = "BOP",
                ),
            ),
        donationPackages =
            listOf(
                donationPackage("ZCHRY1", "On Line Charity Pledge GBP 3", 3.0, "GBP"),
                donationPackage("ZCHRY2", "On Line Charity Pledge GBP 0.30", 0.3, "GBP"),
                donationPackage("ZCHRY7", "On Line Charity Pledge GBP 1", 1.0, "GBP"),
            ),
    )

private val framtiPackageCatalogue =
    PackageCatalogue(
        packages =
            listOf(
                PackageDefinition(
                    code = "OBFCOM",
                    description = "Complimentary Breakfast",
                    price = 0.0,
                    currency = "EUR",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "EVERY_NIGHT",
                ),
                PackageDefinition(
                    code = "BBIB",
                    description = "Premier Inn Breakfast",
                    price = 16.9,
                    currency = "EUR",
                ),
                PackageDefinition(
                    code = "CITYTAX",
                    description = "City Tax",
                    shortDescription = "City Tax",
                    price = 2.0,
                    currency = "EUR",
                    calculationRule = "PER_PERSON",
                    postingRhythm = "EVERY_NIGHT",
                    cityTaxPurposes = setOf("LEI", "BUS"),
                ),
                PackageDefinition(
                    code = "CARPRK",
                    description = "Car Parking",
                    price = 99.0,
                    currency = "EUR",
                    calculationRule = "FLAT_RATE",
                    postingRhythm = "EVERY_NIGHT",
                ),
                PackageDefinition(
                    code = "HSCKIN",
                    description = "Early Check In",
                    price = 15.0,
                    currency = "EUR",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "ARRIVAL_NIGHT",
                    inventoryArticleNumber = "ECI",
                ),
                PackageDefinition(
                    code = "HSCOU2",
                    description = "Late Check Out 2pm",
                    price = 10.0,
                    currency = "EUR",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "LAST_NIGHT",
                    inventoryArticleNumber = "LC2",
                ),
                PackageDefinition(
                    code = "MDP",
                    description = "Meal Deal",
                    price = 29.0,
                    currency = "EUR",
                    composition = mealDealComposition(),
                ),
                PackageDefinition(
                    code = "PPFD15",
                    description = "Premier Plus EUR15 Upsell Front Desk",
                    shortDescription = "Premier Plus EUR15 Upsell Front Desk",
                    price = 15.0,
                    currency = "EUR",
                    calculationRule = "PER_ROOM",
                    postingRhythm = "EVERY_NIGHT",
                ),
            ),
        donationPackages =
            listOf(
                donationPackage("ZCHRY1", "On Line Charity Pledge EUR 3", 3.0, "EUR"),
                donationPackage("ZCHRY2", "On Line Charity Pledge EUR 0.30", 0.3, "EUR"),
                donationPackage("ZCHRY7", "On Line Charity Pledge EUR 1", 1.0, "EUR"),
            ),
    )

private fun donationPackage(
    code: String,
    description: String,
    price: Double,
    currency: String,
): PackageDefinition =
    PackageDefinition(
        code = code,
        description = description,
        price = price,
        currency = currency,
        calculationRule = "FlatRate",
        postingRhythm = "ArrivalNight",
    )

private fun mealDealComposition(): PackageComposition =
    PackageComposition(
        description = "Meal Deal Package",
        members =
            listOf(
                PackageComponent("MD2DIN", "Meal Deal Dinner"),
                PackageComponent("MDBEVA", "Meal Deal Dinner Beverage"),
                PackageComponent("MDBFST", "Meal Deal Breakfast Food"),
            ),
    )

object Hotels {
    val HEAPTI =
        Hotel(
            hotelId = "HEAPTI",
            shortId = "AQN",
            name = "London Heathrow Airport (M4/J4)",
            addressLine = "Shepiston Lane",
            city = "Hayes",
            postcode = "UB3 1RW",
            phone = "01582 424200",
            availableRoomTypes = heaptiRoomTypes,
            packageCatalogue = heaptiPackageCatalogue,
            // No rates - specs provide their own
        )

    val FRAMTI =
        Hotel(
            hotelId = "FRAMTI",
            shortId = "FRM",
            name = "London Farringdon (Smithfield)",
            addressLine = "53-57 St John Street",
            city = "London",
            postcode = "EC1M 4AN",
            phone = "0333 003 8101",
            availableRoomTypes = framtiRoomTypes,
            currency = "EUR",
            packageCatalogue = framtiPackageCatalogue,
            // No rates - specs provide their own
        )
}
