import java.io.*;
import java.util.*;

class Room {
    private int roomNumber;
    private String category;
    private boolean isAvailable;

    public Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isAvailable = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void bookRoom() {
        isAvailable = false;
    }

    public void freeRoom() {
        isAvailable = true;
    }
}

class Reservation {
    private String customerName;
    private int roomNumber;
    private String category;

    public Reservation(String customerName, int roomNumber, String category) {
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.category = category;
    }

    public String toFileString() {
        return customerName + "," + roomNumber + "," + category;
    }

    public static Reservation fromFileString(String line) {
        String[] data = line.split(",");
        return new Reservation(data[0], Integer.parseInt(data[1]), data[2]);
    }

    public String toString() {
        return "Customer: " + customerName +
                " | Room: " + roomNumber +
                " | Category: " + category;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCustomerName() {
        return customerName;
    }
}

public class HotelReservationSystem  {

    private static ArrayList<Room> rooms = new ArrayList<>();
    private static ArrayList<Reservation> reservations = new ArrayList<>();
    private static final String FILE_NAME = "bookings.txt";

    public static void main(String[] args) {
        initializeRooms();
        loadReservationsFromFile();

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View All Bookings");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewAvailableRooms();
                case 2 -> bookRoom(sc);
                case 3 -> cancelReservation(sc);
                case 4 -> viewBookings();
                case 5 -> System.out.println("Exiting system...");
                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 5);

        sc.close();
    }

    private static void initializeRooms() {
        rooms.add(new Room(101, "Standard"));
        rooms.add(new Room(102, "Standard"));
        rooms.add(new Room(201, "Deluxe"));
        rooms.add(new Room(202, "Deluxe"));
        rooms.add(new Room(301, "Suite"));
    }

    private static void viewAvailableRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.println("Room " + room.getRoomNumber() +
                        " - " + room.getCategory());
            }
        }
    }

    private static void bookRoom(Scanner sc) {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        viewAvailableRooms();
        System.out.print("Enter room number to book: ");
        int roomNo = sc.nextInt();

        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNo && room.isAvailable()) {

                // Payment simulation
                System.out.println("Processing payment...");
                System.out.println("Payment successful!");

                room.bookRoom();
                Reservation reservation =
                        new Reservation(name, roomNo, room.getCategory());
                reservations.add(reservation);

                saveReservationToFile(reservation);

                System.out.println("Room booked successfully!");
                return;
            }
        }

        System.out.println("Room not available.");
    }

    private static void cancelReservation(Scanner sc) {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        Iterator<Reservation> iterator = reservations.iterator();

        while (iterator.hasNext()) {
            Reservation res = iterator.next();

            if (res.getCustomerName().equalsIgnoreCase(name)) {
                iterator.remove();

                for (Room room : rooms) {
                    if (room.getRoomNumber() == res.getRoomNumber()) {
                        room.freeRoom();
                    }
                }

                rewriteFile();
                System.out.println("Reservation cancelled.");
                return;
            }
        }

        System.out.println("Reservation not found.");
    }

    private static void viewBookings() {
        System.out.println("\nAll Reservations:");
        for (Reservation res : reservations) {
            System.out.println(res);
        }
    }

    private static void saveReservationToFile(Reservation res) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FILE_NAME, true))) {
            bw.write(res.toFileString());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving reservation.");
        }
    }

    private static void loadReservationsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new FileReader(FILE_NAME))) {

            String line;
            while ((line = br.readLine()) != null) {
                Reservation res = Reservation.fromFileString(line);
                reservations.add(res);

                for (Room room : rooms) {
                    if (room.getRoomNumber() == res.getRoomNumber()) {
                        room.bookRoom();
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading reservations.");
        }
    }

    private static void rewriteFile() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FILE_NAME))) {

            for (Reservation res : reservations) {
                bw.write(res.toFileString());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error updating file.");
        }
    }
}