import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;

public class PembayaranPenyewa extends javax.swing.JFrame {

    private int idPenyewa;
    private String namaPenyewa;
    private int idKontrakAktif = -1;


    public PembayaranPenyewa() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Pembayaran - KosanKu");
    }

    // Constructor with parameters
    public PembayaranPenyewa(int idPenyewa, String namaPenyewa) {
        this.idPenyewa = idPenyewa;
        this.namaPenyewa = namaPenyewa;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Pembayaran - KosanKu");

        // Set nama di label
        jLabelHaloName.setText("Halo, " + namaPenyewa + "!");

        // Get ID kontrak aktif
        getIdKontrakAktif();

        // Set format tanggal
        setupDateFormat();
    }

    private void getIdKontrakAktif() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id_kontrak FROM kontrak_sewa WHERE id_penyewa = ? AND status_kontrak = 'aktif'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, this.idPenyewa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idKontrakAktif = rs.getInt("id_kontrak");
            } else {
                 JOptionPane.showMessageDialog(this, "Tidak ada kontrak sewa aktif yang ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting contract ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupDateFormat() {
        // Set format tanggal 
        try {
            javax.swing.text.MaskFormatter dateFormatter = new javax.swing.text.MaskFormatter("##-##-####");
            dateFormatter.setPlaceholderCharacter('_');
            jFormattedTextFieldTanggalTFPenyewa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(dateFormatter));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void kirimKonfirmasiPembayaran() {
        if (idKontrakAktif == -1) {
            JOptionPane.showMessageDialog(this, "Tidak dapat mengirim konfirmasi karena tidak ada kontrak aktif.");
            return;
        }

        try {
            // Validasi input
            String tanggalTransferStr = jFormattedTextFieldTanggalTFPenyewa.getText().trim();
            String jumlahTransferStr = jTextFieldJumlahTFPenyewa.getText().trim();
            String catatan = jTextAreaCatatanBayarPenyewa.getText().trim();

            if (tanggalTransferStr.contains("_")) {
                JOptionPane.showMessageDialog(this, 
                    "Silakan masukkan tanggal transfer dengan format dd-mm-yyyy!", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (jumlahTransferStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Silakan masukkan jumlah transfer!", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validasi format tanggal
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            java.util.Date tanggalTransferDate;

            try {
                tanggalTransferDate = sdf.parse(tanggalTransferStr);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Format tanggal tidak valid! Gunakan format dd-mm-yyyy", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validasi jumlah transfer
            BigDecimal jumlahTransfer;
            try {
                jumlahTransfer = new BigDecimal(jumlahTransferStr);
                if (jumlahTransfer.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Jumlah transfer harus lebih dari 0!", 
                        "Input Error", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Jumlah transfer harus berupa angka!", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert ke database
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO pembayaran (id_kontrak, tanggal_transfer, jumlah_transfer, catatan, status_konfirmasi) VALUES (?, ?, ?, ?, 'pending')";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, idKontrakAktif);
                stmt.setDate(2, new java.sql.Date(tanggalTransferDate.getTime()));
                stmt.setBigDecimal(3, jumlahTransfer);
                stmt.setString(4, catatan);

                int result = stmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Konfirmasi pembayaran berhasil dikirim!\nMenunggu konfirmasi dari pemilik kos.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Gagal mengirim konfirmasi pembayaran!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                stmt.close();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void clearForm() {
        jFormattedTextFieldTanggalTFPenyewa.setValue(null);
        jFormattedTextFieldTanggalTFPenyewa.setText("");
        jTextFieldJumlahTFPenyewa.setText("");
        jTextAreaCatatanBayarPenyewa.setText("");
    }

    private void showRiwayatPembayaran() {
         if (idKontrakAktif == -1) {
            JOptionPane.showMessageDialog(this, "Tidak dapat menampilkan riwayat karena tidak ada kontrak aktif.");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()){
            String query = """
                SELECT p.tanggal_transfer, p.jumlah_transfer, p.catatan,
                       p.status_konfirmasi, p.tanggal_konfirmasi, p.catatan_pemilik
                FROM pembayaran p
                WHERE p.id_kontrak = ?
                ORDER BY p.created_at DESC
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idKontrakAktif);
            ResultSet rs = stmt.executeQuery();

            // Buat tabel riwayat sementara
            String[] columnNames = {"Tanggal Transfer", "Jumlah", "Catatan Anda", "Status", "Tgl Konfirmasi", "Catatan Pemilik"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = sdf.format(rs.getDate("tanggal_transfer"));
                row[1] = "Rp " + String.format("%,.0f", rs.getBigDecimal("jumlah_transfer"));
                row[2] = rs.getString("catatan");
                row[3] = rs.getString("status_konfirmasi");

                Timestamp tanggalKonfirmasi = rs.getTimestamp("tanggal_konfirmasi");
                row[4] = tanggalKonfirmasi != null ? sdfTime.format(tanggalKonfirmasi) : "-";
                row[5] = rs.getString("catatan_pemilik") != null ? rs.getString("catatan_pemilik") : "-";

                model.addRow(row);
            }
            
            if (model.getRowCount() == 0) {
                 JOptionPane.showMessageDialog(this, "Belum ada riwayat pembayaran.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                 return;
            }

            javax.swing.JTable table = new javax.swing.JTable(model);
            table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
            scrollPane.setPreferredSize(new java.awt.Dimension(800, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Riwayat Pembayaran", JOptionPane.PLAIN_MESSAGE);

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payment history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonLogout = new javax.swing.JButton();
        jButtonPelaporan = new javax.swing.JButton();
        jButtonPembayaran = new javax.swing.JButton();
        jButtonKamarku = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabelHaloName = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jFormattedTextFieldTanggalTFPenyewa = new javax.swing.JFormattedTextField();
        jTextFieldJumlahTFPenyewa = new javax.swing.JTextField();
        jButtonKirimKonfirmasiPembayaranPenyewa = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaCatatanBayarPenyewa = new javax.swing.JTextArea();
        jButtonRiwayatPembayaranPenyewa = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(150, 480));
        jPanel1.setPreferredSize(new java.awt.Dimension(150, 480));

        jButtonLogout.setText("Logout");
        jButtonLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogoutMouseClicked(evt);
            }
        });

        jButtonPelaporan.setText("Pelaporan");
        jButtonPelaporan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonPelaporanMouseClicked(evt);
            }
        });
        jButtonPelaporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPelaporanActionPerformed(evt);
            }
        });

        jButtonPembayaran.setText("Pembayaran");
        jButtonPembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPembayaranActionPerformed(evt);
            }
        });

        jButtonKamarku.setText("Kamarku");
        jButtonKamarku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonKamarkuMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("KOSANKU");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonPembayaran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonKamarku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonPelaporan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGap(37, 37, 37)
                .addComponent(jButtonKamarku)
                .addGap(18, 18, 18)
                .addComponent(jButtonPembayaran)
                .addGap(18, 18, 18)
                .addComponent(jButtonPelaporan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 244, Short.MAX_VALUE)
                .addComponent(jButtonLogout)
                .addGap(18, 18, 18))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabelHaloName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelHaloName.setText("Halo, Nama!");
        getContentPane().add(jLabelHaloName, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, -1, -1));

        jPanel2.setMinimumSize(new java.awt.Dimension(550, 330));
        jPanel2.setPreferredSize(new java.awt.Dimension(550, 330));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Konfirmasi Pembayaran");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Tanggal Transfer                                      :");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Jumlah Transfer                                       :");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Catatan                                                   :");

        jFormattedTextFieldTanggalTFPenyewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldTanggalTFPenyewaActionPerformed(evt);
            }
        });

        jButtonKirimKonfirmasiPembayaranPenyewa.setText("Kirim");
        jButtonKirimKonfirmasiPembayaranPenyewa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonKirimKonfirmasiPembayaranPenyewaMouseClicked(evt);
            }
        });

        jTextAreaCatatanBayarPenyewa.setColumns(20);
        jTextAreaCatatanBayarPenyewa.setRows(5);
        jScrollPane1.setViewportView(jTextAreaCatatanBayarPenyewa);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(190, 190, 190))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonKirimKonfirmasiPembayaranPenyewa, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jFormattedTextFieldTanggalTFPenyewa)
                            .addComponent(jTextFieldJumlahTFPenyewa)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel15)
                .addGap(36, 36, 36)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jFormattedTextFieldTanggalTFPenyewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jTextFieldJumlahTFPenyewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonKirimKonfirmasiPembayaranPenyewa)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 590, 330));

        jButtonRiwayatPembayaranPenyewa.setText("Cek Riwayat");
        jButtonRiwayatPembayaranPenyewa.setPreferredSize(new java.awt.Dimension(72, 23));
        jButtonRiwayatPembayaranPenyewa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonRiwayatPembayaranPenyewaMouseClicked(evt);
            }
        });
        getContentPane().add(jButtonRiwayatPembayaranPenyewa, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 430, 110, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon("D:\\CIRA\\CODING\\LATIHAN\\Semester 4\\PBO\\PenyewaKosanKu\\src\\Assets\\white bg.png")); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 790, 480));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonPelaporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPelaporanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonPelaporanActionPerformed

    private void jButtonPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPembayaranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonPembayaranActionPerformed

    private void jFormattedTextFieldTanggalTFPenyewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldTanggalTFPenyewaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextFieldTanggalTFPenyewaActionPerformed

    private void jButtonLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogoutMouseClicked
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.clearSession();
            new LoginPenyewa().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_jButtonLogoutMouseClicked

    private void jButtonPelaporanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPelaporanMouseClicked
        // TODO add your handling code here:
        new PelaporanPenyewa(this.idPenyewa, this.namaPenyewa).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonPelaporanMouseClicked

    private void jButtonKirimKonfirmasiPembayaranPenyewaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonKirimKonfirmasiPembayaranPenyewaMouseClicked
        // TODO add your handling code here:
        kirimKonfirmasiPembayaran();
    }//GEN-LAST:event_jButtonKirimKonfirmasiPembayaranPenyewaMouseClicked

    private void jButtonRiwayatPembayaranPenyewaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRiwayatPembayaranPenyewaMouseClicked
        // TODO add your handling code here:
        showRiwayatPembayaran();
    }//GEN-LAST:event_jButtonRiwayatPembayaranPenyewaMouseClicked

    private void jButtonKamarkuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonKamarkuMouseClicked
        // TODO add your handling code here:
        new DashboardPenyewa().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonKamarkuMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PembayaranPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PembayaranPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PembayaranPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PembayaranPenyewa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PembayaranPenyewa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonKamarku;
    private javax.swing.JButton jButtonKirimKonfirmasiPembayaranPenyewa;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonPelaporan;
    private javax.swing.JButton jButtonPembayaran;
    private javax.swing.JButton jButtonRiwayatPembayaranPenyewa;
    private javax.swing.JFormattedTextField jFormattedTextFieldTanggalTFPenyewa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelHaloName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaCatatanBayarPenyewa;
    private javax.swing.JTextField jTextFieldJumlahTFPenyewa;
    // End of variables declaration//GEN-END:variables
}
