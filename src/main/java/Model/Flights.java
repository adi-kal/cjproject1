package Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Flights {
    private Long id;
    private String flight_no;
    private String name;
    private int capacity;
    private String seats;
    private Integer from_city_id;
    private Integer to_city_id;
    private Integer price;
    private LocalDate departure_date;
    private LocalTime departure_time;
    private LocalDate arrival_date;
    private LocalTime arrival_time;

    public Flights(){}

    public Flights(Long id, String flight_no, String name, int capacity, String seats, Integer from_city_id, Integer to_city_id, Integer price) {
        this.id = id;
        this.flight_no = flight_no;
        this.name = name;
        this.capacity = capacity;
        this.seats = seats;
        this.from_city_id = from_city_id;
        this.to_city_id = to_city_id;
        this.price = price;
    }


    public String getSeats() {
        return seats;
    }

    public Long getId() {
        return id;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getFlight_no() {
        return flight_no;
    }

    public void setFlight_no(String flight_no) {
        this.flight_no = flight_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Integer getFrom_city_id() {
        return from_city_id;
    }

    public void setFrom_city_id(Integer from_city_id) {
        this.from_city_id = from_city_id;
    }

    public Integer getTo_city_id() {
        return to_city_id;
    }

    public void setTo_city_id(Integer to_city_id) {
        this.to_city_id = to_city_id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDate getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(LocalDate departure_date) {
        this.departure_date = departure_date;
    }

    public LocalTime getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(LocalTime departure_time) {
        this.departure_time = departure_time;
    }

    public LocalDate getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(LocalDate arrival_date) {
        this.arrival_date = arrival_date;
    }

    public LocalTime getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(LocalTime arrival_time) {
        this.arrival_time = arrival_time;
    }

    @Override
    public String toString() {
        return "Flights{" +
                "id::" + id + "\n" +
                "flight_no=" + flight_no + "\n" +
                "name=" + name +"\n" +
                "capacity=" + capacity +"\n" +
                "from_city_id=" + from_city_id +"\n" +
                "to_city_id=" + to_city_id +"\n" +
                "price=" + price +"\n" +
                "departure_date=" + departure_date +"\n" +
                "arrival_date=" + arrival_date +"\n" +
                "departure_time=" + departure_time+"\n" +
                "arrival_time=" + arrival_time+"\n" +
                "}\n";
    }
}
