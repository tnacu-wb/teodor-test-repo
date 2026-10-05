
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for ListTypeEnum&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="ListTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Public"/&gt;
 *     &lt;enumeration value="Private"/&gt;
 *     &lt;enumeration value="SalesForce"/&gt;
 *     &lt;enumeration value="GlobalUnsubscribe"/&gt;
 *     &lt;enumeration value="Master"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "ListTypeEnum")
@XmlEnum
public enum ListTypeEnum {

    @XmlEnumValue("Public")
    PUBLIC("Public"),
    @XmlEnumValue("Private")
    PRIVATE("Private"),
    @XmlEnumValue("SalesForce")
    SALES_FORCE("SalesForce"),
    @XmlEnumValue("GlobalUnsubscribe")
    GLOBAL_UNSUBSCRIBE("GlobalUnsubscribe"),
    @XmlEnumValue("Master")
    MASTER("Master");
    private final String value;

    ListTypeEnum(String v) {
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
    public static ListTypeEnum fromValue(String v) {
        for (ListTypeEnum c: ListTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
