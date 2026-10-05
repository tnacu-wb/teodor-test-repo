
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountBalanceDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountBalanceDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OutstandingBalanceAtRisk" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="OutstandingBalance" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="AvailableToSpend" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CreditLimit" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CurrentSpend" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="SpendSinceLastInvoice" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="PendingTransactions" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="TransactionsReadyToBeInvoicedWithL3" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="TransactionsReadyToBeInvoicedWithoutLevel3" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="LastStatement" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="DepositReceived" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="DepositReturned" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="DepositBalance" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="PayAdjustSinceLastStatement" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="ScheduledPayments" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="InterimPaymentsSinceLastStatement" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CashBalance" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CB_Period_1" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CB_Period_2" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CB_Period_3" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CB_Period_4" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CB_Period_5" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CustomAttributes" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountBalanceDetailsType", propOrder = {
    "outstandingBalanceAtRisk",
    "outstandingBalance",
    "availableToSpend",
    "creditLimit",
    "currentSpend",
    "spendSinceLastInvoice",
    "pendingTransactions",
    "transactionsReadyToBeInvoicedWithL3",
    "transactionsReadyToBeInvoicedWithoutLevel3",
    "lastStatement",
    "depositReceived",
    "depositReturned",
    "depositBalance",
    "payAdjustSinceLastStatement",
    "scheduledPayments",
    "interimPaymentsSinceLastStatement",
    "cashBalance",
    "cbPeriod1",
    "cbPeriod2",
    "cbPeriod3",
    "cbPeriod4",
    "cbPeriod5",
    "customAttributes"
})
public class CustomerAccountBalanceDetailsType {

    /**
     * A. OS_Balance from scheme database + DD payments and cheques which have not yet included in the available balance (authorisation)  Or B + O
     * 
     */
    @XmlElement(name = "OutstandingBalanceAtRisk", required = true)
    protected CurrencyType outstandingBalanceAtRisk;
    /**
     * B. OS_Balance from scheme database
     * 
     */
    @XmlElement(name = "OutstandingBalance", required = true)
    protected CurrencyType outstandingBalance;
    /**
     * C. This is the balance on OLA platform and reflects what the credit the account has. It is a revolving balance and is a sum of all the debits and credits taken away from the credit limit.
     * 
     */
    @XmlElement(name = "AvailableToSpend", required = true)
    protected CurrencyType availableToSpend;
    /**
     * D. Held on the customer table
     * 
     */
    @XmlElement(name = "CreditLimit", required = true)
    protected CurrencyType creditLimit;
    /**
     * E = D-C
     * 
     */
    @XmlElement(name = "CurrentSpend", required = true)
    protected CurrencyType currentSpend;
    /**
     * F = G + H + I
     * 
     */
    @XmlElement(name = "SpendSinceLastInvoice", required = true)
    protected CurrencyType spendSinceLastInvoice;
    /**
     * G . Authorisations where no settlement has been received.  Purchase minus Refunds
     * 
     */
    @XmlElement(name = "PendingTransactions", required = true)
    protected CurrencyType pendingTransactions;
    /**
     * H.Uninvoiced transactions from scheme database where there is a match to level 3
     * 
     */
    @XmlElement(name = "TransactionsReadyToBeInvoicedWithL3", required = true)
    protected CurrencyType transactionsReadyToBeInvoicedWithL3;
    /**
     * I. Uninvoiced transactions from scheme database where there is not a level 3 match
     * 
     */
    @XmlElement(name = "TransactionsReadyToBeInvoicedWithoutLevel3", required = true)
    protected CurrencyType transactionsReadyToBeInvoicedWithoutLevel3;
    /**
     * J. How much owed when the last invoice was generated - from invoice trailer record
     * 
     */
    @XmlElement(name = "LastStatement", required = true)
    protected CurrencyType lastStatement;
    /**
     * K. Deposit was received (if applicable)
     * 
     */
    @XmlElement(name = "DepositReceived", required = true)
    protected CurrencyType depositReceived;
    /**
     * L. Deposit was returned (if applicable)
     * 
     */
    @XmlElement(name = "DepositReturned", required = true)
    protected CurrencyType depositReturned;
    /**
     * M. The value of deposit received (if applicable) K-L
     * 
     */
    @XmlElement(name = "DepositBalance", required = true)
    protected CurrencyType depositBalance;
    /**
     * N. Payments/adjustments since last statement (uninvoiced payments, excluding *future* DDs)
     * 
     */
    @XmlElement(name = "PayAdjustSinceLastStatement", required = true)
    protected CurrencyType payAdjustSinceLastStatement;
    /**
     * O. Future  DDs
     * 
     */
    @XmlElement(name = "ScheduledPayments", required = true)
    protected CurrencyType scheduledPayments;
    /**
     * P.
     * 
     */
    @XmlElement(name = "InterimPaymentsSinceLastStatement", required = true)
    protected CurrencyType interimPaymentsSinceLastStatement;
    /**
     * Q. Cash Balance
     * 
     */
    @XmlElement(name = "CashBalance", required = true)
    protected CurrencyType cashBalance;
    @XmlElement(name = "CB_Period_1", required = true)
    protected CurrencyType cbPeriod1;
    @XmlElement(name = "CB_Period_2", required = true)
    protected CurrencyType cbPeriod2;
    @XmlElement(name = "CB_Period_3", required = true)
    protected CurrencyType cbPeriod3;
    @XmlElement(name = "CB_Period_4", required = true)
    protected CurrencyType cbPeriod4;
    @XmlElement(name = "CB_Period_5", required = true)
    protected CurrencyType cbPeriod5;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * A. OS_Balance from scheme database + DD payments and cheques which have not yet included in the available balance (authorisation)  Or B + O
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getOutstandingBalanceAtRisk() {
        return outstandingBalanceAtRisk;
    }

