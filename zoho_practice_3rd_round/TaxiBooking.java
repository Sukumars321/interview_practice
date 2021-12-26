package zoho_practice_3rd_round;
import java.util.*;

/**
 * TaxiBooking
 */

// Design a Call taxi booking application
// -There are n number of taxi’s. For simplicity, assume 4. But it should work for any number of taxi’s.
// -The are 6 points(A,B,C,D,E,F)
// -All the points are in a straight line, and each point is 15kms away from the adjacent points.
// -It takes 60 mins to travel from one point to another
// -Each taxi charges Rs.100 minimum for the first 5 kilometers and Rs.10 for the subsequent kilometers.
// -For simplicity, time can be entered as absolute time. Eg: 9hrs, 15hrs etc.
// -All taxi’s are initially stationed at A.
// -When a customer books a Taxi, a free taxi at that point is allocated
// -If no free taxi is available at that point, a free taxi at the nearest point is allocated.
// -If two taxi’s are free at the same point, one with lower earning is allocated
// -Note that the taxi only charges the customer from the pickup point to the drop point. Not the distance it travels from an adjacent point to pickup the customer.
// -If no taxi is free at that time, booking is rejected

// 1)    Call taxi booking 
// Input 1:
// Customer ID: 1
// Pickup Point: A
// Drop Point: B
// Pickup Time: 9

// Output 1:
// Taxi can be allotted.
// Taxi-1 is allotted

// Input 2:
// Customer ID: 2
// Pickup Point: B
// Drop Point: D
// Pickup Time: 9

// Output 2:
// Taxi can be allotted.
// Taxi-2 is allotted 

// Input 3:
// Customer ID: 3
// Pickup Point: B
// Drop Point: C
// Pickup Time: 12

// Output 1:
// Taxi can be allotted.
// Taxi-1 is allotted 

// Taxi No:    Total Earnings:
// BookingID    CustomerID    From    To    PickupTime    DropTime    Amount
   
// Output:
// Taxi-1    Total Earnings: Rs. 400

// 1     1     A    B    9    10    200
// 3    3    B    C    12    13    200

// Taxi-2 Total Earnings: Rs. 350
// 2    2    B    D    9    11    350 

//SOURCE  https://www.geeksforgeeks.org/zoho-interview-set-3-campus/

public class TaxiBooking {
    static int minFare = 100;
    static int fareForKM = 10;

    public static void main(String[] args) {
        System.out.println("\tTaxi Application\t");
        
        int tCount = 4;
        Scanner sc = new Scanner(System.in);
        HashMap<Integer,Taxi> taxiList = getTaxiList(tCount);
        HashMap<Character,List<Integer>> stationedList = getStationedList(tCount);
        HashMap<Integer,Booking> BookingList = new  HashMap<Integer,Booking>();
        List<Character> stations = new ArrayList<Character>();
        stations.add('A');
        stations.add('B');
        stations.add('C');
        stations.add('D');
        stations.add('E');
        stations.add('F');

        while (true) {
            System.out.println("Enter your Preference");
            System.out.println("1. Book taxi");
            System.out.println("2. Show all Booking for taxi");
            System.out.println("3. Exit");
            int choice = sc.nextInt();sc.nextLine();
            switch (choice) {
                case 1:
                    bookTaxi(taxiList,BookingList,stationedList,stations,sc);
                    break;
                case 2:
                    printTaxiDetails(taxiList,BookingList,tCount);
                    break;
                case 3:
                    System.out.println("System exited");
                    sc.close();
                    return;    
            }
        }
    }

    public static HashMap<Integer,Taxi> getTaxiList(int tCount) {
        HashMap<Integer,Taxi> taxiList =new HashMap<Integer,Taxi>();
        for (int i = 1; i <= tCount; i++) {
            Taxi t = new Taxi(i,0, 0);
            taxiList.put(i,t);
        }

        return taxiList;
    }

