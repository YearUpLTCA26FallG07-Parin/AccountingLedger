Accounting Ledger App;

About the app:

My project is a Java command-line accounting ledger. From the Home Screen, you can record a deposit, make a payment, or open the ledger. The ledger shows transactions newest first, and the Reports menu lets you filter them by date, vendor, or a combination of fields. The app saves transactions as you add them and reloads them the next time you start it.


My project uses two Java classes, AccountingLedgerApp and Transaction, along with a data file called transactions.csv. Transaction defines one ledger entry, and transactions.csv stores the entries between runs. The three files work together when a payment is added.
 

Each Transaction object stores five pieces of information. It has a date, a time, a description, a vendor, and an amount. The constructor takes those five values and assigns them to the object. For example, in this.vendor = vendor, this.vendor is the field on the transaction, and vendor is the value I passed into the constructor.
I made the fields private and final, so their values stay fixed after the object is created, and the rest of the app reads them through getter methods. For example, the ledger calls getAmount() to decide whether an entry belongs in the Deposits or Payments view.

The constructor receives the five values. In this.vendor = vendor, the left side is the field on this object, and the right side is the value passed into the constructor.


How a Payment Is Appended:


When I add a payment, the app checks the amount and records the current date and time. Since I chose P (payment), the app stores the amount as negative 65 dollars and 75 cents. Then it creates a Transaction object and passes it to saveTransaction. Deposits go through the same method and are saved as positive amounts.
In saveTransaction, I open transactions.csv with new FileWriter(FILE, true). The second argument, true, turns on append mode. That tells Java to write at the end of the file and preserve the entries that are already there.

I use the getters to read the five values from the object and write them in the same order each time. I put a pipe between each value and use System.lineSeparator() to finish the row. For a payment, the row starts with the date and time, followed by description, vendor, and amount.
Once the save succeeds, the app adds the transaction to the list the ledger displays. The file is updated as soon as I add the payment, so the entry is already saved when I choose Exit.
How AccountingLedgerApp loads the file
When I start the app again, main calls loadTransactions. That method reads transactions.csv one line at a time and splits each saved row at the pipes. Everything from the file starts out as text, so I convert the amount to a double. Then I pass the five values into the Transaction constructor and add the recreated object to the ledger’s list.
The payment entered earlier is still in the Payments view after restarting because the app loaded it from the row we appended to transactions.csv.


Explain One Interesting Piece of Code:
The part I want to show is how I reused one method to display different views of the ledger. Each of these menu options calls displayEntries with the same list of transactions and a different rule. In Java, I use a Predicate<Transaction> for that rule. It returns true or false for each transaction. The expression with the arrow is a lambda.


More About My Code:

I used try and catch in places where an operation can fail. When I ask for an amount, I use Double.parseDouble to turn what the user typed into a number. If they type something like ‘abc,’ my catch prints a message and returns to the menu so they can try again. I also check the amount after it’s parsed. For example, zero is a valid number, but my app rejects it as a transaction amount.

I also used try-with-resources for my file reader and writer so Java closes them automatically. In loadTransactions, I have a second try inside the loop that reads the file. If Java can’t parse the date, time, or amount on one row, I can skip that row and keep loading the other transactions. I use try-with-resources for the writer too. Java closes it automatically when the block ends. If an IOException occurs, my method prints an error and returns false, so addTransaction skips adding that entry to the ledger’s list.

How I Organized My Code:

I organized AccountingLedgerApp.java around the menus. main loads the saved transactions and runs the Home Screen. showLedger handles the Ledger menu, and showReports handles the Reports menu. All three use readChoice, so users can type lowercase commands or leave extra spaces around them.

I also use one addTransaction method for deposits and payments because both ask for the same information. I pass in isPayment to tell the method which option the user chose. If it’s a payment, the method makes the amount negative. Then it creates the transaction, saves it, and adds it to the list if the save succeeds.

The file operations have their own methods. loadTransactions reads the saved rows and recreates the transaction objects at startup. saveTransaction adds a new row to the end of the file whenever I record a deposit or payment.
Hypothetical questions (for class presentation):


•	What happens if I type an invalid amount? The app prints an error and returns to the menu without saving the transaction. It also rejects zero and non-finite values such as NaN.

•	What happens if one saved row has a bad date or amount? I catch that error inside the reading loop. The app skips the bad row and keeps loading the other transactions.

•What happens if the file can’t be written? saveTransaction prints an error and returns false. The app only adds the entry to the ledger’s list after the save reports success.

•	What if I leave a field blank in Custom Search? The app skips that field. If I enter both a vendor and an amount, the transaction has to match both.

•	How does it remember transactions? It appends each transaction to transactions.csv using pipes ( | ) to separate the five fields. At startup, it reads the saved rows back into an ArayList<Transaction>.

•	What would you improve? I’d use BigDecimal for amounts. Right now, the app uses double, which can’t represent every decimal value exactly. And, as the project grows, I’d move the file-reading and writing code into its own class so AccountingLedgerApp can focus on the menus. 

