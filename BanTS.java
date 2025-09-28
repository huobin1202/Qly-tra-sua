import java.util.Arrays;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

interface Inhapxuat {

    void nhap();

    void xuat();
}

interface IQuanLy extends Inhapxuat {

    void them(); // Thêm đối tượng vào danh sách

    void sua(); // Sửa đối tượng

    void xoa(); // Xóa đối tượng

    void timkiem(); // Tìm kiếm đối tượng
}

abstract class Drink {
    protected String tenDrink;
    protected int gia;

    public Drink() {
        tenDrink = "";
        gia = 0;
    }

    public Drink(String tenDrink, int gia) {
        this.tenDrink = tenDrink;
        this.gia = gia;
    }

    public int getGia() {
        return gia;
    }

    public String getTen() {
        return tenDrink;
    }

    public abstract int getCost();

    public abstract void nhap();

    public abstract void xuat();
}

class Fruittea extends Drink implements Inhapxuat {
    private String traicay;

    public Fruittea() {
        super();
        traicay = "";
    }

    public Fruittea(String tenDrink, int gia, String traicay) {
        super(tenDrink, gia);
        this.traicay = traicay;
    }

    public String getTraicay() {
        return traicay;
    }

    public int getCost() {
        return gia * 10 / 100;
    }

    @Override
    public void nhap() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Nhap ten tra trai cay, gia, loai trai cay: ");
        tenDrink = rd.nextLine();
        gia = rd.nextInt();
        traicay = rd.nextLine();
    }

    @Override
    public void xuat() {
        System.out.println("Tên tra trai cay: " + tenDrink + ", Giá: " + gia + ", loai trái cây: " + traicay);
    }

}

class Milktea extends Drink implements Inhapxuat {
    private String topping;

    public Milktea() {
        super();
        topping = "";
    }

    public Milktea(String tenDrink, int gia, String topping) {
        super(tenDrink, gia);
        this.topping = topping;
    }

    public String getTopping() {
        return topping;
    }

    public int getCost() {
        return gia * 20 / 100;
    }


    @Override
    public void nhap() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Nhap ten tra sua, gia, loai topping: ");
        tenDrink = rd.nextLine();
        gia = rd.nextInt();
        topping = rd.nextLine();
    }

    @Override
    public void xuat() {
        System.out.println("Tên trà sua: " + tenDrink + ", Giá: " + gia + ", Topping: " + topping);
    }
}

class DSDrink implements IQuanLy {
    private Drink[] dsdrink;
    private int n;

    public DSDrink() {
        n = 0;
        dsdrink = new Drink[0];
    }

    public DSDrink(int n, Drink[] dsDrink) {
        this.n = n;
        this.dsdrink = dsDrink;
    }

