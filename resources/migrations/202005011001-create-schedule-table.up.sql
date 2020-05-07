CREATE TABLE schedule(
  scheduleid int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  fromdate date,
  todate date,
  userid int(11) NOT NULL DEFAULT 0,
  helpeeid int(11) NOT NULL DEFAULT 0
);
--;;
insert  into schedule(scheduleid,fromdate,todate,userid,helpeeid) values
(1,'2019-07-05.','2019-07-06.',1,2),
(2,'2019-07-06.','2019-07-07.',6,2),
(3,'2019-07-25.','2019-07-27.',1,0),
(4,'2019-07-10.','2019-07-11.',0,6),
(5,'2019-07-12.','2019-07-15.',0,5),
(6,'2019-07-12.','2019-07-15.',6,0);
