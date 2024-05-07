import java.util.*;
import java.sql.*;
import java.io.*;

public class project1{

    public static void main(String[] args) throws Exception{

        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "Nathan_P5275");

            Scanner scanner = new Scanner(System.in);
            int option;
            String productName;
            int productId;
            int quantity;
            double price_per_unit;
            char ch;

            do {
                System.out.println();
                System.out.println("Inventory Management System");
                System.out.println("1. Add Product");
                System.out.println("2. Update Product Quantity");
                System.out.println("3. Delete Product");
                System.out.println("4. Generate Report");
                System.out.println("5. Generate CSV and exit");
                System.out.print("Enter your choice: ");
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        System.out.print("Enter Product ID: ");
                        productId = scanner.nextInt();
                        scanner.nextLine();

                        boolean exists=productsExists(connection,productId);

                        if(exists){
                            System.out.println("Product already exists");
                            System.out.println("Do you want to update the given id?(y/n)");
                            ch=scanner.next().charAt(0);
                            if(ch=='y'){
                                System.out.println("Enter the quantity");
                                quantity=scanner.nextInt();
                                updateProduct(connection,productId,quantity);
                            }
                            else {
                                continue;
                            }
                        }
                        else {
                            System.out.print("Enter Product Name: ");
                            productName = scanner.nextLine();
                            System.out.print("Enter Quantity: ");
                            quantity = scanner.nextInt();
                            System.out.print("Enter Price: ");
                            price_per_unit = scanner.nextDouble();
                            scanner.nextLine();

                            addProduct(connection, productId, productName, quantity, price_per_unit);
                        }
                        break;


                    case 2:
                        System.out.print("Enter Product ID: ");
                        productId = scanner.nextInt();
                        scanner.nextLine();

                        boolean exists1=productsExists(connection,productId);
                        if(!exists1){
                            System.out.println("Product Does not exists");
                            System.out.println("Do you want to add the given id?(y/n)");
                            ch=scanner.next().charAt(0);
                            if(ch=='y'){
                                System.out.print("Enter Product Name: ");
                                scanner.nextLine();
                                productName = scanner.nextLine();

                                System.out.print("Enter Quantity: ");
                                quantity = scanner.nextInt();
                                System.out.print("Enter Price: ");
                                price_per_unit = scanner.nextDouble();
                                scanner.nextLine();

                                addProduct(connection,productId,productName,quantity,price_per_unit);
                            }
                            else {
                                continue;
                            }
                        }
                        else {
                            System.out.print("Enter Quantity: ");
                            quantity = scanner.nextInt();
                            scanner.nextLine(); 

                            updateProduct(connection, productId, quantity);
                        }
                        break;


                    case 3:
                        System.out.print("Enter Product ID: ");
                        productId = scanner.nextInt();
                        scanner.nextLine(); 

                        boolean exists2=productsExists(connection,productId);

                        if(!exists2){
                            System.out.println("Product does not exists");
                        }
                        else {
                            deleteProduct(connection, productId);
                        }
                        break;


                    case 4:
                        generateReport(connection);
                        break;
                    case 5:
                        System.out.println("CSV file generated and exited");
                        generateCsv(connection);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }

            } while (option != 5);

            connection.close();
            scanner.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean productsExists(Connection connection,int id) throws SQLException{
        PreparedStatement checkSmt=connection.prepareStatement("SELECT * FROM apple_products WHERE id = ?");
        checkSmt.setInt(1,id);
        ResultSet resultSet = checkSmt.executeQuery();

        if (resultSet.next()) {
            resultSet.close();
            checkSmt.close();
            return true;
        }
        resultSet.close();
        checkSmt.close();
        return false;
    }
    public static void addProduct(Connection connection, int id, String name, int quantity, double price_per_unit) throws SQLException {

        PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO apple_products (id, name, quantity, price_per_unit) VALUES (?, ?, ?, ?)");
        insertStmt.setInt(1, id);
        insertStmt.setString(2, name);
        insertStmt.setInt(3, quantity);
        insertStmt.setDouble(4, price_per_unit);
        insertStmt.executeUpdate();
        System.out.println("New product added successfully.");
        insertStmt.close();
    }

    public static void updateProduct(Connection connection, int id, int quantity) throws SQLException {

        if(quantity==0){
            deleteProduct(connection,id);
        }
        else {
            PreparedStatement updateStmt = connection.prepareStatement("UPDATE apple_products SET quantity = ? WHERE id = ?");
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, id);
            updateStmt.executeUpdate();
            System.out.println("Product quantity updated successfully.");
            updateStmt.close();
        }
    }

    public static void deleteProduct(Connection connection, int id) throws SQLException {

        PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM apple_products WHERE id = ?");
        deleteStmt.setInt(1, id);
        deleteStmt.executeUpdate();
        System.out.println("Product deleted successfully.");
        deleteStmt.close();
    }

    public static void generateReport(Connection connection) throws SQLException {

        PreparedStatement statement= connection.prepareStatement("SELECT id, name, price_per_unit , quantity FROM apple_products");
        ResultSet resultSet = statement.executeQuery();

        System.out.println("Product ID\t\t\t\t\tProduct Name\t\t\t\t Price_Per_Unit\t\t\t\t  Quantity");
        System.out.println("-----------------------------------------------------------------------------------");
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int quantity = resultSet.getInt("quantity");
            double price= resultSet.getDouble("price_per_unit");
            System.out.println(id + "\t\t\t\t\t" + name + "  \t\t\t\t\t" + price +"  \t\t\t\t\t" + quantity);
        }
        resultSet.close();
        statement.close();
    }

    public static void generateCsv(Connection connection) throws Exception{

        PreparedStatement statement = connection.prepareStatement("SELECT * from apple_products");
        ResultSet resultSet= statement.executeQuery();
        FileWriter fw=new FileWriter("C:\\Java\\Inventory.csv");
        BufferedWriter bw=new BufferedWriter(fw);
        bw.write("Product ID,Product Name,Quantity,Price/Unit\n");
        while(resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int quantity = resultSet.getInt("quantity");
            double price=resultSet.getDouble("price_per_unit");
            String str=id+","+name+","+quantity+","+price;
            bw.write(str+"\n");
            bw.flush();
        }
        bw.close();
        fw.close();
        resultSet.close();
        statement.close();

    }
}