    /**
     * Sets the value of the outstandingBalanceAtRisk property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getOutstandingBalanceAtRisk()
     */
    public void setOutstandingBalanceAtRisk(CurrencyType value) {
        this.outstandingBalanceAtRisk = value;
    }

    /**
     * B. OS_Balance from scheme database
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getOutstandingBalance() {
        return outstandingBalance;
    }

    /**
     * Sets the value of the outstandingBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getOutstandingBalance()
     */
    public void setOutstandingBalance(CurrencyType value) {
        this.outstandingBalance = value;
    }

    /**
     * C. This is the balance on OLA platform and reflects what the credit the account has. It is a revolving balance and is a sum of all the debits and credits taken away from the credit limit.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getAvailableToSpend() {
        return availableToSpend;
    }

    /**
     * Sets the value of the availableToSpend property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getAvailableToSpend()
     */
    public void setAvailableToSpend(CurrencyType value) {
        this.availableToSpend = value;
    }

    /**
     * D. Held on the customer table
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the value of the creditLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getCreditLimit()
     */
    public void setCreditLimit(CurrencyType value) {
        this.creditLimit = value;
    }

    /**
     * E = D-C
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCurrentSpend() {
        return currentSpend;
    }

    /**
     * Sets the value of the currentSpend property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getCurrentSpend()
     */
    public void setCurrentSpend(CurrencyType value) {
        this.currentSpend = value;
    }

    /**
     * F = G + H + I
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getSpendSinceLastInvoice() {
        return spendSinceLastInvoice;
    }

    /**
     * Sets the value of the spendSinceLastInvoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getSpendSinceLastInvoice()
     */
    public void setSpendSinceLastInvoice(CurrencyType value) {
        this.spendSinceLastInvoice = value;
    }

    /**
     * G . Authorisations where no settlement has been received.  Purchase minus Refunds
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getPendingTransactions() {
        return pendingTransactions;
    }

    /**
     * Sets the value of the pendingTransactions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getPendingTransactions()
     */
    public void setPendingTransactions(CurrencyType value) {
        this.pendingTransactions = value;
    }

    /**
     * H.Uninvoiced transactions from scheme database where there is a match to level 3
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getTransactionsReadyToBeInvoicedWithL3() {
        return transactionsReadyToBeInvoicedWithL3;
    }

    /**
     * Sets the value of the transactionsReadyToBeInvoicedWithL3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getTransactionsReadyToBeInvoicedWithL3()
     */
    public void setTransactionsReadyToBeInvoicedWithL3(CurrencyType value) {
        this.transactionsReadyToBeInvoicedWithL3 = value;
    }

    /**
     * I. Uninvoiced transactions from scheme database where there is not a level 3 match
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getTransactionsReadyToBeInvoicedWithoutLevel3() {
        return transactionsReadyToBeInvoicedWithoutLevel3;
    }

    /**
     * Sets the value of the transactionsReadyToBeInvoicedWithoutLevel3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getTransactionsReadyToBeInvoicedWithoutLevel3()
     */
    public void setTransactionsReadyToBeInvoicedWithoutLevel3(CurrencyType value) {
        this.transactionsReadyToBeInvoicedWithoutLevel3 = value;
    }