    public static HashMap<Character,List<Integer>> getStationedList(int tCount) {
        HashMap<Character,List<Integer>> stationedList = new HashMap<Character,List<Integer>>();
        List<Integer> ls = new ArrayList<Integer>();
        for (int i = 1; i <= tCount; i++) {
            ls.add(i);
        }
        stationedList.put('A', ls);
        stationedList.put('B', new ArrayList<Integer>());
        stationedList.put('C', new ArrayList<Integer>());
        stationedList.put('D', new ArrayList<Integer>());
        stationedList.put('E', new ArrayList<Integer>());
        stationedList.put('F', new ArrayList<Integer>());
        return stationedList;
    }

    public static void printTaxiDetails(HashMap<Integer,Taxi> taxiList, HashMap<Integer,Booking> BookingList, int count) {
        System.out.println("~~~~~~~~~~~~Printing ALL Taxi details~~~~~~~~~~");
        for (int i = 1; i <= count; i++) {
            Taxi t = taxiList.get(i);
            System.out.println("Taxi No:" + i + "Total Earnings Rs. "+ t.getEarned());
            List<Integer> bList = t.getBooksList();
            for (Integer integer : bList) {
                Booking bk = BookingList.get(integer);
                System.out.println(bk.toString());
            }
            System.out.println();
        }
    }

    public static void bookTaxi(HashMap<Integer,Taxi> taxiList,HashMap<Integer,Booking> BookingList,HashMap<Character,List<Integer>> stationedList,List<Character> stations, Scanner sc){
        System.out.print("Customer ID : ");
        int customerId = sc.nextInt();sc.nextLine();
        System.out.print("PickUp point : ");
        char pickUp = sc.nextLine().charAt(0);
        System.out.print("Drop point : ");
        char drop = sc.nextLine().charAt(0);
        System.out.print("Pickup time: ");
        int pickUpTime = sc.nextInt();sc.nextLine();

        Taxi taxi = findTaxiForBooking(pickUp,pickUpTime,taxiList,stationedList,stations);

        if(taxi != null){
            System.out.println("Taxi can be allocated");
            Booking bk = newBooking(BookingList, customerId, pickUp, drop, pickUpTime);
            taxi.setAvailableAt(bk.getDrop());
            List<Integer> newList = taxi.getBooksList();
            newList.add(bk.getId());
            taxi.setBooksList(newList);
            taxi.setEarned(bk.getFare());
            List<Integer> stationedTaxiList = stationedList.get(drop);
            stationedTaxiList.add(taxi.getId());
            stationedList.put(drop, stationedTaxiList);
            System.out.println("Taxi no "+ taxi.getId()+" is allocated");
            System.out.println(taxi.toString());
        }else{
            System.out.println("Sorry !! No Taxi is available for booking");
        }

    }

    public static Booking newBooking(HashMap<Integer,Booking> BookingList,  int customerId, char pickUp,char drop, int pickUpTime){
        int dropTime = pickUpTime + Math.abs(drop-pickUp);
        int distToCharge = Math.abs(drop-pickUp)*15-5;
        int fare = minFare + (distToCharge*fareForKM);
        Booking bk = new Booking(BookingList.size()+1, customerId, pickUp,drop, pickUpTime, dropTime, fare);
        System.out.println(bk.toString());
        BookingList.put(bk.getId(),bk);
        return bk;
    }

