INSERT INTO `user` (id,email,password,role) VALUES ('user1', 'admin@test.com', '$2a$10$r53yPdAQAGaaYD00pAbPreRy68oMRc97XNDz352WLy4EuutIz4VhK', 'ROLE_ADMIN');
INSERT INTO `user` (id,email,password,role) VALUES ('user2', 'customer@test.com', '$2a$10$r53yPdAQAGaaYD00pAbPreRy68oMRc97XNDz352WLy4EuutIz4VhK', 'ROLE_CUSTOMER');
INSERT INTO `user` (id,email,password,role) VALUES ('user3', 'customer2@test.com', '$2a$10$r53yPdAQAGaaYD00pAbPreRy68oMRc97XNDz352WLy4EuutIz4VhK', 'ROLE_CUSTOMER');

INSERT INTO customer (id, address,first_name,name,phone_number) VALUES ('user2', '30 rue machin, Lyon', 'FirstName1', 'Name1', '0123456789');
INSERT INTO customer (id, address,first_name,name,phone_number) VALUES ('user3', '45 avenue truc, Vénissieux', 'FirstName2', 'Name2', '0663945423');

INSERT INTO room (id,number,capacity,price) VALUES ('room1', 'A1', 2, 65);
INSERT INTO room (id,number,capacity,price) VALUES ('room2', 'A2', 3, 85);
INSERT INTO room (id,number,capacity,price) VALUES ('room3', 'B1', 4, 95);
INSERT INTO room (id,number,capacity,price) VALUES ('room4', 'B2', 2, 70);

INSERT INTO booking (id,customer_id,duration,guest_count,start_date,total) VALUES ('booking1','user2',2 , 2,'2025-01-01',130);
INSERT INTO booking (id,customer_id,duration,guest_count,start_date,total) VALUES ('booking2','user2',7 , 1,'2025-05-01',490);
INSERT INTO booking (id,customer_id,duration,guest_count,start_date,total) VALUES ('booking3','user3',1 , 5,'2025-08-01',150);

INSERT INTO booking_rooms (bookings_id,rooms_id) VALUES ('booking1','room1');
INSERT INTO booking_rooms (bookings_id,rooms_id) VALUES ('booking2','room4');
INSERT INTO booking_rooms (bookings_id,rooms_id) VALUES ('booking3','room1');
INSERT INTO booking_rooms (bookings_id,rooms_id) VALUES ('booking3','room2');