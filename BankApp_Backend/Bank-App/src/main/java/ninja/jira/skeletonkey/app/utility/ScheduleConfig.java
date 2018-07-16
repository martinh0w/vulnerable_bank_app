package ninja.jira.skeletonkey.app.utility;

import ninja.jira.skeletonkey.app.entity.*;
import ninja.jira.skeletonkey.app.repository.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * This class schedules the recurring payment and monthly bill payment.
 * Recurring payment can be scheduled daily/weekly/monthly.
 * Monthly bills are scheduled to monthly payment by default.
 * The respective methods performs a transaction for all payment in that category timely.
 * Time is reset upon restart of the server.
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    @Autowired
    private RecurringPaymentRepository recurringPaymentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private MonthlyBillRepository monthlyBillRepository;
    @Autowired
    private CardRepository cardRepository;

    /**
     * This method performs a transaction for all recurring payments scheduled daily.
     */
    //cron expression executes this method daily
    @Scheduled(cron ="0 0 * * * *")
    public void scheduleDailyPayment() throws Exception{
        //gets all recurring payments that are categorized as daily
        List<RecurringPayment> recurringList = recurringPaymentRepository.findRecurringPaymentsByPeriod("daily");

        for (RecurringPayment recurringPayment : recurringList) {
            //creates a new date format pointing to today's date
            Date date = Util.formatNewDate();

            //retrieve respective accounts from db
            String fromAccount = recurringPayment.getFromAccount();
            Account account1 = accountRepository.findById(fromAccount).get();
            String toAccount = recurringPayment.getToAccount();
            Account account2 = accountRepository.findById(toAccount).get();

            //perform transaction for the two accounts
            Transaction.transfer(account1, account2, recurringPayment.getAmount());

            //save all changes to balance amount to db
            accountRepository.save(account1);
            accountRepository.save(account2);

            //creates a new transaction object and save to db
            Transaction transaction = new Transaction (fromAccount, toAccount, recurringPayment.getAmount(), date, recurringPayment.getDescription());
            transactionRepository.save(transaction);
        }
    }

    /**
     * This method performs a transaction for all recurring payments scheduled weekly.
     */
    //cron expression executes this method weekly
    @Scheduled(cron ="0 0 * * 1 *")
    public void scheduleWeeklyPayment() throws Exception{
        //gets all recurring payment categorized as weekly
        List<RecurringPayment> recurringList = recurringPaymentRepository.findRecurringPaymentsByPeriod("weekly");

        for (RecurringPayment recurringPayment : recurringList) {
            //creates a new date format pointing to today's date
            Date date = Util.formatNewDate();

            //retrieve respective accounts from db
            String fromAccount = recurringPayment.getFromAccount();
            Account account1 = accountRepository.findById(fromAccount).get();
            String toAccount = recurringPayment.getToAccount();
            Account account2 = accountRepository.findById(toAccount).get();

            //perform transaction for the two accounts
            Transaction.transfer(account1, account2, recurringPayment.getAmount());

            //save all changes to balance amount to db
            accountRepository.save(account1);
            accountRepository.save(account2);

            //creates a new transaction object and save to db
            Transaction transaction = new Transaction (fromAccount, toAccount, recurringPayment.getAmount(), date, recurringPayment.getDescription());
            transactionRepository.save(transaction);
        }
    }


    /**
     * This method performs a transaction for all recurring payments and monthly bills scheduled monthly.
     */
    //cron expression executes this method monthly
    @Scheduled(cron ="0 0 1 * * *")
    public void scheduleMonthlyPayment() throws Exception{
        //gets all recurring payment categorized as monthly
        List<RecurringPayment> recurringList = recurringPaymentRepository.findRecurringPaymentsByPeriod("monthly");
        //gets all monthly bills
        Iterable<MonthlyBill> monthlyBillList = monthlyBillRepository.findAll();

        for (RecurringPayment recurringPayment : recurringList) {
            //creates a new date format pointing to today's date
            Date date = Util.formatNewDate();

            //retrieve respective accounts from db
            String fromAccount = recurringPayment.getFromAccount();
            Account account1 = accountRepository.findById(fromAccount).get();
            String toAccount = recurringPayment.getToAccount();
            Account account2 = accountRepository.findById(toAccount).get();

            //perform transaction for the two accounts
            Transaction.transfer(account1, account2, recurringPayment.getAmount());

            //save all changes to balance amount to db
            accountRepository.save(account1);
            accountRepository.save(account2);

            //creates a new transaction object and save to db
            Transaction transaction = new Transaction (fromAccount, toAccount, recurringPayment.getAmount(), date, recurringPayment.getDescription());
            transactionRepository.save(transaction);
        }

        for (MonthlyBill monthlyBill: monthlyBillList) {
            //creates a new date format pointing to today's date
            Date date = Util.formatNewDate();

            //retrieves account tied to card used to pay for bills
            String cardNumber = monthlyBill.getCardNumber();
            Card card = cardRepository.findById(cardNumber).get();
            String fromAccount = card.getAccountNumber();
            Account account1 = accountRepository.findById(fromAccount).get();

            //checks whether balance is enough to perform transaction
            if(account1.getBalance() - monthlyBill.getAmount() >= 0) {
                //deduct the amount from account and save to db
                account1.setBalance(account1.getBalance() - monthlyBill.getAmount());
                accountRepository.save(account1);

                //creates a new transaction object and save to db
                Transaction transaction = new Transaction(fromAccount, monthlyBill.getBillingOrganization(), monthlyBill.getAmount(), date, monthlyBill.getDescription());
                transactionRepository.save(transaction);
            }
        }
    }

    //FOR TESTING PURPOSES.
    //Performs transactions for monthly bills .
    //Uncomment respective cron to use expression.

    //@Scheduled(cron="0 * * * * *") //Every minute
    //@Scheduled(cron ="* * * * * *") //Every second
    /*
    public void scheduleTestPayment() throws Exception{
        Iterable<MonthlyBill> monthlyBillList = monthlyBillRepository.findAll();
        for (MonthlyBill monthlyBill: monthlyBillList) {
            Date date = Util.formatNewDate();

            String cardNumber = monthlyBill.getCardNumber();
            Card card = cardRepository.findById(cardNumber).get();
            String fromAccount = card.getAccountNumber();
            Account account1 = accountRepository.findById(fromAccount).get();

            if(account1.getBalance() - monthlyBill.getAmount() >= 0) {
                account1.setBalance(account1.getBalance() - monthlyBill.getAmount());
                accountRepository.save(account1);

                Transaction transaction = new Transaction(fromAccount, monthlyBill.getBillingOrganization(), monthlyBill.getAmount(), date, monthlyBill.getDescription());
                transactionRepository.save(transaction);
            }
        }
    }
    */
}