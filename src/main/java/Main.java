import Model.*;

import java.io.Console;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static java.time.format.DateTimeFormatter.ofPattern;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.init();
    }

    private void init() {

        while(true){
            System.out.println("1:: to become admin");
            System.out.println("2:: book a flight");
            System.out.println("3:: get my history");
            System.out.println("4:: exit");

            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();

            if(choice == 4){
                break;
            }

            switch (choice){
                case 1 -> {adminpanel();}
                case 2 -> {flightbook();}
                case 3 -> {history();}
            }
        }
    }

    private void history() {
        System.out.println("enter email id");
        Scanner sc = new Scanner(System.in);
        String email = sc.next();
        List<Bookings> historysofbooking = userHistory(email);
        for(Bookings b : historysofbooking){
            System.out.println(b.toString());
        }
    }

    private List<Bookings> userHistory(String email) {
        Callable<List<Bookings>> history = () -> {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select * from member where email = ?");
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();

            Member mem = null;
            while(rs.next()){
                mem = new Member(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }

            Member finalMem = mem;
            Callable<List<Bookings>> bookingHistory = () -> {
                PreparedStatement innerps = con.prepareStatement("select * from bookings where member_id = ?");
                innerps.setLong(1, finalMem.getId());
                ResultSet innerrs = innerps.executeQuery();

                List<Bookings> usrbookings = null;
                while(innerrs.next()){
                    usrbookings.add(new Bookings(innerrs.getLong("id"),innerrs.getInt("member_id"),
                            innerrs.getInt("traveler_id"),innerrs.getInt("flight_id"),innerrs.getString("ticketmno"),
                            rs.getString("seatno"),innerrs.getInt("price"),innerrs.getInt("from_city_id"),
                            innerrs.getInt("to_city_id")));
                }

                return usrbookings;
            };
            List<Bookings> bookingsList = null;
            Future<List<Bookings>> foundBookingList = ThreadManager.executeThread(bookingsList,bookingHistory);
            try{
                if(foundBookingList.isDone()){
                    bookingsList = foundBookingList.get();
                    return bookingsList;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

            return null;
        };
        List<Bookings> memData = null;
        Future<List<Bookings>> isMemData = ThreadManager.executeThread(memData,history);
        try{
            if (isMemData.isDone()){
                memData = isMemData.get();
                return memData;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;

    }

    private void flightbook() {
        String email = null;
        String password = null;

        if(email == null && password == null){
            int created = signupNewUser();
            if(created == 1){
                System.out.println("new user created you can not login");
            }else{
                System.out.println("user may already exist or error occured");
            }
        }

        Console console = System.console();
        if(console == null){
            System.out.println("error opening console");
            return;
        }
        email = console.readLine();
        password = Arrays.toString(console.readPassword());

        Member mem = getMember(email,password);
        Scanner sc = new Scanner(System.in);

        Map<String,Object> selectedFlight = new HashMap<>();
        System.out.println("select start point (use id number of city)");
        for(Cities c : getCityList()){
            System.out.println(c.toString());
        }
        selectedFlight.put("from_city_id",sc.nextInt());

        System.out.println("select end point (use id number of city)");
        for(Cities c : getCityList()){
            System.out.println(c.toString());
        }
        selectedFlight.put("to_city_id",sc.nextInt());

        System.out.println("enter travel date (use format:: yyyy-MM-dd");
        selectedFlight.put("date", LocalDate.parse(sc.next(), ofPattern("yyyy-MM-dd")));

        System.out.println("choose flight");
        List<Flights> availableFlights = findFlight(selectedFlight);
        if (availableFlights == null){
            return;
        }
        for(Flights f : availableFlights){
            System.out.println(f.toString());
        }
        selectedFlight.put("flight_id",sc.nextInt());

        System.out.println("enter passenger details\n\n");
        Traveler passenger = new Traveler();
        System.out.println("enter passenger name");
        passenger.setFirst_name(sc.next());

        System.out.println("enter passenger surname");
        passenger.setSurname(sc.next());

        System.out.println("enter passenger age");
        passenger.setAge(sc.nextInt());

        System.out.println("enter passenger gender");
        passenger.setGender(sc.next());
        passenger.setMember_id(mem.getId());
        passenger.setCreated(LocalDate.now());

        System.out.println("select seat");
        for(Flights f : availableFlights){
            System.out.println(f.getSeats());
        }
        selectedFlight.put("seat_no",sc.next());
        updateAvailableSeats(selectedFlight);
        selectedFlight.put("passenger_details",passenger);
        addTravelerDetails(passenger);
        //add remaining data require for booking table
        Flights myFlight = findFlightById((Integer)selectedFlight.get("flight_id"));
        selectedFlight.put("price",myFlight.getPrice());
        selectedFlight.put("ticketno",generate_random_ticket());
        selectedFlight.put("date",myFlight.getDeparture_date());
        selectedFlight.put("time",myFlight.getDeparture_time());
        selectedFlight.put("member_id",mem.getId());
        Traveler thist = getThisTravelerId(mem,passenger);
        selectedFlight.put("traveler_id",thist.getId());

        if(bookTheFlight(selectedFlight)==1){
            System.out.println("flight booked successfully");
        }
        System.out.println("error booking flight try later");
        return;


    }

    private Integer bookTheFlight(Map<String, Object> selectedFlight) {
        Callable<Integer> bookingOfFlight = () -> {
            PreparedStatement ps = getConnection().prepareStatement("insert into bookings (traveler_id,flight_id,ticketno,date,price,from_city_id,to_city_id,member_id,time,seatno)" +
                    "values (?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1,(int) selectedFlight.get("traveler_id"));
            ps.setInt(2,(int) selectedFlight.get("flight_id"));
            ps.setString(3,selectedFlight.get("ticketno").toString());
            ps.setString(4,selectedFlight.get("date").toString());
            ps.setString(5,selectedFlight.get("price").toString());
            ps.setInt(6,(int)selectedFlight.get("from_city_id"));
            ps.setInt(7,(int)selectedFlight.get("to_city_id"));
            ps.setInt(8,(int)selectedFlight.get("member_id"));
            ps.setString(9,selectedFlight.get("time").toString());
            ps.setString(10,selectedFlight.get("seatno").toString());
            return ps.executeUpdate();
        };

        Future<Integer> booked = ThreadManager.executeThread(Integer.valueOf(1),bookingOfFlight);
        try{
            if(booked.isDone()){
                return booked.get();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private Traveler getThisTravelerId(Member mem, Traveler passenger) {
        Callable<Traveler> tid = () -> {
            PreparedStatement ps = getConnection().prepareStatement("select * from traveler where member_id = ? and name = ? and surname = ? order by created desc limit 1");
            ps.setInt(1,mem.getId().intValue());
            ps.setString(2,passenger.getFirst_name());
            ps.setString(3,passenger.getSurname());
            ResultSet rs = ps.executeQuery();
            Traveler t = null;
            while(rs.next()){
                t = new Traveler(
                        rs.getLong("id"),
                        rs.getLong("member_id"),
                        rs.getString("first_name"),
                        rs.getString("surname"),
                        rs.getInt("age"),
                        rs.getString("gender")
                );
            }
            return t;
        };

        Traveler own = null;
        Future<Traveler> foundTid = ThreadManager.executeThread(own,tid);
        try{
            if(foundTid.isDone()){
                own = foundTid.get();
            }
            return own;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String generate_random_ticket() {
        String random = UUID.randomUUID().toString();
        return random.split("-")[0];
    }

    private Flights findFlightById(Integer flightId) {

        Callable<Flights> byId = () -> {
            PreparedStatement ps = getConnection().prepareStatement("select * from flight where id = ?");
            ps.setInt(1,flightId);
            ResultSet rs = ps.executeQuery();
            Flights idFlight = null;
            while(rs.next()){
                idFlight = new Flights(
                        rs.getLong("id"),
                        rs.getString("flight_no"),
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("seats"),
                        rs.getInt("from_city_id"),
                        rs.getInt("to_city_id"),
                        rs.getInt("price")
                );
            }
            return idFlight;
        };
        Flights f = null;
        Future<Flights> foundFlight = ThreadManager.executeThread(f,byId);
        try{
            if(foundFlight.isDone()){
                f = foundFlight.get();
            }
            return f;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Integer updateAvailableSeats(Map<String, Object> selectedFlight) {
        Callable<Integer> updateFlightSeats = () -> {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select seats from flight where id = ?");
            ps.setString(1,selectedFlight.get("seat_no").toString());
            ResultSet rs = ps.executeQuery();

            String[] previousSeats = null;
            while (rs.next()){
                previousSeats = rs.getString("seats").split(",");
            }

            String[] newAvailableSeats = Arrays.stream(previousSeats).filter((i) -> !i.equals(selectedFlight.get("seat_no")))
                    .toArray(String[]::new);

            Callable<Integer> updateSeats = () -> {
                PreparedStatement psinner = con.prepareStatement("update flight set seats = ? where id = ?");
                psinner.setString(1, Arrays.toString(newAvailableSeats));
                psinner.setString(2,selectedFlight.get("flight_id").toString());
                return psinner.executeUpdate();
            };

            Future<Integer> innerThreadResult = ThreadManager.executeThread(Integer.valueOf(1),updateSeats);
            try{
                if (innerThreadResult.isDone()){
                    return innerThreadResult.get();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            return null;
        };

        Future<Integer> updatedSeats = ThreadManager.executeThread(Integer.valueOf(1),updateFlightSeats);
        try{
            if (updatedSeats.isDone()){
                return updatedSeats.get();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private List<Flights> findFlight(Map<String, Object> selectedFlight) {

        Callable<List<Flights>> lookFlights = () -> {

            String[] date = selectedFlight.get("date").toString().split("-");
            PreparedStatement ps = getConnection().prepareStatement("select * from flight where from_city_id = ? and to_city_id = ? and departure_date = make_date(?,?,?)");
            ps.setString(1,selectedFlight.get("from_city_id").toString());
            ps.setString(2,selectedFlight.get("to_city_id").toString());
            ps.setString(3,date[0]);
            ps.setString(4,date[1]);
            ps.setString(5,date[2]);
            ResultSet rs = ps.executeQuery();

            List<Flights> availableFlights = new LinkedList<>();
            while(rs.next()){
                Flights flight = new Flights(
                        rs.getLong("id"),
                        rs.getString("flight_no"),
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("seats"),
                        rs.getInt("from_city_id"),
                        rs.getInt("to_city_id"),
                        rs.getInt("price"));
                flight.setDeparture_date(LocalDate.parse(rs.getString("departure_date"),DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                flight.setArrival_date(LocalDate.parse(rs.getString("arrival_date"),DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                flight.setDeparture_time(LocalTime.parse(rs.getString("departure_time"), DateTimeFormatter.ofPattern("HH:mm:ss")));
                flight.setArrival_time(LocalTime.parse(rs.getString("arrival_time"), DateTimeFormatter.ofPattern("HH:mm:ss")));
                availableFlights.add(flight);
            }

            return availableFlights;
        };

        List<Flights> resultRef = null;
        Future<List<Flights>> result = ThreadManager.executeThread(resultRef,lookFlights);
        try{
            if(result.isDone()){
                resultRef = result.get();
                return  resultRef;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private Integer addTravelerDetails(Traveler passenger) {
        Callable<Integer> addPassenger = () -> {
            PreparedStatement ps = getConnection().prepareStatement("insert into traveler (first_name,surname,age,gender,member_id) values (?,?,?,?,?)");
            ps.setString(1,passenger.getFirst_name());
            ps.setString(2,passenger.getSurname());
            ps.setInt(3,passenger.getAge());
            ps.setString(4,passenger.getGender());
            ps.setLong(5,passenger.getMember_id());
            return ps.executeUpdate();
        };

        Future<Integer> result = ThreadManager.executeThread(Integer.valueOf(1),addPassenger);
        try{
            if(result.isDone()){
                return result.get();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Member getMember(String email, String password) {

        Callable<Member> findUser = () -> {

            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select * from member where email = ? and password = ?");
            ps.setString(1,email);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            Member mem = null;

            while(rs.next()){
                mem = new Member((long) rs.getInt("id"),rs.getString("username"),rs.getString("email"),rs.getString("password"));
            }

            return mem;
        };

        Member existUser = null;
        Future<Member> result = ThreadManager.executeThread(existUser,findUser);
        try{
            if (result.isDone()){
                return result.get();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Integer signupNewUser() {
        Callable<Integer> newUser = () -> {
            String username = null;
            String email = null;
            String password = null;
            Console console = System.console();
            if(console == null){
                System.out.println("error opening console");
                return 0;
            }
            username = console.readLine("enter username");
            email = console.readLine("enter email");
            password = Arrays.toString(console.readPassword("enter password"));

            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("insert into member (username,email,password) values (?,?,?)");
            ps.setString(1,username);
            ps.setString(2,email);
            ps.setString(3,password);
            return ps.executeUpdate();
        };

        Future<Integer> result = ThreadManager.executeThread(Integer.valueOf(1),newUser);
        try{
            if (result.isDone()){
                return result.get();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private void adminpanel() {

        while(true){
            System.out.println("1:: add flight");
            System.out.println("2:: alter flight");
            System.out.println("3:: exit admin panel");

            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();

            if(choice == 3){
                break;
            }

            switch (choice){
                case 1 -> {
                    addflight();
                }
                case 2 -> {
                    alterflight();
                }
            }

        }
    }

    private void alterflight() {
        System.out.println("enter flight id number to alter");
        for(Flights item : getFlights()){
            System.out.println(item.toString());
        }
        Scanner sc = new Scanner(System.in);
        int flight_to_alter = sc.nextInt();

        Flights toAlter = new Flights();
        Callable<Integer> alterFligthThread = () -> {

            System.out.println("enter flight no");
            toAlter.setFlight_no(sc.next());

            System.out.println("enter name");
            toAlter.setName(sc.next());

            System.out.println("enter capacity");
            toAlter.setCapacity(sc.nextInt());

            System.out.println("enter from city");
            for(Cities c : getCityList()){
                System.out.println(c.toString());
            }
            toAlter.setFrom_city_id(sc.nextInt());

            System.out.println("enter to city");
            for(Cities c : getCityList()){
                System.out.println(c.toString());
            }
            toAlter.setTo_city_id(sc.nextInt());

            System.out.println("enter price");
            toAlter.setPrice(sc.nextInt());

            System.out.println();
            System.out.println("enter departure");
            System.out.print("\nenter departure year\t");
            int year = sc.nextInt();
            System.out.print("\nenter departure month\t");
            int month = sc.nextInt();
            System.out.print("\nenter departure day\t");
            int day = sc.nextInt();

            System.out.print("\nenter departure hour\t");
            int hour = sc.nextInt();
            System.out.print("\nenter departure minute\t");
            int minute = sc.nextInt();
            toAlter.setDeparture_date(LocalDate.of(year,month,day));
            toAlter.setDeparture_time(LocalTime.of(hour,minute));

            System.out.println();
            System.out.println("enter arrival");
            System.out.print("\nenter arrival year\t");
            int ayear = sc.nextInt();
            System.out.print("\nenter arrival month\t");
            int amonth = sc.nextInt();
            System.out.print("\nenter arrival day\t");
            int aday = sc.nextInt();

            System.out.print("\nenter arrival hour\t");
            int ahour = sc.nextInt();
            System.out.print("\nenter arrival minute\t");
            int aminute = sc.nextInt();
            toAlter.setArrival_date(LocalDate.of(ayear,amonth,aday));
            toAlter.setArrival_time(LocalTime.of(ahour,aminute));

            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("update flight set flight_no = ?, name = ?, capacity = ?, from_city_id = ?, to_city_id = ?, price = ?, departure_date = ?, arrival_date = ?, departure_time = ?, arrival_time = ?,seats = ? where id = ?");
            ps.setObject(1,toAlter.getFlight_no());
            ps.setObject(2,toAlter.getName());
            ps.setObject(3,toAlter.getCapacity());
            ps.setObject(4,toAlter.getFrom_city_id());
            ps.setObject(5,toAlter.getTo_city_id());
            ps.setObject(6,toAlter.getPrice());
            ps.setObject(7, toAlter.getDeparture_date());
            ps.setObject(8,toAlter.getArrival_date());
            ps.setObject(9, toAlter.getDeparture_time());
            ps.setObject(10,toAlter.getArrival_time());
            ps.setObject(11,getSeatArray());
            ps.setInt(12,flight_to_alter);

            return ps.executeUpdate();
        };

        Future<Integer> result = ThreadManager.executeThread(Integer.valueOf(1),alterFligthThread);
        int status = 0;
        try{
            status = result.get();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        if(status == 1){
            System.out.println("flight is altered");
        }else {
            System.out.println("flight is not altered");
        }
    }

    private void addflight() {

        Flights newFlightData = new Flights();
        Scanner sc = new Scanner(System.in);

        Callable<Integer> addFlightThread = () -> {

            System.out.println("enter flight no");
            newFlightData.setFlight_no(sc.next());

            System.out.println("enter name");
            newFlightData.setName(sc.next());

            System.out.println("enter capacity");
            newFlightData.setCapacity(sc.nextInt());

            System.out.println("enter from city");
            for(Cities c : getCityList()){
                System.out.println(c.toString());
            }
            newFlightData.setFrom_city_id(sc.nextInt());

            System.out.println("enter to city");
            for(Cities c : getCityList()){
                System.out.println(c.toString());
            }
            newFlightData.setTo_city_id(sc.nextInt());

            System.out.println("enter price");
            newFlightData.setPrice(sc.nextInt());

            System.out.println();
            System.out.println("enter departure");
            System.out.print("\nenter departure year\t");
            int year = sc.nextInt();
            System.out.print("\nenter departure month\t");
            int month = sc.nextInt();
            System.out.print("\nenter departure day\t");
            int day = sc.nextInt();

            System.out.print("\nenter departure hour\t");
            int hour = sc.nextInt();
            System.out.print("\nenter departure minute\t");
            int minute = sc.nextInt();
            newFlightData.setDeparture_date(LocalDate.of(year,month,day));
            newFlightData.setDeparture_time(LocalTime.of(hour,minute));

            System.out.println();
            System.out.println("enter arrival");
            System.out.print("\nenter arrival year\t");
            int ayear = sc.nextInt();
            System.out.print("\nenter arrival month\t");
            int amonth = sc.nextInt();
            System.out.print("\nenter arrival day\t");
            int aday = sc.nextInt();

            System.out.print("\nenter arrival hour\t");
            int ahour = sc.nextInt();
            System.out.print("\nenter arrival minute\t");
            int aminute = sc.nextInt();
            newFlightData.setArrival_date(LocalDate.of(ayear,amonth,aday));
            newFlightData.setArrival_time(LocalTime.of(ahour,aminute));

            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("insert into flight (flight_no, name, capacity, from_city_id, to_city_id, price, departure_date, arrival_date, departure_time, arrival_time,seats) values (?,?,?,?,?,?,?,?,?,?,?)");
            ps.setObject(1,newFlightData.getFlight_no());
            ps.setObject(2,newFlightData.getName());
            ps.setObject(3,newFlightData.getCapacity());
            ps.setObject(4,newFlightData.getFrom_city_id());
            ps.setObject(5,newFlightData.getTo_city_id());
            ps.setObject(6,newFlightData.getPrice());
            ps.setObject(7,newFlightData.getDeparture_date());
            ps.setObject(8,newFlightData.getArrival_date());
            ps.setObject(9,newFlightData.getDeparture_time());
            ps.setObject(10,newFlightData.getArrival_time());
            ps.setObject(11,getSeatArray());

            return ps.executeUpdate();
        };

        Future<Integer> result = ThreadManager.executeThread(Integer.valueOf(1),addFlightThread);
        int status = 0;
        try{
            status = result.get();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        if(status == 1){
            System.out.println("flight is added");
        }else {
            System.out.println("flight is not added");
        }
    }

    private List<Flights> getFlights(){
        Callable<List<Flights>> flightList = () -> {
            Connection con = getConnection();
            List<Flights> flightData = new LinkedList<>();
            ResultSet rs = con.createStatement().executeQuery("select * from flight");

            while(rs.next()){
                String departuredate = rs.getString("departure_date");
                String arrivaldate = rs.getString("arrival_date");

                String departuretime = rs.getString("departure_time");
                String arrivaltime = rs.getString("arrival_time");

                Flights f = new Flights(rs.getLong("id"),rs.getString("flight_no"),rs.getString("name"),
                        rs.getInt("capacity"),rs.getString("seats"),rs.getInt("from_city_id"),rs.getInt("to_city_id"),
                        rs.getInt("price"));
                f.setDeparture_date(
                        LocalDate.parse(departuredate, ofPattern("yyyy-MM-dd"))
                );
                f.setDeparture_time(LocalTime.parse(departuretime, ofPattern("HH:mm:ss")));

                f.setArrival_date(
                        LocalDate.parse(arrivaldate, ofPattern("yyyy-MM-dd"))
                );
                f.setArrival_time(LocalTime.parse(arrivaltime, ofPattern("HH:mm:ss")));
                flightData.add(f);

            }

            return flightData;
        };

        List<Flights> flightDataRef = null;
        Future<List<Flights>> result = (Future<List<Flights>>) ThreadManager.executeThread(flightDataRef,flightList);
        try{
            if(result.isDone()){
                return result.get();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private String getSeatArray(){
        StringBuffer sb = new StringBuffer();
        for(int i = 1; i <=75; i++){
            sb.append(i).append(",");
        }

        for(int i = 1;i<25;i++){
            sb.append(i).append("b").append(",");
        }
        sb.append("25b");
        return sb.toString();
    }

    private List<Cities> getCityList() {
        Callable<List<Cities>> citylist = () -> {
            Connection con = getConnection();
            ResultSet resultSet = con.createStatement().executeQuery("select * from cities;");
            List<Cities> citydata = new LinkedList<>();

            while(resultSet.next()){
                citydata.add(new Cities(resultSet.getLong("id"),resultSet.getString("name"),resultSet.getString("country")));
            }

            return  citydata;
        };

        List<Cities> datatyperef = null;
        Future<List<Cities>> result =
                (Future<List<Cities>>)
                        ThreadManager.executeThread(datatyperef, citylist);
        try{
            if(result.isDone()){
                datatyperef = result.get();
            }
            return datatyperef;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private Connection getConnection(){
        Connection con = null;
        try{
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/databaseName","username","password");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return con;
    }
}
