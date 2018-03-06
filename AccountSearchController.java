/*    
 * author: 			Mhelvin Reyes
 * created date: 	Feb 13, 2018
 * description: 	custom controller for account search page
 * history: 		Feb 13, 2018 Mhelvin Reyes - Created
*/
public class AccountSeachController {
	
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	declare getter and setter
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */    
    public List<Contact> contactsList                                             		{ get; set; }
    public List<Contact> contactsListTemp;
    public Account accountRecord                                                        { get; set; }
    public Contact contactRecord                                                        { get; set; }
    public Config__c config     														{ get; set; }
    private List<String> searchCriteria = new List<String>();
    
    private integer totalRecords = 0;
    private integer offSetSize = 0;
    private integer limitSize = 0;
	
    string noInputErrorMsg = 	system.label.account_search_no_input_provided;
    string minInputErrorMsg = 	system.label.account_search_min_input_value;
    string configProfile = 		system.label.account_search_config_profile;
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	instantiate variables in the contructor
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public AccountSeachController () {
        accountRecord = new Account();
        contactRecord = new Contact();
        contactsList = new List<Contact>();
        contactsListTemp = new List<Contact>();
        Profile profile = [select id, name from profile where name = :configProfile];
        config = Config__c.getInstance(profile.id);
        if (config.Search_Result_Limit__c != null) {
                limitSize = config.Search_Result_Limit__c.intValue();
        } else {
                limitSize = 5;
        }
    }
	
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	invoke the search accounts method
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public void search() {
       seachAccounts();        
    }
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	create a dynamic SOQL statements based on the ealvaluted values of inputs
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public void seachAccounts() {
        if (accountRecord.Name == null && accountRecord.BillingCountry == null && contactRecord.FirstName == null && accountRecord.Type == null) {
            contactsList.clear();
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, noInputErrorMsg);
            ApexPages.addMessage(myMsg);
            return;
        }
        
        searchCriteria.clear();
        
        if (accountRecord.Name != null) {
            if (accountRecord.Name.length() > 1) {
                if (accountRecord.Name.contains('%')) {
                    searchCriteria.add('Account.Name LIKE \''+accountRecord.Name.replace('%','\\%')+'\'');
                } else {
                    searchCriteria.add('Account.Name LIKE \''+String.escapeSingleQuotes(accountRecord.Name.replace('*','%'))+'\'');
                }
            } else {
                contactsList.clear();
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.ERROR, minInputErrorMsg);
                ApexPages.addMessage(myMsg);
                return;
            } 
        }
        
        if (contactRecord.FirstName != null) {
            searchCriteria.add('(FirstName =' +' \''+String.escapeSingleQuotes(contactRecord.FirstName.remove('*'))+'\''+'OR MiddleName = '+'\''+String.escapeSingleQuotes(contactRecord.FirstName.remove('*'))+'\''+'OR LastName = '+'\''+String.escapeSingleQuotes(contactRecord.FirstName.remove('*'))+'\')');
        }
        
        if (accountRecord.BillingCountry != null) {
            searchCriteria.add('(Account.BillingCountry = '+'\''+String.escapeSingleQuotes(accountRecord.BillingCountry.remove('*'))+'\''+' OR Account.ShippingCountry = '+'\'' +String.escapeSingleQuotes(accountRecord.BillingCountry.remove('*'))+'\')');
        }
        
        if (accountRecord.Type != null) {
            searchCriteria.add('Account.Type = ' + '\''+accountRecord.Type+'\'');
        }
        
        String query = 'SELECT id, AccountId, Account.Name, Account.BillingCountry, Account.ShippingCountry, Account.Type, Account.CreatedDate, Name FROM Contact';
        
        if (searchCriteria.size() > 0) {
            query += ' WHERE ' + searchCriteria[0];
            for (Integer i=1; i<searchCriteria.size(); i++)
                query += ' AND ' + searchCriteria[i];
        }
        
        if(totalRecords !=null && totalRecords == 0){
          List<Contact> contactTempRec = Database.query(query);
          totalRecords = (contactTempRec !=null && contactTempRec.size() > 0) ? contactTempRec.size() : 0;
        }
        system.debug(query);
		contactsListTemp = Database.query(query+= ' ORDER BY Account.Name');
        firstPage();
    }
   
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 20, 2018
     * description: 	chunk and fetch list of records for pagination
     * history: 		Feb 20, 2018 Mhelvin Reyes - Created			
    */   
   public void paginator() {
       integer offsetctr = offSetSize;
       contactsList.clear();
       for (integer i=0; i<limitSize; i++) {
           if (offsetctr != contactsListTemp.size()) {
               contactsList.add(contactsListTemp[offsetctr]);
               offsetctr++;
           }
        } 
    }
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	reset offset
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public void firstPage() {
        offSetSize = 0;
        paginator();
    }
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	get the last record to display based on the limit size
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public void lastPage() {
        offSetSize = totalRecords - Math.mod(totalRecords, limitSize);
        paginator();
    }
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	set offset size incrementally 
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public void next() {
        offSetSize = offSetSize + limitSize;
        paginator();
    }
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	set offset size decrementally
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public void previous() {
        offSetSize = offSetSize - limitSize;
        paginator();
    }
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	get the previous record in the pagination
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public boolean getPrevious() {
        if (offSetSize == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /*    
     * author: 			Mhelvin Reyes
     * created date: 	Feb 13, 2018
     * description: 	get the next record in the pagination
     * history: 		Feb 13, 2018 Mhelvin Reyes - Created			
    */   
    public boolean getNext() {
        if (totalRecords == 0 || contactsList.size() < limitSize) {
            return true;
        } else {
            return false;
        }
    }
    

}