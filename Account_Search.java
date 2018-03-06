<apex:page controller="AccountSeachController" sidebar="false">
    <style>
    </style>
    <apex:stylesheet value="{!$Resource.AccountSearchStyles}"/>
    <apex:image value="{!$Resource.AccountSearch}" alt="Account Search"/>
    <apex:form id="account-search">
        <apex:pageMessages ></apex:pageMessages>
        <apex:pageBlock title="{!$Label.account_search_criteria}" helpTitle="{!$Label.how_to_search_help_title}" helpUrl="{!$Label.how_to_search_help_url}">
            <apex:pageBlockSection columns="4">
                <apex:inputField value="{!accountRecord.Name}" required="false" rendered="true"/>
                <apex:inputField value="{!accountRecord.BillingCountry}" rendered="{!IF(config.Enable_Search_for_Account_Country__c, true, false)}"/>
                <apex:inputField value="{!contactRecord.FirstName}" required="false" rendered="{!IF(config.Enable_Search_for_Contact_Name__c, true, false)}"/>
                <apex:inputField value="{!accountRecord.Type}" rendered="{!IF(config.Enable_Search_for_Account_Type__c, true, false)}"/>
            </apex:pageBlockSection>
            <apex:pageBlockSection columns="1" >
                <apex:commandButton value="{!$Label.account_search_search_button}" action="{!search}" status="actStatusId" reRender="account-search" style="float:right; margin: 10px; position: relative; top: 5px; right: 50px"/>
            </apex:pageBlockSection>
        </apex:pageBlock>
        <apex:actionStatus id="actStatusId" >
            <apex:facet name="start" >
                <div class="waitingSearchDiv" id="el_loading" style="background-color: #DCD6D6; height: 100%;opacity:0.80;width:100%;"> 
                    <div class="waitingHolder" style="background-color: #FFFFFF; width: 30%; padding-top: 20px; padding-bottom: 20px;">
                        <img class="waitingImage" src="/img/loading.gif" Title="Searching for matching Accounts..."/>
                        <span class="waitingDescription">{!$Label.account_search_spinner_message}</span>
                    </div>
                </div>
            </apex:facet>
        </apex:actionStatus>
        <apex:pageBlock title="{!$Label.account_search_results}"> 
            <apex:outputPanel rendered="{!IF(contactsList.size == 0, true, false)}">
                <p><em>{!$Label.account_search_no_records}</em></p>
            </apex:outputPanel>
            <apex:pageBlockTable value="{!contactsList}" var="contactListItem" rendered="{!IF(contactsList.size > 0, true, false)}">
                <apex:column headerValue="{!$Label.account_search_account_name_table_header}">
                    <apex:outputLink value="/{!URLFOR(contactListItem.Accountid)}" target="_blank" rendered="{!IF(contactListItem.Accountid != null, true, false)}">{!contactListItem.Account.Name}</apex:outputLink>
                </apex:column>
                <apex:column value="{!contactListItem.Account.BillingCountry}" headerValue="{!$Label.account_search_billing_country_table_header}"/>
                <apex:column value="{!contactListItem.Account.ShippingCountry}" headerValue="{!$Label.account_search_shipping_country_table_header}"/>
                <apex:column headerValue="{!$Label.account_search_contact_name_table_header}">
                    <apex:outputLink value="/{!URLFOR(contactListItem.id)}" target="_blank" >{!contactListItem.Name}</apex:outputLink>
                </apex:column>
                <apex:column value="{!contactListItem.Account.Type}" headerValue="{!$Label.account_search_type_table_header}"/>
                <apex:column value="{!contactListItem.Account.CreatedDate}" headerValue="{!$Label.account_search_year_created_table_header}" />
            </apex:pageBlockTable>
            <apex:pageblockButtons >
                <apex:commandButton styleClass="custom-btn" value="<<" action="{!firstPage}" status="actStatusId" reRender="account-search" disabled="{!Previous}" />
                <apex:commandButton styleClass="custom-btn" value="<Previous" action="{!previous}" status="actStatusId" reRender="account-search" disabled="{!Previous}"/>
                <apex:commandButton styleClass="custom-btn" value="Next>" action="{!next}" status="actStatusId" reRender="account-search" disabled="{!Next}"/>
                <apex:commandButton styleClass="custom-btn" value=">>" action="{!lastPage}" status="actStatusId" reRender="account-search" disabled="{!Next}"/>
            </apex:pageblockButtons>
        </apex:pageBlock>
    </apex:form>
</apex:page>