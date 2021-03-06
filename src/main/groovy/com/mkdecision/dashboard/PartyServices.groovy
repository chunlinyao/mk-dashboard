package com.mkdecision.dashboard

import org.moqui.context.*
import org.moqui.entity.*
import org.moqui.util.*
import org.apache.commons.lang3.StringUtils

class PartyServices {

    static Map<String, Object> getPartyName(ExecutionContext ec) {

        // shortcuts for convenience
        ContextStack cs = ec.getContext()
        EntityFacade ef = ec.getEntity()

        // get the parameters
        String partyId = (String) cs.getOrDefault("partyId", null)

        // get the party
        EntityValue partyDetail = ef.find("mantle.party.PartyDetail")
                .condition("partyId", partyId)
                .one()

        // prepare party name
        String partyName = null
        String partyNickname = null
        if (partyDetail != null) {
            if (partyDetail.getString("partyTypeEnumId").equals("PtyPerson")) {
                partyName = String.format("%s %s", StringUtils.defaultString(partyDetail.getString("firstName")), StringUtils.defaultString(partyDetail.getString("lastName"))).trim()
                partyNickname = partyDetail.getString("nickname")
            } else if (partyDetail.getString("partyTypeEnumId").equals("PtyOrganization")) {
                partyName = partyDetail.getString("organizationName")
            }
        }

        // return the output parameters
        HashMap<String, Object> outParams = new HashMap<>()
        outParams.put("partyId", partyId)
        outParams.put("partyName", partyName)
        outParams.put("partyNickname", partyNickname)
        return outParams
    }

    static Map<String, Object> getPartyPrimaryEmailAddress(ExecutionContext ec) {

        // shortcuts for convenience
        ContextStack cs = ec.getContext()
        EntityFacade ef = ec.getEntity()
        UserFacade uf = ec.getUser()

        // get the parameters
        String partyId = (String) cs.getOrDefault("partyId", null)

        // get the telecom numbers
        EntityList emails = ef.find("mantle.party.contact.PartyContactMechInfo")
                .condition("partyId", partyId)
                .condition("contactMechPurposeId", "EmailPrimary")
                .conditionDate("fromDate", "thruDate", uf.getNowTimestamp())
                .list()

        // prepare email address
        String emailAddress = null
        if (emails != null && !emails.isEmpty()) {
            EntityValue firstEmail = emails.getFirst()
            emailAddress = firstEmail.getString("infoString")
        }

        // return the output parameters
        HashMap<String, Object> outParams = new HashMap<>()
        outParams.put("partyId", partyId)
        outParams.put("emailAddress", emailAddress)
        return outParams
    }

    static Map<String, Object> getPartyPrimaryPostalAddress(ExecutionContext ec) {

        // shortcuts for convenience
        ContextStack cs = ec.getContext()
        EntityFacade ef = ec.getEntity()
        UserFacade uf = ec.getUser()

        // get the parameters
        String partyId = (String) cs.getOrDefault("partyId", null)

        // get the postal addresses
        EntityList postalAddresses = ef.find("mantle.party.contact.PartyContactMechPostalAddress")
                .condition("partyId", partyId)
                .conditionDate("fromDate", "thruDate", uf.getNowTimestamp())
                .list()
        EntityValue postalAddress = postalAddresses != null && !postalAddresses.isEmpty() ? postalAddresses.getFirst() : null

        // return the output parameters
        HashMap<String, Object> outParams = new HashMap<>()
        outParams.put("partyId", partyId)
        outParams.put("postalAddress", postalAddress)
        return outParams
    }

    static Map<String, Object> getPartyPrimaryTelecomNumber(ExecutionContext ec) {

        // shortcuts for convenience
        ContextStack cs = ec.getContext()
        EntityFacade ef = ec.getEntity()
        UserFacade uf = ec.getUser()

        // get the parameters
        String partyId = (String) cs.getOrDefault("partyId", null)

        // get the telecom numbers
        EntityList telecomNumbers = ef.find("mantle.party.contact.PartyContactMechTelecomNumber")
                .condition("partyId", partyId)
                .conditionDate("fromDate", "thruDate", uf.getNowTimestamp())
                .list()

        // prepare telecom number
        String telecomNumber = null
        if (telecomNumbers != null && !telecomNumbers.isEmpty()) {
            EntityValue firstNumber = telecomNumbers.getFirst()
            telecomNumber = firstNumber.getString("contactNumber")
        }

        // return the output parameters
        HashMap<String, Object> outParams = new HashMap<>()
        outParams.put("partyId", partyId)
        outParams.put("telecomNumber", telecomNumber)
        return outParams
    }

    static Map<String, Object> getPartyProductStoreRoles(ExecutionContext ec) {

        // shortcuts for convenience
        ContextStack cs = ec.getContext()
        EntityFacade ef = ec.getEntity()
        UserFacade uf = ec.getUser()

        // get the parameters
        String partyId = (String) cs.getOrDefault("partyId", null)
        String productStoreId = (String) cs.getOrDefault("productStoreId", null)

        // get the product store parties
        EntityList productStoreParties = ef.find("mantle.product.store.ProductStoreParty")
                .condition("productStoreId", productStoreId)
                .condition("partyId", partyId)
                .conditionDate("fromDate", "thruDate", uf.nowTimestamp)
                .list()

        // prepare role types
        Set<String> roleTypeIdSet = new HashSet<>()
        for(EntityValue productStoreParty : productStoreParties) {
            roleTypeIdSet.add(productStoreParty.getString("roleTypeId"))
        }

        // return the output parameters
        HashMap<String, Object> outParams = new HashMap<>()
        outParams.put("partyId", partyId)
        outParams.put("productStoreId", productStoreId)
        outParams.put("roleTypeIdSet", roleTypeIdSet)
        return outParams
    }
}
