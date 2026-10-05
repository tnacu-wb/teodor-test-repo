
package uk.co.whitbread.azure.genericEmail.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for WeekOfMonthEnum&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="WeekOfMonthEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="first"/&gt;
 *     &lt;enumeration value="second"/&gt;
 *     &lt;enumeration value="third"/&gt;
 *     &lt;enumeration value="fourth"/&gt;
 *     &lt;enumeration value="last"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "WeekOfMonthEnum")
@XmlEnum
public enum WeekOfMonthEnum {

    @XmlEnumValue("first")
    FIRST("first"),
    @XmlEnumValue("second")
    SECOND("second"),
    @XmlEnumValue("third")
    THIRD("third"),
    @XmlEnumValue("fourth")
    FOURTH("fourth"),
    @XmlEnumValue("last")
    LAST("last");
    private final String value;

    WeekOfMonthEnum(String v) {
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
    public static WeekOfMonthEnum fromValue(String v) {
        for (WeekOfMonthEnum c: WeekOfMonthEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
