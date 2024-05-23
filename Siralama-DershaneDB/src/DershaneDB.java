import java.awt.Cursor;
import java.awt.EventQueue;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.print.CancelablePrintJob;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DershaneDB extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	
	Connection baglanti = null;
	PreparedStatement sorgu;
	ResultSet gelenVeri;
	private JButton btnSirala;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DershaneDB frame = new DershaneDB();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DershaneDB() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 614, 314);
		this.setTitle("ÖĞRENCİ SIRALAMA EKRANI");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable();

		
		
		JScrollPane scrollBar = new JScrollPane(table);
		scrollBar.setBounds(10, 11, 578, 217);
		contentPane.add(scrollBar);
		
		btnSirala = new JButton("SIRALA");
		btnSirala.setBounds(10, 239, 89, 23);
		contentPane.add(btnSirala);

		btnSirala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Instant start = Instant.now();
					sirala();
					Instant end = Instant.now();

					JOptionPane.showMessageDialog(null, Duration.between(start, end));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		baglan();
		
	

		
	}
	
	public void baglan() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url =  "jdbc:mysql://localhost:3306/ödev?characterEncoding=utf8";
			String username = "root";
			String password = "1234567890";
			baglanti = DriverManager.getConnection(url, username, password);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void listele() {
		try {
			sorgu = baglanti.prepareStatement("SELECT * FROM ogrenci");
			gelenVeri = sorgu.executeQuery();
			

			
			table.setModel(DbUtils.resultSetToTableModel(gelenVeri));
			


		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void sirala() throws SQLException
	{
		sorgu = baglanti.prepareStatement("SELECT * FROM ogrenci ORDER BY puan DESC",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		gelenVeri = sorgu.executeQuery();
		
		//Toplam veri sayısına göre diziler oluşturuldu.
	   gelenVeri.last();			
	   double[] puan = new double[gelenVeri.getRow()+1];
       int[] ogrenci_no = new int[gelenVeri.getRow()+1];
       String[] sehir = new String[gelenVeri.getRow()+1];
    
		
        //Veriler diziye kaydedildi.
        gelenVeri.beforeFirst();
        int say=0;
		while (gelenVeri.next()) {
			puan[say] = gelenVeri.getDouble("puan");
			ogrenci_no[say] = gelenVeri.getInt("ogrenci_no");
			sehir[say]=gelenVeri.getString("sehir"); 
			say++;
		}  
		
		//Sıralama yapılıyor.
        
        for (int i = 0; i < gelenVeri.getRow(); i++) {
            int sehir_sira = 1;

            for (int j =0; j < gelenVeri.getRow(); j++) {
            	   
                if (sehir[i] != null && sehir[i].equals(sehir[j])&&puan[i]<puan[j] ) {
                    sehir_sira++;
                }
              
            }
            
            
        	PreparedStatement sorgu = baglanti.prepareStatement("UPDATE ogrenci SET genel_sira=?, sehir_sira=? WHERE ogrenci_no=?");
        	sorgu.setInt(1, i+1);
        	sorgu.setInt(2, sehir_sira);
        	sorgu.setInt(3, ogrenci_no[i]);
        	sorgu.executeUpdate(); 

          
        }

        

        listele();

	}
	
}
