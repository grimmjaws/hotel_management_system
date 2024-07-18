import java.sql.*;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "root";

    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. view Reservation");
                System.out.println("3. Get room number");
                System.out.println("4. Update room number");
                System.out.println("5. Delete reservation");
                System.out.println("6. exit");
                System.out.println("choose an option : ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> reserveRoom(connection, scanner);
                    case 2 -> viewReservation(connection);
                    case 3 -> getRoomNumber(connection, scanner);
                    case 4 -> updateReservation(connection, scanner);
                    case 5 -> deleteReservation(connection, scanner);
                    case 6 -> {
                        exit();
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }

            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql ="INSERT INTO reservations(guest_name,room_no,contact_no)" +
                    "VALUES ('" + guestName + "',"+ roomNumber +",'"+ contactNumber +"')";

            try(Statement statement= connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows < 0){
                    System.out.println("Reservation successfully ");
                }else{
                    System.out.println("Reservation failed");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection) throws SQLException{
        String sql = "SELECT reservation_id , guest_name , room_no , contact_no , reservation_date FROM reservations;";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("current reservation: ");
            System.out.println("+------------------+----------------+---------------+--------------------+------------------------+");
            System.out.println("| Reservation ID   | Guest          | Room Number   | Contact Number     | Reservation Date       |");
            System.out.println("+------------------+----------------+---------------+--------------------+------------------------+");

            while(resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_no");
                String contactNumber = resultSet.getString("contact_no");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                //format and display the reservation date in s table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId , guestName , roomNumber , contactNumber , reservationDate);
            }

            System.out.println("+----------------+--------------+---------------+-----------------+------------------------+");

        }

    }


    private static void getRoomNumber(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name: ");
            String guestName =scanner.next();

            String sql = "SELECT room_no FROM reservations" +
                    "WHERE reservation_id = "+ reservationId +
                    "AND guest_name= '" + guestName + "'";

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber =resultSet.getInt("room_no");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            "and Guest " + guestName + "is: " + roomNumber);
                }else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }

            }


        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if(reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "',"+ "room_no = " + newRoomNumber + ", " +
                    "contact_no = '" + newContactNumber + ", " + "WHERE reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation updated successfully!");
                }else {
                    System.out.println("reservation update failed.");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter reservation ID to delete: ");
            int reservationID = scanner.nextInt();

            if(reservationExists(connection, reservationID)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " +reservationID;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation deleted successfully!");
                }else{
                    System.out.println("Reservation deletion failed.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection,int reservationID){
        try{
            String sql = " SELECT reservations WHERE reservation_ID = " + reservationID;

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                return !resultSet.next();
            }
        }catch (SQLException e ){
            e.printStackTrace();
            return true;
        }
    }


    public  static void exit() throws InterruptedException{
        System.out.println("Existing System");
        int i = 5;
        while(i!=0){
            System.out.println(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using hotel management system!!");
    }
}