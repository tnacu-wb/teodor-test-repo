
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The possible verification levels
 * 
 * &lt;p&gt;Java class for VerifyLevelType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="VerifyLevelType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Verified"/&gt;
 *     &lt;enumeration value="InteractionRequired"/&gt;
 *     &lt;enumeration value="PremisesPartial"/&gt;
 *     &lt;enumeration value="StreetPartial"/&gt;
 *     &lt;enumeration value="Multiple"/&gt;
 *     &lt;enumeration value="VerifiedPlace"/&gt;
 *     &lt;enumeration value="VerifiedStreet"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "VerifyLevelType")
@XmlEnum
public enum VerifyLevelType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Verified")
    VERIFIED("Verified"),
    @XmlEnumValue("InteractionRequired")
    INTERACTION_REQUIRED("InteractionRequired"),
    @XmlEnumValue("PremisesPartial")
    PREMISES_PARTIAL("PremisesPartial"),
    @XmlEnumValue("StreetPartial")
    STREET_PARTIAL("StreetPartial"),
    @XmlEnumValue("Multiple")
    MULTIPLE("Multiple"),
    @XmlEnumValue("VerifiedPlace")
    VERIFIED_PLACE("VerifiedPlace"),
    @XmlEnumValue("VerifiedStreet")
    VERIFIED_STREET("VerifiedStreet");
    private final String value;

    VerifyLevelType(String v) {
        value = v;
    }

    /**
     * Gets the value associated to the enum constant.
     * 
     * @return
     *     The value linked to the enum.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the enum associated to the value passed as parameter.
     * 
     * @param v
     *     The value to get the enum from.
     * @return
     *     The enum which corresponds to the value, if it exists.
     * @throws IllegalArgumentException
     *     If no value matches in the enum declaration.
     */
    public static VerifyLevelType fromValue(String v) {
        for (VerifyLevelType c: VerifyLevelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
