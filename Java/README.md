# CS6310-G65-A5

# Docker required

# To build and run Docker image
```
sudo docker build -t gatech/grocery_express -f Dockerfile ./
sudo docker run -ti gatech/grocery_express sh
```
## To run & test in interactive mode
```
java -jar CS6310-G65-A5.jar < sample_scenarios.txt > results.txt
diff -s results.txt sample_result.txt > diff.txt
cat diff.txt
```
The last two command in sample_scenarios is display order_history.
Since the completed date will be based on the time when the case is run, the expected result doesn't have them. 
## To run in interactive mode
#### Step 1 from the container
```
java -jar CS6310-G65-A5.jar
```
#### Step 2 from the jar
* From there you can run any of the commands listed from the assignment:
* Add two extra command for check order history and archive orders.
```
order_history,<store>
archive_orders,<day>
make_store,<Store>,<InitialRevenue>
display_stores
sell_item,<Store>,<Item>,<Weight>
display_items,<Store>
make_pilot,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<TaxId>,<LicenseId>,<ExperienceLevel>
display_pilots
make_drone,<Store>,<DroneId>,<WeightCapacity>,<NumberOfDeliveries>
display_drones,<Store>
fly_drone,<Store>,<DroneId>,<PilotAccount>
make_customer,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<CustomerRating>,<Credits>
display_customers
start_order,<Store>,<OrderId>,<DroneId>,<CustomerAccount>
display_orders,<Store>
request_item,<Store>,<OrderId>,<Item>,<Quantity>,<UnitPrice>
purchase_order,<Store>,<OrderId>
cancel_order,<Store>,<OrderId>
stop
```

## To using JMeter in GUI MODE
```
cd apache-jmeter-5.4.1/bin
[Windows].\jmeter.bat
[Unix]./jmeter 
```
### To run test plan
```
File->Open->JDBCRequest.jmx->OK
```
Database Connection Configuration->Database URL: modify path "/" or "\\" depend on your system

**After run JMeter the database will be changed, please explore it at last.**