    /**
     * J. How much owed when the last invoice was generated - from invoice trailer record
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getLastStatement() {
        return lastStatement;
    }

    /**
     * Sets the value of the lastStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getLastStatement()
     */
    public void setLastStatement(CurrencyType value) {
        this.lastStatement = value;
    }

    /**
     * K. Deposit was received (if applicable)
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getDepositReceived() {
        return depositReceived;
    }

    /**
     * Sets the value of the depositReceived property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getDepositReceived()
     */
    public void setDepositReceived(CurrencyType value) {
        this.depositReceived = value;
    }

    /**
     * L. Deposit was returned (if applicable)
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getDepositReturned() {
        return depositReturned;
    }

    /**
     * Sets the value of the depositReturned property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getDepositReturned()
     */
    public void setDepositReturned(CurrencyType value) {
        this.depositReturned = value;
    }

    /**
     * M. The value of deposit received (if applicable) K-L
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getDepositBalance() {
        return depositBalance;
    }

    /**
     * Sets the value of the depositBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getDepositBalance()
     */
    public void setDepositBalance(CurrencyType value) {
        this.depositBalance = value;
    }

    /**
     * N. Payments/adjustments since last statement (uninvoiced payments, excluding *future* DDs)
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getPayAdjustSinceLastStatement() {
        return payAdjustSinceLastStatement;
    }

    /**
     * Sets the value of the payAdjustSinceLastStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getPayAdjustSinceLastStatement()
     */
    public void setPayAdjustSinceLastStatement(CurrencyType value) {
        this.payAdjustSinceLastStatement = value;
    }

    /**
     * O. Future  DDs
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getScheduledPayments() {
        return scheduledPayments;
    }

    /**
     * Sets the value of the scheduledPayments property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getScheduledPayments()
     */
    public void setScheduledPayments(CurrencyType value) {
        this.scheduledPayments = value;
    }

    /**
     * P.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getInterimPaymentsSinceLastStatement() {
        return interimPaymentsSinceLastStatement;
    }

    /**
     * Sets the value of the interimPaymentsSinceLastStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getInterimPaymentsSinceLastStatement()
     */
    public void setInterimPaymentsSinceLastStatement(CurrencyType value) {
        this.interimPaymentsSinceLastStatement = value;
    }

    /**
     * Q. Cash Balance
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCashBalance() {
        return cashBalance;
    }

    /**
     * Sets the value of the cashBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     * @see #getCashBalance()
     */
    public void setCashBalance(CurrencyType value) {
        this.cashBalance = value;
    }

    /**
     * Gets the value of the cbPeriod1 property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCBPeriod1() {
        return cbPeriod1;
    }

    /**
     * Sets the value of the cbPeriod1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCBPeriod1(CurrencyType value) {
        this.cbPeriod1 = value;
    }

    /**
     * Gets the value of the cbPeriod2 property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCBPeriod2() {
        return cbPeriod2;
    }

    /**
     * Sets the value of the cbPeriod2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCBPeriod2(CurrencyType value) {
        this.cbPeriod2 = value;
    }

    /**
     * Gets the value of the cbPeriod3 property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCBPeriod3() {
        return cbPeriod3;
    }

    /**
     * Sets the value of the cbPeriod3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCBPeriod3(CurrencyType value) {
        this.cbPeriod3 = value;
    }

    /**
     * Gets the value of the cbPeriod4 property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCBPeriod4() {
        return cbPeriod4;
    }

    /**
     * Sets the value of the cbPeriod4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCBPeriod4(CurrencyType value) {
        this.cbPeriod4 = value;
    }

    /**
     * Gets the value of the cbPeriod5 property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCBPeriod5() {
        return cbPeriod5;
    }

    /**
     * Sets the value of the cbPeriod5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCBPeriod5(CurrencyType value) {
        this.cbPeriod5 = value;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the customAttributes property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCustomAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomAttributeType }
     * </p>
     * 
     * 
     * @return
     *     The value of the customAttributes property.
     */
    public List<CustomAttributeType> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new ArrayList<>();
        }
        return this.customAttributes;
    }

}
