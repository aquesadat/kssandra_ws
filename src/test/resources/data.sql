DELETE FROM prediction;
DELETE FROM crypto_data;
DELETE FROM crypto_currency;
DELETE FROM av_account;

INSERT INTO av_account (user_id, api_key) VALUES ('test_acc_1','AAAAAAAAAAAA'), ('test_acc_2','BBBBBBBBBBB');

INSERT INTO crypto_currency (code, name, av_account, active) VALUES ('ADA','Crypto Currency Test 1','test_acc_1',1),
('BTC','Crypto Currency Test 2','test_acc_2',1);

INSERT INTO crypto_data (cx_code, read_time, open, close, high, low) VALUES 
('ADA','2022-09-14 00:01:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 01:00:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 02:45:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 03:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 04:15:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 05:00:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 06:45:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 07:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 08:15:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 09:00:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 10:45:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 11:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 12:15:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 13:00:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 14:45:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 15:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 16:15:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 17:00:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 18:45:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 19:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 20:15:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 21:00:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 22:45:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 23:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 00:14:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 01:00:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 02:46:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 03:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 04:14:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 05:01:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 06:46:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 07:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 08:14:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 09:01:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 10:46:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 11:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 12:14:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 13:01:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 14:46:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 15:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 16:14:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 17:01:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 18:46:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 19:30:00',18362.15163,18311.01804,18454.03245,18307.3732),
('ADA','2022-09-14 20:14:00',18357.3239,18399.01359,18399.01359,18334.13731),
('ADA','2022-09-14 21:01:00',18369.7683,18357.3239,18400.68695,18339.00351),
('ADA','2022-09-14 22:46:00',18311.00842,18369.77791,18385.7229,18285.1387),
('ADA','2022-09-14 23:30:00',18362.15163,18311.01804,18454.03245,18307.3732);

INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 14:45',0.4668466666666973,45,NULL,'2022-09-08 14:30',0.08074831536804083,15);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 14:45',0.4677050000000236,60,NULL,'2022-09-08 14:30',0.26475443223006323,15);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 15:00',0.4653716666671244,45,NULL,'2022-09-08 14:30',0.30598400447206586,30);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 15:00',0.4667450000001736,60,NULL,'2022-09-08 14:30',0.011782347863402265,30);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 15:15',0.4638966666670967,45,NULL,'2022-09-08 14:30',0.7197991124648553,45);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 15:15',0.46578500000009626,60,NULL,'2022-09-08 14:30',0.31567007659627677,45);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 15:30',0.4648250000000189,60,NULL,'2022-09-08 14:30',0.6317072127882852,60);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 15:30',0.46740142857140654,120,NULL,'2022-09-08 14:30',0.08092937461914573,60);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 15:30',0.4704362499999988,240,NULL,'2022-09-08 14:30',0.4819193472593497,60);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 16:30',0.4609850000001643,60,NULL,'2022-09-08 14:30',2.157,120);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 16:30',0.4654728571427995,120,NULL,'2022-09-08 14:30',1.204,120);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 16:30',0.47031125000000173,240,NULL,'2022-09-08 14:30',0.17802186140258414,120);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 17:30',0.4571450000000823,60,NULL,'2022-09-08 14:30',3.637,180);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 17:30',0.4635442857141925,120,NULL,'2022-09-08 14:30',22,180);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 17:30',0.4701862500000047,240,NULL,'2022-09-08 14:30',0.888227234400361,180);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 18:30',0.4533050000000003,60,NULL,'2022-09-08 14:30',23.68,240);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 18:30',0.46161571428558545,120,NULL,'2022-09-08 14:30',0.5781360573798224,240);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 18:30',0.47006125000000054,240,NULL,'2022-09-08 14:30',0.6813849383140251,240);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 19:30',0.44946500000014566,60,NULL,'2022-09-08 14:30',36.888,300);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 19:30',0.4596871428569784,120,NULL,'2022-09-08 14:30',14.984,300);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 19:30',0.4699362500000035,240,NULL,'2022-09-08 14:30',0.33440442385368385,300);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 19:30',0.4701909355072189,360,NULL,'2022-09-08 14:30',0.38878141367271724,300);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 20:30',0.4701262659419996,360,NULL,'2022-09-08 14:30',0.0800991893559484,360);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 20:30',0.46457696917070734,720,NULL,'2022-09-08 14:30',0.597605928770065,360);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 20:30',0.46834210608400895,1080,NULL,'2022-09-08 14:30',0,360);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 21:30',0.4700615963767767,360,NULL,'2022-09-08 14:30',0.06131681157080493,420);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 21:30',0.46384216131139056,720,NULL,'2022-09-08 14:30',1.383,420);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 21:30',0.4679367854846248,1080,NULL,'2022-09-08 14:30',0.5130678251036898,420);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 22:30',0.46999692681155736,360,NULL,'2022-09-08 14:30',12.217,480);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 22:30',0.46310735345207377,720,NULL,'2022-09-08 14:30',2.669,480);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 22:30',0.46753146488521224,1080,NULL,'2022-09-08 14:30',17.398,480);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 23:30',0.4699322572463345,360,NULL,'2022-09-08 14:30',0.6968583466106395,540);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 23:30',0.462372545592757,720,NULL,'2022-09-08 14:30',22.943,540);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-08 23:30',0.4671261442858281,1080,NULL,'2022-09-08 14:30',12.898,540);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 0:30',0.46986758768111514,360,NULL,'2022-09-08 14:30',0.5023742840261036,600);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 0:30',0.4616377377334402,720,NULL,'2022-09-08 14:30',2.245,600);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 0:30',0.46672082368641554,1080,NULL,'2022-09-08 14:30',11.687,600);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 1:30',0.4698029181158958,360,NULL,'2022-09-08 14:30',0.6402051232164183,660);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 1:30',0.4609029298741234,720,NULL,'2022-09-08 14:30',2.522,660);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 1:30',0.4663155030870314,1080,NULL,'2022-09-08 14:30',13.777,660);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 2:30',0.4601681220148066,720,NULL,'2022-09-08 14:30',39.074,720);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 2:30',0.46591018248764726,1080,NULL,'2022-09-08 14:30',27.083,720);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 2:30',0.4775316439794466,1440,NULL,'2022-09-08 14:30',0.281564488087497,720);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 3:30',0.4594333141554898,720,NULL,'2022-09-08 14:30',3.982,780);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 3:30',0.4655048618882347,1080,NULL,'2022-09-08 14:30',27.137,780);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 3:30',0.4777449676365251,1440,NULL,'2022-09-08 14:30',0.15570489738028925,780);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 4:30',0.45869850629617304,720,NULL,'2022-09-08 14:30',4.196,840);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 4:30',0.46509954128885056,1080,NULL,'2022-09-08 14:30',28.593,840);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 4:30',0.47795829129360357,1440,NULL,'2022-09-08 14:30',0.17371054249178997,840);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 5:30',0.45796369843685625,720,NULL,'2022-09-08 14:30',5.038,900);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 5:30',0.464694220689438,1080,NULL,'2022-09-08 14:30',36.423,900);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 5:30',0.47817161495068206,1440,NULL,'2022-09-08 14:30',0.8477553704055794,900);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 6:30',0.45722889057753946,720,NULL,'2022-09-08 14:30',5.655,960);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 6:30',0.46428890009005386,1080,NULL,'2022-09-08 14:30',4.199,960);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 6:30',0.47838493860776055,1440,NULL,'2022-09-08 14:30',12.906,960);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 7:30',0.45649408271816583,720,NULL,'2022-09-08 14:30',6.383,1020);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 7:30',0.4638835794906697,1080,NULL,'2022-09-08 14:30',4.867,1020);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 7:30',0.47859826226485325,1440,NULL,'2022-09-08 14:30',18.501,1020);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 8:30',0.45575927485884904,720,NULL,'2022-09-08 14:30',5.998,1080);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 8:30',0.46347825889125716,1080,NULL,'2022-09-08 14:30',4.405,1080);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 8:30',0.47881158592193174,1440,NULL,'2022-09-08 14:30',12.433,1080);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 9:30',0.45502446699953225,720,NULL,'2022-09-08 14:30',6.608,1140);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 9:30',0.463072938291873,1080,NULL,'2022-09-08 14:30',49.560,1140);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 9:30',0.4790249095790102,1440,NULL,'2022-09-08 14:30',1.682,1140);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 10:30',0.45428965914021546,720,NULL,'2022-09-08 14:30',7.119,1200);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 10:30',0.46266761769246045,1080,NULL,'2022-09-08 14:30',540.622,1200);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 10:30',0.4792382332360887,1440,NULL,'2022-09-08 14:30',2.018,1200);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 11:30',0.4535548512808987,720,NULL,'2022-09-08 14:30',7.005,1260);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 11:30',0.4622622970930763,1080,NULL,'2022-09-08 14:30',5.219,1260);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 11:30',0.4794515568931672,1440,NULL,'2022-09-08 14:30',16.953,1260);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 12:30',0.4528200434215819,720,NULL,'2022-09-08 14:30',7.606,1320);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 12:30',0.4618569764936922,1080,NULL,'2022-09-08 14:30',5.762,1320);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 12:30',0.4796648805502599,1440,NULL,'2022-09-08 14:30',21.291,1320);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 13:30',0.4520852355622651,720,NULL,'2022-09-08 14:30',8.267,1380);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 13:30',0.4614516558942796,1080,NULL,'2022-09-08 14:30',6.366,1380);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 13:30',0.4798782042073384,1440,NULL,'2022-09-08 14:30',2.628,1380);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 14:30',0.4800915278644169,1440,NULL,'2022-09-08 14:30',2.628,1440);
INSERT INTO prediction (cx_code, predict_time, predict_val, sample_size, sample_slope, curr_time, success, advance) VALUES ('ADA','2022-09-09 14:30',0.48309549423942144,2100,NULL,'2022-09-08 14:30',2.628,1440);