    public void nhap() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Nhập số lượng đồ uống:");
        n = rd.nextInt();
        dsdrink = new Drink[n];
        for (int i = 0; i < n; i++) {
            System.out.println("Nhap loai do uong: ");
            System.out.println(" 1.Tra sua    2.Tra trai cay");
            int x = rd.nextInt();

            while (x != 1 && x != 2) {
                System.out.println("Khong hop le, nhap lai!");
                x = rd.nextInt();
            }
            if (x == 1) {
                dsdrink[i] = new Milktea();
            } else if (x == 2) {
                dsdrink[i] = new Fruittea();
            }
            dsdrink[i].nhap();
        }
    }

    public void xuat() {
        for (int i = 0; i < n; i++) {
            dsdrink[i].xuat();
        }
    }

    public void them() {
        dsdrink = Arrays.copyOf(dsdrink, n + 1);

        System.out.println("Nhap loai do uong: 1.Tra sua 2.Tra trai cay");
        Scanner rd = new Scanner(System.in);
        int x = rd.nextInt();
        while (x != 1 && x != 2) {
            System.out.println("Ko hop le nhap lai!");
            x = rd.nextInt();
        }
        if (x == 1) {
            dsdrink[n] = new Milktea();
        } else if (x == 2) {
            dsdrink[n] = new Fruittea();
        }

        dsdrink[n].nhap();
        n++;
    }

    public void xoa() {
        Scanner rd = new Scanner(System.in);
        System.out.println("ten do uong can xoa: ");
        String tenDrink = rd.nextLine();
        xoa(tenDrink);
    }

    public void xoa(String tenDrink) {
        for (int i = 0; i < n; i++) {
            if (dsdrink[i].getTen().equals(tenDrink)) {
                for (int j = i; j < n - 1; j++) {
                    dsdrink[j] = dsdrink[j + 1];
                }
                dsdrink = Arrays.copyOf(dsdrink, n - 1);
                n--;
                break;
            }
        }
    }

    public void timkiem() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Nhap ten do uong can tim: ");
        String tenDrink = rd.nextLine();
        timkiem(tenDrink);
    }

    public void timkiem(String tenDrink) {
        boolean x = false;
        for (int i = 0; i < n; i++) {
            if (dsdrink[i].getTen().equals(tenDrink)) {
                dsdrink[i].xuat();
                x = true;
            }
        }
        if (x == false) {
            System.out.println("Khong tim thay");
        }
    }

    public void sua() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Nhap ten do uong can sua");
        String tenDrink = rd.nextLine();
        sua(tenDrink);
    }

    public void sua(String tenDrink) {
        for (int i = 0; i < n; i++) {
            if (dsdrink[i].getTen().equals(tenDrink)) {

                dsdrink[i].nhap();
                break;
            }
        }
    }

    public void ghiFile(String tenFile) {
        try (FileWriter fw = new FileWriter(tenFile)) {
            fw.write(n + "\n"); // Ghi số lượng phần tử vào file
            for (int i = 0; i < n; i++) {
                if (dsdrink[i] instanceof Milktea) {
                    fw.write("Milktea," + dsdrink[i].getTen() + "," + dsdrink[i].getGia() + ","
                            + ((Milktea) dsdrink[i]).getTopping() + "\n");
                } else if (dsdrink[i] instanceof Fruittea) {
                    fw.write("Fruittea," + dsdrink[i].getTen() + "," + dsdrink[i].getGia() + ","
                            + ((Fruittea) dsdrink[i]).getTraicay() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void docFile(String tenFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(tenFile))) {
            n = Integer.parseInt(br.readLine()); // Đọc số lượng phần tử từ file
            dsdrink = new Drink[n]; // Khởi tạo mảng với kích thước n
            String line;
            for (int i = 0; i < n; i++) {
                line = br.readLine();
                String[] parts = line.split(",");
                if (parts[0].equals("Milktea")) {
                    dsdrink[i] = new Milktea(parts[1], Integer.parseInt(parts[2]), parts[3]);
                } else if (parts[0].equals("Fruittea")) {
                    dsdrink[i] = new Fruittea(parts[1], Integer.parseInt(parts[2]), parts[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Customer implements Inhapxuat {
    private String tenKH;
    private String sdtKH;

    public Customer() {
        tenKH = "";
        sdtKH = "";
    }

    public Customer(String tenKH, String sdtKH) {
        this.tenKH = tenKH;
        this.sdtKH = sdtKH;
    }

    public String gettenKH() {
        return tenKH;
    }

    public String getSdtKH() {
        return sdtKH;
    }

    public void nhap() {
        Scanner rd = new Scanner(System.in);
        System.out.println("nhap ten khach hang, so dien thoai: ");
        tenKH = rd.nextLine();
        sdtKH = rd.nextLine();
    }

    public void xuat() {
        System.out.println("tên khách hàng: " + tenKH + ", so dien thoai: " + sdtKH);
    }
}

class CustomerList implements IQuanLy {
    private Customer[] dscustomer;
    private int n;

    public CustomerList() {
        dscustomer = new Customer[0];
        n = 0;
    }

    public CustomerList(Customer[] dscustomer, int n) {
        this.dscustomer = dscustomer;
        this.n = n;
    }

    public void nhap() {
        Scanner rd = new Scanner(System.in);
        System.out.println("nhap so luong KH:");
        n = rd.nextInt();
        dscustomer = new Customer[n];
        for (int i = 0; i < n; i++) {
            dscustomer[i] = new Customer();
            dscustomer[i].nhap();
        }
    }

    public void xuat() {
        for (int i = 0; i < n; i++) {
            dscustomer[i].xuat();
        }
    }

    public void timkiem() {
        Scanner rd = new Scanner(System.in);
        System.out.println("nhập tên khách cần tìm");
        String name = rd.nextLine();
        timKiemKhachHang(name);
    }

    // Tìm kiếm khách hàng theo tên
    public void timKiemKhachHang(String tenKH) {
        boolean x = false;
        for (int i = 0; i < n; i++) {
            if (dscustomer[i].gettenKH().equals(tenKH)) {
                dscustomer[i].xuat();
                x = true;
            }
        }
        if (x == false) {
            System.out.println("Không tìm thấy khách hàng.");
        }
    }

    public void them() {
        dscustomer = Arrays.copyOf(dscustomer, n + 1);
        dscustomer[n] = new Customer();
        dscustomer[n].nhap();
        n++;
    }

    public void sua() {
        Scanner rd = new Scanner(System.in);
        System.out.println("nhập tên khách hàng cần sửa:");
        String suatt = rd.nextLine();
        sua(suatt);
    }

    public void sua(String suatt) {
        boolean x = false;
        for (int i = 0; i < n; i++) {
            if (dscustomer[i].gettenKH().equals(suatt)) {
                dscustomer[i] = new Customer();
                dscustomer[i].nhap();
                x = true;
                break;
            }
        }
        if (x == false) {
            System.out.println("ko tìm thấy");
        }
    }

    public void xoa() {
        Scanner rd = new Scanner(System.in);
        System.out.println("nhập tên khách hàng");
        String nn = rd.nextLine();
        xoaKhachHang(nn);
    }

    // Xóa khách hàng khỏi danh sách
    public void xoaKhachHang(String nn) {
        for (int i = 0; i < n; i++) {
            if (dscustomer[i].gettenKH().equals(nn)) {
                for (int j = i; j < n - 1; j++) {
                    dscustomer[j] = dscustomer[j + 1];
                }
                dscustomer[n - 1] = null;
                n--;
                System.out.println("Đã xóa khách hàng: " + nn);
                return;
            }
        }
        System.out.println("Khách hàng không tồn tại.");
    }

    public void ghiFile(String tenFile) {
        try (FileWriter fw = new FileWriter(tenFile)) {
            fw.write(n + "\n"); // Ghi số lượng khách hàng
            for (int i = 0; i < n; i++) {
                fw.write(dscustomer[i].gettenKH() + "," + dscustomer[i].getSdtKH() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void docFile(String tenFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(tenFile))) {
            n = Integer.parseInt(br.readLine()); // Đọc số lượng khách hàng
            dscustomer = new Customer[n];
            String line;
            for (int i = 0; i < n; i++) {
                line = br.readLine();
                String[] parts = line.split(",");
                dscustomer[i] = new Customer(parts[0], parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HoaDon implements Inhapxuat {
    private Drink nuoc;
    private Customer khach;
    public static int slnuoc = 0;

    public HoaDon() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Chọn loại đồ uống: 1. Trà sua 2. Trà trái cây");
        int choice = rd.nextInt();
        rd.nextLine(); // Đọc bỏ dòng mới
        if (choice == 1) {
            nuoc = new Milktea();
        } else if (choice == 2) {
            nuoc = new Fruittea();
        } else {
            System.out.println("lua chon khong hop le , mac dinh se chon tra sua");
            nuoc = new Milktea();
        }
        khach = new Customer();
        slnuoc++;
    }

    public HoaDon(Drink nuoc, Customer khach) {
        this.nuoc = nuoc;
        this.khach = khach;
        slnuoc++;
    }

    public static void howmanyNuoc() {
        System.out.println("so luong nuoc ban ra: " + slnuoc);
    }

    public Drink getNuoc() {
        return nuoc;
    }

    public Customer getKhach() {
        return khach;
    }

    public void nhap() {
        System.out.println("Nhập thông tin khách hàng:");
        khach.nhap();
        System.out.println("Nhập thông tin đồ uống:");
        nuoc.nhap();
    }

    public void xuat() {
        System.out.println("Thông tin hóa don:");
        khach.xuat();
        nuoc.xuat();
    }

}

class DSHoaDon implements IQuanLy {
    private HoaDon[] dsbill;
    private int n;

    public DSHoaDon() {
        n = 0;
        dsbill = new HoaDon[0];
    }

    public DSHoaDon(HoaDon[] dsbill, int n) {
        this.n = n;
        this.dsbill = dsbill;
    }

    public void nhap() {
        dsbill = new HoaDon[n];
        for (int i = 0; i < n; i++) {
            dsbill[i] = new HoaDon();
            dsbill[i].nhap();
        }
    }

    public void xuat() {
        for (int i = 0; i < n; i++) {
            dsbill[i].xuat();
        }
    }

    public void them() {
        dsbill = Arrays.copyOf(dsbill, n + 1);
        dsbill[n] = new HoaDon();
        dsbill[n].nhap();
        n++;
    }

    public void xoa() {
        Scanner rd = new Scanner(System.in);
        System.out.println("nhập ten khách hàng: ");
        String sdt = rd.nextLine();
        xoa(sdt);
    }

    public void xoa(String sdt) {
        for (int i = 0; i < n; i++) {
            if (dsbill[i].getKhach().gettenKH().equals(sdt)) {
                for (int j = i; j < n - 1; j++) {
                    dsbill[j] = dsbill[j + 1];
                }
                dsbill = Arrays.copyOf(dsbill, n - 1);
                n--;
                break;
            }
        }
    }

    // Hàm sửa hóa đơn không tham số
    public void sua() {
        Scanner rd = new Scanner(System.in);
        System.out.println("Nhập tên khách hàng cần sửa: ");
        String tenkhach = rd.nextLine();
        sua(tenkhach);
    }

    // Hàm sửa hóa đơn có tham số
    public void sua(String tenkhach) {
        for (int i = 0; i < n; i++) {
            if (dsbill[i].getKhach().gettenKH().equals(tenkhach)) {
                System.out.println("Sửa thông tin khách hàng và đồ uống:");
                dsbill[i].nhap();
                return;
            }
        }
        System.out.println("Khong tim thay: " + tenkhach);
    }

    public void timkiem() {
        Scanner rd = new Scanner(System.in);
        System.out.println("nhap ten KH can tim: ");
        String sdt = rd.nextLine();
        timkiem(sdt);
    }

    public void timkiem(String sdt) {
        boolean found = false;
        for (int i = 0; i < n; i++) {
            if (dsbill[i].getKhach().gettenKH().equals(sdt)) {
                System.out.println("tim thay hoa don:");
                dsbill[i].xuat();
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Khong tim thay: " + sdt);
        }
    }

    public void ghiFile(String tenFile) {
        try (FileWriter fw = new FileWriter(tenFile)) {
            fw.write(n + "\n"); // Ghi số lượng hóa đơn
            for (int i = 0; i < n; i++) {
                HoaDon hd = dsbill[i];
                fw.write(hd.getKhach().gettenKH() + "," + hd.getKhach().getSdtKH() + "," +
                        hd.getNuoc().getClass().getSimpleName() + "," +
                        hd.getNuoc().getTen() + "," + hd.getNuoc().getGia() + ",");
                if (hd.getNuoc() instanceof Milktea) {
                    fw.write(((Milktea) hd.getNuoc()).getTopping() + "\n");
                } else if (hd.getNuoc() instanceof Fruittea) {
                    fw.write(((Fruittea) hd.getNuoc()).getTraicay() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void docFile(String tenFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(tenFile))) {
            n = Integer.parseInt(br.readLine()); // Đọc số lượng hóa đơn
            dsbill = new HoaDon[n];
            String line;
            for (int i = 0; i < n; i++) {
                line = br.readLine();
                String[] parts = line.split(",");
                Customer kh = new Customer(parts[0], parts[1]);
                Drink nuoc;
                if (parts[2].equals("Milktea")) {
                    nuoc = new Milktea(parts[3], Integer.parseInt(parts[4]), parts[5]);
                } else {
                    nuoc = new Fruittea(parts[3], Integer.parseInt(parts[4]), parts[5]);
                }
                dsbill[i] = new HoaDon(nuoc, kh);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doanhthu() {
        int tong1, tong2, tong3, tong4;
        tong1 = tong2 = tong3 = tong4 = 0;

        for (int i = 0; i < n; i++) {
            if (dsbill[i].getNuoc() instanceof Milktea) {
                tong1 += dsbill[i].getNuoc().getGia();
                tong3 += dsbill[i].getNuoc().getCost();
            } else {
                tong2 += dsbill[i].getNuoc().getGia();
                tong4 += dsbill[i].getNuoc().getCost();
            }
        }
        int tong = tong1 + tong2;
        int cost = tong3 + tong4;
        HoaDon.howmanyNuoc();
        System.out.println("Doanh thu cua tra sua: " + tong1);
        System.out.println("Doanh thu cua tra trai cay: " + tong2);
        System.out.println("chi phí tra sua: " + tong3);
        System.out.println("chi phi tra trai cay " + tong4);
        System.out.println("Tong doanh thu: " + tong);
        System.out.println("Tong chi phi: " + cost);
        System.out.println("Loi nhuan: " + (tong - cost));
    }

}

public class BanTS {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSDrink dsDoUong = new DSDrink();
        CustomerList dsKhachHang = new CustomerList();
        DSHoaDon dsHoaDon = new DSHoaDon();

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Quan ly do uong");
            System.out.println("2. Quan ly khach hang");
            System.out.println("3. Quan ly hoa don");
            System.out.println("4. Thoat");
            System.out.print("Chon chuc nang: ");
            int chon = scanner.nextInt();
            scanner.nextLine(); // Bỏ dòng trống

            switch (chon) {
                case 1: // Quản lý đồ uống
                    int chonDoUong;
                    do {
                        System.out.println("\n*** Quan ly do uong *");
                        System.out.println("1. Nhap danh sach do uong");
                        System.out.println("2. Xuat danh sach do uong");
                        System.out.println("3. Them do uong");
                        System.out.println("4. Sua do uong");
                        System.out.println("5. Xoa do uong");
                        System.out.println("6. Tim kiem do uong");
                        System.out.println("7. Doc danh sach tu file");
                        System.out.println("8. Ghi danh sach vao file");
                        System.out.println("9. Tro ve menu chinh");
                        System.out.print("Chon chuc nang: ");
                        chonDoUong = scanner.nextInt();
                        scanner.nextLine();

                        switch (chonDoUong) {
                            case 1:
                                dsDoUong.nhap();
                                break;
                            case 2:
                                dsDoUong.xuat();
                                break;
                            case 3:
                                dsDoUong.them();
                                break;
                            case 4:
                                dsDoUong.sua();
                                break;
                            case 5:
                                dsDoUong.xoa();
                                break;
                            case 6:
                                dsDoUong.timkiem();
                                break;
                            case 7:
                                System.out.print("Nhap ten file de doc: ");
                                String fileDoUongDoc = scanner.nextLine();
                                dsDoUong.docFile(fileDoUongDoc);
                                break;
                            case 8:
                                System.out.print("Nhap ten file de ghi: ");
                                String fileDoUongGhi = scanner.nextLine();
                                dsDoUong.ghiFile(fileDoUongGhi);
                                break;
                            case 9:
                                System.out.println("Tro ve menu chinh.");
                                break;
                            default:
                                System.out.println("Chuc nang khong hop le. Vui long chon lai.");
                        }
                    } while (chonDoUong != 9);
                    break;

                case 2: // Quản lý khách hàng
                    int chonKhachHang;
                    do {
                        System.out.println("\n*** Quan ly khach hang *");
                        System.out.println("1. Nhap danh sach khach hang");
                        System.out.println("2. Xuat danh sach khach hang");
                        System.out.println("3. Them khach hang");
                        System.out.println("4. Sua thong tin khach hang");
                        System.out.println("5. Xoa khach hang");
                        System.out.println("6. Tim kiem khach hang");
                        System.out.println("7. Doc danh sach tu file");
                        System.out.println("8. Ghi danh sach vao file");
                        System.out.println("9. Tro ve menu chinh");
                        System.out.print("Chon chuc nang: ");
                        chonKhachHang = scanner.nextInt();
                        scanner.nextLine();

                        switch (chonKhachHang) {
                            case 1:
                                dsKhachHang.nhap();
                                break;
                            case 2:
                                dsKhachHang.xuat();
                                break;
                            case 3:
                                dsKhachHang.them();
                                break;
                            case 4:
                                dsKhachHang.sua();
                                break;
                            case 5:
                                dsKhachHang.xoa();
                                break;
                            case 6:
                                dsKhachHang.timkiem();
                                break;
                            case 7:
                                System.out.print("Nhap ten file de doc: ");
                                String fileKhachHangDoc = scanner.nextLine();
                                dsKhachHang.docFile(fileKhachHangDoc);
                                break;
                            case 8:
                                System.out.print("Nhap ten file de ghi: ");
                                String fileKhachHangGhi = scanner.nextLine();
                                dsKhachHang.ghiFile(fileKhachHangGhi);
                                break;
                            case 9:
                                System.out.println("Tro ve menu chinh.");
                                break;
                            default:
                                System.out.println("Chuc nang khong hop le. Vui long chon lai.");
                        }
                    } while (chonKhachHang != 9);
                    break;

                case 3: // Quản lý hóa đơn
                    int chonHoaDon;
                    do {
                        System.out.println("\n*** Quan ly hoa don *");
                        System.out.println("1. Nhap danh sach hoa don");
                        System.out.println("2. Xuat danh sach hoa don");
                        System.out.println("3. Them hoa don");
                        System.out.println("4. Sua hoa don");
                        System.out.println("5. Xoa hoa don");
                        System.out.println("6. Tim kiem hoa don");
                        System.out.println("7. Doc danh sach tu file");
                        System.out.println("8. Ghi danh sach vao file");
                        System.out.println("9. Tro ve menu chinh");
                        System.out.println("10. Xem doanh thu");
                        System.out.print("Chon chuc nang: ");
                        chonHoaDon = scanner.nextInt();
                        scanner.nextLine();

                        switch (chonHoaDon) {
                            case 1:
                                System.out.print("Nhap so luong hoa don: ");
                                int soLuongHD = scanner.nextInt();
                                scanner.nextLine();
                                dsHoaDon = new DSHoaDon(new HoaDon[soLuongHD], soLuongHD);
                                dsHoaDon.nhap();
                                break;
                            case 2:
                                dsHoaDon.xuat();
                                break;
                            case 3:
                                dsHoaDon.them();
                                break;
                            case 4:
                                dsHoaDon.sua();
                                break;
                            case 5:
                                dsHoaDon.xoa();
                                break;
                            case 6:
                                dsHoaDon.timkiem();
                                break;
                            case 7:
                                System.out.print("Nhap ten file de doc: ");
                                String fileHoaDonDoc = scanner.nextLine();
                                dsHoaDon.docFile(fileHoaDonDoc);
                                break;
                            case 8:
                                System.out.print("Nhap ten file de ghi: ");
                                String fileHoaDonGhi = scanner.nextLine();
                                dsHoaDon.ghiFile(fileHoaDonGhi);
                                break;
                            case 10:
                                dsHoaDon.doanhthu();
                                break;
                            case 9:
                                System.out.println("Tro ve menu chinh.");
                                break;
                            default:
                                System.out.println("Chuc nang khong hop le. Vui long chon lai.");
                        }
                    } while (chonHoaDon != 9);
                    break;

                case 4: // Thoát
                    System.out.println("Thoat chuong trinh.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Chuc nang khong hop le. Vui long chon lai.");
            }
        }
    }
}