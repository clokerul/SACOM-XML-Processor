Developer Name: George Lazureanu

Application Name: SACOM XMLProcessor

Description:
The solution presented below starts with two premises:
	-> the orders placed by customers are directed to an API accesible witihn the application,for 	simplicity and ease, I used a directory
	-> the purpose of this application is obtaining the Supplier XML Files (the distribution via
	Internet could be done at a later stage)

The approach. Solving the main problem comes to solving the following three subproblems:
	-> Create an observer that awaits for new product orders, then
	-> Deserialize data accordingly, making sure that there won't be any data loss, then
	-> Process the data, serialize the data and deliver it to the customer

In Order to keep customers happy, the application needs to be performant, mainly regarding time. For this objective in particular, the application will exploit the system's architecture in two ways:
	-> It will use multi-thread computation, fragmenting big serialized tasks (time
	bottlenecks) into smaller parallel tasks
	-> It will use memory wisely, declaring single instances, and making sure to mark variables for 
	garbage collection as soon as they are no longer usefull, thus keeping the heap usage low and
	increasing cache hit rate

Classes:

XMLProcessor.java:
	This is the driver class, it's objective is to manage multithread computation for deserialization, serialization and data processing. The purpose here is to maximize parallelism while making sure that conccurrency between tasks is controled accordingly. By achieveing it's purpose, this class assures time performance.
	The class itself is a thread that can be instantiated using the desired directory and the rate at which the XMLProcessor will scan it.
	@run method:
		When the XMLProc. is started, it will start the observer (scanner), then will check
	periodically for updates from it, by a shared-object syncOrderList, which is a thread-safe list that
	will contain every file found by the scanner at a given time.
		Once started as a thread (with start), the XMLProc. will only be stopped by the proccesAndEnd.
	@processAndEnd:
		This method provides a reliable and safe way to stop the XMLProc. It guarantees that every
	pending order will be processed before closing the processor
	@setPathToProcessed:
		Gives the user a way to chose it's own Supplier XML directory

DirectoryObserver.java:
	This class solves the first problem, it awaits for new product orders and then notifyies XMLProc. via shared memory of them. At this level the application does an integrity check of the file names.

OrderProcessorTask.java:
        Here, every order is processed in 4 steps: deserialization, data processing, serialization and delivery.	
	@processOrderFile:
		This method does deserialize the input, extracting the data with no loses and returing it as
	Orders class
	@sendProductsToSuppliers:
		This method does the last 3 steps, firsly it processes the data in an efficient way, using
	hashmaping to link each supplier to it's products, then it does serialize the data into XML format and 
	delivers it to the suppliers accordingly

XMLProcessorTest.java:
	This class can be found in the test folder, I have used it to emulate a RealTime situation. To achieve that, I have used parallel independent threads to act like Customers, sending an order at defined intervals. With those orders programatically controlled, I could test the XMLProc. using Junit 5.
	How would one test work? A number of customers (usually 10) will place orders (usually 15), one each 0.5 seconds the XMLProc. should process them as soon as it is notified of their existance by DirectoryObserver. To see this in real-time, I have decided to delete the processed files (but they could also be moved in an industrial environment for back-up purposes), then check if the files resulted after the processing would match the correct output files.

In this approach I have used:
	com.fasterxml.jackson library to access fast and reliable XML Serialization tools
	commons-io for file comparrison

To avoid human error the application has the following fallbacks:
	-> if an order is submited twice (eg. order01.xml), it will be processed once, sent, then processed twice and sent again and only the second version of Supplier XML Files saved
	-> if an order has more than 2 digits, the application will still work properly, as it is considered a scaling fallback rather than a bug

To avoid application missuse:
	-> There isn't a minimum scanRate, but I strongly advise you to put a scanRate > 100ms when you don't expect a large amount of product orders, a scanrate of 1000ms (1s) would most likely do the job even with large amounts of orders (10k+)

Demo.java:
	This class acts as the main class, showcasing the application API's and usage.

Possible updates:
	The application could be later using Springboot to provide access to APIs regarding order status etc. along with JPA Persistance, in order to ensure data persistance.