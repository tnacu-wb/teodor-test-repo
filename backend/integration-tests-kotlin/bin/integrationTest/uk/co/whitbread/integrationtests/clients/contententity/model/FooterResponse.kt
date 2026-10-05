package uk.co.whitbread.integrationtests.clients.contententity.model

import kotlinx.serialization.Serializable

@Serializable
data class FooterResponse(
    val tabs: List<LinkTab> = emptyList(),
    val socialMediaIcons: List<SocialMediaIcon> = emptyList(),
    val copyrightInfo: String? = null,
    val newsletterSignup: NewsletterSignup? = null,
    val bottomLinks: List<LinkItem> = emptyList(),
)

@Serializable
data class LinkTab(
    val name: String? = null,
    val intro: Intro? = null,
    val columns: List<LinkColumn> = emptyList(),
)

@Serializable
data class Intro(
    val name: String? = null,
    val description: String? = null,
)

@Serializable
data class LinkColumn(
    val name: String? = null,
    val linkItems: List<LinkItem> = emptyList(),
)

@Serializable
data class LinkItem(
    val name: String? = null,
    val linkSrc: String? = null,
    val openInNewTab: Boolean = false,
)

@Serializable
data class SocialMediaIcon(
    val linkSrc: String? = null,
    val label: String? = null,
    val iconSrc: String? = null,
)

@Serializable
data class NewsletterSignup(
    val newsletterText: String? = null,
    val newsletterButtonLabel: String? = null,
    val firstNameLabel: String? = null,
    val firstNameMaxLengthError: String? = null,
    val firstNameValidError: String? = null,
    val firstNameMinLengthError: String? = null,
    val lastNameLabel: String? = null,
    val lastNameValidError: String? = null,
    val lastNameMaxLengthError: String? = null,
    val lastNameMinLengthError: String? = null,
    val emailLabel: String? = null,
    val emailValidError: String? = null,
    val emailMaxLengthError: String? = null,
    val emailMinLengthError: String? = null,
    val countrySelectLabel: String? = null,
    val countrySelectEmptyFieldError: String? = null,
    val serverError: String? = null,
    val signupTitle: String? = null,
    val doubleOptInTitle: String? = null,
    val doubleOptInText: String? = null,
    val confirmationText: String? = null,
    val bookStayButtonText: String? = null,
    val bookStayButtonUrl: String? = null,
    val introViewTitle: String? = null,
    val privacyPolicyText: String? = null,
    val introViewText: String? = null,
    val signUpButtonText: String? = null,
)
