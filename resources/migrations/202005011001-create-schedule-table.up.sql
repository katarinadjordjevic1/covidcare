CREATE TABLE schedule(
  scheduleid int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  fromdate date,
  todate date,
  userid int(11) NOT NULL,
  helpee int(11) NOT NULL,
  FOREIGN KEY(userid) REFERENCES user(userid)
);
--;;
insert  into schedule(scheduleid,fromdate,todate,userid,helpee) values
(1,'2019-07-05.','2019-07-06.',1,2),
(2,'2019-07-06.','2019-07-07.',3,2),
(3,'2019-07-25.','2019-07-27.',1,0);
