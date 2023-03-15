CREATE TABLE person ( account TEXT NOT NULL PRIMARY KEY ,
 firstName TEXT,
 lastName TEXT,
 phoneNumber TEXT,
 createdDate DATETIME );

CREATE TABLE customer ( custId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
 rating TEXT ,
 credit INTEGER,
 account UNIQUE NOT NULL REFERENCES person(account),
 createdDate DATETIME );

CREATE TABLE pilot ( pilotId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
 account UNIQUE NOT NULL REFERENCES person(account),
 licenseId TEXT UNIQUE NOT NULL,
 taxId TEXT ,
 level INTEGER,
 assigned TEXT,
 createdDate DATETIME );

CREATE TABLE store ( storeName TEXT PRIMARY KEY NOT NULL ,
 revenue INTEGER,
 createdDate DATETIME );

CREATE TABLE 'order' ( orderId TEXT NOT NULL,
 store TEXT NOT NULL ,
 droneId TEXT NOT NULL,
 createdDate DATETIME,
 customerAccount REFERENCES customer(account),
 status TEXT DEFAULT "PENDING",
 FOREIGN KEY(store,droneId) REFERENCES drone(storeName,droneId),
 PRIMARY KEY (orderId,store) );

CREATE TABLE drone ( droneId TEXT NOT NULL,
 storeName NOT NULL REFERENCES store(storeName),
 weightCapacity INTEGER,
 remainingCapacity INTEGER,
 numberOfDeliveries INTEGER,
 remainingFuel INTEGER,
 assignedPilot REFERENCES pilot(account),
 createdDate DATETIME,
 PRIMARY KEY (droneId, storeName) );

CREATE TABLE item ( itemName NOT NULL ,
 store NOT NULL,
 weight INTEGER,
 createdDate DATETIME,
 PRIMARY KEY (itemName,store),
 FOREIGN KEY(store) REFERENCES store(storeName) );

CREATE TABLE line ( orderId TEXT NOT NULL,
store TEXT NOT NULL ,
itemName NOT NULL,
quantity INTEGER,
price INTEGER,
createdDate DATETIME,
FOREIGN KEY(store,orderId) REFERENCES 'order'(store,orderId),
FOREIGN KEY(store,itemName) REFERENCES item(store,itemName),
PRIMARY KEY (store,orderId,itemName) );


CREATE INDEX indx_store_store ON store(storeName);
CREATE INDEX indx_store_date ON store(createdDate);
CREATE INDEX indx_item_name ON item(itemName);
CREATE INDEX indx_item_store ON item(store);
CREATE INDEX indx_item_date ON item(createdDate);
CREATE INDEX indx_person_account ON person(account);
CREATE INDEX indx_person_date ON person(createdDate);
CREATE INDEX indx_pilot_account ON pilot(account);
CREATE INDEX indx_pilot_date ON pilot(createdDate);
CREATE INDEX indx_drone_id ON drone(droneId);
CREATE INDEX indx_drone_store ON drone(storeName);
CREATE INDEX indx_drone_date ON drone(createdDate);
CREATE INDEX indx_customer_account ON customer(account);
CREATE INDEX indx_customer_date ON customer(createdDate);
CREATE INDEX indx_order_store ON 'order'(store);
CREATE INDEX indx_order_id ON 'order'(orderId);
CREATE INDEX indx_order_date ON 'order'(createdDate);

