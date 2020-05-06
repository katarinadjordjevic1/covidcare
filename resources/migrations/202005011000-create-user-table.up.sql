CREATE TABLE user(
  userid int(11) NOT NULL AUTO_INCREMENT,
  firstname varchar(50) NOT NULL,
  lastname varchar(50) NOT NULL,
  username varchar(50) NOT NULL,
  password varchar(50) NOT NULL,
  email varchar(50) NOT NULL,
  role varchar(10) NOT NULL,
  PRIMARY KEY (userid)
);
--;;
insert into user(userid,firstname,lastname,username,password,email,role) values
(1,'John','Smith','john','123','johnsmith@gmail.com','admin'),
(2,'George','Martin','george','123','georgemartin@gmail.com','user'),
(3,'Thomas','Lucas','thomas','123','tomluc@gmail.com','user'),
(4,'User','User','user','user','user@gmail.com','user'),
(5,'Admin','Admin','admin','admin','admin@gmail.com','admin');
