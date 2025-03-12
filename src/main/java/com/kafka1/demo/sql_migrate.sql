create table users(
id serial PRIMARY KEY ,
email varchar,
password varchar,
role varchar,
last_name varchar,
first_name varchar,
code_email varchar,
time_send_email_code timestamp,
balance int,
secret_key varchar);

create table doctors(
id serial PRIMARY KEY,
speciality varchar,
rating integer,
user_id int,
price_per_minute int,
FOREIGN KEY (user_id) REFERENCES users(id));

create table doctor_sessions(
    id serial PRIMARY KEY,
    time_start time,
    time_end time,
    doctor_id int,
    user_id int,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    zoom_url varchar,
    date date,
    time_really_start time
);

create table "doctor_schedule"
(
    id serial PRIMARY KEY,
    doctor_id int,
    day_week int CHECK (day_week>=0 AND day_week<7),
    start_time TIME,
    end_time TIME,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

create table chats
(
    id serial PRIMARY KEY,
    doctor_id int,
    user_id int,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

create table messages(
    id serial PRIMARY KEY ,
    text varchar,
    owner int,
    chat int,
    FOREIGN KEY (chat) REFERENCES chats(id)
);

