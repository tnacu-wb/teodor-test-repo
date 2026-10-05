
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for AutomationStatus&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="AutomationStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Error"/&gt;
 *     &lt;enumeration value="BuildingError"/&gt;
 *     &lt;enumeration value="Building"/&gt;
 *     &lt;enumeration value="Ready"/&gt;
 *     &lt;enumeration value="Running"/&gt;
 *     &lt;enumeration value="Paused"/&gt;
 *     &lt;enumeration value="Stopped"/&gt;
 *     &lt;enumeration value="Scheduled"/&gt;
 *     &lt;enumeration value="AwaitingTrigger"/&gt;
 *     &lt;enumeration value="InactiveTrigger"/&gt;
 *     &lt;enumeration value="Skipped"/&gt;
 *     &lt;enumeration value="Unknown"/&gt;
 *     &lt;enumeration value="New"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "AutomationStatus")
@XmlEnum
public enum AutomationStatus {

    @XmlEnumValue("Error")
    ERROR("Error"),
    @XmlEnumValue("BuildingError")
    BUILDING_ERROR("BuildingError"),
    @XmlEnumValue("Building")
    BUILDING("Building"),
    @XmlEnumValue("Ready")
    READY("Ready"),
    @XmlEnumValue("Running")
    RUNNING("Running"),
    @XmlEnumValue("Paused")
    PAUSED("Paused"),
    @XmlEnumValue("Stopped")
    STOPPED("Stopped"),
    @XmlEnumValue("Scheduled")
    SCHEDULED("Scheduled"),
    @XmlEnumValue("AwaitingTrigger")
    AWAITING_TRIGGER("AwaitingTrigger"),
    @XmlEnumValue("InactiveTrigger")
    INACTIVE_TRIGGER("InactiveTrigger"),
    @XmlEnumValue("Skipped")
    SKIPPED("Skipped"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("New")
    NEW("New");
    private final String value;

    AutomationStatus(String v) {
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
    public static AutomationStatus fromValue(String v) {
        for (AutomationStatus c: AutomationStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
