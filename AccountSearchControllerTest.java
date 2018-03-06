@isTest
/*    
 * author:          Mhelvin Reyes
 * created date:    Feb 13, 2018
 * description:     test class for account search page
 * history:         Feb 13, 2018 Mhelvin Reyes - Created
*/
public class AccountSearchControllerTest {
    
    /*    
     * author:          Mhelvin Reyes
     * created date:    Feb 13, 2018
     * description:     prepare a test data before calling any test method 
     * history:         Feb 13, 2018 Mhelvin Reyes - Created
    */
    @testSetup static void setup() {
        List<Account> testAccts = new List<Account>();
        for(Integer i=0;i<30;i++) {
            testAccts.add(new Account(Name = 'UnitTestAcct'+i, BillingCountry = 'PH', Type = 'Prospect'));
        }
        insert testAccts;
        
        List<Contact> testCons = new List<Contact>();
        integer i = 0;
        for(Account acc: testAccts){
            testCons.add(new Contact(FirstName = 'UnitTestCons'+i, MiddleName ='UnitTestCons'+i, LastName = 'UnitTestCons'+i, AccountId = acc.id));
            i++;
        }
        insert testCons;
    }
    
    /*    
     * author:          Mhelvin Reyes
     * created date:    Feb 13, 2018
     * description:     verify search result to check if search is working
     * history:         Feb 13, 2018 Mhelvin Reyes - Created
    */
    @isTest static void verifySearch() {
            
        AccountSeachController accountSearchController = new AccountSeachController();
        
        Test.startTest();
            accountSearchController.accountRecord.Name  = '*test*';     
            accountSearchController.search();
        Test.stopTest();
    
        integer searchResult = accountSearchController.contactsListTemp.size();

        system.assertEquals(30, searchResult);
    }
    
     /*    
     * author:          Mhelvin Reyes
     * created date:    Feb 13, 2018
     * description:     test wildcard search keyword 
     * history:         Feb 13, 2018 Mhelvin Reyes - Created
    */
     @isTest static void verifySearchWildcard() {
            
        AccountSeachController accountSearchController = new AccountSeachController();
        
        Test.startTest();
            accountSearchController.accountRecord.Name  = '%%';     
            accountSearchController.search();
        Test.stopTest();

        integer searchResult = accountSearchController.contactsList.size();

        system.assertEquals(0, searchResult);
    }
    
    /*    
     * author:          Mhelvin Reyes
     * created date:    Feb 13, 2018
     * description:     check empty search keyword
     * history:         Feb 13, 2018 Mhelvin Reyes - Created
    */
    @isTest static void verifyEmptySearch() {
        String noInputErrorMsg =    system.label.account_search_no_input_provided;
            
        AccountSeachController accountSearchController = new AccountSeachController();
        
        Test.startTest();
            accountSearchController.search();
        Test.stopTest();  
        
        List<Apexpages.Message> msgs = ApexPages.getMessages();
        boolean verifyMsg = false;
        for(Apexpages.Message msg:msgs){
            if(msg.getDetail().contains(noInputErrorMsg)) {
                verifyMsg = true;
            }
        }
        system.assert(verifyMsg);
    }
    
    /*    
     * author:          Mhelvin Reyes
     * created date:    Feb 13, 2018
     * description:     verify if a search keyword is more than 1 if it is throw error
     * history:         Feb 13, 2018 Mhelvin Reyes - Created
    */
    @isTest static void verifySearchLength() {
        string minInputErrorMsg =   system.label.account_search_min_input_value;
            
        AccountSeachController accountSearchController = new AccountSeachController();
        
        Test.startTest();
            accountSearchController.accountRecord.Name = 'a';
            accountSearchController.search();
        Test.stopTest();
        
        List<Apexpages.Message> msgs = ApexPages.getMessages();
        boolean verifyMsg = false;
        for(Apexpages.Message msg:msgs){
            if(msg.getDetail().contains(minInputErrorMsg)) {
                verifyMsg = true;
            }
        }
        system.assert(verifyMsg);
    }
    
    /*    
     * author:          Mhelvin Reyes
     * created date:    Feb 13, 2018
     * description:     check if user can search in all fields
     * history:         Feb 13, 2018 Mhelvin Reyes - Created
    */
     @isTest static void verifySearchAllFields() {
            
        AccountSeachController accountSearchController = new AccountSeachController();
        
        Test.startTest();
            accountSearchController.accountRecord.Name              = '*test*';
            accountSearchController.accountRecord.BillingCountry    = 'PH';
            accountSearchController.contactRecord.FirstName         = 'UnitTestCons1';
            accountSearchController.accountRecord.Type              = 'Prospect';           
            accountSearchController.search();
        Test.stopTest();

        integer searchResult = accountSearchController.contactsListTemp.size();

        system.assertEquals(1, searchResult);
    }
    
      /*    
     * author:          Mhelvin Reyes
     * created date:    Feb 13, 2018
     * description:     test if paginations are working
     * history:         Feb 13, 2018 Mhelvin Reyes - Created
    */
    @isTest static void testPagination() {
            
        AccountSeachController accountSearchController = new AccountSeachController();
        
        Test.startTest();       
            accountSearchController.accountRecord.Name = '**';
            accountSearchController.search();
            accountSearchController.firstPage(); 
            accountSearchController.next();
            accountSearchController.previous();
            accountSearchController.lastPage();
            accountSearchController.getNext();
            accountSearchController.getPrevious();
        Test.stopTest();

        integer searchResult = accountSearchController.contactsList.size();

    }
    
    
}