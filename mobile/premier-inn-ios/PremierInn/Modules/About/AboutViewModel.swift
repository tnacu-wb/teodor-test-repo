import UIKit

struct AboutViewModel {
    var title: String = PILocalizedString("aboutScreenTitle", comment: "About controller title")
    let navigationTheme: UINavigationController.NavigationBarColours
    var text: String = PILocalizedString("aboutPIHotelLocationsInfo", comment: "") + "\n\n"
    + PILocalizedString("aboutPIAppInfo", comment: "") + "\n\n"
    + PILocalizedString("aboutPIHotelInfo", comment: "") + "\n\n"
    + PILocalizedString("aboutLibrarAcknowledgments", comment: "")

    var version: String {
        guard let appVersionString = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String
            else { return "" }
        return "v " + appVersionString
    }

    init(navigationTheme: UINavigationController.NavigationBarColours = .premierInn) {
        self.navigationTheme = navigationTheme
    }
}
