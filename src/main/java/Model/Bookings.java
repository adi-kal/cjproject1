package Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Bookings {

    private Long id;
    private Integer member_id;//
    private Integer traveler_id;//
    private Integer flight_id;//
    private String seatno;//
    private String ticketno;//
    private LocalDate date;//
    private LocalTime time;//
    private Integer price;//
    private Integer from_city_id; //--------
    private Integer to_city_id; //

    public Bookings(Integer member_id, Integer traveler_id, Integer flight_id, String ticketno, LocalDate date,String seatno, LocalTime time, Integer price, Integer from_city_id, Integer to_city_id, Integer passenger) {
        this.member_id = member_id;
        this.traveler_id = traveler_id;
        this.flight_id = flight_id;
        this.ticketno = ticketno;
        this.date = date;
        this.seatno = seatno;
        this.time = time;
        this.price = price;
        this.from_city_id = from_city_id;
        this.to_city_id = to_city_id;
    }

    public Bookings(Long id, Integer member_id, Integer traveler_id, Integer flight_id, String ticketno, String seatno, Integer price, Integer from_city_id, Integer to_city_id) {
        this.id = id;
        this.member_id = member_id;
        this.traveler_id = traveler_id;
        this.flight_id = flight_id;
        this.ticketno = ticketno;
        this.seatno = seatno;
        this.price = price;
        this.from_city_id = from_city_id;
        this.to_city_id = to_city_id;
    }

    @Override
    public String toString() {
        return "Bookings{" +
                "id::" + id + "\n" +
                "member_id=" + member_id +"\n" +
                "traveler_id=" + traveler_id +"\n" +
                "flight_id=" + flight_id +"\n" +
                "date=" + date+"\n" +
                "time=" + time+"\n" +
                "price=" + price +"\n" +
                "from=" + from_city_id +"\n" +
                "to=" + to_city_id + "\n" +
                "ticket no=" + ticketno + "\n" +
                "}";
    }

    public Long getId() {
        return id;
    }

    public Integer getMember_id() {
        return member_id;
    }

    public void setMember_id(Integer member_id) {
        this.member_id = member_id;
    }

    public Integer getTraveler_id() {
        return traveler_id;
    }

    public void setTraveler_id(Integer traveler_id) {
        this.traveler_id = traveler_id;
    }

    public Integer getFlight_id() {
        return flight_id;
    }

    public void setFlight_id(Integer flight_id) {
        this.flight_id = flight_id;
    }

    public String getTicketno() {
        return ticketno;
    }

    public void setTicketno(String ticketno) {
        this.ticketno = ticketno;
    }


    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
    public String getSeatno() {
        return seatno;
    }

    public void setSeatno(String seatno) {
        this.seatno = seatno;
    }

}