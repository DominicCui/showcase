INSERT INTO 'order' (orderId, store, droneId, createdDate, status) values ("purchaseA", "publix", "1", "2021-10-28 00:24:43", "COMPLETED" );
INSERT INTO 'order' (orderId, store, droneId, createdDate, status) values ("purchaseA", "kroger", "1", "2021-10-28 01:12:36", "COMPLETED" );
INSERT INTO 'order' (orderId, store, droneId, createdDate, status) values ("purchaseB", "publix", "1", "2021-11-08 08:27:23", "COMPLETED" );
INSERT INTO 'order' (orderId, store, droneId, createdDate, status) values ("purchaseC", "publix", "2", "2021-11-20 13:24:43", "COMPLETED" );
INSERT INTO 'order' (orderId, store, droneId, createdDate, status) values ("purchaseB", "kroger", "1", "2021-11-20 13:25:08", "COMPLETED" );
INSERT INTO 'order' (orderId, store, droneId, createdDate, status) values ("purchaseD", "publix", "1", "2021-11-28 16:42:35", "COMPLETED" );

-- The following records can used to test archive locally, in docker, we only using the fake order above to show the functionality
-- INSERT INTO line (orderId, store, itemName, quantity, price, createdDate) values ("purchaseA", "publix", "item1", "4", "23", "2021-10-28 00:24:43");
-- INSERT INTO line (orderId, store, itemName, quantity, price, createdDate) values ("purchaseA", "kroger", "item1", "2", "10", "2021-10-28 01:12:36");
-- INSERT INTO line (orderId, store, itemName, quantity, price, createdDate) values ("purchaseB", "publix", "item2", "3", "20", "2021-11-08 08:32:38");
-- INSERT INTO line (orderId, store, itemName, quantity, price, createdDate) values ("purchaseC", "publix", "item1", "3", "18", "2021-11-20 13:35:26");
-- INSERT INTO line (orderId, store, itemName, quantity, price, createdDate) values ("purchaseB", "kroger", "item3", "3", "21", "2021-11-20 13:29:33");
-- INSERT INTO line (orderId, store, itemName, quantity, price, createdDate) values ("purchaseD", "publix", "item1", "2", "23", "2021-11-28 16:47:19");

-- INSERT INTO item (itemName, store, weight) values ("item1", "publix", "4");
-- INSERT INTO item (itemName, store, weight) values ("item2", "publix", "7");
-- INSERT INTO item (itemName, store, weight) values ("item1", "kroger", "3");
-- INSERT INTO item (itemName, store, weight) values ("item3", "kroger", "5");
