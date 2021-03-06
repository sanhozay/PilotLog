insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(100, 'G-SHOZ', 'pup150', 'EGCJ', 24, '2018-01-06 10:44:00.000', 0, 'EGNT', 15.8, '2018-01-06 11:12:00.000', 80, 4000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(101, 'G-SHOZ', '707', 'EGNT', 1000, '2018-01-06 14:23:00.000', 0, 'EGLL', 350, '2018-01-06 15:42:00.000', 190, 18000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(102, 'G-SHOZ', 'tu154b', 'EGLL', 800, '2018-01-06 10:12:00.000', 0, 'EGNM', 550, '2018-01-06 11:12:00.000', 170, 17000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(103, 'G-SHOZ', 'tu154b', 'EGNM', 1800.2342, '2018-01-06 10:17:00.000', 0, 'BIKF', 550, '2018-01-06 14:37:00.000', 670, 38000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(104, 'G-SHOZ', 'tu154b', 'BIKF', 1400.2342, '2018-01-06 15:17:00.000', 0, 'KJFK', 150, '2018-01-06 19:37:00.000', 2870, 37000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(105, 'G-SHOZ', 'pup150', 'EGCJ', 24, '2018-01-09 10:44:00.000', 0, 'EGNT', 15.8, '2018-01-09 11:12:00.000', 80, 4000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(106, 'G-SHOZ', '707', 'EGNT', 1000, '2018-01-09 14:23:00.000', 0, 'EGLL', 350, '2018-01-09 15:42:00.000', 190, 18000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(107, 'G-SHOZ', 'tu154b', 'EGNM', 1800.2342, '2018-01-09 10:12:00.000', 0, 'BIKF', 550, '2018-01-09 14:37:00.000', 670, 38000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(108, 'G-SHOZ', 'tu154b', 'BIKF', 1400.2342, '2018-01-09 15:12:00.000', 0, 'KJFK', 150, '2018-01-09 19:37:00.000', 2870, 37000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(109, 'G-SHOZ', 'pup150', 'EGCJ', 24, '2018-01-10 10:44:00.000', 0, 'EGNT', 15.8, '2018-01-10 11:12:00.000', 80, 4000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(110, 'G-SHOZ', '707', 'EGNT', 1000, '2018-01-10 14:23:00.000', 0, 'EGLL', 350, '2018-01-10 15:42:00.000', 190, 18000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(111, 'G-SHOZ', 'tu154b', 'EGLL', 800, '2018-01-10 10:12:00.000', 0, 'EGNM', 550, '2018-01-10 11:12:00.000', 170, 17000, 'COMPLETE', false);

insert into flight (id, callsign, aircraft, origin, start_fuel, start_time, start_odometer, destination, end_fuel, end_time, end_odometer, altitude, status, tracked)
values(112, 'G-SHOZ', 'tu154b', 'EGNM', 1800.2342, '2018-01-10 10:15:00.000', 0, 'BIKF', 550, '2018-01-10 14:37:00.000', 670, 38000, 'COMPLETE', false);

alter sequence flight_sequence restart with 113;
