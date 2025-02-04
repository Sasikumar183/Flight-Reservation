create table user(user_id int auto increment primary key,username varchar(20),mail varchar(40),phone varchar(10),password varchar(16))

create table booking(book_id int auto increment primary key,user_id int,sche_id int,flight_id int)

create table flight(flight_id int auto increment primary key, flight_name varchar(20),available_seats int)

create table ticket(ticket_id int auto increment primary key, user_id int, flight_id int,seat_no int)

create table schedule(sche_id int auto_increment primary key,flight_id int,dep_loc varchar(20),arr_loc varchar(20),dep_time time,arr_time time,price int)



//cors error

setCORSHeaders(response);
private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }