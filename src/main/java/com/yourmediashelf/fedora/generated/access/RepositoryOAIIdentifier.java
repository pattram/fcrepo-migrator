//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.08 at 01:10:14 AM SGT 
//


package com.yourmediashelf.fedora.generated.access;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}OAI-namespaceIdentifier"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}OAI-delimiter"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}OAI-sample"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "oaiNamespaceIdentifier",
    "oaiDelimiter",
    "oaiSample"
})
@XmlRootElement(name = "repositoryOAI-identifier")
public class RepositoryOAIIdentifier {

    @XmlElement(name = "OAI-namespaceIdentifier", required = true)
    protected String oaiNamespaceIdentifier;
    @XmlElement(name = "OAI-delimiter", required = true)
    protected String oaiDelimiter;
    @XmlElement(name = "OAI-sample", required = true)
    protected String oaiSample;

    /**
     * Gets the value of the oaiNamespaceIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOAINamespaceIdentifier() {
        return oaiNamespaceIdentifier;
    }

    /**
     * Sets the value of the oaiNamespaceIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOAINamespaceIdentifier(String value) {
        this.oaiNamespaceIdentifier = value;
    }

    /**
     * Gets the value of the oaiDelimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOAIDelimiter() {
        return oaiDelimiter;
    }

    /**
     * Sets the value of the oaiDelimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOAIDelimiter(String value) {
        this.oaiDelimiter = value;
    }

    /**
     * Gets the value of the oaiSample property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOAISample() {
        return oaiSample;
    }

    /**
     * Sets the value of the oaiSample property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOAISample(String value) {
        this.oaiSample = value;
    }

}