    public static Taxi findTaxiForBooking(char pickUp,int pickUpTime,HashMap<Integer,Taxi> taxiList,HashMap<Character,List<Integer>> stationedList,List<Character> stations){
        List<Integer> atStationList = stationedList.get(pickUp);
        Taxi t = getTaxi(atStationList, taxiList, pickUpTime);
        if(t!= null){
            updateStationedList(atStationList,stationedList,pickUp,t.getId());
            return t;
        }

        int index = stations.indexOf(pickUp);
        int j = index-1;
        index = index+1;
        for (; j >= 0 && index < stations.size(); index++,j--) {
            char left = stations.get(j);
            char right = stations.get(index);
            List<Integer> stationedListLeft =  stationedList.get(left);
            List<Integer> stationedListRight =  stationedList.get(right);
            Taxi tl = getTaxi(stationedListLeft, taxiList, pickUpTime);
            Taxi tr = getTaxi(stationedListRight, taxiList, pickUpTime);

            if(tl != null && tr != null){
                if(tl.getEarned()< tr.getEarned()){
                    updateStationedList(stationedListLeft,stationedList,left,tl.getId());
                    return tl;
                }
                else{
                    updateStationedList(stationedListRight,stationedList,right,tr.getId());
                    return tr;
                }
            }else if(tl != null){
                updateStationedList(stationedListLeft,stationedList,left,tl.getId());
                return tl;
            }else if(tr != null){
                updateStationedList(stationedListRight,stationedList,right,tr.getId());
                return tr;
            }
        }
        
        while (index < stations.size()) {
            char right = stations.get(index);
            List<Integer> stationedListRight =  stationedList.get(right);
            Taxi tr = getTaxi(stationedListRight, taxiList, pickUpTime);
            if(tr != null){
                updateStationedList(stationedListRight,stationedList,right,tr.getId());
                return tr;
            }
            index++;
        }

        while (j >= 0) {
            char left = stations.get(j);
            List<Integer> stationedListLeft =  stationedList.get(left);
            Taxi tl = getTaxi(stationedListLeft, taxiList, pickUpTime);
            if(tl != null){
                updateStationedList(stationedListLeft,stationedList,left,tl.getId());
                return tl;
            }
            j--;
        }

        return null;
    }



  public static void updateStationedList(List<Integer> udpateList,HashMap<Character,List<Integer>> stationedList,char stationChar,int taxiId){
    int ind = udpateList.indexOf(taxiId);
    udpateList.remove(ind);
    stationedList.put(stationChar, udpateList);
  }

    public static Taxi getTaxi(List<Integer> atStationList,HashMap<Integer,Taxi> taxiList,int pickUpTime){
        Taxi taxi = null; 
        for (Integer id : atStationList) {
            Taxi t = taxiList.get(id);
            if (t.getAvailableAt() <= pickUpTime && taxi != null && t.getEarned() < taxi.getEarned()) {
                 taxi = t;
            }
            else if(t.getAvailableAt() <= pickUpTime){
                taxi = t;
            }
        }
        return taxi;
    }
}

class Taxi {
    private int id;
    private int availableAt;
    private int earned;
    private List<Integer> booksList;

    public List<Integer> getBooksList() {
        return booksList;
    }

    public void setBooksList(List<Integer> booksList) {
        this.booksList = booksList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(int availableAt) {
        this.availableAt = availableAt;
    }

    public int getEarned() {
        return earned;
    }

    public void setEarned(int earned) {
        this.earned += earned;
    }

    public Taxi(int id,int availableAt, int earned) {
        this.id = id;
        this.availableAt = availableAt;
        this.earned = earned;
        this.booksList = new ArrayList<Integer>();
    }

    @Override
    public String toString() {
        return "Taxi [availableAt=" + availableAt + ", booksList=" + booksList + ", earned=" + earned + ", id=" + id
                + "]";
    }
    
}

class Booking {
    private int id;
    private int customerId;
    private char from;
    private char to;
    private int pickup;
    private int drop;
    private int fare;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public char getFrom() {
        return from;
    }

    public void setFrom(char from) {
        this.from = from;
    }

    public char getTo() {
        return to;
    }

    public void setTo(char to) {
        this.to = to;
    }

    public int getPickup() {
        return pickup;
    }

    public void setPickup(int pickup) {
        this.pickup = pickup;
    }

    public int getDrop() {
        return drop;
    }

    public void setDrop(int drop) {
        this.drop = drop;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }
    
    public Booking(int id, int customerId, char from, char to, int pickup, int drop, int fare) {
        this.id = id;
        this.customerId = customerId;
        this.from = from;
        this.to = to;
        this.pickup = pickup;
        this.drop = drop;
        this.fare = fare;
    }

    @Override
    public String toString() {
        return "Booking [customerId=" + customerId + ", drop=" + drop + ", fare=" + fare + ", from=" + from + ", id="
                + id + ", pickup=" + pickup + ", to=" + to + "]";
    }
}
